package tessera.app;

import java.awt.AWTEvent;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GraphicsConfiguration;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.HierarchyEvent;
import java.util.prefs.Preferences;
import javax.swing.Timer;

/**
 * Recuerda el tamano de las ventanas de la app: cada JFrame y JDialog se abre como el usuario la dejo la
 * ultima vez (y maximizada, si la dejo asi).
 *
 * Se activa una sola vez con {@link #install}; no hay que tocar cada ventana. Justo antes de mostrarse se le
 * pone el tamano guardado (centrada donde estaba) y cuando el usuario cambia su tamano se guarda en el
 * {@link Store}. Solo aplica a las ventanas de la app que se pueden redimensionar: los dialogos de
 * JOptionPane, los avisos (Toast, Notification) y las ventanas ajenas a la app quedan fuera.
 *
 * <pre>
 * WindowPrefs.install("com.miapp.");                 // guarda en las preferencias del usuario
 * WindowPrefs.install("com.miapp.", miPropioStore);  // o donde quiera la app
 * </pre>
 */
public final class WindowPrefs {

  /** Tamano y estado guardados de una ventana. */
  public static final class Saved {
    public final int width;
    public final int height;
    public final boolean maximized;

    public Saved(int width, int height, boolean maximized) {
      this.width = width;
      this.height = height;
      this.maximized = maximized;
    }
  }

  /** Donde se guardan los tamanos, por clave (el nombre de la clase de la ventana). */
  public interface Store {
    /** Lo guardado con esa clave, o null si no hay nada. */
    Saved load(String key);

    void save(String key, Saved saved);
  }

  /** Pausa tras el ultimo cambio de tamano antes de guardar: arrastrar el borde genera muchos eventos. */
  private static final int SAVE_DELAY_MS = 500;

  private static volatile Store store;
  private static volatile String[] packages = new String[0];

  private WindowPrefs() {
  }

  /** Activa el recuerdo de tamano con las preferencias del usuario ({@link Preferences}) como almacen. */
  public static void install(String... appPackages) {
    install(preferencesStore(), appPackages);
  }

  /**
   * Activa el recuerdo de tamano en las ventanas cuya clase empieza con alguno de los paquetes dados
   * ({@code "com.miapp."}). Se llama una vez al iniciar.
   */
  public static void install(Store store, String... appPackages) {
    WindowPrefs.store = store;
    WindowPrefs.packages = appPackages;

    Toolkit.getDefaultToolkit().addAWTEventListener(event -> {
      final HierarchyEvent e = (HierarchyEvent) event;

      // La ventana acaba de crear su peer (esta por mostrarse): ya se le puede cambiar el tamano sin que se note.
      if ((e.getChangeFlags() & HierarchyEvent.DISPLAYABILITY_CHANGED) != 0
          && e.getComponent() == e.getChanged() && e.getComponent() instanceof Window) {
        final Window window = (Window) e.getComponent();
        if (window.isDisplayable() && isRemembered(window)) {
          remember(window, window.getClass().getName());
        }
      }
    }, AWTEvent.HIERARCHY_EVENT_MASK);
  }

  /**
   * Le pone a la ventana el tamano guardado con esa clave y empieza a guardar sus cambios. Las ventanas de la
   * app lo hacen solas; esto es para las que no se detectan, como un JDialog creado sin subclase: se llama
   * antes de mostrarlas.
   */
  public static void remember(Window window, String key) {
    Remember remember = null;
    for (ComponentListener listener : window.getComponentListeners()) {
      if (listener instanceof Remember) {
        remember = (Remember) listener;
      }
    }
    if (remember == null) {
      remember = new Remember(window, key);
      window.addComponentListener(remember);
    }
    remember.restore();
  }

  /** Almacen por defecto: las preferencias del usuario del sistema (registro en Windows, plist en macOS). */
  private static Store preferencesStore() {
    final Preferences prefs = Preferences.userRoot().node("tessera/windows");
    return new Store() {
      @Override
      public Saved load(String key) {
        final String value = prefs.get(key(key), null);
        if (value == null) {
          return null;
        }
        final String[] parts = value.split(",");
        try {
          return new Saved(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]), Boolean.parseBoolean(parts[2]));
        } catch (RuntimeException e) {
          return null;
        }
      }

      @Override
      public void save(String key, Saved saved) {
        prefs.put(key(key), saved.width + "," + saved.height + "," + saved.maximized);
      }

      /** Las claves de Preferences miden 80 como maximo: se usa el final del nombre de la clase. */
      private String key(String key) {
        return key.length() <= 80 ? key : key.substring(key.length() - 80);
      }
    };
  }

  /** Ventanas de la app que el usuario puede cambiar de tamano. */
  private static boolean isRemembered(Window window) {
    boolean inApp = false;
    for (String prefix : packages) {
      inApp |= window.getClass().getName().startsWith(prefix);
    }
    if (!inApp) {
      return false;
    }
    if (window instanceof Frame) {
      final Frame frame = (Frame) window;
      return frame.isResizable() && !frame.isUndecorated();
    }
    if (window instanceof Dialog) {
      final Dialog dialog = (Dialog) window;
      return dialog.isResizable() && !dialog.isUndecorated();
    }
    return false;
  }

  /** Zona de la pantalla donde esta la ventana, sin las barras del sistema (barra de tareas, dock). */
  private static Rectangle usableScreen(Window window) {
    final GraphicsConfiguration gc = window.getGraphicsConfiguration();
    final Rectangle bounds = gc.getBounds();
    final Insets insets = Toolkit.getDefaultToolkit().getScreenInsets(gc);
    return new Rectangle(bounds.x + insets.left, bounds.y + insets.top,
        bounds.width - insets.left - insets.right, bounds.height - insets.top - insets.bottom);
  }

  private static int clamp(int value, int min, int max) {
    return Math.max(min, Math.min(value, max));
  }

  /** Pone y guarda el tamano de una ventana; lo guarda poco despues de que el usuario lo cambie. */
  private static final class Remember extends ComponentAdapter {
    private final Window window;
    private final String key;
    private final Timer timer = new Timer(SAVE_DELAY_MS, e -> save());

    /** Tamano al mostrarse o tras el ultimo cambio: al mostrarse llegan avisos de resize sin cambio real. */
    private Dimension lastSize;

    Remember(Window window, String key) {
      this.window = window;
      this.key = key;
      timer.setRepeats(false);
    }

    @Override
    public void componentResized(ComponentEvent e) {
      if (window.isShowing() && !window.getSize().equals(lastSize)) {
        lastSize = window.getSize();
        timer.restart();
      }
    }

    void restore() {
      final Saved saved = store.load(key);
      if (saved != null) {
        apply(saved);
      }
      lastSize = window.getSize();
    }

    private void apply(Saved saved) {
      if (saved.width > 0 && saved.height > 0) {
        // Dentro de la pantalla y sin bajar del minimo de la ventana (si tiene)
        final Rectangle screen = usableScreen(window);
        final Dimension min = window.isMinimumSizeSet() ? window.getMinimumSize() : new Dimension();
        final int w = clamp(saved.width, min.width, screen.width);
        final int h = clamp(saved.height, min.height, screen.height);

        // Se queda centrada donde estaba: las ventanas se centran sobre su dueno antes de mostrarse
        final Rectangle old = window.getBounds();
        window.setBounds(
            clamp(old.x + (old.width - w) / 2, screen.x, screen.x + screen.width - w),
            clamp(old.y + (old.height - h) / 2, screen.y, screen.y + screen.height - h),
            w, h);
      }

      if (saved.maximized && window instanceof Frame) {
        ((Frame) window).setExtendedState(Frame.MAXIMIZED_BOTH);
      }
    }

    private void save() {
      final int state = window instanceof Frame ? ((Frame) window).getExtendedState() : Frame.NORMAL;
      if ((state & Frame.ICONIFIED) != 0) {
        return;
      }

      // Maximizada no se toca el tamano: el que cuenta es el de antes de maximizar, que ya esta guardado.
      final boolean maximized = (state & Frame.MAXIMIZED_BOTH) == Frame.MAXIMIZED_BOTH;
      final Saved old = store.load(key);
      final int width = maximized && old != null ? old.width : window.getWidth();
      final int height = maximized && old != null ? old.height : window.getHeight();

      store.save(key, new Saved(width, height, maximized));
    }
  }
}
