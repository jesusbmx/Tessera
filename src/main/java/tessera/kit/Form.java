package tessera.kit;

import java.awt.Component;
import java.awt.Dimension;
import javax.swing.JPanel;

/**
 * Formulario de dos columnas: la etiqueta a la izquierda y su campo a la derecha, que ocupa el ancho que
 * sobra. Las etiquetas de texto se crean con el estilo de {@link Label#field}.
 *
 * <pre>
 * Form.create()
 *     .row("Nombre:", txtName)
 *     .row("Direccion:", Row.gap(8).fill(txtAddress).children(btnMap))
 *     .wide(chkEnabled)                                    // ocupa las dos columnas
 * </pre>
 */
public class Form extends Box<Form> {

  /** Espacio (px) entre la etiqueta y su campo. */
  private int labelGap = 14;

  /** Etiquetas alineadas a la derecha de su columna (ver {@link #labelsRight()}). */
  private boolean labelsRight;

  protected Form(int gap) {
    super(gap);
    relayout();
  }

  /** Formulario con 10 px entre filas. */
  public static Form create() {
    return new Form(10);
  }

  /** Formulario con ese espacio (px) entre filas. */
  public static Form gap(int gap) {
    return new Form(gap);
  }

  /** Espacio (px) entre la etiqueta y su campo (14 por defecto). */
  public Form labelGap(int labelGap) {
    this.labelGap = labelGap;
    relayout();
    return this;
  }

  /** Las etiquetas quedan pegadas a su campo (alineadas a la derecha de su columna) en vez de a la izquierda. */
  public Form labelsRight() {
    this.labelsRight = true;
    relayout();
    return this;
  }

  /** Una fila con una etiqueta de texto. */
  public Form row(String label, Component field) {
    return row(Label.field(label), field);
  }

  /** Una fila: etiqueta (o cualquier componente) a la izquierda y campo a la derecha. */
  public Form row(Component label, Component field) {
    put(label, "aligny center");
    put(field, Col.constraints(field, "wrap"));
    return this;
  }

  /** Una fila con una etiqueta de texto alineada arriba (junto a un campo alto: un area de texto). */
  public Form rowTop(String label, Component field) {
    return rowTop(Label.field(label), field);
  }

  /** Una fila cuya etiqueta (o cualquier componente) queda arriba en vez de centrada, junto a un campo alto. */
  public Form rowTop(Component label, Component field) {
    put(label, "aligny top");
    put(field, Col.constraints(field, "wrap"));
    return this;
  }

  /**
   * La ULTIMA fila del formulario, solo con el campo en la columna de los campos (la de las etiquetas queda vacia):
   * un enlace debajo de un area de texto. No cierra la fila con "wrap", asi no queda el hueco de un gap al final.
   */
  public Form field(Component field) {
    put(field, Col.constraints(field, "skip 1"));
    return this;
  }

  /** Una fila que ocupa las dos columnas (un titulo, una casilla, un texto de ayuda). */
  public Form wide(Component child) {
    put(child, Col.constraints(child, "span 2, wrap"));
    return this;
  }

  /**
   * Como {@link #wide(Component)}, con {@code gapAbove} px mas de espacio sobre la fila (se suman al gap del formulario).
   * A diferencia de {@link #space}, el espacio desaparece junto con la fila si el componente se oculta.
   */
  public Form wide(Component child, int gapAbove) {
    put(child, Col.constraints(child, "span 2, wrap, gaptop " + Kit.px(gapAbove) + "px"));
    return this;
  }

  /** Un hueco de ese alto (px) entre la fila anterior y la siguiente, ademas del espacio normal: separa grupos de filas (se usa con row(...), no con children(...)). */
  public Form space(int px) {
    final JPanel spacer = new JPanel();
    spacer.setOpaque(false);
    spacer.setPreferredSize(new Dimension(0, Kit.px(px)));
    put(spacer, "span 2, wrap");
    return this;
  }

  @Override
  protected String migLayout() {
    return migInsets() + ", gapy " + Kit.px(gap()) + "px, gapx " + Kit.px(labelGap) + "px, hidemode 3, novisualpadding";
  }

  @Override
  protected String columns() {
    return labelsRight ? "[right][grow]" : "[][grow]";
  }

  /** Con children(...) los componentes se alternan: etiqueta, campo, etiqueta, campo... */
  @Override
  protected void place(Component child) {
    if (getComponentCount() % 2 == 0) {
      put(child, "aligny center");
    } else {
      put(child, Col.constraints(child, "wrap"));
    }
  }
}
