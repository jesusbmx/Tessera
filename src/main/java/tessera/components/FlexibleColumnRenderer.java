package tessera.components;

import java.awt.*;
import java.util.*;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.table.DefaultTableCellRenderer;

/**
 * Renderizador de tabla que resalta columnas por nombre, con fondo, texto y borde propios por columna.
 */
public class FlexibleColumnRenderer extends DefaultTableCellRenderer {

    private final Set<String> highlightedColumns = new HashSet<>();
    private final Map<String, Color> columnBackgrounds = new HashMap<>();
    private final Map<String, Color> columnForegrounds = new HashMap<>();
    private final Map<String, Border> columnBorders = new HashMap<>();

    private Color defaultBackground = new Color(255, 243, 205);
    private Color defaultForeground = Color.BLACK;
    private Border defaultBorder = UIManager.getBorder("Table.cellNoFocusBorder");

    public FlexibleColumnRenderer(String... highlightedColumns) {
        this.highlightedColumns.addAll(Arrays.asList(highlightedColumns));
    }

    public void setDefaultBackground(Color color) {
        this.defaultBackground = color;
    }

    public void setDefaultForeground(Color color) {
        this.defaultForeground = color;
    }

    public void setDefaultBorder(Border border) {
        this.defaultBorder = border;
    }

    public void setColumnBackground(String column, Color color) {
        columnBackgrounds.put(column, color);
    }

    public void setColumnForeground(String column, Color color) {
        columnForegrounds.put(column, color);
    }

    public void setColumnBorder(String column, Border border) {
        columnBorders.put(column, border);
    }

    public void addHighlightedColumn(String column) {
        highlightedColumns.add(column);
    }

    public void removeHighlightedColumn(String column) {
        highlightedColumns.remove(column);
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
                                                   boolean isSelected, boolean hasFocus,
                                                   int row, int column) {
        Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

        String columnName = table.getColumnName(column);

        if (isSelected) {
            c.setBackground(table.getSelectionBackground());
            c.setForeground(table.getSelectionForeground());
        } else if (highlightedColumns.contains(columnName)) {
            c.setBackground(columnBackgrounds.getOrDefault(columnName, defaultBackground));
            c.setForeground(columnForegrounds.getOrDefault(columnName, defaultForeground));
        } else {
            c.setBackground(Color.WHITE);
            c.setForeground(Color.BLACK);
        }

        // Borde visual, siempre que el componente lo permita
        Border border = columnBorders.getOrDefault(columnName, defaultBorder);
        if (c instanceof JComponent) {
            ((JComponent) c).setBorder(border);
        }

        return c;
    }

    public void applyToTable(JTable table) {
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(this);
        }
    }
}
