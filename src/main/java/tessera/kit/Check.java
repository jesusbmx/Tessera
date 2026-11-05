package tessera.kit;

import javax.swing.JCheckBox;

/**
 * Casilla de verificacion.
 *
 * <pre>
 * Check.of("Aceleracion por hardware").selected(true).onClick(this::onChanged)
 * </pre>
 */
public class Check extends JCheckBox implements Clickable<Check> {

  protected Check(String text) {
    super(text);
  }

  public static Check of(String text) {
    return new Check(text);
  }
}
