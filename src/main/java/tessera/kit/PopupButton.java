package tessera.kit;

import java.awt.Color;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.SwingConstants;

/**
 * Base de los botones que abren un menu al hacer clic: {@link MenuButton}, {@link ChoiceButton} y
 * {@link MultiChoiceButton}. Pone la flecha, abre el {@link Menu} debajo del boton y deja el color del texto
 * y de la flecha en uno solo. No se usa directamente.
 *
 * @param <B> el propio boton, para que la cadena siga devolviendo el tipo concreto
 */
public abstract class PopupButton<B extends PopupButton<B>> extends JButton implements Styled<B> {

  private static final String ARROW = "tessera/img/arrow_down.svg";
  private static final int ARROW_SIZE = 16;

  private final Menu menu = Menu.create();
  private Icon customIcon;
  private boolean arrow = true;
  private boolean arrowLeft;
  private Color arrowColor;

  protected PopupButton(String text) {
    super(text);
    updateIcon();
    addActionListener(e -> open());
  }

  /** El menu que abre este boton (para casos avanzados: abrirlo por codigo, agregarle opciones...). */
  public Menu menu() {
    return menu;
  }

  /** Abre el menu debajo del boton; si no tiene opciones no hace nada. */
  protected void open() {
    if (menu.getComponentCount() > 0) {
      menu.showBelow(this);
    }
  }

  /** La flecha a la izquierda del texto en lugar de a la derecha. */
  public B arrowLeft() {
    arrowLeft = true;
    updateIcon();
    return self();
  }

  /** Sin flecha (por ejemplo un boton que solo lleva un icono). */
  public B noArrow() {
    arrow = false;
    updateIcon();
    return self();
  }

  /** Un icono propio, antes del texto, en lugar de la flecha. */
  public B icon(String resource, int size) {
    return icon(Kit.icon(resource, size));
  }

  public B icon(Icon icon) {
    customIcon = icon;
    updateIcon();
    return self();
  }

  /** Espacio (px) entre el icono y el texto. */
  public B iconGap(int gap) {
    setIconTextGap(Kit.px(gap));
    return self();
  }

  /** Color del texto y de la flecha. */
  @Override
  public B color(Color color) {
    setForeground(color);
    arrowColor = color;
    updateIcon();
    return self();
  }

  private void updateIcon() {
    if (customIcon != null) {
      setIcon(customIcon);
    } else if (arrow) {
      setIcon(arrowColor == null ? Kit.icon(ARROW, ARROW_SIZE) : Kit.icon(ARROW, ARROW_SIZE, arrowColor));
    } else {
      setIcon(null);
    }
    // la flecha va despues del texto salvo que se pida a la izquierda; un icono propio va antes
    setHorizontalTextPosition(customIcon != null || arrowLeft ? SwingConstants.TRAILING : SwingConstants.LEADING);
  }
}
