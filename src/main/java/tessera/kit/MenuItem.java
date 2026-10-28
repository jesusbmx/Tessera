package tessera.kit;

import java.awt.Insets;
import javax.swing.Icon;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import tessera.theme.Icons;

/**
 * Opcion de un {@link Menu}: texto, icono opcional y la accion que corre al elegirla.
 *
 * <pre>
 * MenuItem.of("Exportar", this::export)
 * MenuItem.of("Importar", this::importFile).icon("app/img/pro.svg", 16).iconRight().padding(6, 10)
 * </pre>
 */
public class MenuItem extends JMenuItem implements Styled<MenuItem> {

  protected MenuItem(String text) {
    super(text);
  }

  public static MenuItem of(String text) {
    return new MenuItem(text);
  }

  /** Opcion que corre {@code action} al elegirla. */
  public static MenuItem of(String text, Runnable action) {
    return new MenuItem(text).onClick(action);
  }

  /** Encabezado de un menu: texto en negrita, sin accion (ver {@link Menu#title}). */
  public static MenuItem title(String text) {
    final MenuItem item = new MenuItem(text).bold();
    item.setEnabled(false);
    return item;
  }

  /** Que hacer al elegir la opcion. */
  public MenuItem onClick(Runnable action) {
    addActionListener(e -> action.run());
    return this;
  }

  /** Icono desde un recurso del classpath (SVG o imagen) a ese tamano. */
  public MenuItem icon(String resource, int size) {
    return icon(Icons.of(resource, size));
  }

  public MenuItem icon(Icon icon) {
    setIcon(icon);
    return this;
  }

  /** Atajo de teclado que se muestra junto al texto (ej. {@code KeyStroke.getKeyStroke("control O")}). */
  public MenuItem accelerator(KeyStroke keyStroke) {
    setAccelerator(keyStroke);
    return this;
  }

  /** El icono despues del texto (como una insignia) en lugar de antes. */
  public MenuItem iconRight() {
    setHorizontalTextPosition(SwingConstants.LEFT);
    return this;
  }

  /** Margen interno (px): arriba/abajo y a los lados. */
  public MenuItem padding(int vertical, int horizontal) {
    setMargin(new Insets(Kit.px(vertical), Kit.px(horizontal), Kit.px(vertical), Kit.px(horizontal)));
    return this;
  }
}
