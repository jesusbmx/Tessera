package tessera.demo;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;
import tessera.kit.Button;
import tessera.kit.Card;
import tessera.kit.Check;
import tessera.kit.Col;
import tessera.kit.Combo;
import tessera.kit.Footer;
import tessera.kit.Form;
import tessera.kit.Grid;
import tessera.kit.Hint;
import tessera.kit.Input;
import tessera.kit.Kit;
import tessera.kit.Label;
import tessera.kit.Radio;
import tessera.kit.Row;
import tessera.kit.Switch;
import tessera.theme.Theme;

/**
 * Genera las imagenes de docs/img/ que ilustran los ejemplos de las guias. Cada imagen se genera en una
 * ejecucion aparte (el tema se instala una sola vez por proceso); el script scripts/doc-images.sh las
 * genera todas.
 *
 * <pre>
 * java ... tessera.demo.DocImages &lt;nombre&gt; &lt;archivo.png&gt;
 *
 * screen-example    guides/screens.md: pantalla armada con la convencion
 * containers        guides/screens.md: los contenedores Row, Col, Form, Grid y Card
 * theme-default     guides/customizing.md: el tema por defecto
 * theme-custom      guides/customizing.md: un tema propio (acento, fondo, esquinas)
 * arc-0 | arc-12 | arc-24     guides/customizing.md: radio de las esquinas
 * styled            guides/customizing.md: metodos encadenables de un widget
 * </pre>
 */
public final class DocImages {

  private DocImages() {
  }

  public static void main(String[] args) throws Exception {
    final String name = args[0];
    final String path = args[1];

    switch (name) {
      case "theme-custom":
        Theme.builder().accent("#0F766E").primary("#0F766E").panelBackground("#F3F4F6")
            .surface("#FFFFFF").cardLine("#D1D5DB").arc(6).install();
        break;
      case "arc-0":
        Theme.builder().accent("#1976D2").panelBackground("#F7F6F3").arc(0).install();
        break;
      case "arc-24":
        Theme.builder().accent("#1976D2").panelBackground("#F7F6F3").arc(24).install();
        break;
      default:
        Theme.builder().accent("#1976D2").panelBackground("#F7F6F3").install();
        break;
    }

    SwingUtilities.invokeAndWait(() -> save(content(name), path));
    System.exit(0);
  }

  private static JComponent content(String name) {
    switch (name) {
      case "screen-example":
        return new ScreenExample();
      case "containers":
        return containers();
      case "styled":
        return styled();
      default:
        return sample();
    }
  }

  /** Muestra con un poco de todo: sirve para comparar temas y radios de esquina. */
  private static JComponent sample() {
    final Check check = Check.of("Casilla").selected(true);
    final JProgressBar progress = new JProgressBar(0, 100);
    progress.setValue(60);
    return Col.gap(10).padding(12, 16, 12, 16).children(
        Card.create("Conexion").subtitle("Como se conecta la impresora").children(
            Row.gap(14).children(Radio.of("Driver").selected(true), Radio.of("Red"), Radio.of("USB")),
            Row.gap(10).fill(Input.text().placeholder("192.168.0.10")).children(Button.of("Buscar"))),
        Card.section("Controles").children(
            Row.gap(10).children(Combo.of("Gap", "Continuo"), check, Switch.of("Interruptor").selected(true)),
            progress,
            Row.gap(8).glue().children(Button.of("Cancelar"), Button.primary("Guardar"))));
  }

  private static JComponent styled() {
    return Col.gap(10).padding(12, 16, 12, 16).children(
        Card.section("Metodos encadenables").children(
            Label.text("Total").bold().fontSize(14).color(Kit.MUTED).tip("Suma de todo"),
            Input.text().placeholder("Nombre").columns(20).width(240),
            Row.gap(10).children(
                Button.of("Buscar").icon("tessera/img/info.svg", 16).iconGap(8),
                Button.primary("Guardar"),
                Button.of("Deshabilitado").enabled(false)),
            Hint.of("Texto de ayuda: pequeno, gris y se parte solo al ancho disponible.")));
  }

  private static JComponent containers() {
    return Col.gap(10).padding(12, 16, 12, 16).children(
        Card.section("Row.gap(10).fill(txt).children(btn)").children(
            Row.gap(10).fill(Input.text().placeholder("Ruta del archivo")).children(Button.of("Buscar"))),
        Card.section("Row.gap(8).glue().children(btnCancel, btnOk)").children(
            Row.gap(8).glue().children(Button.of("Cancelar"), Button.primary("Aceptar"))),
        Card.section("Form.create().row(\"Etiqueta\", campo)").children(
            Form.create()
                .row("Nombre", Input.text().placeholder("Nombre completo"))
                .row("Tipo", Combo.of("Gap", "Continuo", "Marca negra"))),
        Card.section("Grid.gap(6).row(celdas...)").children(
            Grid.gap(6)
                .row(Label.text("Producto").bold(), Label.text("Cantidad").bold(), Label.text("Precio").bold())
                .row(Label.text("Etiqueta"), Label.text("100"), Label.text("12.50"))
                .row(Label.text("Cinta de impresion"), Label.text("4"), Label.text("30.00"))),
        Footer.of(Button.of("Footer.of(botones...)"), Button.primary("alineados a la derecha")));
  }

  /** Pinta el componente fuera de pantalla con ese ancho y lo guarda como PNG. */
  private static void save(JComponent content, String path) {
    final JFrame frame = new JFrame();
    frame.setContentPane(content);
    frame.pack();
    final int width = Math.max(560, content.getPreferredSize().width);
    // el alto depende del ancho (textos que se parten): se calcula con el ancho final
    content.setSize(width, 10);
    content.validate();
    final Dimension size = new Dimension(width, content.getPreferredSize().height);
    content.setSize(size);
    content.validate();
    final BufferedImage image = new BufferedImage(size.width, size.height, BufferedImage.TYPE_INT_RGB);
    final java.awt.Graphics2D g = image.createGraphics();
    g.setColor(Color.WHITE);
    g.fillRect(0, 0, size.width, size.height);
    content.paint(g);
    g.dispose();
    try {
      new File(path).getAbsoluteFile().getParentFile().mkdirs();
      ImageIO.write(image, "png", new File(path));
    } catch (Exception e) {
      throw new IllegalStateException(e);
    }
  }
}
