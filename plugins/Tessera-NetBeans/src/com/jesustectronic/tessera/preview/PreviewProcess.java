package com.jesustectronic.tessera.preview;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.List;
import java.util.function.Consumer;

/**
 * Una vista previa en marcha: un proceso java aparte con la pantalla y un vigilante que lo relanza cuando cambian las
 * clases compiladas (el IDE las compila al guardar). No depende de NetBeans, para poder probarse sola.
 */
final class PreviewProcess {

  private final List<String> command;
  private final File workDir;
  private final List<File> watchRoots;
  private final Consumer<String> out;
  private final Runnable onClosed;
  private ClassWatcher watcher;

  private Process process;
  private volatile boolean stopped;
  /** Cada lanzamiento tiene su numero: el aviso de que murio un proceso viejo (al relanzar) no cierra el nuevo. */
  private int generation;

  PreviewProcess(List<String> command, File workDir, List<File> watchRoots, Consumer<String> out, Runnable onClosed) {
    this.command = command;
    this.workDir = workDir;
    this.watchRoots = watchRoots;
    this.out = out;
    this.onClosed = onClosed;
  }

  /** Lanza la pantalla y empieza a vigilar. */
  void start() throws IOException {
    launch();
    watcher = new ClassWatcher(watchRoots, this::restartIfRunning);
    watcher.start();
  }

  /** Cierra la pantalla y deja de vigilar. */
  void stop() {
    stopped = true;
    if (watcher != null) {
      watcher.stop();
    }
    final Process p;
    synchronized (this) {
      p = process;
    }
    if (p != null) {
      p.destroyForcibly();
    }
  }

  private synchronized void launch() throws IOException {
    final ProcessBuilder builder = new ProcessBuilder(command).directory(workDir).redirectErrorStream(true);
    final Process started = builder.start();
    process = started;
    final int mine = ++generation;

    final Thread pump = new Thread(() -> pump(started), "Tessera Preview output");
    pump.setDaemon(true);
    pump.start();

    final Thread exit = new Thread(() -> {
      try {
        started.waitFor();
      } catch (InterruptedException e) {
        return;
      }
      // si se cerro la ventana (y no es un relanzado), se termina la sesion
      synchronized (PreviewProcess.this) {
        if (mine != generation || stopped) {
          return;
        }
        stopped = true;
      }
      out.accept("[Preview] ventana cerrada");
      onClosed.run();
    }, "Tessera Preview exit");
    exit.setDaemon(true);
    exit.start();
  }

  private void pump(Process p) {
    try (BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream(), Charset.defaultCharset()))) {
      String line;
      while ((line = reader.readLine()) != null) {
        out.accept(line);
      }
    } catch (IOException ignored) {
      // el proceso se cerro
    }
  }

  private void restart() {
    out.accept("[Preview] cambio detectado: relanzando...");
    final Process old;
    synchronized (this) {
      old = process;
      generation++;            // el proceso viejo ya no cuenta
    }
    if (old != null) {
      old.destroyForcibly();
      try {
        old.waitFor();
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
        return;
      }
    }
    try {
      launch();
    } catch (IOException e) {
      out.accept("[Preview] no se pudo relanzar: " + e.getMessage());
      stopped = true;
      onClosed.run();
    }
  }

  private void restartIfRunning() {
    if (!stopped) {
      restart();
    }
  }
}
