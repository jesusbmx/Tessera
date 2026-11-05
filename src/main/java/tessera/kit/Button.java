package tessera.kit;

import javax.swing.JButton;
import tessera.theme.Variant;

/**
 * Boton con estilos y eventos encadenables.
 *
 * <pre>
 * Button.primary("Guardar").onClick(e -> save())
 * Button.of("Cancelar").onClick(this::dispose)
 * Button.of("Buscar").icon("app/img/search.svg", 16).iconGap(8)
 * Button.danger("Eliminar").onClick(this::delete)
 * Button.outline(Variant.SUCCESS, "Aprobar")
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

  /** Boton relleno con el color de una variante (primary, success, danger...). */
  public static Button of(Variant variant, String text) {
    return new Button(text).variant(variant);
  }

  /** Boton de apoyo (gris). */
  public static Button secondary(String text) {
    return of(Variant.SECONDARY, text);
  }

  /** Boton de confirmacion (verde). */
  public static Button success(String text) {
    return of(Variant.SUCCESS, text);
  }

  /** Boton de una accion destructiva (rojo). */
  public static Button danger(String text) {
    return of(Variant.DANGER, text);
  }

  /** Boton de atencion (amarillo). */
  public static Button warning(String text) {
    return of(Variant.WARNING, text);
  }

  /** Boton informativo (celeste). */
  public static Button info(String text) {
    return of(Variant.INFO, text);
  }

  /** Boton claro con texto oscuro. */
  public static Button light(String text) {
    return of(Variant.LIGHT, text);
  }

  /** Boton oscuro con texto claro. */
  public static Button dark(String text) {
    return of(Variant.DARK, text);
  }

  /** Boton solo con borde y texto del color de la variante; se rellena al pasar el mouse. */
  public static Button outline(Variant variant, String text) {
    return new Button(text).outline(variant);
  }

  /** Rellena este boton con el color de la variante. */
  public Button variant(Variant variant) {
    Kit.makeVariant(this, variant);
    return this;
  }

  /** Deja este boton solo con borde y texto del color de la variante. */
  public Button outline(Variant variant) {
    Kit.makeOutline(this, variant);
    return this;
  }
}
