package tessera.kit;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
import javax.swing.JCheckBoxMenuItem;

/**
 * Boton que abre un menu de casillas para marcar <b>varias</b> opciones. El texto del boton es fijo; las
 * marcadas se leen con {@link #getValues()} o {@link #isChecked}.
 *
 * <pre>
 * MultiChoiceButton&lt;String&gt; unzip = MultiChoiceButton.&lt;String&gt;of("Configuracion para descomprimir")
 *     .option("residues", "Borrar los residuos del zip").checked("residues")
 *     .option("sameFolder", "Extraer en la misma carpeta")
 *     .onChange(values -&gt; save(values));
 * boolean sameFolder = unzip.isChecked("sameFolder");
 * unzip.setChecked("sameFolder", true);
 * </pre>
 *
 * Para elegir una sola opcion y que el boton la muestre usa {@link ChoiceButton}; si el clic solo corre una
 * accion, {@link MenuButton}.
 *
 * @param <T> tipo del valor de cada opcion (un enum, un entero, un texto)
 */
public class MultiChoiceButton<T> extends PopupButton<MultiChoiceButton<T>> {

  /** Una opcion del menu: su valor y su casilla. */
  private final class Option {
    final T value;
    final JCheckBoxMenuItem item;

    Option(T value, String text, String tip) {
      this.value = value;
      this.item = new JCheckBoxMenuItem(text);
      item.setToolTipText(tip);
      item.addItemListener(e -> changed());
    }
  }

  private final List<Option> options = new ArrayList<>();
  private final List<Consumer<Set<T>>> listeners = new ArrayList<>();
  private boolean adjusting;

  protected MultiChoiceButton(String text) {
    super(text);
  }

  /** Boton con ese texto y sin opciones (se agregan con {@link #option}). Se escribe {@code MultiChoiceButton.<Tipo>of(texto)}. */
  public static <T> MultiChoiceButton<T> of(String text) {
    return new MultiChoiceButton<>(text);
  }

  // ------------------------------------------------------------------------ //
  //   C O N F I G U R A C I O N
  // ------------------------------------------------------------------------ //

  /** Agrega una opcion (casilla) con su valor y su texto. Empieza sin marcar. */
  public MultiChoiceButton<T> option(T value, String text) {
    return option(value, text, null);
  }

  /** Agrega una opcion con una ayuda breve que sale al dejar el mouse sobre ella. */
  public MultiChoiceButton<T> option(T value, String text, String tip) {
    final Option option = new Option(value, text, tip);
    options.add(option);
    menu().add(option.item);
    return this;
  }

  /** Deja marcada esa opcion (para el estado inicial al armar el boton). */
  public MultiChoiceButton<T> checked(T value) {
    setChecked(value, true);
    return this;
  }

  /** Habilita o deshabilita una opcion (sin efecto si ese valor no existe). */
  public MultiChoiceButton<T> optionEnabled(T value, boolean enabled) {
    final Option option = find(value);
    if (option != null) {
      option.item.setEnabled(enabled);
    }
    return this;
  }

  /** Que hacer cada vez que cambian las marcadas (tambien por codigo), con el conjunto de valores marcados. */
  public MultiChoiceButton<T> onChange(Consumer<Set<T>> action) {
    listeners.add(action);
    return this;
  }

  public MultiChoiceButton<T> onChange(Runnable action) {
    return onChange(values -> action.run());
  }

  // ------------------------------------------------------------------------ //
  //   V A L O R E S
  // ------------------------------------------------------------------------ //

  /** Los valores marcados, en el orden en que se agregaron las opciones. */
  public Set<T> getValues() {
    final Set<T> values = new LinkedHashSet<>();
    for (Option option : options) {
      if (option.item.isSelected()) {
        values.add(option.value);
      }
    }
    return values;
  }

  /** Esa opcion esta marcada. */
  public boolean isChecked(T value) {
    final Option option = find(value);
    return option != null && option.item.isSelected();
  }

  /** Marca o desmarca una opcion (un valor que no existe se ignora). */
  public void setChecked(T value, boolean checked) {
    final Option option = find(value);
    if (option != null) {
      option.item.setSelected(checked);
    }
  }

  /** Deja marcadas exactamente esas opciones y avisa una sola vez a los {@link #onChange} si algo cambio. */
  public void setValues(Collection<? extends T> values) {
    final Set<T> before = getValues();
    adjusting = true;
    try {
      for (Option option : options) {
        option.item.setSelected(values.contains(option.value));
      }
    } finally {
      adjusting = false;
    }
    if (!before.equals(getValues())) {
      changed();
    }
  }

  private Option find(T value) {
    for (Option option : options) {
      if (Objects.equals(option.value, value)) {
        return option;
      }
    }
    return null;
  }

  private void changed() {
    if (adjusting) {
      return;
    }
    final Set<T> values = getValues();
    for (Consumer<Set<T>> listener : new ArrayList<>(listeners)) {
      listener.accept(values);
    }
  }
}
