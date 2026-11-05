package tessera.kit;

import java.awt.Component;
import javax.swing.JComponent;
import tessera.theme.Metrics;

/**
 * Lo que un componente le pide a su contenedor (Row, Col, Form, Card, Stack...): ocupar el espacio que sobra y un
 * ancho o alto fijo. Se guarda como propiedades del componente y los contenedores las leen al armar el layout.
 *
 * <pre>
 * Layout.fill(txtNombre, true, false)       // ocupa el ancho que sobra
 * </pre>
 *
 * En Row y Col se prefiere {@code .fill(c)} y {@code .fillBoth(c)}; el ancho y alto fijos se piden con
 * {@code .width(px)} y {@code .height(px)} ({@link Styled}).
 */
public final class Layout {

  /** Eje(s) en que un componente ocupa el espacio que sobra dentro de su contenedor (ver {@link #fill}). */
  private static final String FILL_KEY = "kit.fill";

  /** Ancho / alto fijos (px) que pide un componente a su contenedor (ver {@link Styled#width}). */
  static final String WIDTH_KEY = "kit.width";
  static final String HEIGHT_KEY = "kit.height";

  private Layout() {
  }

  /**
   * Marca que el componente ocupa el espacio que sobra del contenedor (Row, Col, Form, Card...) en el eje
   * indicado. Es lo que hace Stack.fill(...); en Row y Col se prefiere {@code .fill(c)} y {@code .fillBoth(c)}.
   */
  public static <C extends JComponent> C fill(C component, boolean horizontal, boolean vertical) {
    component.putClientProperty(FILL_KEY, (horizontal ? "x" : "") + (vertical ? "y" : ""));
    return component;
  }

  /** Tamano fijo (px) que pidio el componente con width()/height(): "w 120!, h 50!" para MigLayout, o vacio. */
  static String sizeHint(Component component) {
    if (!(component instanceof JComponent)) {
      return "";
    }
    final JComponent c = (JComponent) component;
    final Object w = c.getClientProperty(WIDTH_KEY);
    final Object h = c.getClientProperty(HEIGHT_KEY);
    return (w instanceof Integer ? "w " + Metrics.px((Integer) w) + "px!" : "")
        + (w instanceof Integer && h instanceof Integer ? ", " : "")
        + (h instanceof Integer ? "h " + Metrics.px((Integer) h) + "px!" : "");
  }

  static boolean fillsX(Component component) {
    return hint(component).contains("x");
  }

  static boolean fillsY(Component component) {
    return hint(component).contains("y");
  }

  private static String hint(Component component) {
    if (component instanceof JComponent) {
      final Object value = ((JComponent) component).getClientProperty(FILL_KEY);
      if (value instanceof String) {
        return (String) value;
      }
    }
    return "";
  }
}
