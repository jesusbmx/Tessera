package tessera.kit;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.geom.RoundRectangle2D;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.SwingConstants;
import javax.swing.border.AbstractBorder;
import tessera.theme.Icons;

/**
 * Bloque con fondo suave y borde redondeado: titulo (con icono opcional), texto de ayuda y debajo sus hijos en columna
 * (con las mismas reglas de {@link Col}).
 *
 * <pre>
 * Card.create("Conexion")
 *     .icon("app/img/print.svg")
 *     .subtitle("Como se conecta la impresora")
 *     .padding(0, 20, 10, 20)
 *     .gap(6)
 *     .children(
 *         Row.gap(14).children(rbtnPrinter, rbtnIp, rbtnUsb),
 *         Row.gap(10).fill(txtIp).children(btnScan))
 *     .build();
 * </pre>
 *
 * El borde y el relleno se ven igual en todas las pantallas; el titulo puede ser un texto o cualquier
 * componente (para uno con otro estilo, un interruptor al lado...).
 */
public class Card extends Box<Card> {

  /** Titulo y ayuda: quedan arriba aunque los hijos se agreguen antes de llamar a subtitle(). */
  private final Col header = Col.gap(2);

  private final JComponent title;
  private Hint subtitle;

  protected Card(JComponent title) {
    super(6);
    this.title = title;
    padding(10, 12, 12, 12);
    setBorder(new CardBorder());
    if (title != null) {
      header.children(title);
    }
    put(header, "growx");
  }

  /** Bloque con un titulo de texto (negrita, un poco mas grande que el texto de los campos). */
  public static Card create(String title) {
    final Label label = Label.text(title);
    label.setFont(label.getFont().deriveFont(java.awt.Font.BOLD, label.getFont().getSize2D() + 3f));
    label.setIconTextGap(Kit.px(8));
    return new Card(label);
  }

  /**
   * Bloque compacto para agrupar campos: titulo en negrita del tamano del texto (sin icono ni ayuda).
   * Reemplaza a los bordes con titulo de Swing.
   */
  public static Card section(String title) {
    final Label label = Label.text(title);
    label.setFont(label.getFont().deriveFont(java.awt.Font.BOLD));
    return new Card(label).padding(8, 12, 12, 12);
  }

  /** Bloque con un titulo propio (cualquier componente). */
  public static Card create(JComponent title) {
    return new Card(title);
  }

  /** Bloque sin titulo. */
  public static Card create() {
    return new Card(null);
  }

  /** Espacio (px) entre los hijos y entre el encabezado y el primer hijo (6 por defecto). */
  public Card gap(int gap) {
    setGap(gap);
    return this;
  }

  /** Icono al lado del titulo (solo si el titulo es de texto); SVG de 24 px. */
  public Card icon(String resource) {
    return icon(Icons.of(resource, 24));
  }

  public Card icon(Icon icon) {
    if (title instanceof Label) {
      ((Label) title).setIcon(icon);
      ((Label) title).setHorizontalTextPosition(SwingConstants.TRAILING);
    }
    return this;
  }

  /** Texto de ayuda debajo del titulo (se parte en lineas al ancho disponible). */
  public Card subtitle(String text) {
    if (subtitle != null) {
      header.remove(subtitle);
    }
    subtitle = text == null ? null : Hint.of(text);
    if (subtitle != null) {
      header.children(subtitle);
    }
    return this;
  }

  /** Titulo de la tarjeta (para cambiarlo despues de armada). */
  public JComponent title() {
    return title;
  }

  @Override
  protected String migLayout() {
    return migInsets() + ", wrap 1, gapy " + Kit.px(gap()) + "px, hidemode 3, novisualpadding";
  }

  @Override
  protected String columns() {
    return "[grow]";
  }

  @Override
  protected void place(Component child) {
    put(child, Col.constraints(child));
  }

  /** Fondo y borde de la tarjeta, pintados antes que los hijos. */
  private static final class CardBorder extends AbstractBorder {

    private static final int ARC = 12;

    @Override
    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
      final Graphics2D g2 = (Graphics2D) g.create();
      g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
      final float arc = Kit.px(ARC);
      g2.setColor(Kit.SURFACE);
      g2.fill(new RoundRectangle2D.Float(x, y, width - 1, height - 1, arc, arc));
      g2.setColor(Kit.CARD_LINE);
      g2.draw(new RoundRectangle2D.Float(x + 0.5f, y + 0.5f, width - 1.5f, height - 1.5f, arc, arc));
      g2.dispose();
    }

    @Override
    public Insets getBorderInsets(Component c) {
      return new Insets(1, 1, 1, 1);
    }
  }
}
