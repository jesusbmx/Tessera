package tessera.kit;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import javax.swing.ButtonGroup;
import javax.swing.JRadioButtonMenuItem;

/**
 * Boton que abre un menu para elegir <b>una</b> opcion: el boton muestra la elegida. Sirve para pocas opciones
 * que merecen una ayuda breve cada una; para una lista larga usa {@link Combo}.
 *
 * <pre>
 * ChoiceButton&lt;Integer&gt; method = ChoiceButton.&lt;Integer&gt;of()
 *     .placeholder("Elegir metodo")
 *     .option(0, "Driver", "Imprime con el driver del sistema")
 *     .option(1, "Red", "Imprime por IP")
 *     .bold().onChange(v -&gt; refresh());
 * method.setValue(1);                 // elige por codigo (tambien dispara onChange)
 * Integer chosen = method.getValue(); // null si no hay nada elegido
 * </pre>
 *
 * Para marcar varias opciones usa {@link MultiChoiceButton}; si no queda nada elegido y solo corre una
 * accion, {@link MenuButton}.
 *
 * @param <T> tipo del valor de cada opcion (un enum, un entero, un texto)
 */
public class ChoiceButton<T> extends PopupButton<ChoiceButton<T>> {

  /** Una opcion del menu: su valor, el texto que se muestra y su ayuda. */
  private final class Option {
    final T value;
    final String text;
    final String tip;
    final JRadioButtonMenuItem item;

    Option(T value, String text, String tip) {
      this.value = value;
      this.text = text;
      this.tip = tip;
      this.item = new JRadioButtonMenuItem(text);
      item.setToolTipText(tip);
      item.addActionListener(e -> choose(this));
    }
  }

  private final ButtonGroup group = new ButtonGroup();
  private final List<Option> options = new ArrayList<>();
  private final List<Consumer<T>> listeners = new ArrayList<>();
  private Option selected;
  private String placeholder = "";

  protected ChoiceButton() {
    super("");
  }

  /** Boton sin opciones (se agregan con {@link #option}). Se escribe {@code ChoiceButton.<Tipo>of()}. */
  public static <T> ChoiceButton<T> of() {
    return new ChoiceButton<>();
  }

  // ------------------------------------------------------------------------ //
  //   C O N F I G U R A C I O N
  // ------------------------------------------------------------------------ //

  /** Texto del boton mientras no hay nada elegido. */
  public ChoiceButton<T> placeholder(String text) {
    placeholder = text == null ? "" : text;
    if (selected == null) {
      setText(placeholder);
    }
    return this;
  }

  /** Agrega una opcion: su valor y el texto del menu (y del boton al elegirla). */
  public ChoiceButton<T> option(T value, String text) {
    return option(value, text, null);
  }

  /** Agrega una opcion con una ayuda breve que sale al dejar el mouse sobre ella en el menu. */
  public ChoiceButton<T> option(T value, String text, String tip) {
    final Option option = new Option(value, text, tip);
    options.add(option);
    group.add(option.item);
    menu().add(option.item);
    return this;
  }

  /** Habilita o deshabilita una opcion (sin efecto si ese valor no existe). */
  public ChoiceButton<T> optionEnabled(T value, boolean enabled) {
    return optionEnabled(value, enabled, null);
  }

  /** Igual, y mientras la opcion esta deshabilitada el menu muestra {@code disabledTip} en lugar de su ayuda. */
  public ChoiceButton<T> optionEnabled(T value, boolean enabled, String disabledTip) {
    final Option option = find(value);
    if (option != null) {
      option.item.setEnabled(enabled);
      option.item.setToolTipText(!enabled && disabledTip != null ? disabledTip : option.tip);
    }
    return this;
  }

  /** Que hacer cada vez que cambia la opcion elegida (tambien si se elige por codigo), con su valor. */
  public ChoiceButton<T> onChange(Consumer<T> action) {
    listeners.add(action);
    return this;
  }

  public ChoiceButton<T> onChange(Runnable action) {
    return onChange(value -> action.run());
  }

  // ------------------------------------------------------------------------ //
  //   V A L O R
  // ------------------------------------------------------------------------ //

  /** Valor de la opcion elegida, o {@code null} si no hay ninguna. */
  public T getValue() {
    return selected == null ? null : selected.value;
  }

  /** Valor de la opcion elegida, o {@code defaultValue} si no hay ninguna. */
  public T getValue(T defaultValue) {
    return selected == null ? defaultValue : selected.value;
  }

  /** Hay una opcion elegida. */
  public boolean hasValue() {
    return selected != null;
  }

  /** La opcion elegida es esa. */
  public boolean is(T value) {
    return selected != null && Objects.equals(selected.value, value);
  }

  /** Ayuda de la opcion elegida (la que se dio en {@link #option}), o {@code null}. */
  public String getValueTip() {
    return selected == null ? null : selected.tip;
  }

  /**
   * Elige la opcion con ese valor y avisa a los {@link #onChange} si cambio. {@code null} deja el boton sin
   * eleccion (con su {@link #placeholder}); un valor que no esta entre las opciones se ignora.
   */
  public void setValue(T value) {
    if (value == null) {
      choose(null);
      return;
    }
    final Option option = find(value);
    if (option != null) {
      choose(option);
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

  private void choose(Option option) {
    if (option == selected) {
      return;
    }
    selected = option;
    if (option == null) {
      group.clearSelection();
    } else {
      option.item.setSelected(true);
    }
    setText(option == null ? placeholder : option.text);

    final T value = getValue();
    for (Consumer<T> listener : new ArrayList<>(listeners)) {
      listener.accept(value);
    }
  }
}
