package tessera.components;

import java.awt.Component;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JSeparator;
import javax.swing.ListCellRenderer;
import javax.swing.border.EmptyBorder;
import tessera.theme.Theme;

/**
 * Renderizador de combos: texto simple, o el propio componente si el elemento ya lo es.
 * La fila seleccionada usa los colores de seleccion de la lista.
 */
public class ComboBoxRenderer extends JLabel implements ListCellRenderer<Object> {
   
    public ComboBoxRenderer() {
      setOpaque(true);
      setBorder(new EmptyBorder(1, 1, 1, 1));
    }

    @Override
    public Component getListCellRendererComponent(JList list, Object value,
        int index, boolean isSelected, boolean cellHasFocus) {

        if (value instanceof Component) {
            return (Component) value;
        }

        String str = (value == null) ? "" : value.toString();

        if (isSelected) {
          setBackground(list.getSelectionBackground());
          setForeground(list.getSelectionForeground());
        } else {
          setBackground(list.getBackground());
          setForeground(list.getForeground());
        }

        setFont(list.getFont());
        setText(str);

        return this;
    }
    
    public static class Separator extends JSeparator {
        
        public Separator() {
            super();
        }
    }
    
    public static class Button extends JLabel {
        
        public final int id;
        
        public Button(int id, String text) {
            super(text);
            this.id = id;
            setForeground(Theme.accent());
        }
        
        public Button(int id, Icon icon) {
            super(icon);
            this.id = id;
            setForeground(Theme.accent());
        }
    }
}