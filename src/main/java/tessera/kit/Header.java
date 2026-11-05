package tessera.kit;

import java.awt.Component;

/**
 * Banner oscuro con un titulo y, opcionalmente, una descripcion: la cabecera de cada paso de los
 * asistentes y de los dialogos.
 *
 * <pre>
 * Header.create("Nombre del servicio").subtitle("Elige como se llamara")
 * </pre>
 */
public class Header extends Box<Header> {

  private final Label title;
  private Label subtitle;

  protected Header(String title) {
    super(6);
    this.title = Label.text(title).bold().fontSize(18).color(Kit.ON_DARK);
    background(Kit.DARK);
    padding(14);
    put(this.title, "growx");
    relayout();
  }

  public static Header create(String title) {
    return new Header(title);
  }

  public static Header create(String title, String subtitle) {
    return new Header(title).subtitle(subtitle);
  }

  /** Descripcion debajo del titulo. */
  public Header subtitle(String text) {
    if (subtitle == null) {
      subtitle = Label.text(text).fontSize(16).color(Kit.ON_DARK);
      put(subtitle, "growx, gapleft " + Kit.px(16) + "px");
    } else {
      subtitle.setText(text);
    }
    return this;
  }

  /** Un componente (un enlace de ayuda, por ejemplo) a la derecha del titulo, en su misma fila. */
  public Header corner(Component component) {
    remove(title);
    add(Row.gap(8).fill(title).children(component), "growx", 0);
    return this;
  }

  /** Etiqueta del titulo (para cambiar el texto despues de armado). */
  public Label title() {
    return title;
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
    put(child, Col.constraints(child));
  }
}
