package tessera.app;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GraphicsEnvironment;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.Callable;
import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import tessera.kit.Button;
import tessera.kit.Kit;
import tessera.theme.Theme;

/**
 * Muestra una pantalla en una ventana propia para ver como va quedando, sin arrancar la app. Una clase que es un
 * componente con constructor sin parametros se muestra tal cual; {@link Preview} solo hace falta para dar un nombre
 * o un tamano, o para armar la pantalla con datos de muestra en un metodo estatico.
 *
 * <pre>
 * public static void main(String[] args) {              // en la propia pantalla: Run File en el IDE
 *   Previewer.show(UI::setLookAndFeel, ToolsPanel.class);    // el tema de la app, y la pantalla
 * }
 * </pre>
 *
 * La ventana trae un boton <b>Recargar</b> (F5) que vuelve a crear la pantalla. No recarga el codigo: despues de
 * cambiarlo, vuelve a ejecutar el archivo (con "compilar al guardar" del IDE es casi inmediato).
 *
 * Tambien se puede ejecutar por linea de comandos: {@code java -cp ... tessera.app.Previewer paquete.MiPantalla}.
 */
public final class Previewer {

  /** Una vista previa: su nombre, el tamano que pide y como se crea el componente. */
  private static final class Entry {
    final String name;
    final int width;
    final int height;
    final Callable<JComponent> factory;

    Entry(String name, Preview preview, Callable<JComponent> factory) {
      this.name = preview.value().isEmpty() ? name : preview.value();
      this.width = preview.width();
      this.height = preview.height();
      this.factory = factory;
    }
  }

  /** Una pantalla sin @Preview: nombre y tamano por defecto. */
  @Preview
  private static final class Plain {
  }

  private static final Preview PLAIN = Plain.class.getAnnotation(Preview.class);

  private Previewer() {
  }

  /** Por linea de comandos: los nombres de las clases con {@link Preview}. Usa el tema por defecto. */
  public static void main(String[] args) throws Exception {
    final Class<?>[] classes = new Class<?>[args.length];
    for (int i = 0; i < args.length; i++) {
      classes[i] = Class.forName(args[i]);
    }
    show(classes);
  }

  /** Abre la ventana con las vistas previas de esas clases, con el tema por defecto de Tessera. */
  public static void show(Class<?>... classes) {
    show(() -> Theme.builder().install(), classes);
  }

  /**
   * Abre la ventana con las vistas previas de esas clases.
   *
   * @param theme lo que la app hace para instalar su tema antes de crear ventanas (por ejemplo {@code UI::setLookAndFeel});
   *              {@code null} deja el aspecto de Swing por defecto
   */
  public static void show(Runnable theme, Class<?>... classes) {
    if (theme != null) {
      theme.run();
    }
    final List<Entry> entries = collect(classes);
    SwingUtilities.invokeLater(() -> open(entries));
  }

  /**
   * Guarda la primera vista previa como imagen PNG, sin abrir la ventana (para revisar una pantalla o para la documentacion).
   * Necesita un escritorio: crea la ventana, aunque no se ve.
   */
  public static void save(Runnable theme, File png, Class<?> screen) throws Exception {
    if (theme != null) {
      theme.run();
    }
    final Entry entry = collect(screen).get(0);
    SwingUtilities.invokeAndWait(() -> {
      try {
        final JComponent component = entry.factory.call();
        final JFrame frame = new JFrame();
        frame.setContentPane(component);
        frame.pack();
        final Dimension size = component.getPreferredSize();
        component.setSize(size);
        component.doLayout();
        final BufferedImage image = new BufferedImage(size.width, size.height, BufferedImage.TYPE_INT_RGB);
        component.paint(image.createGraphics());
        png.getAbsoluteFile().getParentFile().mkdirs();
        ImageIO.write(image, "png", png);
      } catch (Exception e) {
        throw new IllegalStateException(e);
      }
    });
  }

  // ---------------------------------------------------------------------- //
  //   B U S C A R   L O   M A R C A D O
  // ---------------------------------------------------------------------- //

  private static List<Entry> collect(Class<?>... classes) {
    final List<Entry> entries = new ArrayList<>();
    for (final Class<?> type : classes) {
      final Preview onType = type.getAnnotation(Preview.class);
      boolean found = false;
      if (onType != null) {
        entries.add(new Entry(type.getSimpleName(), onType, () -> create(type)));
        found = true;
      }

      final Method[] methods = type.getDeclaredMethods();
      Arrays.sort(methods, Comparator.comparing(Method::getName));
      for (final Method method : methods) {
        final Preview onMethod = method.getAnnotation(Preview.class);
        if (onMethod == null) {
          continue;
        }
        if (!Modifier.isStatic(method.getModifiers()) || method.getParameterTypes().length != 0
            || !JComponent.class.isAssignableFrom(method.getReturnType())) {
          throw new IllegalArgumentException(type.getName() + "." + method.getName()
              + ": un metodo con @Preview debe ser estatico, sin parametros y devolver un componente");
        }
        entries.add(new Entry(type.getSimpleName() + "." + method.getName(), onMethod, () -> call(method)));
        found = true;
      }

      // sin @Preview: una pantalla (un componente con constructor sin parametros) se muestra tal cual
      if (!found) {
        if (!JComponent.class.isAssignableFrom(type)) {
          throw new IllegalArgumentException(type.getName()
              + " no es un componente ni tiene @Preview en un metodo estatico que devuelva uno");
        }
        entries.add(new Entry(type.getSimpleName(), PLAIN, () -> create(type)));
      }
    }
    return entries;
  }

  private static JComponent create(Class<?> type) throws Exception {
    final Constructor<?> constructor = type.getDeclaredConstructor();
    constructor.setAccessible(true);
    return (JComponent) unwrap(constructor::newInstance);
  }

  private static JComponent call(Method method) throws Exception {
    method.setAccessible(true);
    return (JComponent) unwrap(() -> method.invoke(null));
  }

  /** Muestra el error real de la pantalla, no el InvocationTargetException que lo envuelve. */
  private static Object unwrap(Callable<Object> action) throws Exception {
    try {
      return action.call();
    } catch (InvocationTargetException e) {
      if (e.getCause() instanceof Exception) {
        throw (Exception) e.getCause();
      }
      throw e;
    }
  }

  // ---------------------------------------------------------------------- //
  //   L A   V E N T A N A
  // ---------------------------------------------------------------------- //

  private static void open(List<Entry> entries) {
    final JPanel[] holders = new JPanel[entries.size()];
    final JTabbedPane tabs = new JTabbedPane();
    for (int i = 0; i < entries.size(); i++) {
      holders[i] = new JPanel(new BorderLayout());
      build(holders[i], entries.get(i));
      tabs.addTab(entries.get(i).name, holders[i]);
    }

    final JFrame frame = new JFrame("Preview");
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    final Runnable reload = () -> {
      final int selected = tabs.getSelectedIndex();
      build(holders[selected], entries.get(selected));
    };
    final Button btnReload = Button.of("Recargar (F5)").onClick(reload);
    final JToolBar bar = new JToolBar();
    bar.setFloatable(false);
    bar.add(btnReload);

    frame.getRootPane().registerKeyboardAction(e -> reload.run(),
        KeyStroke.getKeyStroke("F5"), JComponent.WHEN_IN_FOCUSED_WINDOW);

    frame.setLayout(new BorderLayout());
    frame.add(bar, BorderLayout.NORTH);
    if (entries.size() == 1) {
      frame.add(holders[0], BorderLayout.CENTER);
    } else {
      frame.add(tabs, BorderLayout.CENTER);
    }

    frame.pack();
    frame.setSize(sizeFor(frame, entries.get(0)));
    frame.setLocationRelativeTo(null);
    frame.setVisible(true);
  }

  /** (Re)crea la pantalla dentro de su contenedor; si falla, muestra el error en lugar de la pantalla. */
  private static void build(JPanel holder, Entry entry) {
    holder.removeAll();
    JComponent content;
    try {
      content = entry.factory.call();
    } catch (Throwable e) {
      final StringWriter text = new StringWriter();
      e.printStackTrace(new PrintWriter(text));
      final JTextArea error = new JTextArea(text.toString());
      error.setEditable(false);
      error.setForeground(Kit.PRIMARY.darker());
      content = error;
    }
    holder.add(new JScrollPane(content), BorderLayout.CENTER);
    holder.revalidate();
    holder.repaint();
  }

  /** El tamano que pide la pantalla (o el de su @Preview), sin pasar del 90% de la pantalla. */
  private static Dimension sizeFor(JFrame frame, Entry entry) {
    final Dimension screen = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds().getSize();
    final Dimension wanted = frame.getSize();
    final int width = entry.width > 0 ? Kit.px(entry.width) : wanted.width + 30;
    final int height = entry.height > 0 ? Kit.px(entry.height) : wanted.height + 30;
    return new Dimension(Math.min(width, screen.width * 9 / 10), Math.min(height, screen.height * 9 / 10));
  }
}
