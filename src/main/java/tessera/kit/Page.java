package tessera.kit;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import javax.swing.JPanel;

/**
 * Estructura de una ventana o un panel: arriba, centro y abajo (y a los lados si hace falta).
 *
 * <pre>
 * setContentPane(Page.create()
 *     .top(Header.create("Titulo"))
 *     .center(content)
 *     .bottom(Footer.of(btnCancel, btnOk)));
 * </pre>
 */
public class Page extends JPanel implements Styled<Page> {

  /** Ancho minimo (px) que pide la pagina; 0 = el de su contenido (ver {@link #minWidth}). */
  private int minWidth;

  protected Page() {
    super(new BorderLayout());
  }

  public static Page create() {
    return new Page();
  }

  public Page top(Component c) {
    add(c, BorderLayout.NORTH);
    return this;
  }

  public Page bottom(Component c) {
    add(c, BorderLayout.SOUTH);
    return this;
  }

  public Page left(Component c) {
    add(c, BorderLayout.WEST);
    return this;
  }

  public Page right(Component c) {
    add(c, BorderLayout.EAST);
    return this;
  }

  public Page center(Component c) {
    add(c, BorderLayout.CENTER);
    return this;
  }

  /** Ancho minimo (px) que pide la pagina: pack() no abre la ventana mas angosta aunque su contenido lo sea. */
  public Page minWidth(int px) {
    this.minWidth = px;
    revalidate();
    return this;
  }

  @Override
  public Dimension getPreferredSize() {
    final Dimension size = super.getPreferredSize();
    if (minWidth > 0 && size != null) {
      size.width = Math.max(size.width, Kit.px(minWidth));
    }
    return size;
  }

  public Page build() {
    return this;
  }
}
