package com.jesustectronic.tessera.preview;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import javax.swing.JPanel;
import javax.swing.Scrollable;
import javax.swing.UIManager;

/**
 * Pinta el ultimo cuadro de la pantalla viva y manda al proceso lo que el usuario hace encima (mouse y teclado), con las
 * coordenadas de la pantalla, no las del panel. Va dentro de un JScrollPane: cuando la pantalla es mas grande que el
 * panel se desplaza.
 */
final class LivePanel extends JPanel implements Scrollable {

  /** A donde van el mouse y el teclado (la sesion viva). */
  interface Sink {
    void mouse(int id, int x, int y, int button, int modifiers, int clicks, boolean popup);

    void key(int id, int code, char ch, int modifiers);
  }

  private BufferedImage image;
  private Sink sink;
  /** Con "ajustar al panel" el ancho siempre es el del panel; si no, el de la imagen. */
  private boolean fit = true;

  LivePanel() {
    setFocusable(true);
    setFocusTraversalKeysEnabled(false);               // Tab es para la pantalla, no para el IDE
    setBackground(UIManager.getColor("Panel.background"));

    final MouseAdapter mouse = new MouseAdapter() {
      @Override
      public void mousePressed(MouseEvent e) {
        requestFocusInWindow();
        forward(e);
      }

      @Override
      public void mouseReleased(MouseEvent e) {
        forward(e);
      }

      @Override
      public void mouseClicked(MouseEvent e) {
        forward(e);
      }

      @Override
      public void mouseMoved(MouseEvent e) {
        forward(e);
      }

      @Override
      public void mouseDragged(MouseEvent e) {
        forward(e);
      }

      @Override
      public void mouseEntered(MouseEvent e) {
        forward(e);
      }

      @Override
      public void mouseExited(MouseEvent e) {
        forward(e);
      }
    };
    addMouseListener(mouse);
    addMouseMotionListener(mouse);

    addKeyListener(new KeyListener() {
      @Override
      public void keyTyped(KeyEvent e) {
        forward(e);
      }

      @Override
      public void keyPressed(KeyEvent e) {
        forward(e);
      }

      @Override
      public void keyReleased(KeyEvent e) {
        forward(e);
      }
    });
  }

  void setSink(Sink sink) {
    this.sink = sink;
  }

  void setFit(boolean fit) {
    this.fit = fit;
    revalidate();
  }

  void setImage(BufferedImage image) {
    this.image = image;
    revalidate();
    repaint();
  }

  /** El margen izquierdo: la pantalla se centra cuando es mas angosta que el panel. */
  private int offsetX() {
    return image == null ? 0 : Math.max(0, (getWidth() - image.getWidth()) / 2);
  }

  private void forward(MouseEvent e) {
    if (sink == null || image == null) {
      return;
    }
    final int x = e.getX() - offsetX();
    final int y = e.getY();
    sink.mouse(e.getID(), x, y, e.getButton(), e.getModifiersEx(), e.getClickCount(), e.isPopupTrigger());
  }

  private void forward(KeyEvent e) {
    if (sink != null && image != null) {
      sink.key(e.getID(), e.getKeyCode(), e.getKeyChar(), e.getModifiersEx());
      e.consume();
    }
  }

  @Override
  protected void paintComponent(Graphics g) {
    super.paintComponent(g);
    if (image != null) {
      g.drawImage(image, offsetX(), 0, null);
    }
  }

  @Override
  public Dimension getPreferredSize() {
    return image == null ? new Dimension(0, 0) : new Dimension(image.getWidth(), image.getHeight());
  }

  // ---------------------------------------------------------------------- //
  //   S C R O L L A B L E
  // ---------------------------------------------------------------------- //

  @Override
  public Dimension getPreferredScrollableViewportSize() {
    return getPreferredSize();
  }

  @Override
  public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {
    return 24;
  }

  @Override
  public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {
    return orientation == javax.swing.SwingConstants.VERTICAL ? visibleRect.height : visibleRect.width;
  }

  @Override
  public boolean getScrollableTracksViewportWidth() {
    // un ancho fijo mas angosto que el panel se centra (el panel ocupa todo el ancho y la imagen se dibuja en medio)
    return fit || (getParent() != null && getParent().getWidth() >= getPreferredSize().width);
  }

  @Override
  public boolean getScrollableTracksViewportHeight() {
    return false;
  }
}
