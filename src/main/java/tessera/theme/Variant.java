package tessera.theme;

import java.awt.Color;
import tessera.kit.Kit;

/**
 * Colores semanticos del kit, como las variantes de Bootstrap: cada widget que los admite
 * ({@code Button}, {@code Toggle}, {@code Label}, {@code Hint}) los pide con {@code .variant(Variant.X)}
 * o con una fabrica ({@code Button.danger("Eliminar")}, {@code Label.success("Guardado")}).
 *
 * <pre>
 * Button.success("Aprobar")
 * Button.outline(Variant.DANGER, "Eliminar")
 * Label.warning("Quedan 2 dias")
 * Theme.builder().variant(Variant.SUCCESS, "#2E7D32").install();    // cambiar un color para toda la app
 * </pre>
 *
 * {@link #PRIMARY}, {@link #ACCENT} e {@link #INVERSE} siguen al tema ({@code Kit.PRIMARY}, {@code Theme.accent()},
 * {@code Kit.DARK}); el resto trae su color (el de Bootstrap) y se cambia con {@link #setColor(Color)}.
 */
public enum Variant {

  /** La accion principal (el {@code Kit.PRIMARY} del tema). */
  PRIMARY(null),
  /** El color de acento del tema. */
  ACCENT(null),
  /** Accion o texto de segundo plano: gris. */
  SECONDARY(Color.decode("#6C757D")),
  /** Todo salio bien: verde. */
  SUCCESS(Color.decode("#198754")),
  /** Peligro o accion destructiva: rojo. */
  DANGER(Color.decode("#DC3545")),
  /** Atencion: amarillo (con texto oscuro encima). */
  WARNING(Color.decode("#FFC107")),
  /** Informacion neutra: celeste (con texto oscuro encima). */
  INFO(Color.decode("#0DCAF0")),
  /** Fondo oscuro con texto claro (el {@code Kit.DARK} del tema). */
  INVERSE(null);

  /** Por encima de esta luminancia (0..1) el texto sobre el color va oscuro; por debajo, blanco. */
  private static final double LIGHT_BACKGROUND = 0.4;

  private static final Color ON_LIGHT = Color.decode("#212529");

  private final Color defaultColor;
  private Color override;

  Variant(Color defaultColor) {
    this.defaultColor = defaultColor;
  }

  /** El color de la variante, el del tema si sigue al tema. */
  public Color color() {
    if (override != null) {
      return override;
    }
    switch (this) {
      case PRIMARY:
        return Kit.PRIMARY;
      case ACCENT:
        return Theme.accent();
      case INVERSE:
        return Kit.DARK;
      default:
        return defaultColor;
    }
  }

  /** Cambia el color de la variante en toda la app (mejor antes de crear las ventanas; ver {@code Theme.Builder.variant}). */
  public void setColor(Color color) {
    this.override = color;
  }

  /** Color del texto que va encima de {@link #color()}: blanco, u oscuro si el color es claro (amarillo, celeste). */
  public Color onColor() {
    return luminance(color()) > LIGHT_BACKGROUND ? ON_LIGHT : Color.WHITE;
  }

  /** El color al pasar el mouse por encima. */
  public Color hover() {
    return color().darker();
  }

  /** El color al presionar. */
  public Color pressed() {
    return hover().darker();
  }

  /** Luminancia relativa (0 negro .. 1 blanco) segun WCAG. */
  private static double luminance(Color color) {
    return 0.2126 * channel(color.getRed()) + 0.7152 * channel(color.getGreen()) + 0.0722 * channel(color.getBlue());
  }

  private static double channel(int value) {
    final double c = value / 255.0;
    return c <= 0.03928 ? c / 12.92 : Math.pow((c + 0.055) / 1.055, 2.4);
  }
}
