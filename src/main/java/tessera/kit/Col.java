package tessera.kit;

import java.awt.Component;
import java.awt.Dimension;
import javax.swing.AbstractButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * Hijos en una columna, de arriba hacia abajo. Por defecto cada hijo ocupa todo el ancho, menos los botones
 * y las etiquetas, que se quedan a la izquierda con su ancho natural. {@link #start} y {@link #fill} cambian
 * eso hijo por hijo; {@link #fillBoth} deja que uno ocupe tambien el alto que sobra (una tabla, un scroll).
 *
 * <pre>
 * Col.gap(6).children(lblTitle, txtName, cbType)             // titulo, campo y combo
 * Col.gap(8).children(Row.gap(10).fill(txtIp).children(btnScan)).fillBoth(scrollTable)
 * </pre>
 */
public class Col extends Box<Col> {

  protected Col(int gap) {
    super(gap);
    relayout();
  }

  /** Columna con ese espacio (px) entre hijos. */
  public static Col gap(int gap) {
    return new Col(gap);
  }

  /** Columna con 8 px entre hijos. */
  public static Col create() {
    return new Col(8);
  }

  /** Agrega hijos con su ancho natural, a la izquierda. */
  public Col start(Component... children) {
    for (Component child : children) {
      put(child, "");
    }
    return this;
  }

  /** Agrega hijos que ocupan todo el ancho. */
  public Col fill(Component... children) {
    for (Component child : children) {
      put(child, "growx");
    }
    return this;
  }

  /** Agrega hijos que ocupan todo el ancho y el alto que sobra (se reparte entre ellos). */
  public Col fillBoth(Component... children) {
    for (Component child : children) {
      put(child, "grow, push");
    }
    return this;
  }

  /** Deja un hueco elastico: lo que se agregue despues queda pegado abajo. */
  public Col glue() {
    final JPanel spacer = new JPanel();
    spacer.setOpaque(false);
    spacer.setPreferredSize(new Dimension(0, 0));
    spacer.setMinimumSize(new Dimension(0, 0));
    put(spacer, "growy, pushy");
    return this;
  }

  @Override
  protected String migLayout() {
    return migInsets() + ", wrap 1, gapy " + Kit.px(gap()) + "px, hidemode 3, novisualpadding";
  }

  @Override
  protected String columns() {
    return "[grow]";
  }

  @Override
  protected void place(Component child) {
    put(child, constraints(child));
  }

  /**
   * Restricciones de un hijo que se apila en vertical (Col, Card, Form): ancho completo salvo botones y
   * etiquetas, y el alto que sobra solo si el hijo lo pide.
   */
  static String constraints(Component child) {
    final boolean natural = child instanceof AbstractButton || child instanceof JLabel;
    final boolean x = Kit.fillsX(child) || !natural;
    final boolean y = Kit.fillsY(child);
    return (x && y) ? "grow, push" : x ? "growx" : y ? "growy, pushy" : "";
  }

  /** Lo mismo que {@link #constraints(Component)} mas otras restricciones de MigLayout. */
  static String constraints(Component child, String extra) {
    final String base = constraints(child);
    return base.isEmpty() ? extra : base + ", " + extra;
  }
}
