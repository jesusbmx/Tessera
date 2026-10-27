package tessera.components;

import java.awt.Component;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import javax.swing.JTable;
import javax.swing.table.*;

public abstract class BaseTableModel<T> extends AbstractTableModel
        implements Iterable<T> {

    private static final long serialVersionUID = -3559749545223591549L;

    /**
     * Columnas del JTable.
     */
    private final Vector<String> columns;
    /**
     * Filas o items del JTable.
     */
    private final Vector<T> rows = new Vector<T>();

    /**
     * Construye la clase con los nombres de las columnas de la tabla.
     */
    public BaseTableModel(String... columns) {
        this.columns = convertVector(columns);
    }

    /**
     * Convierte un arreglo en un vector.
     */
    public static <V> Vector<V> convertVector(V... array) {
        if (array == null) {
            return new Vector<V>();
        }
        Vector<V> v = new Vector<V>(array.length);
        for (V o : array) {
            v.addElement(o);
        }
        return v;
    }

    /**
     * Obtiene el numero de columnas que contiene la tabla.
     */
    @Override
    public int getColumnCount() {
        return columns.size();
    }

    /**
     * Obtiene el nombre de la columna segun el indice.
     */
    @Override
    public String getColumnName(int columnIndex) {
        return columns.get(columnIndex);
    }

    /**
     * Agrega un nuevo columna a la tabla.
     */
    public void addColumn(String columnName) {
        columns.addElement(columnName);
        fireTableStructureChanged();
    }

    /**
     * Asigna los encabezados de la tabla.
     */
    public void setColumnIdentifiers(String... columnsName) {
        columns.clear();
        columns.addAll(convertVector(columnsName));
        // Notifica el cambio a la vista.
        fireTableStructureChanged();
    }

    /**
     * Asigna los encabezados de la tabla.
     */
    public void setColumnIdentifiers(List<String> columnsName) {
        columns.clear();
        columns.addAll(columnsName);
        // Notifica el cambio a la vista.
        fireTableStructureChanged();
    }

    /**
     * Elimina las columnas de la tabla.
     */
    public void clearColumns() {
        columns.clear();
        fireTableStructureChanged();
    }

    /**
     * Obtiene las columnas.
     */
    public List<String> getColumns() {
        return new ArrayList<String>(columns);
    }

    /**
     * Devuelve el número de filas de la tabla.
     */
    @Override
    public int getRowCount() {
        return rows.size();
    }

    /**
     * Obtiene una fila segun el indice.
     */
    public T getRow(int rowIndex) {
        return rows.get(rowIndex);
    }

    /**
     * Asigna un item a la fila, segun el indice.
     */
    public void setRow(int rowIndex, T item) {
        rows.set(rowIndex, item);
        rows.setSize(getRowCount());
        //items.add(item);
        // Notifica el cambio a la vista.
        fireTableRowsUpdated(rowIndex, rowIndex);
    }

    /**
     * Agrega un nuevo item a la fila.
     */
    public final void addRow(T item) {
        addRow(getRowCount(), item);
    }

    /**
     * Agrega un nuevo item a la fila. segun el indice.
     */
    public void addRow(int rowIndex, T item) {
        rows.insertElementAt(item, rowIndex);
        rows.setSize(getRowCount());
        //items.add(item);
        // Notifica el cambio a la vista.
        fireTableRowsInserted(rowIndex, rowIndex);
    }

    /**
     * Valida si existe un item en la tabla.
     */
    public boolean containsRow(T item) {
        return rows.contains(item);
    }

    /**
     * Elimina un item de la tabla segun el indice.
     */
    public T removeRow(int rowIndex) {
        try {
            return rows.remove(rowIndex);
        } finally {
            // Notifica el cambio a la vista.
            fireTableRowsDeleted(rowIndex, rowIndex);
        }
    }

    /**
     * Elimina un item de la tabla.
     */
    public final T removeRow(T item) {
        int index = rows.indexOf(item);
        if (index != -1) {
            return BaseTableModel.this.removeRow(index);
        }
        return null;
    }

    /**
     * Elimina una lista de items de la tabla.
     */
    public boolean removeAllRows(Collection<T> toRemove) {
        try {
            return rows.removeAll(toRemove);
        } finally {
            // Notifica el cambio a la vista.
            fireTableDataChanged();
        }
    }

    /**
     * Elimina todas las filas de la tabla.
     */
    public void clearRows() {
        rows.clear();
        // Notifica el cambio a la vista.
        fireTableDataChanged();
    }

    /**
     * Obtiene todos los items de la tabla.
     */
    public ArrayList<T> getRows() {
        return new ArrayList<T>(rows);
    }

    /**
     * Limpia y asigna una nueva colecion a la tabla.
     */
    public void setItems(Collection<T> vector) {
        rows.clear();
        rows.addAll(vector);
        // Notifica el cambio a la vista.
        this.fireTableDataChanged();
    }

    /**
     * Limpia y asigna una nueva colecion a la tabla.
     */
    public void setItems(T... array) {
        rows.clear();
        rows.addAll(convertVector(array));
        // Notifica el cambio a la vista.
        this.fireTableDataChanged();
    }

    @Override
    public abstract Object getValueAt(int row, int col);

    /**
     * Devuelve la superclase más específica para todos los valores de celda en
     * la columna.
     */
    @Override
    public Class<?> getColumnClass(int col) {
        if (getRowCount() > 0) {
            Object cell = getValueAt(0, col);
            if (cell != null) {
                return cell.getClass();
            }
        }
        return super.getColumnClass(col);
    }

    /**
     * Convierte los items de la tabla en una arreglo.
     */
    public T[] rowsToArray(Class<T> clazz) {
        T[] genericArray = (T[]) Array.newInstance(clazz, rows.size());
        return rows.toArray(genericArray);
    }

    @Override
    public Iterator<T> iterator() {
        return getRows().iterator();
    }

    public static class TArray extends BaseTableModel<Object[]> {

        public TArray(String... columns) {
            super(columns);
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            Object[] row = getRow(rowIndex);
            return row[columnIndex];
        }

        public Object getValueAt(int row, String columnName) {
            int index = findColumn(columnName);
            return (index != -1) ? getValueAt(row, index) : null;
        }

        @Override
        public void setValueAt(Object value, int rowIndex, int columnIndex) {
            Object[] row = getRow(rowIndex);
            row[columnIndex] = value;
            fireTableCellUpdated(rowIndex, columnIndex);
        }

        public void setValueAt(Object value, int row, String columnName) {
            int index = findColumn(columnName);
            if (index != -1) {
                super.setValueAt(value, row, index);
            }
        }
    }

    public static class TList<T> extends BaseTableModel<List<T>> {

        public TList(String... columns) {
            super(columns);
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            List<T> row = getRow(rowIndex);
            return (columnIndex < row.size()) ? row.get(columnIndex) : null;
        }

        public Object getValueAt(int row, String columnName) {
            int index = findColumn(columnName);
            return (index != -1) ? getValueAt(row, index) : null;
        }

        @Override
        public void setValueAt(Object value, int rowIndex, int columnIndex) {
            List<T> row = getRow(rowIndex);
            row.set(columnIndex, (T) value);
            fireTableCellUpdated(rowIndex, columnIndex);
        }

        public void setValueAt(Object value, int row, String columnName) {
            int index = findColumn(columnName);
            if (index != -1) {
                super.setValueAt(value, row, index);
            }
        }
    }

    public static void adjustColumnPreferredWidths(JTable table) {
        TableColumnModel columnModel = table.getColumnModel();
        for (int column = 0; column < table.getColumnCount(); column++) {
            int maxwidth = 0; // Ancho máximo necesario para los datos en la columna
            
            // Primero, calcular el ancho máximo necesario para los datos de las celdas
            for (int row = 0; row < table.getRowCount(); row++) {
                TableCellRenderer rend = table.getCellRenderer(row, column);
                Object value = table.getValueAt(row, column);
                Component comp = rend.getTableCellRendererComponent(table, value, false, false, row, column);
                maxwidth = Math.max(comp.getPreferredSize().width, maxwidth);
            }
            
            // Luego, considerar el ancho del encabezado de la columna
            TableCellRenderer headerRenderer = table.getTableHeader().getDefaultRenderer();
            Object headerValue = columnModel.getColumn(column).getHeaderValue();
            Component headerComp = headerRenderer.getTableCellRendererComponent(table, headerValue, false, false, 0, column);
            maxwidth = Math.max(headerComp.getPreferredSize().width, maxwidth);
            
            TableColumn tableColumn = columnModel.getColumn(column);
            // Puedes ajustar este margen si lo deseas
            tableColumn.setPreferredWidth(maxwidth + 10);
        }
    }
}
