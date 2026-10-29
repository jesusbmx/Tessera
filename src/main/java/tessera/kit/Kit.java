package tessera.kit;

import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.util.UIScale;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.util.HashMap;
import java.util.Map;
import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import javax.swing.JComponent;
import javax.swing.UIManager;
import tessera.theme.Variant;

/**
 * Colores, medidas y utilidades comunes del kit de UI (tessera.kit).
 *
 * El kit es una capa delgada sobre Swing + FlatLaf + MigLayout: los widgets (Label, Button, Input...) son
 * los de Swing con metodos encadenables, y los contenedores (Row, Col, Stack, Form, Card...) arman el
 * layout sin escribir restricciones de MigLayout.
 *
 * Los colores son la paleta del kit; se cambian una vez al iniciar la app, con {@code Theme} (ver tessera.theme).
 *
 * Ejemplo:
 * <pre>
 * Card.create("Conexion").subtitle("Como se conecta la impresora").padding(10, 12, 12, 12).gap(6)
 *     .children(
 *         Row.gap(14).children(rbtnPrinter, rbtnIp),
 *         Row.gap(10).fill(txtIp).children(btnScan));
 * </pre>
 */
public final class Kit {

  /** Fondo oscuro: banners de los pasos, barra lateral. */
  public static Color DARK = new Color(35, 47, 52);

  /** Texto sobre {@link #DARK}. */
  public static Color ON_DARK = new Color(204, 204, 204);

  /** Texto secundario: ayudas, descripciones. */
  public static Color MUTED = new Color(102, 102, 102);

  /** Bordes finos de tarjetas y separadores. */
  public static Color LINE = new Color(229, 229, 229);

  /** Relleno de las tarjetas: apenas mas oscuro que el fondo para que se distingan sin necesitar un borde fuerte. */
  public static Color SURFACE = new Color(248, 250, 252);

  /** Borde de las tarjetas. */
  public static Color CARD_LINE = new Color(226, 232, 240);

  /** Filas alternas de listas y su fila seleccionada (ver LabelListCell). */
  public static Color ROW_ODD = Color.decode("#FAFAFA");
  public static Color ROW_EVEN = Color.decode("#FFFFFF");
  public static Color ROW_SELECTED = Color.decode("#BBDEFB");

  /** Color de acento (botones principales, seleccion). */
  public static Color PRIMARY = Color.decode("#3B82F6");

  /** Eje(s) en que un componente ocupa el espacio que sobra dentro de su contenedor (ver {@link #fill}). */
  private static final String FILL_KEY = "kit.fill";

  /** Ancho / alto fijos (px) que pide un componente a su contenedor (ver {@link Styled#width}). */
  static final String WIDTH_KEY = "kit.width";
  static final String HEIGHT_KEY = "kit.height";

  private Kit() {
  }

  // ------------------------------------------------------------------------ //
  //   M E D I D A S
  // ------------------------------------------------------------------------ //

  /** Escala un tamano en pixeles con el zoom de la interfaz (igual en 100%). */
  public static int px(int value) {
    return UIScale.scale(value);
  }

  /** Fuente de la interfaz (la que fijo el tema) con ese tamano y estilo. */
  public static Font font(int size, boolean bold) {
    Font base = UIManager.getFont("defaultFont");
    if (base == null) {
      base = UIManager.getFont("Label.font");
    }
    if (base == null) {
      base = new Font(Font.SANS_SERIF, Font.PLAIN, 12);
    }
    return base.deriveFont(bold ? Font.BOLD : Font.PLAIN, UIScale.scale((float) size));
  }

  // ------------------------------------------------------------------------ //
  //   B O T O N E S
  // ------------------------------------------------------------------------ //

  /** Estilo de boton principal (azul) para un boton puntual: la accion que se espera del usuario. */
  public static void makePrimary(AbstractButton button) {
    makeVariant(button, Variant.PRIMARY);
  }

  /** Boton relleno con el color de la variante (success, danger...) y texto que contrasta. */
  public static void makeVariant(AbstractButton button, Variant variant) {
    final Map<String, Object> style = new HashMap<>();
    style.put("background", variant.color());
    style.put("foreground", variant.onColor());
    style.put("hoverBackground", variant.hover());
    style.put("pressedBackground", variant.pressed());
    style.put("borderColor", variant.hover());
    style.put("hoverBorderColor", variant.pressed());
    style.put("focusedBorderColor", variant.pressed());
    style.put("borderWidth", 1f);
    button.putClientProperty(FlatClientProperties.STYLE, style);
  }

  /** Boton solo con borde y texto del color de la variante; se rellena al pasar el mouse. */
  public static void makeOutline(AbstractButton button, Variant variant) {
    final Color color = variant.color();
    final Map<String, Object> style = new HashMap<>();
    style.put("background", new Color(0, 0, 0, 0));
    style.put("foreground", color);
    style.put("hoverBackground", color);
    style.put("hoverForeground", variant.onColor());
    style.put("pressedBackground", variant.hover());
    style.put("pressedForeground", variant.onColor());
    style.put("borderColor", color);
    style.put("hoverBorderColor", color);
    style.put("focusedBorderColor", variant.hover());
    style.put("borderWidth", 1f);
    button.putClientProperty(FlatClientProperties.STYLE, style);
  }

  /** Agrupa botones de radio / toggles: solo uno queda seleccionado a la vez. */
  public static ButtonGroup group(AbstractButton... buttons) {
    final ButtonGroup group = new ButtonGroup();
    for (AbstractButton button : buttons) {
      group.add(button);
    }
    return group;
  }

  // ------------------------------------------------------------------------ //
  //   C R E C I M I E N T O   D E N T R O   D E   U N   C O N T E N E D O R
  // ------------------------------------------------------------------------ //

  /**
   * Marca que el componente ocupa el espacio que sobra del contenedor (Row, Col, Form, Card...) en el eje
   * indicado. Es lo que hace Stack.fill(...); en Row y Col se prefiere {@code .fill(c)} y {@code .fillBoth(c)}.
   */
  public static <C extends JComponent> C fill(C component, boolean horizontal, boolean vertical) {
    component.putClientProperty(FILL_KEY, (horizontal ? "x" : "") + (vertical ? "y" : ""));
    return component;
  }

  /** Tamano fijo (px) que pidio el componente con width()/height(): "w 120!, h 50!" para MigLayout, o vacio. */
  static String sizeHint(Component component) {
    if (!(component instanceof JComponent)) {
      return "";
    }
    final JComponent c = (JComponent) component;
    final Object w = c.getClientProperty(WIDTH_KEY);
    final Object h = c.getClientProperty(HEIGHT_KEY);
    return (w instanceof Integer ? "w " + px((Integer) w) + "px!" : "")
        + (w instanceof Integer && h instanceof Integer ? ", " : "")
        + (h instanceof Integer ? "h " + px((Integer) h) + "px!" : "");
  }

  static boolean fillsX(Component component) {
    return hint(component).contains("x");
  }

  static boolean fillsY(Component component) {
    return hint(component).contains("y");
  }

  private static String hint(Component component) {
    if (component instanceof JComponent) {
      final Object value = ((JComponent) component).getClientProperty(FILL_KEY);
      if (value instanceof String) {
        return (String) value;
      }
    }
    return "";
  }
}
