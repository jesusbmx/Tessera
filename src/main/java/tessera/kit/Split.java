package tessera.kit;

import java.awt.Component;
import javax.swing.JSplitPane;

/**
 * Dos zonas separadas por una barra que se arrastra. Dentro de un Row o un Col ocupa el espacio que sobra.
 *
 * <pre>
 * Split.horizontal(list, details).divider(260)        // lista a la izquierda, detalle a la derecha
 * Split.vertical(editor, console).weight(0.7)         // el editor se queda con el 70% al crecer
 * </pre>
 */
public class Split extends JSplitPane implements Styled<Split> {

  protected Split(int orientation, Component first, Component second) {
    super(orientation, first, second);
    setBorder(null);
    Kit.fill(this, true, true);
  }

  /** Una zona a la izquierda y otra a la derecha. */
  public static Split horizontal(Component left, Component right) {
    return new Split(HORIZONTAL_SPLIT, left, right);
  }

  /** Una zona arriba y otra abajo. */
  public static Split vertical(Component top, Component bottom) {
    return new Split(VERTICAL_SPLIT, top, bottom);
  }

  /** Posicion inicial de la barra (px desde el borde izquierdo o superior). */
  public Split divider(int px) {
    setDividerLocation(Kit.px(px));
    return this;
  }

  /** Cuanto del espacio extra se queda la primera zona al cambiar el tamano (0 a 1). */
  public Split weight(double weight) {
    setResizeWeight(weight);
    return this;
  }
}
