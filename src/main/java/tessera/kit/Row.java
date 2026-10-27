package tessera.kit;

import java.awt.Component;
import java.awt.Dimension;
import javax.swing.JPanel;

/**
 * Hijos en una fila, de izquierda a derecha, cada uno con su ancho natural. Para que uno ocupe lo que sobra
 * (un campo junto a su boton) se agrega con {@link #fill}; {@link #glue()} empuja lo que sigue a la derecha.
 *
 * <pre>
 * Row.gap(10).fill(txtPath).children(btnBrowse)              // campo + boton
 * Row.gap(8).glue().children(btnCancel, btnOk)               // botones a la derecha
 * Row.gap(14).children(rbtnPrinter, rbtnIp, rbtnUsb)         // opciones una al lado de otra
 * </pre>
 */
public class Row extends Box<Row> {

  /** Alineacion vertical de los hijos cuando no miden lo mismo. */
  private String align = "center";

  protected Row(int gap) {
    super(gap);
    relayout();
  }

  /** Fila con ese espacio (px) entre hijos. */
  public static Row gap(int gap) {
    return new Row(gap);
  }

  /** Fila con 8 px entre hijos. */
  public static Row create() {
    return new Row(8);
  }

  /** Agrega hijos que ocupan el ancho que sobra (se reparte entre ellos). */
  public Row fill(Component... children) {
    for (Component child : children) {
      put(child, "growx, pushx");
    }
    return this;
  }

  /** Agrega hijos que ocupan el ancho y el alto que sobran (una tabla, un area de texto). */
  public Row fillBoth(Component... children) {
    for (Component child : children) {
      put(child, "grow, push");
    }
    return this;
  }

  /**
   * Agrega hijos (dos columnas lado a lado, p. ej.) que ocupan el ancho y el alto que sobran y miden SIEMPRE lo mismo
   * de ancho entre si, aunque sus anchos naturales sean distintos.
   */
  public Row fillEqual(Component... children) {
    for (Component child : children) {
      put(child, "grow, push, sizegroupx equal");
    }
    return this;
  }

  /** Deja un hueco elastico: lo que se agregue despues queda pegado a la derecha. */
  public Row glue() {
    final JPanel spacer = new JPanel();
    spacer.setOpaque(false);
    spacer.setPreferredSize(new Dimension(0, 0));
    spacer.setMinimumSize(new Dimension(0, 0));
    put(spacer, "growx, pushx");
    return this;
  }

  /** Los hijos se alinean arriba (por defecto, al centro). */
  public Row top() {
    align = "top";
    relayout();
    return this;
  }

  /** Los hijos se alinean al centro vertical (por defecto). */
  public Row center() {
    align = "center";
    relayout();
    return this;
  }

  @Override
  protected String migLayout() {
    return migInsets() + ", gap " + Kit.px(gap()) + "px 0, hidemode 3, novisualpadding";
  }

  @Override
  protected String rows() {
    return "[" + align + "]";
  }

  @Override
  protected void place(Component child) {
    final boolean x = Kit.fillsX(child);
    final boolean y = Kit.fillsY(child);
    put(child, (x && y) ? "grow, push" : x ? "growx, pushx" : y ? "growy, pushy" : "");
  }
}
