package tessera.kit;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import javax.swing.JMenuItem;

/**
 * Boton que abre un menu de acciones: el usuario hace clic en una opcion y pasa algo; no queda nada elegido.
 *
 * <pre>
 * MenuButton.of("Agregar")
 *     .item("Crear servicio", this::create)
 *     .separator()
 *     .item("Importar de un archivo", this::importFile)
 *
 * MenuButton.of("").icon("app/img/more.svg", 24).item("Exportar", this::export)    // solo icono
 * </pre>
 *
 * Para elegir una opcion y que el boton la muestre usa {@link ChoiceButton}; para marcar varias,
 * {@link MultiChoiceButton}.
 */
public class MenuButton extends PopupButton<MenuButton> {

  private final List<Consumer<Menu>> openListeners = new ArrayList<>();

  protected MenuButton(String text) {
    super(text);
  }

  public static MenuButton of(String text) {
    return new MenuButton(text);
  }

  /** Agrega una opcion que corre {@code action} al elegirla. */
  public MenuButton item(String text, Runnable action) {
    menu().item(text, action);
    return this;
  }

  /** Agrega una opcion ya armada ({@link MenuItem}, con icono o ayuda). */
  public MenuButton item(JMenuItem item) {
    menu().item(item);
    return this;
  }

  /** Agrega un encabezado en negrita, sin accion. */
  public MenuButton title(String text) {
    menu().title(text);
    return this;
  }

  public MenuButton separator() {
    menu().separator();
    return this;
  }

  /** Quita todas las opciones. */
  public MenuButton clear() {
    menu().clear();
    return this;
  }

  /**
   * Corre justo antes de abrir el menu, para los que se llenan al momento: recibe el menu, que se vacia con
   * {@code clear()} y se vuelve a llenar. Si queda sin opciones, no se abre.
   */
  public MenuButton onOpen(Consumer<Menu> filler) {
    openListeners.add(filler);
    return this;
  }

  @Override
  protected void open() {
    for (Consumer<Menu> listener : openListeners) {
      listener.accept(menu());
    }
    super.open();
  }
}
