package tessera.kit;

import javax.swing.JSeparator;
import javax.swing.SwingConstants;

/** Linea fina que separa secciones. Dentro de un Col ocupa todo el ancho. */
public class Separator extends JSeparator implements Styled<Separator> {

  protected Separator(int orientation) {
    super(orientation);
  }

  public static Separator horizontal() {
    return new Separator(SwingConstants.HORIZONTAL);
  }

  public static Separator vertical() {
    return new Separator(SwingConstants.VERTICAL);
  }
}
