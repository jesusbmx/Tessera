package tessera.theme;

import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatIntelliJLaf;
import com.formdev.flatlaf.FlatLaf;
import java.awt.Color;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.io.InputStream;
import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ToolTipManager;
import javax.swing.UIManager;

/**
 * Tema de la aplicacion: look and feel (FlatLaf), fuente Inter empaquetada, color de acento, fondo de los
 * paneles y la paleta del kit. Se instala una sola vez, antes de crear cualquier ventana.
 *
 * <pre>
 * Theme.builder()
 *     .accent("#1976D2")
 *     .panelBackground("#F7F6F3")
 *     .success("#2E7D32").danger("#C62828")      // colores de Variant (Button.success, Label.danger...)
 *     .install();
 * </pre>
 *
 * Hay cuatro temas listos, que se pueden ajustar antes de instalar:
 *
 * <pre>
 * Theme.light().install();                       // claro: el de siempre (azul, fondo blanco)
 * Theme.dark().install();                        // oscuro
 * Theme.ubuntu().install();                      // estilo Ubuntu (Yaru) claro: naranja #E95420
 * Theme.ubuntuDark().install();                  // estilo Ubuntu (Yaru) oscuro
 * Theme.dark().accent("#4F9EFF").install();      // un tema listo con un color propio
 * </pre>
 *
 * {@code Theme.builder()} no toca la paleta del kit (solo lo que se le pida), asi que sigue funcionando como antes.
 * Un tema listo la define toda, y por eso se puede pasar de uno a otro sin que queden colores del anterior.
 * Conviene elegir el tema al arrancar: los widgets leen la paleta al crearse y no se repintan solos al cambiarla.
 * {@link #isDark()} dice si el tema es oscuro, para los colores que la app fija por su cuenta.
 *
 * Despues de {@code install()} la app puede ajustar lo suyo con {@code UIManager.put(...)}
 * (pestanas, barra de titulo, etc.): el tema solo fija lo que es comun a todas las apps.
 */
public final class Theme {

  public static final String DEFAULT_FONT_FAMILY = "Inter";

  private static final Logger LOG = Logger.getLogger(Theme.class.getName());

  /**
   * Archivos de Inter dentro de la libreria. Son TTF y no el OTF del jar flatlaf-fonts-inter: el Java 8 de
   * Oracle escala mal los OTF y todo el texto sale al doble.
   */
  private static final String[] FONT_FILES = {
    "Inter-Regular.ttf", "Inter-Bold.ttf", "Inter-Italic.ttf", "Inter-BoldItalic.ttf"
  };

  private static String fontFamily = DEFAULT_FONT_FAMILY;
  /** La paleta instalada (la clara hasta que se instale un tema). Es la unica fuente de los colores del kit. */
  private static Palette palette = Palette.LIGHT;
  private static boolean fontsRegistered;
  /** Si ya se fijo {@code @accentColor} de FlatLaf (para quitarlo al pasar a un tema que no lo usa). */
  private static boolean tintApplied;

  private Theme() {
  }

  public static Builder builder() {
    return new Builder();
  }

  /** Tema claro: el de siempre. Define toda la paleta del kit ({@link Palette#LIGHT}). */
  public static Builder light() {
    return builder().palette(Palette.LIGHT);
  }

  /** Tema oscuro: fondos grafito, texto claro y acento azul ({@link Palette#DARK}). */
  public static Builder dark() {
    return builder().palette(Palette.DARK);
  }

  /** Estilo Ubuntu (Yaru) claro: acento naranja, fondo calido y banners berenjena ({@link Palette#UBUNTU}). */
  public static Builder ubuntu() {
    return builder().palette(Palette.UBUNTU);
  }

  /** Estilo Ubuntu (Yaru) oscuro: acento naranja sobre grises neutros ({@link Palette#UBUNTU_DARK}). */
  public static Builder ubuntuDark() {
    return builder().palette(Palette.UBUNTU_DARK);
  }
  /** Si el tema instalado es oscuro. Para los colores que la app fija por su cuenta (fondos, iconos...). */
  public static boolean isDark() {
    return palette.dark;
  }

  /**
   * La paleta instalada: fondos, tarjetas, texto secundario, filas de listas, banners, acento y si es oscura.
   * Es lo que los widgets leen al crearse ({@code Theme.palette().muted}, {@code .surface}...).
   */
  public static Palette palette() {
    return palette;
  }

  /** Familia de la fuente de la interfaz (la de {@link #builder()} o Inter). Para fuentes creadas a mano. */
  public static String fontFamily() {
    return fontFamily;
  }

  /** Color de acento: foco, seleccion, enlaces. */
  public static Color accent() {
    return palette.accent;
  }

  /** Fondo estandar de los paneles de contenido. */
  public static Color panelBackground() {
    return palette.panelBackground;
  }

  private static String hex(Color color) {
    return String.format("#%06X", color.getRGB() & 0xFFFFFF);
  }

  private static Color orElse(Color value, Color fallback) {
    return value != null ? value : fallback;
  }

  /** Registra las fuentes empaquetadas (una sola vez); false si no se pudo cargar ninguna. */
  private static boolean registerFonts() {
    if (fontsRegistered) {
      return true;
    }
    final GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();
    boolean registered = false;
    for (String file : FONT_FILES) {
      try (InputStream in = Theme.class.getResourceAsStream("/tessera/fonts/" + file)) {
        if (in == null) {
          LOG.warning("No se encontro la fuente " + file);
          continue;
        }
        registered |= env.registerFont(Font.createFont(Font.TRUETYPE_FONT, in));
      } catch (Exception e) {
        LOG.log(Level.WARNING, "No se pudo cargar la fuente " + file, e);
      }
    }
    fontsRegistered = registered;
    return registered;
  }

  public static final class Builder {

    private String font = DEFAULT_FONT_FAMILY;
    private boolean bundledFont = true;
    private Color accent = Theme.palette.accent;
    private Color primary;
    private Color surface;
    private Color cardLine;
    private Color panelBackground = Color.WHITE;
    private boolean darkMode;
    private boolean tintControls;
    private Color muted;
    private Color line;
    private Color rowOdd;
    private Color rowEven;
    private Color rowSelected;
    private Color banner;
    private Color onBanner;
    private final Map<Variant, Color> variants = new EnumMap<>(Variant.class);
    private int arc = 12;
    private int tooltipDismissMs = 8000;

    private Builder() {
    }

    /** Usa esa fuente instalada en el sistema en vez de la Inter empaquetada. */
    public Builder font(String family) {
      this.font = family;
      this.bundledFont = false;
      return this;
    }

    /** Color de acento global (foco, checkboxes, radios, barras de progreso, enlaces). */
    public Builder accent(Color color) {
      this.accent = color;
      return this;
    }

    /** Igual, con el color en hexadecimal ({@code "#1976D2"}). */
    public Builder accent(String hex) {
      return accent(Color.decode(hex));
    }

    /** Color de los botones principales ({@code Button.primary}); por defecto el del kit. */
    public Builder primary(Color color) {
      this.primary = color;
      return this;
    }

    public Builder primary(String hex) {
      return primary(Color.decode(hex));
    }

    /** Relleno de las tarjetas ({@code Card}). */
    public Builder surface(Color color) {
      this.surface = color;
      return this;
    }

    public Builder surface(String hex) {
      return surface(Color.decode(hex));
    }

    /** Borde de las tarjetas ({@code Card}). */
    public Builder cardLine(Color color) {
      this.cardLine = color;
      return this;
    }

    public Builder cardLine(String hex) {
      return cardLine(Color.decode(hex));
    }

    /** Fondo por defecto de paneles, scrolls y viewports (las listas y tablas siguen blancas). */
    public Builder panelBackground(Color color) {
      this.panelBackground = color;
      return this;
    }

    public Builder panelBackground(String hex) {
      return panelBackground(Color.decode(hex));
    }

    /** Look and feel oscuro (FlatDarkLaf) en vez del claro. Los temas listos ya lo indican. */
    public Builder darkMode(boolean dark) {
      this.darkMode = dark;
      return this;
    }

    /**
     * Pinta radios, casillas, barras de progreso y la seleccion con el acento (fija {@code @accentColor} de FlatLaf
     * antes de instalarlo). Sin esto esos componentes siguen con el azul de FlatLaf; ver docs/guides/customizing.md.
     */
    public Builder tintControls() {
      this.tintControls = true;
      return this;
    }

    /** Texto secundario ({@link Palette#muted}): ayudas y descripciones. */
    public Builder muted(Color color) {
      this.muted = color;
      return this;
    }

    public Builder muted(String hex) {
      return muted(Color.decode(hex));
    }

    /** Bordes finos y separadores ({@link Palette#line}). */
    public Builder line(Color color) {
      this.line = color;
      return this;
    }

    public Builder line(String hex) {
      return line(Color.decode(hex));
    }

    /** Filas de las listas: las pares, las impares y la seleccionada ({@link Palette#rowOdd}, {@link Palette#rowEven}, {@link Palette#rowSelected}). */
    public Builder rows(Color odd, Color even, Color selected) {
      this.rowOdd = odd;
      this.rowEven = even;
      this.rowSelected = selected;
      return this;
    }

    public Builder rows(String odd, String even, String selected) {
      return rows(Color.decode(odd), Color.decode(even), Color.decode(selected));
    }

    /** Banners y barras oscuras ({@code Header}): su fondo y el texto que va encima ({@link Palette#banner}, {@link Palette#onBanner}). */
    public Builder banner(Color background, Color text) {
      this.banner = background;
      this.onBanner = text;
      return this;
    }

    public Builder banner(String backgroundHex, String textHex) {
      return banner(Color.decode(backgroundHex), Color.decode(textHex));
    }

    /**
     * Toma todos los colores de una {@link Palette} (un tema entero: look and feel, fondos, tarjetas, filas, banners,
     * acento y variantes). Lo que se llame despues la ajusta: {@code builder().palette(Palette.UBUNTU).accent("#00897B")}.
     */
    public Builder palette(Palette p) {
      this.darkMode = p.dark;
      this.tintControls = p.tintControls;
      this.accent = p.accent;
      this.primary = p.primary;
      this.panelBackground = p.panelBackground;
      this.surface = p.surface;
      this.cardLine = p.cardLine;
      this.muted = p.muted;
      this.line = p.line;
      this.rowOdd = p.rowOdd;
      this.rowEven = p.rowEven;
      this.rowSelected = p.rowSelected;
      this.banner = p.banner;
      this.onBanner = p.onBanner;
      resetVariants();
      variants.putAll(p.variants());
      return this;
    }
    /** Devuelve todas las variantes a su color de fabrica (para que un tema no arrastre los de otro). */
    Builder resetVariants() {
      for (Variant variant : Variant.values()) {
        variants.put(variant, null);
      }
      return this;
    }

    /** Cambia el color de una variante ({@code Button.success}, {@code Label.danger}...) en toda la app. */
    public Builder variant(Variant variant, Color color) {
      variants.put(variant, color);
      return this;
    }

    public Builder variant(Variant variant, String hex) {
      return variant(variant, Color.decode(hex));
    }

    /** Color de {@code Variant.SECONDARY}: botones y textos de segundo plano. */
    public Builder secondary(Color color) {
      return variant(Variant.SECONDARY, color);
    }

    public Builder secondary(String hex) {
      return variant(Variant.SECONDARY, hex);
    }

    /** Color de {@code Variant.SUCCESS}: confirmaciones. */
    public Builder success(Color color) {
      return variant(Variant.SUCCESS, color);
    }

    public Builder success(String hex) {
      return variant(Variant.SUCCESS, hex);
    }

    /** Color de {@code Variant.DANGER}: acciones destructivas y errores. */
    public Builder danger(Color color) {
      return variant(Variant.DANGER, color);
    }

    public Builder danger(String hex) {
      return variant(Variant.DANGER, hex);
    }

    /** Color de {@code Variant.WARNING}: avisos de atencion. */
    public Builder warning(Color color) {
      return variant(Variant.WARNING, color);
    }

    public Builder warning(String hex) {
      return variant(Variant.WARNING, hex);
    }

    /** Color de {@code Variant.INFO}: informacion neutra. */
    public Builder info(Color color) {
      return variant(Variant.INFO, color);
    }

    public Builder info(String hex) {
      return variant(Variant.INFO, hex);
    }

    /** Color de {@code Variant.LIGHT}: fondo claro con texto oscuro. */
    public Builder light(Color color) {
      return variant(Variant.LIGHT, color);
    }

    public Builder light(String hex) {
      return variant(Variant.LIGHT, hex);
    }

    /** Color de {@code Variant.DARK}: fondo oscuro con texto claro. */
    public Builder dark(Color color) {
      return variant(Variant.DARK, color);
    }

    public Builder dark(String hex) {
      return variant(Variant.DARK, hex);
    }

    /** Radio (px) de las esquinas de botones, campos y barras (12 por defecto). */
    public Builder arc(int arc) {
      this.arc = arc;
      return this;
    }

    /** Cuanto dura un tooltip en pantalla (8000 ms por defecto). */
    public Builder tooltipDismissMs(int ms) {
      this.tooltipDismissMs = ms;
      return this;
    }

    /** Aplica el tema. Si algo falla queda el look and feel del sistema y se anota en el log. */
    public void install() {
      Theme.fontFamily = font;
      for (Map.Entry<Variant, Color> entry : variants.entrySet()) {
        entry.getKey().setColor(entry.getValue());
      }
      // Lo que el builder no toca conserva el valor del tema anterior (Theme.builder() solo cambia lo que se le pide).
      final Palette before = Theme.palette;
      Theme.palette = Palette.resolved(darkMode, tintControls,
          new Color[] {
            accent, orElse(primary, before.primary), panelBackground, orElse(surface, before.surface),
            orElse(cardLine, before.cardLine), orElse(muted, before.muted), orElse(line, before.line),
            orElse(rowOdd, before.rowOdd), orElse(rowEven, before.rowEven), orElse(rowSelected, before.rowSelected),
            orElse(banner, before.banner), orElse(onBanner, before.onBanner)
          },
          Variant.overrides());
      try {
        // La fuente se registra y se elige antes del tema: asi la usan tambien los componentes que no
        // fijan la suya (botones, combos, tablas, menus...).
        if (!bundledFont || registerFonts()) {
          FlatLaf.setPreferredFontFamily(font);
        } else {
          Theme.fontFamily = Font.SANS_SERIF;
        }

        // @accentColor se lee al instalar FlatLaf: va antes. Solo se quita lo que puso un tema anterior, no lo de la app.
        if (tintControls) {
          FlatLaf.setGlobalExtraDefaults(Collections.singletonMap("@accentColor", hex(accent)));
          tintApplied = true;
        } else if (tintApplied) {
          FlatLaf.setGlobalExtraDefaults(null);
          tintApplied = false;
        }

        UIManager.setLookAndFeel(darkMode ? new FlatDarkLaf() : new FlatIntelliJLaf());

        UIManager.put("Component.accentColor", accent);

        // FlatDarkLaf pinta casillas y radios en gris aunque haya acento (el claro ya los pinta con el acento).
        if (tintControls && darkMode) {
          UIManager.put("CheckBox.icon.selectedBackground", accent);
          UIManager.put("CheckBox.icon.selectedBorderColor", accent);
          UIManager.put("CheckBox.icon.focusedSelectedBackground", accent);
          UIManager.put("CheckBox.icon.selectedHoverBackground", accent.brighter());
          UIManager.put("CheckBox.icon.selectedPressedBackground", accent.darker());
          UIManager.put("CheckBox.icon.checkmarkColor", Color.WHITE);
        }

        // Fondo de todo panel que no fije el suyo. "List.background" no se toca a proposito: el contenido
        // de listas y tarjetas sigue blanco.
        UIManager.put("Panel.background", panelBackground);
        UIManager.put("ScrollPane.background", panelBackground);
        UIManager.put("Viewport.background", panelBackground);

        UIManager.put("Button.arc", arc);
        UIManager.put("Component.arc", arc);
        UIManager.put("ProgressBar.arc", arc);
        UIManager.put("TextComponent.arc", arc);

        UIManager.put("ScrollBar.trackArc", 999);
        UIManager.put("ScrollBar.thumbArc", 999);

      } catch (Exception e) {
        LOG.log(Level.SEVERE, "No se pudo configurar el look and feel", e);
      }

      ToolTipManager.sharedInstance().setDismissDelay(tooltipDismissMs);
    }
  }
}
