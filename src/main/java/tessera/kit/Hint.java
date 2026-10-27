package tessera.kit;

import com.formdev.flatlaf.util.UIScale;
import java.awt.Dimension;
import java.awt.Insets;
import javax.swing.JLabel;
import javax.swing.UIManager;
import javax.swing.plaf.basic.BasicHTML;
import javax.swing.text.View;

/**
 * Texto de ayuda: pequeno, gris, y se parte en lineas al ancho que le toque.
 *
 * Un ancho fijo (width en el html) lo dejaba con el mismo minimo que su preferido y el panel ya no se
 * podia angostar: aqui el texto se parte al ancho real de la etiqueta y de esas lineas sale su alto.
 * Sin CSS a proposito: Swing toma los "px" del html como 1.3 pixeles reales y el texto se pasaba del borde.
 */
public class Hint extends JLabel implements Styled<Hint> {

  /** Ancho (px) en el que se parte el texto; con menos espacio se parte al ancho que haya. */
  private static final int DEFAULT_WIDTH = 325;

  /** Ancho minimo (px): por debajo el texto se haria muy alto. */
  private static final int MIN_WIDTH = 100;

  private int width = DEFAULT_WIDTH;

  protected Hint(String text) {
    super("<html>" + text + "</html>");
    putClientProperty("FlatLaf.styleClass", "small");
    setForeground(UIManager.getColor("Label.disabledForeground"));
    Kit.fill(this, true, false);
    wrapAt(UIScale.scale(width));
  }

  public static Hint of(String text) {
    return new Hint(text);
  }

  /** Como {@link #of}, para un texto que ya viene envuelto en {@code <html>...</html>}: quita esas marcas. */
  public static Hint ofHtml(String text) {
    return new Hint(text.replaceAll("(?i)^\\s*<html>|</html>\\s*$", "").trim());
  }

  /** Ancho (px) al que se parte el texto cuando hay espacio (325 por defecto). */
  public Hint width(int width) {
    this.width = width;
    wrapAt(UIScale.scale(width));
    return this;
  }

  /** Cambia el texto (se parte igual que el original). */
  public Hint html(String text) {
    setText("<html>" + text + "</html>");
    wrapAt(UIScale.scale(width));
    return this;
  }

  /** Parte el texto a ese ancho; devuelve true si cambio (y con el su alto). */
  private boolean wrapAt(int textWidth) {
    final View view = (View) getClientProperty(BasicHTML.propertyKey);
    if (view == null || textWidth <= 0 || textWidth == (int) view.getPreferredSpan(View.X_AXIS)) {
      return false;
    }
    view.setSize(textWidth, 0);
    return true;
  }

  /** El ancho preferido no pasa de {@code width} aunque el texto se este partiendo a uno mayor. */
  @Override
  public Dimension getPreferredSize() {
    final Dimension size = super.getPreferredSize();
    size.width = Math.min(size.width, UIScale.scale(width));
    return size;
  }

  @Override
  public Dimension getMinimumSize() {
    return new Dimension(UIScale.scale(MIN_WIDTH), super.getPreferredSize().height);
  }

  @Override
  public void setBounds(int x, int y, int width, int height) {
    super.setBounds(x, y, width, height);

    final Insets insets = getInsets();
    if (wrapAt(width - insets.left - insets.right)) {
      revalidate(); // el alto cambio: el bloque se vuelve a acomodar
    }
  }
}
