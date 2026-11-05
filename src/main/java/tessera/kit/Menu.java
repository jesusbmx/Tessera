package tessera.kit;

import java.awt.Component;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

/**
 * Menu emergente de acciones: una lista de opciones que corren algo al elegirlas. Se abre debajo de un boton
 * ({@link MenuButton} lo hace solo) o donde se haga clic, por ejemplo el clic derecho de una lista.
 *
 * <pre>
 * final Menu menu = Menu.create()
 *     .title("Servicio")
 *     .item("Editar", this::edit)
 *     .separator()
 *     .item(MenuItem.of("Eliminar", this::delete).icon("app/img/trash.svg", 16));
 *
 * menu.show(list, evt.getX(), evt.getY());     // clic derecho: donde esta el mouse
 * menu.showBelow(button);                      // debajo de un boton
 * </pre>
 *
 * Para que un boton elija una opcion usa {@link ChoiceButton}; para marcar varias, {@link MultiChoiceButton}.
 */
public class Menu extends JPopupMenu implements Styled<Menu> {

  protected Menu() {
  }

  public static Menu create() {
    return new Menu();
  }

  /** Agrega una opcion que corre {@code action} al elegirla. */
  public Menu item(String text, Runnable action) {
    return item(MenuItem.of(text, action));
  }

  /** Agrega una opcion ya armada (con icono, ayuda...): conserva su referencia para cambiarla despues. */
  public Menu item(JMenuItem item) {
    add(item);
    return this;
  }

  /** Agrega un encabezado: texto en negrita, sin accion. */
  public Menu title(String text) {
    return item(MenuItem.title(text));
  }

  /** Agrega una linea que separa grupos de opciones. */
  public Menu separator() {
    addSeparator();
    return this;
  }

  /** Quita todas las opciones (para volver a llenarlo). */
  public Menu clear() {
    removeAll();
    return this;
  }

  /** Abre el menu debajo del componente, alineado a su borde izquierdo. */
  public void showBelow(Component invoker) {
    show(invoker, 0, invoker.getHeight());
  }
}
