package tessera.theme;

import com.formdev.flatlaf.FlatIntelliJLaf;
import com.formdev.flatlaf.FlatLaf;
import java.awt.Color;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ToolTipManager;
import javax.swing.UIManager;
import tessera.kit.Kit;

/**
 * Tema de la aplicacion: look and feel (FlatLaf), fuente Inter empaquetada, color de acento, fondo de los
 * paneles y la paleta del kit. Se instala una sola vez, antes de crear cualquier ventana.
 *
 * <pre>
 * Theme.builder()
 *     .accent("#1976D2")
 *     .panelBackground("#F7F6F3")
 *     .install();
 * </pre>
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
  private static Color accent = Color.decode("#1976D2");
  private static Color panelBackground = Color.WHITE;
  private static boolean fontsRegistered;

  private Theme() {
  }

  public static Builder builder() {
    return new Builder();
  }

  /** Familia de la fuente de la interfaz (la de {@link #builder()} o Inter). Para fuentes creadas a mano. */
  public static String fontFamily() {
    return fontFamily;
  }

  /** Color de acento: foco, seleccion, enlaces. */
  public static Color accent() {
    return accent;
  }

  /** Fondo estandar de los paneles de contenido. */
  public static Color panelBackground() {
    return panelBackground;
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
    private Color accent = Theme.accent;
    private Color primary;
    private Color surface;
    private Color cardLine;
    private Color panelBackground = Color.WHITE;
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

    public Builder accent(String hex) {
      return accent(Color.decode(hex));
    }

    /** Color de los botones principales ({@code Button.primary}); por defecto el del kit. */
    public Builder primary(String hex) {
      this.primary = Color.decode(hex);
      return this;
    }

    /** Relleno de las tarjetas ({@code Card}). */
    public Builder surface(String hex) {
      this.surface = Color.decode(hex);
      return this;
    }

    /** Borde de las tarjetas ({@code Card}). */
    public Builder cardLine(String hex) {
      this.cardLine = Color.decode(hex);
      return this;
    }

    /** Fondo por defecto de paneles, scrolls y viewports (las listas y tablas siguen blancas). */
    public Builder panelBackground(String hex) {
      this.panelBackground = Color.decode(hex);
      return this;
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
      Theme.accent = accent;
      Theme.panelBackground = panelBackground;
      Theme.fontFamily = font;
      if (primary != null) {
        Kit.PRIMARY = primary;
      }
      if (surface != null) {
        Kit.SURFACE = surface;
      }
      if (cardLine != null) {
        Kit.CARD_LINE = cardLine;
      }

      try {
        // La fuente se registra y se elige antes del tema: asi la usan tambien los componentes que no
        // fijan la suya (botones, combos, tablas, menus...).
        if (!bundledFont || registerFonts()) {
          FlatLaf.setPreferredFontFamily(font);
        } else {
          Theme.fontFamily = Font.SANS_SERIF;
        }

        UIManager.setLookAndFeel(new FlatIntelliJLaf());

        UIManager.put("Component.accentColor", accent);

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
