package tessera.kit;

import javax.swing.ButtonGroup;
import javax.swing.JToggleButton;

/**
 * Boton que queda presionado (modo de vista, filtro). Los de un mismo grupo son excluyentes.
 *
 * <pre>
 * Toggle.group(Toggle.of("Lista"), Toggle.of("Tarjetas"));
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
}
