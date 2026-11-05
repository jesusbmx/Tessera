package tessera.kit;

import javax.swing.ButtonGroup;
import javax.swing.JRadioButton;

/**
 * Opcion de un grupo de opciones excluyentes.
 *
 * <pre>
 * Radio rbtnPrinter = Radio.of("Impresora");
 * Radio rbtnIp = Radio.of("IP");
 * Radio.group(rbtnPrinter, rbtnIp);          // solo una seleccionada a la vez
 * </pre>
 */
public class Radio extends JRadioButton implements Clickable<Radio> {

  protected Radio(String text) {
    super(text);
  }

  public static Radio of(String text) {
    return new Radio(text);
  }

  /** Agrupa las opciones: al elegir una se desmarca la otra. */
  public static ButtonGroup group(Radio... radios) {
    return Kit.group(radios);
  }
}
