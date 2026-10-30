package com.jesustectronic.tessera.preview;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.KeyboardFocusManager;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import javax.imageio.ImageIO;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.RepaintManager;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

/**
 * Programa que corre en un proceso aparte, con el classpath del proyecto: tiene la pantalla viva en una ventana fuera
 * del escritorio y le manda a la pestana "Tessera Preview" del IDE cada cambio como una imagen. La pestana le manda el
 * tamano que debe tener y los clics y teclas del usuario, asi la pantalla reacciona como en la app (hover, estirar,
 * menus). No depende de NetBeans ni de Tessera al compilar (todo por reflexion): vive en el jar del plugin y se agrega
 * al final del classpath del proyecto.
 *
 * <pre>
 * java [-Dtessera.preview.theme=paquete.UI#setLookAndFeel] -cp proyecto;plugin LiveRenderer paquete.Pantalla
 * </pre>
 *
 * Protocolo (todo con DataInput/OutputStream). Por stdin, del IDE a este proceso:
 * <ul>
 * <li>{@code 'S' int ancho, int alto}: tamano de la ventana (crece en alto si la pantalla necesita mas)</li>
 * <li>{@code 'M' int id, int x, int y, int boton, int modificadores, int clics, boolean popup}: evento de mouse</li>
 * <li>{@code 'K' int id, int codigo, int caracter, int modificadores}: evento de teclado</li>
 * </ul>
 * Por stdout, de este proceso al IDE:
 * <ul>
 * <li>{@code 'F' int ancho, int alto, int bytes, <png>}: un cuadro nuevo</li>
 * <li>{@code 'E' UTF texto}: no se pudo armar la pantalla</li>
 * </ul>
 * Lo que la app escriba en System.out se desvia a System.err, para no mezclarse con los cuadros.
 */
public final class LiveRenderer {

  /** Espera (ms) para juntar varios repintados en un solo cuadro. */
  private static final int COALESCE_MS = 35;
  private static final int START_WIDTH = 900;
  private static final int START_HEIGHT = 650;
  private static final String PREVIEW_ANNOTATION = "tessera.app.Preview";

  private static DataOutputStream out;
  private static JFrame frame;
  private static Timer capture;
  private static Component lastPressed;
  private static int[] lastPixels;
  private static int lastWidth;
  private static int lastHeight;

  private LiveRenderer() {
  }

  public static void main(String[] args) throws Exception {
    out = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(FileDescriptor.out), 1 << 16));
    System.setOut(System.err);

    try {
      installTheme(System.getProperty("tessera.preview.theme"));
      final String className = args[0];
      SwingUtilities.invokeAndWait(() -> open(className));
    } catch (Throwable e) {
      sendError(unwrap(e));
      System.exit(1);
    }

    final DataInputStream in = new DataInputStream(new BufferedInputStream(System.in));
    try {
      while (true) {
        final int command = in.read();
        if (command < 0) {
          break;
        }
        switch (command) {
          case 'S': {
            final int w = in.readInt();
            final int h = in.readInt();
            EventQueue.invokeLater(() -> resize(w, h));
            break;
          }
          case 'M': {
            final int id = in.readInt(), x = in.readInt(), y = in.readInt(), button = in.readInt();
            final int modifiers = in.readInt(), clicks = in.readInt();
            final boolean popup = in.readBoolean();
            EventQueue.invokeLater(() -> mouse(id, x, y, button, modifiers, clicks, popup));
            break;
          }
          case 'K': {
            final int id = in.readInt(), code = in.readInt(), ch = in.readInt(), modifiers = in.readInt();
            EventQueue.invokeLater(() -> key(id, code, (char) ch, modifiers));
            break;
          }
          default:
            return;                  // protocolo roto: mejor terminar que seguir leyendo basura
        }
      }
    } catch (EOFException e) {
      // el IDE cerro la tuberia
    }
    System.exit(0);
  }

  // ---------------------------------------------------------------------- //
  //   P A N T A L L A
  // ---------------------------------------------------------------------- //

  private static void open(String className) {
    try {
      final JComponent screen = create(Class.forName(className));

      RepaintManager.setCurrentManager(new RepaintManager() {
        @Override
        public void paintDirtyRegions() {
          super.paintDirtyRegions();
          scheduleCapture();
        }
      });

      frame = new JFrame();
      frame.setUndecorated(true);
      frame.setType(Window.Type.UTILITY);              // sin boton en la barra de tareas
      frame.setFocusableWindowState(false);            // no le quita el foco al IDE
      frame.setAutoRequestFocus(false);
      frame.getContentPane().setLayout(new BorderLayout());
      frame.getContentPane().add(screen, BorderLayout.CENTER);
      frame.setBounds(-20000, -20000, START_WIDTH, START_HEIGHT);       // fuera del escritorio
      frame.setVisible(true);

      capture = new Timer(COALESCE_MS, e -> sendFrame());
      capture.setRepeats(false);
      resize(START_WIDTH, START_HEIGHT);
    } catch (Throwable e) {
      sendError(unwrap(e));
      System.exit(1);
    }
  }

  /** Un componente con constructor sin parametros, o un metodo estatico con @Preview que devuelva uno. */
  private static JComponent create(Class<?> type) throws Exception {
    if (JComponent.class.isAssignableFrom(type) && !Modifier.isAbstract(type.getModifiers())) {
      final Constructor<?> constructor = type.getDeclaredConstructor();
      constructor.setAccessible(true);
      return (JComponent) constructor.newInstance();
    }
    for (Method method : type.getDeclaredMethods()) {
      if (Modifier.isStatic(method.getModifiers()) && method.getParameterTypes().length == 0
          && JComponent.class.isAssignableFrom(method.getReturnType()) && hasPreview(method)) {
        method.setAccessible(true);
        return (JComponent) method.invoke(null);
      }
    }
    throw new IllegalArgumentException(type.getName() + " no es un componente (JPanel...) con constructor sin parametros, "
        + "ni tiene un metodo estatico con @Preview que devuelva uno");
  }

  private static boolean hasPreview(Method method) {
    for (Annotation annotation : method.getAnnotations()) {
      if (annotation.annotationType().getName().equals(PREVIEW_ANNOTATION)) {
        return true;
      }
    }
    return false;
  }

  private static void resize(int width, int height) {
    final int w = Math.max(40, width);
    final int h = Math.max(40, height);
    frame.setSize(w, h);
    frame.validate();
    // una pantalla mas alta que el panel se muestra completa (el IDE da la barra de desplazamiento)
    final Dimension preferred = frame.getContentPane().getComponent(0).getPreferredSize();
    if (preferred.height > h) {
      frame.setSize(w, preferred.height);
      frame.validate();
    }
    scheduleCapture();
  }

  // ---------------------------------------------------------------------- //
  //   C U A D R O S
  // ---------------------------------------------------------------------- //

  private static void scheduleCapture() {
    if (capture != null && !capture.isRunning()) {
      capture.start();
    }
  }

  private static void sendFrame() {
    final int w = frame.getWidth();
    final int h = frame.getHeight();
    frame.validate();
    final BufferedImage image = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
    final java.awt.Graphics2D g = image.createGraphics();
    frame.getRootPane().paint(g);
    g.dispose();

    final int[] pixels = ((DataBufferInt) image.getRaster().getDataBuffer()).getData();
    if (w == lastWidth && h == lastHeight && Arrays.equals(pixels, lastPixels)) {
      return;                                          // nada cambio: no se manda
    }
    lastPixels = pixels.clone();
    lastWidth = w;
    lastHeight = h;

    try {
      final ByteArrayOutputStream png = new ByteArrayOutputStream(1 << 16);
      ImageIO.write(image, "png", png);
      synchronized (out) {
        out.writeByte('F');
        out.writeInt(w);
        out.writeInt(h);
        out.writeInt(png.size());
        png.writeTo(out);
        out.flush();
      }
    } catch (IOException e) {
      System.exit(0);                                  // el IDE ya no escucha
    }
  }

  private static void sendError(Throwable error) {
    final StringWriter text = new StringWriter();
    error.printStackTrace(new PrintWriter(text));
    String message = text.toString();
    if (message.length() > 30000) {
      message = message.substring(0, 30000) + "\n...";
    }
    try {
      synchronized (out) {
        out.writeByte('E');
        out.writeUTF(message);
        out.flush();
      }
    } catch (IOException ignored) {
      // el IDE ya no escucha
    }
  }

  private static Throwable unwrap(Throwable e) {
    Throwable current = e;
    while ((current instanceof InvocationTargetException || current instanceof ExceptionInInitializerError)
        && current.getCause() != null) {
      current = current.getCause();
    }
    return current;
  }

  // ---------------------------------------------------------------------- //
  //   M O U S E   Y   T E C L A D O
  // ---------------------------------------------------------------------- //

  /** El evento se manda a la ventana y Swing lo reparte al componente que esta debajo (hover, arrastres, menus). */
  private static void mouse(int id, int x, int y, int button, int modifiers, int clicks, boolean popup) {
    if (id == MouseEvent.MOUSE_PRESSED) {
      lastPressed = SwingUtilities.getDeepestComponentAt(frame.getContentPane(), x, y);
    }
    final MouseEvent event = new MouseEvent(frame, id, System.currentTimeMillis(), modifiers, x, y, clicks, popup, button);
    Toolkit.getDefaultToolkit().getSystemEventQueue().postEvent(event);
  }

  /** La ventana nunca tiene el foco del sistema: las teclas van al componente con foco o, si no hay, al ultimo clicado. */
  private static void key(int id, int code, char ch, int modifiers) {
    Component target = KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner();
    if (target == null) {
      target = lastPressed;
    }
    if (target != null) {
      target.dispatchEvent(new KeyEvent(target, id, System.currentTimeMillis(), modifiers, code, ch));
    }
  }

  // ---------------------------------------------------------------------- //
  //   T E M A
  // ---------------------------------------------------------------------- //

  /** El tema de la app (paquete.Clase#metodo) o, sin configurar, el tema por defecto de Tessera. */
  private static void installTheme(String spec) throws Exception {
    if (spec != null && spec.indexOf('#') > 0) {
      final int hash = spec.indexOf('#');
      Class.forName(spec.substring(0, hash)).getMethod(spec.substring(hash + 1)).invoke(null);
    } else {
      final Object builder = Class.forName("tessera.theme.Theme").getMethod("builder").invoke(null);
      final Method install = builder.getClass().getMethod("install");
      install.setAccessible(true);
      install.invoke(builder);
    }
  }
}
