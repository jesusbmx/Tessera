package com.jesustectronic.tessera.preview;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import javax.imageio.ImageIO;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

/**
 * Programa que corre en un proceso aparte, con el classpath del proyecto: arma la pantalla, la dibuja en memoria a su
 * tamano natural y manda la imagen PNG como bytes por su salida estandar (no toca el disco). La pestana "Tessera
 * Preview" del editor lee esos bytes y los muestra.
 *
 * <pre>
 * java [-Dtessera.preview.theme=paquete.UI#setLookAndFeel] -cp proyecto;plugin Renderer paquete.Pantalla
 * </pre>
 *
 * Por stdout, una sola respuesta: {@code 'F' int bytes, <png>} con la imagen, o {@code 'E' UTF texto} si no se pudo armar
 * la pantalla. Lo que la app escriba en System.out se desvia a System.err, para no mezclarse con la respuesta.
 *
 * No depende de NetBeans ni de Tessera al compilar (todo por reflexion): vive en el jar del plugin y se agrega al final
 * del classpath del proyecto.
 */
public final class Renderer {

  private static final String PREVIEW_ANNOTATION = "tessera.app.Preview";

  private Renderer() {
  }

  public static void main(String[] args) throws Exception {
    final DataOutputStream out = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(FileDescriptor.out), 1 << 16));
    System.setOut(System.err);
    int code = 0;
    try {
      installTheme(System.getProperty("tessera.preview.theme"));
      final byte[] png = render(Class.forName(args[0]));
      out.writeByte('F');
      out.writeInt(png.length);
      out.write(png);
    } catch (Throwable e) {
      code = 1;
      out.writeByte('E');
      out.writeUTF(describe(unwrap(e)));
    }
    out.flush();
    System.exit(code);          // la ventana oculta que se crea para dibujar mantendria vivo el proceso
  }

  /** Crea la pantalla y la dibuja a su tamano natural. */
  private static byte[] render(Class<?> type) throws Throwable {
    final byte[][] result = new byte[1][];
    final Throwable[] failure = new Throwable[1];
    SwingUtilities.invokeAndWait(() -> {
      try {
        final JComponent screen = create(type);
        final JFrame frame = new JFrame();
        frame.setContentPane(screen);
        frame.pack();
        // validate() y no doLayout(): doLayout() solo reacomoda el contenedor de afuera, y los de adentro conservarian la
        // distribucion que les dio pack() (a otro alto), asi que la imagen saldria con el pie cortado. Se repite hasta que el
        // tamano preferido se estabiliza: el alto de un texto que se parte en lineas (TextArea con wrap, Hint) solo se conoce
        // con el ancho ya aplicado.
        Dimension size = screen.getPreferredSize();
        for (int pass = 0; pass < 3; pass++) {
          screen.setSize(size);
          screen.validate();
          final Dimension next = screen.getPreferredSize();
          if (next.equals(size)) {
            break;
          }
          size = next;
        }
        screen.setSize(size);
        screen.validate();
        final BufferedImage image = new BufferedImage(Math.max(1, size.width), Math.max(1, size.height), BufferedImage.TYPE_INT_RGB);
        final Graphics2D g = image.createGraphics();
        screen.paint(g);
        g.dispose();
        final ByteArrayOutputStream png = new ByteArrayOutputStream(1 << 16);
        ImageIO.write(image, "png", png);
        result[0] = png.toByteArray();
        frame.dispose();
      } catch (Throwable e) {
        failure[0] = e;
      }
    });
    if (failure[0] != null) {
      throw failure[0];
    }
    return result[0];
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

  private static Throwable unwrap(Throwable e) {
    Throwable current = e;
    while ((current instanceof InvocationTargetException || current instanceof ExceptionInInitializerError)
        && current.getCause() != null) {
      current = current.getCause();
    }
    return current;
  }

  private static String describe(Throwable error) {
    final StringWriter text = new StringWriter();
    error.printStackTrace(new PrintWriter(text));
    final String message = text.toString();
    return message.length() > 30000 ? message.substring(0, 30000) + "\n..." : message;
  }

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
