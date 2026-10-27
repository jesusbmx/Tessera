
package tessera.components;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

/**
 * Barra de navegacion hecha con etiquetas: cursor de mano, color al pasar el mouse y al seleccionar.
 * Se arma con {@link Builder}.
 */
public class InteractiveNavbar implements MouseListener {
    
    private static final Cursor HAND_CURSOR = new Cursor(Cursor.HAND_CURSOR);
    private static final Cursor DEFAULT_CURSOR = new Cursor(Cursor.DEFAULT_CURSOR);
    
    final Color foreground;
    final Color selectedForeground;
    final Color hoverForeground;
    final Color hoverSelectedForeground;
    final Component[] items;
    
    Component selectedItem;

    public InteractiveNavbar(Builder builder) {
        this.foreground = builder.foreground;
        this.selectedForeground = builder.selectedForeground;
        this.hoverForeground = builder.hoverForeground;
        this.hoverSelectedForeground = builder.hoverSelectedForeground;
        this.items = builder.items;
        
        initComponents();
    }
    
    private void initComponents() {
        for (Component item : items) {
            item.addMouseListener(this);
        }
    }

    public void updateComponents() {
        for (int i = 0; i < items.length; i++) {
            Component it = items[i];
            it.setForeground(foreground);
        }
        if (selectedItem != null) {
            selectedItem.setForeground(selectedForeground);
        }
    }

    public void setSelectedItem(Component selectedItem) {
        this.selectedItem = selectedItem;
        updateComponents();
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        updateComponents();
    }

    @Override
    public void mousePressed(MouseEvent e) {
        setSelectedItem((Component) e.getSource());
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        final Component component = (Component) e.getSource();
        component.setCursor(HAND_CURSOR);
        
        if (component != selectedItem) {
            component.setForeground(hoverForeground);
        } else {
            component.setForeground(hoverSelectedForeground);
        }
    }

    @Override
    public void mouseExited(MouseEvent e) {
        final Component component = (Component) e.getSource();
        component.setCursor(DEFAULT_CURSOR);
        
        if (component != selectedItem) {
            component.setForeground(foreground);
        } else {
            component.setForeground(selectedForeground);
        }
    }
    
   

    public static class Builder {
        
        Color foreground;
        Color selectedForeground;
        Color hoverForeground;
        Color hoverSelectedForeground;
        Component[] items;

        public Builder foreground(Color color) {
            this.foreground = color;
            return this;
        }

        public Builder selectedForeground(Color color) {
            this.selectedForeground = color;
            return this;
        }
        
        public Builder hoverForeground(Color color) {
            this.hoverForeground = color;
            return this;
        }

        public Builder hoverSelectedForeground(Color hoverSelectedForeground) {
            this.hoverSelectedForeground = hoverSelectedForeground;
            return this;
        }

        public Builder items(Component... array) {
            this.items = array;
            return this;
        }

        public InteractiveNavbar build() {
            if (this.foreground == null) {
                this.foreground = Color.BLACK;
            }
            
            if (this.selectedForeground == null) {
                this.selectedForeground = Color.BLUE;
            }
            
            if (this.hoverForeground == null) {
                this.hoverForeground = increaseBrightness(
                        this.foreground, 1.5f);
            }
            
            if (this.hoverSelectedForeground == null) {
                this.hoverSelectedForeground = increaseBrightness(
                        this.selectedForeground, 1.5f);
            }
            
            if (this.items == null) {
                this.items = new Component[0];
            }
            
            return new InteractiveNavbar(this);
        }
        
        public static Color increaseBrightness(Color color, float factor) {
            float[] hsb = Color.RGBtoHSB(
                    color.getRed(), color.getGreen(), color.getBlue(), null);

            // Increase the brightness component
            hsb[2] = Math.min(1.0f, hsb[2] * factor);

            // Convert back to RGB
            int rgb = Color.HSBtoRGB(hsb[0], hsb[1], hsb[2]);

            return new Color(rgb);
        }
    }
}