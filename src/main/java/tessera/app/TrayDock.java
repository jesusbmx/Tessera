package tessera.app;

import java.awt.Desktop;
import java.awt.Font;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.event.ActionListener;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;
import tessera.theme.Theme;

/**
 * Icono de la bandeja del sistema (Windows, Linux) y reapertura desde el Dock (macOS).
 *
 * El menu se pide con un {@code Supplier}: cada vez que cambia algo (un servicio se inicia o se detiene)
 * basta llamar a {@link #refresh()} y se arma de nuevo.
 *
 * <pre>
 * TrayDock dock = TrayDock.create(image, "Mi App")
 *     .onOpen(this::showWindow)
 *     .menu(() -&gt; Arrays.asList(
 *         TrayDock.Item.title("Mi App v1.0"),
 *         TrayDock.Item.separator(),
 *         TrayDock.Item.of("Abrir", this::showWindow),
 *         TrayDock.Item.of("Salir", this::exit)));
 * dock.install();
 * TrayDock.onAppReopened(this::showWindow);   // macOS: clic en el icono del Dock
 * </pre>
 */
public final class TrayDock {

  private static final Logger LOG = Logger.getLogger(TrayDock.class.getName());

  /** Una linea del menu de la bandeja. */
  public static final class Item {
    private final String label;
    private final Runnable action;
    private final boolean bold;

    private Item(String label, Runnable action, boolean bold) {
      this.label = label;
      this.action = action;
      this.bold = bold;
    }

    /** Linea normal. */
    public static Item of(String label, Runnable action) {
      return new Item(label, action, false);
    }

    /** Linea en negrita (el nombre de la app arriba del menu). */
    public static Item title(String label) {
      return new Item(label, null, true);
    }

    /** Titulo en negrita que ademas hace algo al elegirlo. */
    public static Item title(String label, Runnable action) {
      return new Item(label, action, true);
    }

    public static Item separator() {
      return new Item(null, null, false);
    }
  }

  private final Image image;
  private final String tooltip;
  private Runnable onOpen;
  private Supplier<List<Item>> menu = () -> new ArrayList<>();
  private TrayIcon trayIcon;

  private TrayDock(Image image, String tooltip) {
    this.image = image;
    this.tooltip = tooltip;
  }

  /** @param image icono de la bandeja (el sistema lo ajusta de tamano) */
  public static TrayDock create(Image image, String tooltip) {
    return new TrayDock(image, tooltip);
  }

  /** Que hacer al hacer clic (doble clic en Windows) sobre el icono. */
  public TrayDock onOpen(Runnable action) {
    this.onOpen = action;
    return this;
  }

  /** Las lineas del menu; se llama de nuevo en cada {@link #refresh()}. */
  public TrayDock menu(Supplier<List<Item>> menu) {
    this.menu = menu;
    return this;
  }

  /** Pone el icono en la bandeja; no hace nada si el sistema no la soporta. */
  public void install() {
    try {
      if (!SystemTray.isSupported() || trayIcon != null) {
        return;
      }
      trayIcon = new TrayIcon(image, tooltip, createPopupMenu());
      trayIcon.setImageAutoSize(true);
      trayIcon.setToolTip(tooltip);
      if (onOpen != null) {
        trayIcon.addActionListener(e -> onOpen.run());
      }
      SystemTray.getSystemTray().add(trayIcon);

    } catch (Exception e) {
      LOG.log(Level.WARNING, "No se pudo crear el icono de la bandeja", e);
    }
  }

  /** Quita el icono de la bandeja. */
  public void remove() {
    try {
      if (trayIcon != null && SystemTray.isSupported()) {
        SystemTray.getSystemTray().remove(trayIcon);
      }
      trayIcon = null;
    } catch (Exception e) {
      LOG.log(Level.WARNING, "No se pudo quitar el icono de la bandeja", e);
    }
  }

  /** Arma el menu de nuevo (cuando cambia el estado de la app). */
  public void refresh() {
    if (trayIcon != null) {
      trayIcon.setPopupMenu(createPopupMenu());
    }
  }

  private PopupMenu createPopupMenu() {
    final PopupMenu popup = new PopupMenu();
    for (Item item : menu.get()) {
      if (item.label == null) {
        popup.addSeparator();
        continue;
      }
      final MenuItem menuItem = new MenuItem(item.label);
      menuItem.setFont(new Font(Theme.fontFamily(), item.bold ? Font.BOLD : Font.PLAIN, 14));
      if (item.action != null) {
        final ActionListener listener = e -> item.action.run();
        menuItem.addActionListener(listener);
      }
      popup.add(menuItem);
    }
    return popup;
  }

  // ---------------------------------------------------------------------- //
  //   D O C K   ( m a c O S )
  // ---------------------------------------------------------------------- //

  /**
   * En macOS, al minimizar la ventana el unico camino para abrirla es el Dock: el usuario reabre la app ya
   * abierta con un clic en su icono. Es {@code Desktop.addAppEventListener(AppReopenedListener)}, que existe
   * desde Java 9; se usa por reflexion porque la libreria compila para Java 8. {@code onReopen} corre fuera
   * del EDT.
   *
   * @return true si quedo registrado (false en otros sistemas o con Java 8)
   */
  public static boolean onAppReopened(Runnable onReopen) {
    return register("addAppEventListener", "java.awt.desktop.AppReopenedListener", "appReopened", onReopen);
  }

  private static boolean register(String registrar, String listenerType, String eventMethod, Runnable handler) {
    try {
      final Class<?> type = Class.forName(listenerType);
      final Object listener = Proxy.newProxyInstance(type.getClassLoader(), new Class<?>[] {type},
          (proxy, method, args) -> {
            if (method.getName().equals(eventMethod)) {
              try {
                handler.run();
              } catch (RuntimeException e) {
                // Este hilo es del sistema: no se le devuelve la excepcion.
                LOG.log(Level.WARNING, "Error al atender " + eventMethod, e);
              }
            }
            return null;
          });

      final Method method = findMethod(Desktop.getDesktop().getClass(), registrar);
      method.invoke(Desktop.getDesktop(), listener);
      return true;

    } catch (Exception | LinkageError e) {
      LOG.log(Level.FINE, registrar + " no esta disponible: " + e);
      return false;
    }
  }

  private static Method findMethod(Class<?> type, String name) throws NoSuchMethodException {
    for (Method method : type.getMethods()) {
      if (method.getName().equals(name) && method.getParameterTypes().length == 1) {
        return method;
      }
    }
    throw new NoSuchMethodException(Arrays.asList(type.getName(), name).toString());
  }
}
