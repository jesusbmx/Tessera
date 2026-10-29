package tessera.kit;

import javax.swing.ButtonGroup;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JToggleButton;
import com.formdev.flatlaf.FlatClientProperties;
import tessera.theme.Variant;

/**
 * Boton que queda presionado (modo de vista, filtro). Los de un mismo grupo son excluyentes.
 *
 * <pre>
 * Toggle.group(Toggle.of("Lista"), Toggle.of("Tarjetas"));
 * Toggle.of("Activo").variant(Variant.SUCCESS)     // presionado, se pinta de verde
 * </pre>
 */
public class Toggle extends JToggleButton implements Clickable<Toggle> {

  protected Toggle(String text) {
    super(text);
  }

  public static Toggle of(String text) {
    return new Toggle(text);
  }

  /** Agrupa los botones: al presionar uno se suelta el otro. */
  public static ButtonGroup group(Toggle... toggles) {
    return Kit.group(toggles);
  }

  /** El color del boton mientras esta presionado (en vez del gris del tema): el de la variante, con texto que contrasta. */
  public Toggle variant(Variant variant) {
    final Map<String, Object> style = new HashMap<>();
    style.put("selectedBackground", variant.color());
    style.put("selectedForeground", variant.onColor());
    putClientProperty(FlatClientProperties.STYLE, style);
    return this;
  }
}
