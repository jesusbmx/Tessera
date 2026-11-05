package tessera.components;

import java.awt.Component;
import java.awt.Point;
import java.awt.Window;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.SwingUtilities;

/**
 * Mueve una ventana sin decoracion arrastrando un componente (la barra de titulo propia, por ejemplo).
 */
public class WindowDragListener extends MouseAdapter {

    public final Window window;
    private Point startPoint = null;
        
    public WindowDragListener(Window window) {
        this.window = window;
    }
    

    @Override
    public void mousePressed(MouseEvent e) {
        final Component movablePanel = (Component) e.getSource();
        startPoint = SwingUtilities.convertPoint(movablePanel, e.getPoint(), movablePanel);
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        final Component movablePanel = (Component) e.getSource();
        
        // Solo ejecutamos si startPoint es diferente de null para evitar NullPointerException.
        if (startPoint != null) {
            final Point location = window.getLocation();
            final Point currentPoint  = SwingUtilities.convertPoint(movablePanel, e.getPoint(), movablePanel);
            // Calculamos la nueva ubicación solo una vez para reducir el número de operaciones.
            int newX = location.x + (currentPoint.x - startPoint.x);
            int newY = location.y + (currentPoint.y - startPoint.y);
            window.setLocation(newX, newY);
        }
    }

    public void setup(Component topPane) {
        topPane.addMouseListener(this);
        topPane.addMouseMotionListener(this);
    }
}
