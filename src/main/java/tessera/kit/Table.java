package tessera.kit;

import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.TableModel;

/**
 * Tabla. Se pone dentro de un {@link Scroll}.
 *
 * <pre>
 * Table table = Table.of(model).rowHeight(28).singleSelection().onSelect(this::onRowSelected);
 * Scroll.of(table)
 * </pre>
 */
public class Table extends JTable implements Styled<Table> {

  protected Table(TableModel model) {
    super(model);
  }

  public static Table of(TableModel model) {
    return new Table(model);
  }

  /** Alto de cada fila (px). */
  public Table rowHeight(int px) {
    setRowHeight(Kit.px(px));
    return this;
  }

  /** Solo una fila seleccionada a la vez. */
  public Table singleSelection() {
    setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    return this;
  }

  /** Que hacer cuando cambia la fila seleccionada (una sola vez por cambio). */
  public Table onSelect(Runnable action) {
    getSelectionModel().addListSelectionListener(e -> {
      if (!e.getValueIsAdjusting()) {
        action.run();
      }
    });
    return this;
  }

  /** Ancho preferido (px) de una columna, por posicion en la vista. */
  public Table columnWidth(int column, int px) {
    getColumnModel().getColumn(column).setPreferredWidth(Kit.px(px));
    return this;
  }
}
