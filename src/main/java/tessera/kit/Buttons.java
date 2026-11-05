package tessera.kit;

import com.formdev.flatlaf.FlatClientProperties;
import java.awt.Color;
import java.util.HashMap;
import java.util.Map;
import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import tessera.theme.Variant;

/**
 * Utilidades para botones de Swing ya creados: darles el estilo de una {@link Variant} y agruparlos. Los de Tessera
 * ({@link Button}) traen esto en sus metodos ({@code Button.danger(..)}, {@code .outline(..)}).
 *
 * <pre>
 * Buttons.primary(miJButton);                           // el azul de siempre, como Button.primary
 * Buttons.variant(miJButton, Variant.DANGER);           // relleno con el color de la variante
 * Buttons.outline(miJButton, Variant.SUCCESS);          // solo borde; se rellena al pasar el mouse
 * Buttons.group(rbtnA, rbtnB, rbtnC);                   // solo uno queda seleccionado
 * </pre>
 */
public final class Buttons {

  private Buttons() {
  }

  /** Estilo de boton principal (azul) para un boton puntual: la accion que se espera del usuario. */
  public static void primary(AbstractButton button) {
    variant(button, Variant.PRIMARY);
  }

  /** Relleno con el color de la variante (success, danger...) y texto que contrasta. */
  public static void variant(AbstractButton button, Variant variant) {
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

  /** Solo borde y texto del color de la variante; se rellena al pasar el mouse. */
  public static void outline(AbstractButton button, Variant variant) {
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
}
