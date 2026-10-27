
package tessera.components;

import java.awt.Component;
import javax.swing.JCheckBox;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.border.EmptyBorder;

/**
 * Renderizador de celda que muestra un JCheckBox para cada elemento de la lista.
 */
class CheckBoxCellRenderer extends JCheckBox implements ListCellRenderer<Object> {
    private static final EmptyBorder EMPTY_BORDER = new EmptyBorder(6, 6, 6, 6);

    @Override
    public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                  boolean isSelected, boolean cellHasFocus) {
        setBorder(EMPTY_BORDER);
        setComponentOrientation(list.getComponentOrientation());
        setFont(list.getFont());
        setBackground(list.getBackground());
        setForeground(list.getForeground());
        setSelected(((CheckableListModel<?>) list.getModel()).isChecked(index));
        setText(value == null ? "" : value.toString());
        return this;
    }
}