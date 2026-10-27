package tessera.kit;

import javax.swing.JComboBox;

/**
 * Lista desplegable.
 *
 * <pre>
 * Combo.of("USB", "Red", "Archivo").onChange(this::onModeChanged)
 * Combo&lt;ComboItem&lt;Integer&gt;&gt; cb = Combo.empty();     // se llena despues con un Binder
 * </pre>
 *
 * @param <T> tipo de los elementos
 */
public class Combo<T> extends JComboBox<T> implements Styled<Combo<T>> {

  protected Combo() {
  }

  /** Combo vacio (se llena despues). */
  public static <T> Combo<T> empty() {
    return new Combo<>();
  }

  /** Combo con esos elementos. */
  @SafeVarargs
  public static <T> Combo<T> of(T... items) {
    final Combo<T> combo = new Combo<>();
    for (T item : items) {
      combo.addItem(item);
    }
    return combo;
  }

  /** Que hacer cada vez que se elige un elemento (tambien si se elige por codigo). */
  public Combo<T> onChange(Runnable action) {
    addActionListener(e -> action.run());
    return this;
  }

  /** Cuantos elementos se ven a la vez al desplegar. */
  public Combo<T> maxRows(int rows) {
    setMaximumRowCount(rows);
    return this;
  }

  /** El usuario puede escribir un valor que no esta en la lista. */
  public Combo<T> editable(boolean editable) {
    setEditable(editable);
    return this;
  }
}
