package tessera.test;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Insets;
import java.awt.image.BufferedImage;
import javax.swing.JPanel;
import javax.swing.UIManager;
import tessera.components.RoundedBorder;
import tessera.kit.Card;
import tessera.kit.Input;
import tessera.kit.Label;
import tessera.kit.Scroll;
import tessera.theme.Theme;

/** RoundedBorder, Card y Scroll.rounded(): medidas, colores de las esquinas y quien manda sobre el relleno. */
final class BorderAndCardTests {

  private BorderAndCardTests() {
  }

  static void run(Checks t) {
    Theme.light().install();
    roundedBorder(t);
    card(t);
    scroll(t);
  }

  private static BufferedImage paintBorder(JPanel panel, int w, int h) {
    return Shots.paint(panel, w, h);
  }

  private static void roundedBorder(Checks t) {
    t.group("RoundedBorder");
    final JPanel any = new JPanel();

    // Medidas: margen + un cuarto del radio, como siempre.
    Insets i = new RoundedBorder(20).getBorderInsets(any);
    t.ok("constructor(20): espacio interior 5 por lado", i.top == 5 && i.left == 5 && i.bottom == 5 && i.right == 5);
    i = RoundedBorder.createCardBorder(Color.WHITE, 20).getBorderInsets(any);
    t.ok("tarjeta(20): 11, 13, 7, 13", i.top == 11 && i.left == 13 && i.bottom == 7 && i.right == 13);
    i = RoundedBorder.of(14).margin(6, 8, 2, 8).padding(1, 2, 3, 4).getBorderInsets(any);
    t.ok("padding propio: 7, 10, 5, 12", i.top == 7 && i.left == 10 && i.bottom == 5 && i.right == 12);

    // Cada metodo devuelve un borde nuevo.
    final RoundedBorder base = RoundedBorder.of(10);
    final RoundedBorder derived = base.margin(5).gutter(Color.RED);
    t.ok("inmutable: el original no cambia",
        base.getBorderInsets(any).top == 2 && derived.getBorderInsets(any).top == 7);

    // Pintura
    final JPanel panel = new JPanel();
    panel.setOpaque(false);
    panel.setBackground(new Color(0, 200, 0));
    panel.setBorder(RoundedBorder.of(20).line(Color.BLUE).margin(6, 8, 2, 8).gutter(Color.WHITE));
    BufferedImage img = paintBorder(panel, 100, 60);
    t.color("gutter fijo: la punta de la esquina es del color del gutter", Color.WHITE, Shots.at(img, 0, 0));
    t.color("el relleno es el fondo del componente", new Color(0, 200, 0), Shots.at(img, 50, 30));
    t.color("trazo del color pedido", Color.BLUE, Shots.at(img, 50, 6));

    panel.setBorder(RoundedBorder.of(20).margin(6, 8, 2, 8).gutter(Color.WHITE).fill(Color.YELLOW));
    t.color("fill propio", Color.YELLOW, Shots.at(paintBorder(panel, 100, 60), 50, 30));

    panel.setBorder(RoundedBorder.of(20).noLine().margin(6, 8, 2, 8).gutter(Color.WHITE));
    t.color("noLine: el borde superior es relleno, sin trazo", new Color(0, 200, 0),
        Shots.at(paintBorder(panel, 100, 60), 50, 6));

    // El trazo por omision es el del tema (no un gris fijo)
    Theme.dark().install();
    panel.setBorder(RoundedBorder.of(20).margin(6, 8, 2, 8).gutter(Color.WHITE));
    t.color("trazo por omision = Component.borderColor del tema oscuro", UIManager.getColor("Component.borderColor"),
        Shots.at(paintBorder(panel, 100, 60), 50, 6));
    Theme.light().install();

    // Margen que toma el fondo del padre al pintar
    final JPanel parent = new JPanel();
    parent.setBackground(Color.MAGENTA);
    parent.add(panel);
    panel.setBorder(RoundedBorder.createCardBorder(20));
    t.color("parentGutter: la esquina es el fondo del padre", Color.MAGENTA, Shots.at(paintBorder(panel, 100, 60), 0, 0));
    parent.setBackground(Color.ORANGE);
    t.color("parentGutter: sigue al padre si cambia", Color.ORANGE, Shots.at(paintBorder(panel, 100, 60), 0, 0));

    final JPanel alone = new JPanel();
    alone.setOpaque(false);
    alone.setBorder(RoundedBorder.createCardBorder(20));
    t.ok("sin padre: no falla y la esquina queda transparente",
        (paintBorder(alone, 100, 60).getRGB(0, 0) >>> 24) == 0);
  }

  private static Card sample() {
    return Card.create("Titulo").children(Label.text("Hola"), Scroll.of(Input.area(2)).rounded().height(50));
  }

  private static BufferedImage paintCard(Card card) {
    final JPanel root = new JPanel(new BorderLayout());
    root.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10));
    root.add(card, BorderLayout.CENTER);
    return Shots.paint(root, 300, 140);
  }

  private static void card(Checks t) {
    t.group("Card");
    Theme.dark().install();
    final Card themed = sample();
    t.color("al crearse toma el relleno del tema (Theme.palette().surface)", Theme.palette().surface, themed.getBackground());
    t.ok("no es opaca (el relleno lo pinta el borde, redondeado)", !themed.isOpaque());
    BufferedImage img = paintCard(themed);
    t.color("pinta el relleno del tema", Theme.palette().surface, Shots.at(img, 150, 25));
    t.ok("la punta de la esquina no lleva relleno", !Shots.at(img, 10, 10).equals(Theme.palette().surface));

    final Color custom = new Color(255, 240, 200);
    final Card own = sample().background(custom);
    t.ok("background(color) no la vuelve opaca", !own.isOpaque());
    img = paintCard(own);
    t.color("pinta el color que puso el dev", custom, Shots.at(img, 150, 25));
    t.ok("con color propio tambien queda redondeada", !Shots.at(img, 10, 10).equals(custom));
    Theme.light().install();
  }

  private static void scroll(Checks t) {
    t.group("Scroll");
    t.ok("rounded() pone un RoundedBorder", Scroll.of(Input.area(2)).rounded().getBorder() instanceof RoundedBorder);
    t.ok("rounded(radio) tambien", Scroll.of(Input.area(2)).rounded(14).getBorder() instanceof RoundedBorder);
    t.ok("noBorder() lo quita", Scroll.of(Input.area(2)).rounded().noBorder().getBorder() == null);
  }
}
