
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
 * Borde de esquinas redondeadas. Se arma con metodos encadenables; cada uno devuelve un borde nuevo (el original no
 * cambia), asi que se puede reutilizar uno como base.
 *
 * <pre>
 * RoundedBorder.of(14)                                                  // solo esquinas y trazo gris claro
 * RoundedBorder.of(14).line(Color.GRAY).margin(6, 8, 2, 8).gutter(Color.WHITE)
 * RoundedBorder.of(10).line(new Color(0x1976D2)).fill(Color.WHITE)      // trazo azul, relleno propio
 * RoundedBorder.of(10).noLine()                                         // solo relleno redondeado
 * </pre>
 *
 * Partes, de afuera hacia adentro:
 * <ol>
 *   <li><b>margen</b> ({@link #margin}): espacio exterior de esquinas rectas. Si hay {@link #gutter(Color) gutter}
 *       (o {@link #parentGutter()}), se pinta de ese color: tapa las puntas cuadradas del componente y deja ver el
 *       fondo de atras, sin depender de que el padre sea de un color en particular.</li>
 *   <li><b>forma redondeada</b>: se rellena con {@link #fill(Color)}, o con el fondo ACTUAL del componente si no se
 *       indico uno (en una fila de lista cambia al seleccionarla), y se traza con {@link #line(Color)}.</li>
 *   <li><b>relleno interior</b> ({@link #padding}): espacio entre el trazo y el contenido; por omision un cuarto del
 *       radio.</li>
 * </ol>
 *
 * El componente que use este borde debe ser no-opaco ({@code setOpaque(false)}) si pinta su propio fondo: si no, el
 * relleno rectangular normal de Swing asoma en las esquinas fuera del trazo redondeado. {@code Scroll.rounded()} ya
 * se ocupa de eso.
 *
 * Antes esto se armaba como CompoundBorder(MatteBorder + RoundedBorder sin relleno); esa combinacion dejaba sin
 * pintar el pedacito entre la esquina recta del margen y la curva del trazo, y ahi se colaba el color de fondo de lo
 * que hubiera detras (ej. blanco de "List.background"). Unificar todo en un solo Border evita ese hueco.
 */
public class RoundedBorder implements Border {

    private final int radius;
    /** Color del trazo; null = sin trazo. */
    private final Color line;
    /** Relleno de la forma; null = el fondo actual del componente. */
    private final Color fill;
    private final Insets margin;
    /** Color fijo del margen; null = sin color (o el del padre si {@code parentGutter}). */
    private final Color gutter;
    /** Si el margen toma el fondo del padre cada vez que se pinta. */
    private final boolean parentGutter;
    /** Espacio entre el trazo y el contenido; null = un cuarto del radio. */
    private final Insets padding;

    /** Borde de {@code radius} px con trazo gris claro, sin margen. */
    public static RoundedBorder of(int radius) {
        return new RoundedBorder(radius, Color.lightGray, null, new Insets(0, 0, 0, 0), null, false, null);
    }

    public RoundedBorder(int radius, Color line) {
        this(radius, line, null, new Insets(0, 0, 0, 0), null, false, null);
    }

    public RoundedBorder(int radius) {
        this(radius, Color.lightGray);
    }

    public RoundedBorder() {
        this(20);
    }

    private RoundedBorder(int radius, Color line, Color fill, Insets margin, Color gutter, boolean parentGutter,
            Insets padding) {
        this.radius = Math.max(0, radius);
        this.line = line;
        this.fill = fill;
        this.margin = margin;
        this.gutter = gutter;
        this.parentGutter = parentGutter;
        this.padding = padding;
    }

    // ---------------------------------------------------------------------
    // Configuracion (cada metodo devuelve un borde nuevo)
    // ---------------------------------------------------------------------

    /** Radio de las esquinas, en px. */
    public RoundedBorder radius(int radius) {
        return new RoundedBorder(radius, line, fill, margin, gutter, parentGutter, padding);
    }

    /** Color del trazo. */
    public RoundedBorder line(Color color) {
        return new RoundedBorder(radius, color, fill, margin, gutter, parentGutter, padding);
    }

    /** Sin trazo: solo el relleno redondeado. */
    public RoundedBorder noLine() {
        return line(null);
    }

    /** Relleno de la forma redondeada. Sin esto se usa el fondo actual del componente. */
    public RoundedBorder fill(Color color) {
        return new RoundedBorder(radius, line, color, margin, gutter, parentGutter, padding);
    }

    /** Espacio exterior (arriba, izquierda, abajo, derecha), de esquinas rectas. */
    public RoundedBorder margin(int top, int left, int bottom, int right) {
        return new RoundedBorder(radius, line, fill, new Insets(top, left, bottom, right), gutter, parentGutter,
                padding);
    }

    /** Espacio exterior igual por los cuatro lados. */
    public RoundedBorder margin(int all) {
        return margin(all, all, all, all);
    }

    /** Color con que se pinta el margen (el fondo que hay detras del componente). */
    public RoundedBorder gutter(Color color) {
        return new RoundedBorder(radius, line, fill, margin, color, false, padding);
    }

    /** El margen toma el fondo del padre del componente cada vez que se pinta: no hay que indicar ningun color. */
    public RoundedBorder parentGutter() {
        return new RoundedBorder(radius, line, fill, margin, null, true, padding);
    }

    /** Espacio entre el trazo y el contenido (arriba, izquierda, abajo, derecha). Por omision, un cuarto del radio. */
    public RoundedBorder padding(int top, int left, int bottom, int right) {
        return new RoundedBorder(radius, line, fill, margin, gutter, parentGutter,
                new Insets(top, left, bottom, right));
    }

    /** Espacio interior igual por los cuatro lados. */
    public RoundedBorder padding(int all) {
        return padding(all, all, all, all);
    }

    // ---------------------------------------------------------------------
    // Border
    // ---------------------------------------------------------------------

    @Override
    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
        final Graphics2D g2d = (Graphics2D) g.create();
        try {
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // Margen exterior: rectangulo recto, sin depender de que haya detras del componente.
            final Color gutterColor = parentGutter
                    ? (c.getParent() != null ? c.getParent().getBackground() : null)
                    : gutter;
            if (gutterColor != null && (margin.top > 0 || margin.left > 0 || margin.bottom > 0 || margin.right > 0)) {
                g2d.setColor(gutterColor);
                g2d.fillRect(x, y, width, height);
            }

            final int innerX = x + margin.left;
            final int innerY = y + margin.top;
            final int innerWidth = width - margin.left - margin.right;
            final int innerHeight = height - margin.top - margin.bottom;
            final Shape shape = new RoundRectangle2D.Float(innerX, innerY, innerWidth - 1, innerHeight - 1, radius,
                    radius);

            // Relleno redondeado: el indicado o el fondo ACTUAL del componente (en una fila cambia de color
            // segun este seleccionada o no).
            final Color bg = fill != null ? fill : c.getBackground();
            if (bg != null) {
                g2d.setColor(bg);
                g2d.fill(shape);
            }

            if (line != null) {
                g2d.setColor(line);
                g2d.draw(shape);
            }
        } finally {
            g2d.dispose();
        }
    }

    /** Ancho entre el borde y el contenido del componente: margen + relleno interior. */
    @Override
    public Insets getBorderInsets(Component c) {
        final int a = radius / 4;
        final Insets p = padding != null ? padding : new Insets(a, a, a, a);
        return new Insets(margin.top + p.top, margin.left + p.left, margin.bottom + p.bottom, margin.right + p.right);
    }

    @Override
    public boolean isBorderOpaque() {
        return false;
    }

    // ---------------------------------------------------------------------
    // Preset "tarjeta"
    // ---------------------------------------------------------------------

    /**
     * Borde de "tarjeta": margen exterior (6, 8, 2, 8) relleno de {@code marginBackground} (el gutter entre tarjetas)
     * + relleno redondeado con el fondo real del componente + trazo gris claro encima. El componente que lo use debe
     * ser no-opaco (setOpaque(false)). Equivale a
     * {@code of(radius).margin(6, 8, 2, 8).gutter(marginBackground)}.
     */
    public static Border createCardBorder(Color marginBackground, int cornerRadius) {
        return of(cornerRadius).margin(6, 8, 2, 8).gutter(marginBackground);
    }

    /**
     * Igual que {@link #createCardBorder(Color, int)}, pero el margen toma el fondo del padre del componente cada
     * vez que se pinta: no hay que pasar un color ni cuidar que coincida con lo que hay detras. Equivale a
     * {@code of(radius).margin(6, 8, 2, 8).parentGutter()}.
     */
    public static Border createCardBorder(int cornerRadius) {
        return of(cornerRadius).margin(6, 8, 2, 8).parentGutter();
    }
}
