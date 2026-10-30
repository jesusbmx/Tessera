package tessera.kit;

import java.awt.Component;
import java.awt.Insets;
import javax.swing.JPanel;
import net.miginfocom.swing.MigLayout;

/**
 * Base de los contenedores del kit (Row, Col, Stack, Form, Card): un JPanel transparente con MigLayout por
 * dentro, con {@code gap} y {@code padding} configurables y {@code children(...)} para llenarlo.
 * Quien use el kit no necesita saber de MigLayout; si hace falta algo muy puntual, {@code add(c, "restriccion")}
 * de JPanel sigue funcionando con la sintaxis de MigLayout.
 *
 * @param <T> el contenedor concreto, para encadenar
 */
public abstract class Box<T extends Box<T>> extends JPanel implements Styled<T> {

  /** Espacio (px) entre un hijo y el siguiente. */
  private int gap;

  /** Espacio (px) entre el borde del contenedor y sus hijos. */
  private final Insets padding = new Insets(0, 0, 0, 0);

  protected Box(int gap) {
    super(new MigLayout());
    this.gap = gap;
    setOpaque(false);
  }

  // ------------------------------------------------------------------------ //
  //   C O N F I G U R A C I O N
  // ------------------------------------------------------------------------ //

  /** Cambia el espacio (px) entre hijos; cada contenedor lo expone como quiere ({@code Row.gap(n)}, {@code card.gap(n)}). */
  protected final void setGap(int gap) {
    this.gap = gap;
    relayout();
  }

  /** Espacio (px) entre el borde y los hijos, igual por los cuatro lados. */
  public T padding(int all) {
    return padding(all, all, all, all);
  }

  /** Espacio (px): vertical (arriba y abajo) y horizontal (izquierda y derecha). */
  public T padding(int vertical, int horizontal) {
    return padding(vertical, horizontal, vertical, horizontal);
  }

  /** Espacio (px) arriba, izquierda, abajo y derecha (el mismo orden de Insets). */
  public T padding(int top, int left, int bottom, int right) {
    this.padding.top = top;
    this.padding.left = left;
    this.padding.bottom = bottom;
    this.padding.right = right;
    relayout();
    return self();
  }

  /** Copia del espacio (px) entre el borde y los hijos: arriba, izquierda, abajo y derecha. Cambiarla no afecta al contenedor. */
  public Insets padding() {
    return new Insets(padding.top, padding.left, padding.bottom, padding.right);
  }

  /** Espacio (px) arriba y abajo; izquierda y derecha quedan como estaban. */
  public T paddingVertical(int vertical) {
    return padding(vertical, padding.left, vertical, padding.right);
  }

  /** Espacio (px) a la izquierda y a la derecha; arriba y abajo quedan como estaban. */
  public T paddingHorizontal(int horizontal) {
    return padding(padding.top, horizontal, padding.bottom, horizontal);
  }

  /** Agrega los hijos en orden, cada uno con el comportamiento por defecto de este contenedor. */
  public T children(Component... children) {
    for (Component child : children) {
      if (child != null) {
        place(child);
      }
    }
    return self();
  }

  /** Cierra la cadena: devuelve el contenedor ya armado (es opcional, el contenedor ya es un JPanel). */
  public T build() {
    return self();
  }

  // ------------------------------------------------------------------------ //
  //   P A R A   L O S   C O N T E N E D O R E S
  // ------------------------------------------------------------------------ //

  protected final int gap() {
    return gap;
  }

  /** Restricciones de MigLayout para el contenedor: insets, gaps y modo de ocultar. */
  protected final String migInsets() {
    return "insets " + Kit.px(padding.top) + "px " + Kit.px(padding.left) + "px "
        + Kit.px(padding.bottom) + "px " + Kit.px(padding.right) + "px";
  }

  /** Restricciones de columna y fila: por defecto ninguna. */
  protected String columns() {
    return "";
  }

  protected String rows() {
    return "";
  }

  /** Restricciones de MigLayout del contenedor. */
  protected abstract String migLayout();

  /** Agrega un hijo con las restricciones que le tocan en este contenedor. */
  protected abstract void place(Component child);

  /** Agrega un hijo con esas restricciones de MigLayout mas el tamano fijo que haya pedido (width/height). */
  protected final void put(Component child, String constraints) {
    final String size = Kit.sizeHint(child);
    add(child, size.isEmpty() ? constraints : constraints.isEmpty() ? size : constraints + ", " + size);
  }

  /** Vuelve a calcular las restricciones del contenedor (los hijos ya agregados conservan las suyas). */
  protected final void relayout() {
    final MigLayout mig = (MigLayout) getLayout();
    mig.setLayoutConstraints(migLayout());
    mig.setColumnConstraints(columns());
    mig.setRowConstraints(rows());
    revalidate();
  }
}
