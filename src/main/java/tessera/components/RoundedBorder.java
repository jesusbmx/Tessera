
package tessera.components;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.RoundRectangle2D;
import javax.swing.border.Border;

/**
 * Borde de "tarjeta": un margen exterior (gutter) de esquinas rectas relleno
 * de {@code marginBackground}, y dentro de ese margen una forma redondeada
 * rellena con el fondo ACTUAL del componente (por eso el componente que use
 * este borde debe ser no-opaco -- setOpaque(false) -- si no, el relleno
 * rectangular normal de Swing en paintComponent asoma en las esquinas fuera
 * del trazo redondeado).
 *
 * Antes esto se armaba como CompoundBorder(MatteBorder + RoundedBorder sin
 * relleno); esa combinación dejaba sin pintar el pedacito entre la esquina
 * recta del margen y la curva del trazo, y ahí se colaba el color de fondo
 * de lo que hubiera detrás (ej. blanco de "List.background") -- se veía como
 * un borde blanco puntiagudo en las esquinas. Unificar todo en un solo Border
 * evita ese hueco: el margen y el relleno redondeado se pintan juntos, sin
 * depender de qué color tenga el componente padre.
 */
public class RoundedBorder implements Border {

    private final int cornerRadius;
    private final Color borderColor;
    private final Color marginBackground;
    private final Insets margin;

    public RoundedBorder(int cornerRadius, Color borderColor) {
        this(cornerRadius, borderColor, null, new Insets(0, 0, 0, 0));
    }

    public RoundedBorder(int cornerRadius) {
        this(cornerRadius, Color.lightGray);
    }

    public RoundedBorder() {
        this(20);
    }

    private RoundedBorder(int cornerRadius, Color borderColor, Color marginBackground, Insets margin) {
        this.cornerRadius = cornerRadius;
        this.borderColor = borderColor;
        this.marginBackground = marginBackground;
        this.margin = margin;
    }

    @Override
    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
        final Graphics2D g2d = (Graphics2D) g.create();
        try {
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // Margen exterior (gutter entre tarjetas): rectángulo recto, sin
            // depender de qué haya detrás del componente.
            if (marginBackground != null && (margin.top > 0 || margin.left > 0 || margin.bottom > 0 || margin.right > 0)) {
                g2d.setColor(marginBackground);
                g2d.fillRect(x, y, width, height);
            }

            final int innerX = x + margin.left;
            final int innerY = y + margin.top;
            final int innerWidth = width - margin.left - margin.right;
            final int innerHeight = height - margin.top - margin.bottom;
            final Shape roundedRectangle = new RoundRectangle2D.Float(innerX, innerY,
                    innerWidth - 1, innerHeight - 1, cornerRadius, cornerRadius);

            // Relleno redondeado con el fondo ACTUAL del componente (en
            // la fila cambia de color: blanco normal, celeste si
            // está seleccionada).
            final Color bg = c.getBackground();
            if (bg != null) {
                g2d.setColor(bg);
                g2d.fill(roundedRectangle);
            }

            g2d.setColor(borderColor);
            g2d.draw(roundedRectangle);
        } finally {
            g2d.dispose();
        }
    }

    /** Ancho entre el borde y el contenido del panel. */
    @Override
    public Insets getBorderInsets(Component c) {
        final int a = cornerRadius / 4;
        return new Insets(margin.top + a, margin.left + a, margin.bottom + a, margin.right + a);
    }

    @Override
    public boolean isBorderOpaque() {
        return false;
    }

    /**
     * Borde de "tarjeta": margen exterior relleno de {@code marginBackground}
     * (el gutter entre tarjetas) + relleno redondeado con el fondo real del
     * componente + trazo fino encima. El componente que lo use debe ser
     * no-opaco (setOpaque(false)).
     */
    public static Border createCardBorder(Color marginBackground, int cornerRadius) {
        return new RoundedBorder(cornerRadius, Color.lightGray, marginBackground, new Insets(6, 8, 2, 8));
    }
}
