
package tessera.components;

import java.awt.BasicStroke;
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
 * Borde punteado. Se usa para dar la pista visual de "clic para elegir" en
 * campos de solo lectura que abren un selector de archivo/carpeta.
 */
public class DashedBorder implements Border {

    private final int cornerRadius;
    private final Color borderColor;
    private final float dashLength;
    private final float gapLength;

    public DashedBorder(int cornerRadius, Color borderColor) {
        this(cornerRadius, borderColor, 4f, 3f);
    }

    public DashedBorder(int cornerRadius, Color borderColor, float dashLength, float gapLength) {
        this.cornerRadius = cornerRadius;
        this.borderColor = borderColor;
        this.dashLength = dashLength;
        this.gapLength = gapLength;
    }

    @Override
    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
        final Graphics2D g2d = (Graphics2D) g.create();

        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setColor(borderColor);
        g2d.setStroke(new BasicStroke(1f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND,
                0, new float[]{dashLength, gapLength}, 0));

        final Shape roundedRectangle = new RoundRectangle2D.Float(x, y,
                width - 1, height - 1, cornerRadius, cornerRadius);

        g2d.draw(roundedRectangle);
        g2d.dispose();
    }

    @Override
    public Insets getBorderInsets(Component c) {
        return new Insets(4, 8, 4, 8);
    }

    @Override
    public boolean isBorderOpaque() {
        return false;
    }
}
