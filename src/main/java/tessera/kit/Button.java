package tessera.kit;

import javax.swing.JButton;

/**
 * Boton con estilos y eventos encadenables.
 *
 * <pre>
 * Button.primary("Guardar").onClick(e -> save())
 * Button.of("Cancelar").onClick(this::dispose)
 * Button.of("Buscar").icon("app/img/search.svg", 16).iconGap(8)
 * </pre>
 */
public class Button extends JButton implements Clickable<Button> {

  protected Button(String text) {
    super(text);
  }

  /** Boton normal. */
  public static Button of(String text) {
    return new Button(text);
  }

  /** Boton principal (azul): la accion que se espera del usuario. */
  public static Button primary(String text) {
    return new Button(text).primary();
  }

  /** Convierte este boton en el principal (azul). */
  public Button primary() {
    Kit.makePrimary(this);
    return this;
  }
}
