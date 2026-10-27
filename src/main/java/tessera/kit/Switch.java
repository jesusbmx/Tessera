package tessera.kit;

import javax.swing.JCheckBox;
import tessera.theme.Icons;

/**
 * Interruptor estilo iOS (un si/no que se aplica al instante).
 *
 * <pre>
 * Switch.of("Iniciar con el sistema").selected(true).onClick(this::onChanged)
 * </pre>
 */
public class Switch extends JCheckBox implements Clickable<Switch> {

  protected Switch(String text) {
    super(text);
    Icons.setupSwitch(this);
  }

  public static Switch of(String text) {
    return new Switch(text);
  }
}
