package tessera.components;

import java.awt.Color;
import java.awt.Component;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

/**
 * Renderizador de tabla que resalta (fondo amarillo suave) las columnas indicadas por nombre.
 */
public class HighlightColumnRenderer extends DefaultTableCellRenderer {
    
    private static Color SELECTED_COLOR = new Color(255,243,205);
    
    private List<String> highlightedColumns;

    public HighlightColumnRenderer(List<String> highlightedColumns) {
        this.highlightedColumns = highlightedColumns;
    }
    
    public HighlightColumnRenderer() {
        this(new ArrayList<>());
    }
    
    public HighlightColumnRenderer(String... highlightedColumns) {
        this(Arrays.asList(highlightedColumns));
    }

    public void setHighlightedColumns(List<String> columns) {
        highlightedColumns = columns;
    }
    
    public void addHighlightedColumn(String column) {
        highlightedColumns.add(column);
    }
    
    public void removeHighlightedColumn(String column) {
        highlightedColumns.remove(column);
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
                                                   boolean isSelected, boolean hasFocus, int row, int column) {
        final Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

        final String columnName = table.getColumnName(column);

        if (isSelected) {
            // Usa el estilo de selección por defecto
            c.setBackground(table.getSelectionBackground());
            c.setForeground(table.getSelectionForeground());
        } else if (highlightedColumns.contains(columnName)) {
            c.setBackground(SELECTED_COLOR); // color personalizado
            c.setForeground(Color.BLACK);
        } else {
            c.setBackground(Color.WHITE); // fondo normal
            c.setForeground(Color.BLACK);
        }

        // Solo mostrar el borde si tiene foco (opcional)
        if (hasFocus) {
            setBorder(javax.swing.BorderFactory.createDashedBorder(Color.BLUE));
        } else {
            setBorder(null);
        }

        return c;
    }
    
    // Aplicar este renderer a todas las columnas
    public void applyCellRenderer(JTable tableView) {
        for (int i = 0; i < tableView.getColumnCount(); i++) {
            tableView.getColumnModel()
                    .getColumn(i)
                    .setCellRenderer(this);
        }
    }
}
