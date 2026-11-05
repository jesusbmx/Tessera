package tessera.components;

import java.awt.BorderLayout;
import java.awt.Component;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;
import tessera.kit.Col;
import tessera.kit.Kit;
import tessera.kit.Label;

/**
 * Diseño para los items. Se registra como renderer por defecto de las listas ("List.cellRenderer"):
 * se crea una sola vez y se reutiliza para pintar todas las filas.
 */
public class LabelListCell extends JPanel implements ListCellRenderer {

    /**
     * Creates new LabelListCell
     */
    public LabelListCell() {
        buildUI();
    }

    @Override
    public Component getListCellRendererComponent(JList list, Object value, int index,
            boolean isSelected, boolean cellHasFocus) {

      label.setComponentOrientation(list.getComponentOrientation());
      label.setFont(list.getFont());
      if (isSelected) {
        setBackground(Kit.ROW_SELECTED);
      } else {
        setBackground(index % 2 == 0 ? Kit.ROW_ODD : Kit.ROW_EVEN);
      }

      label.setForeground(list.getForeground());
      label.setEnabled(list.isEnabled());
      label.setText(value == null ? "" : value.toString());

      return this;
    }

    // ---------------------------------------------------------------------
    // UI: widgets y construcción de la pantalla
    // Cada bloque lleva sus campos (widgets) justo encima del método que lo arma:
    // campos del bloque -> método del bloque. Empieza en buildUI() y sigue el orden
    // en que se usan. La lógica (eventos, datos, etc.) va fuera de esta zona.
    // ---------------------------------------------------------------------

    private final Label label = Label.text("");

    private void buildUI() {
        setLayout(new BorderLayout());
        add(Col.gap(0).padding(8).children(label), BorderLayout.CENTER);
    }
}
