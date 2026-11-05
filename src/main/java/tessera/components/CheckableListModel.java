package tessera.components;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.ListSelectionModel;

/**
 * Modelo de lista que soporta elementos con casillas de verificación.
 */
public class CheckableListModel<T> extends DefaultListModel<T> {

    private final Map<T, Boolean> checkStatusMap = new HashMap<>();

    /**
     * Agrega o actualiza el estado de verificación de un elemento en el modelo.
     */
    public void setChecked(int index, boolean isChecked) {
        T element = get(index);
        checkStatusMap.put(element, isChecked);
        fireContentsChanged(this, index, index);
    }

    /**
     * Obtiene el estado de verificación de un elemento dado su índice.
     */
    public boolean isChecked(int index) {
        T element = get(index);
        return checkStatusMap.getOrDefault(element, false);
    }

    /**
     * Limpia todos los estados de verificación.
     */
    public void clearChecks() {
        checkStatusMap.clear();
        fireContentsChanged(this, 0, getSize() - 1);
    }

    /**
     * Establece o despeja la verificación de todos los elementos.
     */
    public void checkAll(boolean check) {
        for (int i = 0; i < getSize(); i++) {
            setChecked(i, check);
        }
    }

    /**
     * Devuelve una lista de todos los elementos seleccionados.
     */
    public List<T> getCheckedItems() {
        List<T> checkedItems = new ArrayList<>();
        for (Map.Entry<T, Boolean> entry : checkStatusMap.entrySet()) {
            if (entry.getValue()) {
                checkedItems.add(entry.getKey());
            }
        }
        return checkedItems;
    }

    /**
     * Configura un JList para usar este modelo con casillas de verificación.
     */
    public void configureList(JList<T> list) {
        list.setCellRenderer(new CheckBoxCellRenderer());
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        removeAllMouseListener(list);
        attachClickListener(list);
        list.setModel(this);
    }

    /**
     * Adjunta un controlador de clics para manejar cambios de estado de verificación.
     */
    private void attachClickListener(JList<T> list) {
        list.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int index = list.locationToIndex(e.getPoint());
                if (index >= 0) {
                    setChecked(index, !isChecked(index));
                }
            }
        });
    }
    
    private static void removeAllMouseListener(JList<?> list) {
        for( MouseListener ml : list.getMouseListeners()) {
            list.removeMouseListener( ml );
        }
    }
}
