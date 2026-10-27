package tessera.kit;

import java.awt.Component;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;

/**
 * Contenedor con barras de desplazamiento. Dentro de un Row o un Col ocupa el espacio que sobra (ancho y alto).
 *
 * <pre>
 * Scroll.of(table)
 * Scroll.of(content).noBorder().vertical()       // sin borde y sin barra horizontal
 * </pre>
 */
public class Scroll extends JScrollPane implements Styled<Scroll> {

  protected Scroll(Component view) {
    super(view);
    Kit.fill(this, true, true);
  }

  public static Scroll of(Component view) {
    return new Scroll(view);
  }

  /** Sin borde alrededor. */
  public Scroll noBorder() {
    setBorder(null);
    return this;
  }

  /** Solo desplazamiento vertical: el contenido toma el ancho de la ventana. */
  public Scroll vertical() {
    setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
    return this;
  }

  /** Cuanto avanza la rueda del mouse (px por paso). */
  public Scroll step(int px) {
    getVerticalScrollBar().setUnitIncrement(Kit.px(px));
    getHorizontalScrollBar().setUnitIncrement(Kit.px(px));
    return this;
  }
}
