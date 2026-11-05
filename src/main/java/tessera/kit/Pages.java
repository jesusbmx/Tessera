package tessera.kit;

import java.awt.CardLayout;
import java.awt.Component;
import javax.swing.JPanel;

/**
 * Varias paginas en el mismo lugar, de las que se ve una a la vez (los pasos de un asistente, las vistas
 * de la ventana principal).
 *
 * <pre>
 * Pages pages = Pages.create().page("name", namePanel).page("type", typePanel);
 * pages.show("type");
 * </pre>
 */
public class Pages extends JPanel implements Styled<Pages> {

  private final CardLayout cards = new CardLayout();

  protected Pages() {
    setLayout(cards);
  }

  public static Pages create() {
    return new Pages();
  }

  /** Agrega una pagina con ese nombre. */
  public Pages page(String name, Component content) {
    add(content, name);
    return this;
  }

  /** Muestra la pagina con ese nombre. */
  public void show(String name) {
    cards.show(this, name);
  }
}
