package tessera.theme;

import java.awt.Color;
import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;

/**
 * Los colores de un tema, como datos: fondos, tarjetas, texto secundario, filas de listas, banners, el acento y los
 * colores de variantes que cambian. {@link Theme} los aplica al instalar; no tiene logica propia.
 *
 * <pre>
 * Palette.LIGHT   Palette.DARK   Palette.UBUNTU   Palette.UBUNTU_DARK
 *
 * Theme.builder().palette(Palette.UBUNTU).install();                    // igual que Theme.ubuntu().install()
 * Theme.builder().palette(Palette.UBUNTU).accent("#00897B").install();  // y se ajusta como cualquier otro tema
 * </pre>
 *
 * Un tema nuevo es una constante mas aqui: no hace falta tocar {@code Theme}. Hasta que se instale un tema,
 * {@code Theme.palette()} es {@link #LIGHT}.
 */
public final class Palette {

  /** Claro: el de siempre (azul, fondo blanco). */
  public static final Palette LIGHT = of(false, false,
      "#1976D2", "#3B82F6", "#FFFFFF", "#F8FAFC", "#E2E8F0",
      "#666666", "#E5E5E5", "#FAFAFA", "#FFFFFF", "#BBDEFB", "#232F34", "#CCCCCC");

  /** Oscuro: fondos grafito, texto claro y acento azul. */
  public static final Palette DARK = of(true, true,
      "#4C9AFF", "#3B82F6", "#1E2227", "#272C33", "#3A414A",
      "#9AA4AF", "#3A414A", "#242930", "#1E2227", "#2B4A73", "#14181C", "#D0D5DA",
      Variant.DARK, "#3A414A");

  /** Estilo Ubuntu (Yaru) claro: acento naranja, fondo calido y banners color berenjena. */
  public static final Palette UBUNTU = of(false, true,
      "#E95420", "#E95420", "#F6F5F4", "#FFFFFF", "#DEDDDA",
      "#77767B", "#DEDDDA", "#FAF9F8", "#FFFFFF", "#F9D3C6", "#2C001E", "#F2E4EC",
      Variant.SECONDARY, "#77767B", Variant.SUCCESS, "#0E8420", Variant.DANGER, "#C7162B",
      Variant.WARNING, "#F99B11");

  /** Estilo Ubuntu (Yaru) oscuro: acento naranja sobre grises neutros y banners color berenjena. */
  public static final Palette UBUNTU_DARK = of(true, true,
      "#E95420", "#E95420", "#2D2D2D", "#383838", "#4A4A4A",
      "#ADADAD", "#4A4A4A", "#323232", "#2D2D2D", "#6B3A27", "#2C001E", "#F2E4EC",
      Variant.DARK, "#4A4A4A", Variant.SECONDARY, "#8D8D8D", Variant.SUCCESS, "#26A269",
      Variant.DANGER, "#E0334A", Variant.WARNING, "#F99B11");

  /** Si usa el look and feel oscuro. */
  public final boolean dark;
  /** Si radios, casillas, barras de progreso y la seleccion toman el acento (ver {@code Theme.Builder.tintControls}). */
  public final boolean tintControls;
  /** Foco, seleccion, enlaces. */
  public final Color accent;
  /** Botones principales. */
  public final Color primary;
  /** Fondo de paneles, scrolls y viewports. */
  public final Color panelBackground;
  /** Relleno de las tarjetas. */
  public final Color surface;
  /** Borde de las tarjetas. */
  public final Color cardLine;
  /** Texto secundario. */
  public final Color muted;
  /** Bordes finos y separadores. */
  public final Color line;
  /** Filas de listas: impares, pares y la seleccionada. */
  public final Color rowOdd;
  public final Color rowEven;
  public final Color rowSelected;
  /** Banners oscuros y el texto que va encima. */
  public final Color banner;
  public final Color onBanner;

  /** Solo las variantes que este tema cambia; el resto conserva su color de fabrica. */
  private final Map<Variant, Color> variants;

  private Palette(boolean dark, boolean tintControls, Color[] c, Map<Variant, Color> variants) {
    this.dark = dark;
    this.tintControls = tintControls;
    this.accent = c[0];
    this.primary = c[1];
    this.panelBackground = c[2];
    this.surface = c[3];
    this.cardLine = c[4];
    this.muted = c[5];
    this.line = c[6];
    this.rowOdd = c[7];
    this.rowEven = c[8];
    this.rowSelected = c[9];
    this.banner = c[10];
    this.onBanner = c[11];
    this.variants = variants;
  }

  /**
   * Colores en este orden: acento, principal, fondo, tarjeta, borde de tarjeta, texto secundario, linea, fila impar,
   * fila par, fila seleccionada, banner y texto del banner. Despues, pares {@code Variant, "#hex"}.
   */
  private static Palette of(boolean dark, boolean tintControls, String... colors) {
    return of(dark, tintControls, colors, new Object[0]);
  }

  private static Palette of(boolean dark, boolean tintControls, String a, String b, String c, String d, String e,
      String f, String g, String h, String i, String j, String k, String l, Object... variantPairs) {
    return of(dark, tintControls, new String[] {a, b, c, d, e, f, g, h, i, j, k, l}, variantPairs);
  }

  private static Palette of(boolean dark, boolean tintControls, String[] hex, Object[] variantPairs) {
    final Color[] colors = new Color[hex.length];
    for (int n = 0; n < hex.length; n++) {
      colors[n] = Color.decode(hex[n]);
    }
    final Map<Variant, Color> variants = new EnumMap<>(Variant.class);
    for (int n = 0; n + 1 < variantPairs.length; n += 2) {
      variants.put((Variant) variantPairs[n], Color.decode((String) variantPairs[n + 1]));
    }
    return new Palette(dark, tintControls, colors, Collections.unmodifiableMap(variants));
  }

  /** La paleta que {@link Theme} instalo: los colores ya resueltos y las variantes cambiadas. */
  static Palette resolved(boolean dark, boolean tintControls, Color[] colors, Map<Variant, Color> variants) {
    final Map<Variant, Color> copy = new EnumMap<>(Variant.class);
    copy.putAll(variants);
    return new Palette(dark, tintControls, colors, Collections.unmodifiableMap(copy));
  }

  /** Los colores de variante que este tema cambia (solo lectura). */
  public Map<Variant, Color> variants() {
    return variants;
  }
}
