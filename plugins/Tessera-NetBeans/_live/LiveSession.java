package com.jesustectronic.tessera.preview;

import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;

/**
 * Una pantalla viva: el proceso {@link LiveRenderer} y la tuberia con el. Recibe los cuadros que manda el proceso y le
 * manda el tamano, el mouse y el teclado. No depende de NetBeans.
 */
final class LiveSession {

  /** Lo que pasa en la pantalla viva. Se llama desde hilos del lector, no desde el hilo de Swing. */
  interface Listener {
    void frame(BufferedImage image);

    /** No se pudo armar la pantalla (con el motivo). */
    void error(String text);

    /** El proceso termino sin avisar de un error: {@code log} es lo que escribio por la salida de errores. */
    void ended(String log);
  }

  private static final int LOG_LIMIT = 20000;

  private final Listener listener;
  private final Process process;
  private final DataOutputStream out;
  private final StringBuilder log = new StringBuilder();
  private final Thread logThread;
  private volatile boolean stopped;

  /** Lanza el proceso con el classpath del proyecto mas el jar del plugin (donde vive LiveRenderer). */
  LiveSession(PreviewTarget target, String pluginJar, Listener listener) throws IOException {
    this.listener = listener;
    final List<String> command = new ArrayList<>();
    command.add(target.java.getAbsolutePath());
    if (target.theme != null) {
      command.add("-Dtessera.preview.theme=" + target.theme);
    }
    command.add("-Djava.awt.headless=false");
    command.add("-cp");
    command.add(target.classpath + File.pathSeparator + pluginJar);
    command.add(LiveRenderer.class.getName());
    command.add(target.className);

    process = new ProcessBuilder(command).directory(target.projectDir).start();
    out = new DataOutputStream(new BufferedOutputStream(process.getOutputStream()));
    logThread = daemon("Tessera Preview log", this::readLog);
    daemon("Tessera Preview frames", this::readFrames);
  }

  private static Thread daemon(String name, Runnable body) {
    final Thread thread = new Thread(body, name);
    thread.setDaemon(true);
    thread.start();
    return thread;
  }

  boolean isAlive() {
    return !stopped && process.isAlive();
  }

  void stop() {
    stopped = true;
    process.destroyForcibly();
  }

  // ---------------------------------------------------------------------- //
  //   D E L   I D E   A L   P R O C E S O
  // ---------------------------------------------------------------------- //

  void resize(int width, int height) {
    send(o -> {
      o.writeByte('S');
      o.writeInt(width);
      o.writeInt(height);
    });
  }

  void mouse(int id, int x, int y, int button, int modifiers, int clicks, boolean popup) {
    send(o -> {
      o.writeByte('M');
      o.writeInt(id);
      o.writeInt(x);
      o.writeInt(y);
      o.writeInt(button);
      o.writeInt(modifiers);
      o.writeInt(clicks);
      o.writeBoolean(popup);
    });
  }

  void key(int id, int code, char ch, int modifiers) {
    send(o -> {
      o.writeByte('K');
      o.writeInt(id);
      o.writeInt(code);
      o.writeInt(ch);
      o.writeInt(modifiers);
    });
  }

  private interface Message {
    void write(DataOutputStream out) throws IOException;
  }

  private void send(Message message) {
    if (stopped) {
      return;
    }
    synchronized (out) {
      try {
        message.write(out);
        out.flush();
      } catch (IOException e) {
        // el proceso termino: lo avisa el lector
      }
    }
  }

  // ---------------------------------------------------------------------- //
  //   D E L   P R O C E S O   A L   I D E
  // ---------------------------------------------------------------------- //

  private void readFrames() {
    boolean reported = false;
    try (DataInputStream in = new DataInputStream(new BufferedInputStream(process.getInputStream(), 1 << 16))) {
      int type;
      while ((type = in.read()) >= 0) {
        if (type == 'F') {
          in.readInt();
          in.readInt();
          final byte[] png = new byte[in.readInt()];
          in.readFully(png);
          final BufferedImage image = ImageIO.read(new ByteArrayInputStream(png));
          if (image != null && !stopped) {
            listener.frame(image);
          }
        } else if (type == 'E') {
          reported = true;
          if (!stopped) {
            listener.error(in.readUTF());
          }
        } else {
          break;
        }
      }
    } catch (IOException ignored) {
      // el proceso termino
    }
    if (!stopped && !reported) {
      try {
        process.waitFor();
        logThread.join(1000);                  // que alcance a leer lo ultimo que escribio
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
      }
      synchronized (log) {
        listener.ended(log.toString());
      }
    }
  }

  /** Lo que la app escribe en System.out / System.err y los errores de la JVM. */
  private void readLog() {
    try (Reader reader = new InputStreamReader(process.getErrorStream(), Charset.defaultCharset())) {
      final char[] buffer = new char[2048];
      int count;
      while ((count = reader.read(buffer)) > 0) {
        synchronized (log) {
          log.append(buffer, 0, count);
          if (log.length() > LOG_LIMIT) {
            log.delete(0, log.length() - LOG_LIMIT);
          }
        }
      }
    } catch (IOException ignored) {
      // el proceso termino
    }
  }

  /** Lo escrito hasta ahora por la salida de errores del proceso. */
  String log() {
    synchronized (log) {
      return log.toString();
    }
  }
}
