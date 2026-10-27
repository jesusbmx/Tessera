package tessera.kit;

import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

/**
 * Selector numerico con flechas.
 *
 * <pre>
 * Spinner.integer(1, 1, 99, 1).onChange(this::onCopiesChanged)
 * Spinner.decimal(1.0, 0.0, 10.0, 0.1)
 * </pre>
 */
public class Spinner extends JSpinner implements Styled<Spinner> {

  protected Spinner(SpinnerNumberModel model) {
    super(model);
  }

  /** Numeros enteros: valor inicial, minimo, maximo y paso. */
  public static Spinner integer(int value, int min, int max, int step) {
    return new Spinner(new SpinnerNumberModel(value, min, max, step));
  }

  /** Numeros con decimales: valor inicial, minimo, maximo y paso. */
  public static Spinner decimal(double value, double min, double max, double step) {
    return new Spinner(new SpinnerNumberModel(value, min, max, step));
  }

  /** Con un modelo propio: tipo de numero (float...), limites abiertos (null)... */
  public static Spinner of(SpinnerNumberModel model) {
    return new Spinner(model);
  }

  /** Que hacer cada vez que cambia el valor. */
  public Spinner onChange(Runnable action) {
    addChangeListener(e -> action.run());
    return this;
  }
}
