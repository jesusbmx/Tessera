package tessera.kit;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Rectangle;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.Scrollable;
import javax.swing.SwingConstants;

/**
 * Contenedor con barras de desplazamiento. Dentro de un Row o un Col ocupa el espacio que sobra (ancho y alto).
 *
 * <pre>
 * Scroll.of(table)
 * Scroll.of(content).noBorder().vertical()       // sin borde y sin barra horizontal
 * </pre>
 */
public class Scroll extends JScrollPane implements Styled<Scroll> {

  /** Si es solo vertical (ver {@link #vertical()}). */
  private boolean vertical;

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

  /**
   * Solo desplazamiento vertical: el contenido toma el ancho de la ventana (no el que piden sus hijos),
   * asi los textos largos se parten en varias lineas y las tarjetas no se salen por la derecha.
   * Si el contenido ya es {@link Scrollable} (tabla, area de texto) se deja como esta.
   */
  public Scroll vertical() {
    vertical = true;
    setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
    setViewportView(getViewport().getView());
    return this;
  }

  /** En modo vertical, envuelve el contenido para que siga el ancho de la ventana. */
  @Override
  public void setViewportView(Component view) {
    if (vertical && view != null && !(view instanceof Scrollable)) {
      view = new FitWidth(view);
    }
    super.setViewportView(view);
  }

  /** Cuanto avanza la rueda del mouse (px por paso). */
  public Scroll step(int px) {
    getVerticalScrollBar().setUnitIncrement(Kit.px(px));
    getHorizontalScrollBar().setUnitIncrement(Kit.px(px));
    return this;
  }

  /** Contenido de un scroll vertical: ocupa el ancho de la ventana y crece a lo alto lo que necesite. */
  private static final class FitWidth extends JPanel implements Scrollable {

    FitWidth(Component content) {
      super(new BorderLayout());
      setOpaque(false);
      add(content, BorderLayout.CENTER);
    }

    @Override
    public Dimension getPreferredScrollableViewportSize() {
      return getPreferredSize();
    }

    @Override
    public int getScrollableUnitIncrement(Rectangle visible, int orientation, int direction) {
      return Kit.px(16);
    }

    @Override
    public int getScrollableBlockIncrement(Rectangle visible, int orientation, int direction) {
      return orientation == SwingConstants.VERTICAL ? visible.height : visible.width;
    }

    @Override
    public boolean getScrollableTracksViewportWidth() {
      return true;
    }

    @Override
    public boolean getScrollableTracksViewportHeight() {
      return false;
    }
  }
}
