package tessera.test;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;
import javax.swing.JComponent;
import javax.swing.SwingUtilities;

/** Dibuja un componente en una imagen sin abrir ventanas, para revisar colores en puntos concretos. */
final class Shots {

  private Shots() {
  }

  /** Acomoda los hijos y dibuja el componente en una imagen del tamano dado. */
  static BufferedImage paint(JComponent root, int width, int height) {
    root.setSize(width, height);
    layout(root);
    layout(root);
    final BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
    final Graphics2D g = image.createGraphics();
    try {
      root.paint(g);
    } finally {
      g.dispose();
    }
    return image;
  }

  /** doLayout de todo el arbol: sin ventana, Swing no lo hace solo. */
  static void layout(Component component) {
    if (component instanceof Container) {
      component.doLayout();
      for (Component child : ((Container) component).getComponents()) {
        layout(child);
      }
    }
  }

  /** Color del pixel (x, y) del componente {@code inner}, dentro de la imagen de {@code root}. */
  static Color at(BufferedImage image, JComponent root, Component inner, int x, int y) {
    final Point p = SwingUtilities.convertPoint(inner, x, y, root);
    return new Color(image.getRGB(p.x, p.y), true);
  }

  static Color at(BufferedImage image, int x, int y) {
    return new Color(image.getRGB(x, y), true);
  }

  /** Cuantos colores distintos tiene la imagen (una pantalla en blanco o toda de un color tiene muy pocos). */
  static int distinctColors(BufferedImage image) {
    final java.util.Set<Integer> colors = new java.util.HashSet<>();
    for (int y = 0; y < image.getHeight(); y++) {
      for (int x = 0; x < image.getWidth(); x++) {
        colors.add(image.getRGB(x, y));
      }
    }
    return colors.size();
  }
}
