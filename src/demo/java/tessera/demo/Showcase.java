package tessera.demo;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import javax.swing.DefaultListModel;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;
import tessera.kit.Button;
import tessera.kit.Card;
import tessera.kit.Check;
import tessera.kit.Col;
import tessera.kit.Combo;
import tessera.kit.Footer;
import tessera.kit.Form;
import tessera.kit.Header;
import tessera.kit.Hint;
import tessera.kit.Input;
import tessera.kit.Label;
import tessera.kit.Page;
import tessera.kit.Radio;
import tessera.kit.Row;
import tessera.kit.Scroll;
import tessera.kit.Spinner;
import tessera.kit.Switch;
import tessera.kit.Table;
import tessera.kit.Tabs;
import tessera.kit.Toggle;
import tessera.notify.Notification;
import tessera.notify.Toast;
import tessera.theme.Theme;

/**
 * Pantalla de muestra: todo lo que ofrece Tessera en una ventana.
 *
 * <pre>
 * ./build.sh --demo                 abre la ventana
 * java ... tessera.demo.Showcase --png docs/showcase.png [pestana]    la guarda como imagen
 * </pre>
 */
public final class Showcase {

  /** Pestana que se muestra al abrir (para las capturas). */
  private static int startTab;

  private Showcase() {
  }

  public static void main(String[] args) throws Exception {
    Theme.builder().accent("#1976D2").panelBackground("#F7F6F3").install();

    if (args.length >= 2 && args[0].equals("--png")) {
      startTab = args.length > 2 ? Integer.parseInt(args[2]) : 0;
      SwingUtilities.invokeAndWait(() -> save(args[1]));
      System.exit(0);
    }
    SwingUtilities.invokeLater(() -> {
      final JFrame frame = new JFrame("Tessera");
      frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      frame.setContentPane(content());
      frame.pack();
      frame.setLocationRelativeTo(null);
      frame.setVisible(true);
    });
  }

  private static void save(String path) {
    final JFrame frame = new JFrame();
    frame.setContentPane(content());
    frame.pack();
    final Dimension size = frame.getContentPane().getPreferredSize();
    frame.getContentPane().setSize(size);
    frame.getContentPane().doLayout();
    final BufferedImage image = new BufferedImage(size.width, size.height, BufferedImage.TYPE_INT_RGB);
    frame.getContentPane().paint(image.getGraphics());
    try {
      new File(path).getAbsoluteFile().getParentFile().mkdirs();
      ImageIO.write(image, "png", new File(path));
    } catch (Exception e) {
      throw new IllegalStateException(e);
    }
  }

  private static JComponent content() {
    final Tabs tabs = Tabs.create()
        .tab("Formulario", forms())
        .tab("Controles", controls())
        .tab("Avisos", notifications());
    tabs.setSelectedIndex(startTab);

    return Page.create()
        .top(Header.create("Tessera", "Kit de UI para Swing: layout, widgets, tema y avisos"))
        .center(Col.gap(0).padding(12, 16, 0, 16).fillBoth(tabs))
        .bottom(Footer.of(Button.of("Cancelar"), Button.primary("Guardar")))
        .minWidth(760)
        .build();
  }

  // ---------------------------------------------------------------------- //
  //   P E S T A N A S
  // ---------------------------------------------------------------------- //

  private static JComponent forms() {
    final Card connection = Card.create("Conexion")
        .subtitle("Como se conecta la impresora")
        .children(
            Row.gap(14).children(Radio.of("Driver").selected(true), Radio.of("Red"), Radio.of("USB")),
            Row.gap(10).fill(Input.text().placeholder("192.168.0.10")).children(Button.of("Buscar")));

    final Card paper = Card.section("Papel y calidad").children(
        Form.create()
            .row("Ancho (mm)", Spinner.integer(100, 10, 300, 1))
            .row("Tipo", Combo.of("Gap", "Continuo", "Marca negra"))
            .row("Velocidad", Combo.of("2 ips", "4 ips", "6 ips")));

    return Scroll.of(Col.gap(10).padding(8, 0, 12, 0).children(connection, paper)).noBorder();
  }

  private static JComponent controls() {
    final DefaultListModel<String> model = new DefaultListModel<>();
    model.addElement("Etiqueta de producto");
    model.addElement("Etiqueta de envio");
    model.addElement("Codigo de barras");
    final JList<String> list = new JList<>(model);

    final Table table = Table.of(new DefaultTableModel(
        new Object[][] {{"Impresora A", "192.168.0.10", "Listo"}, {"Impresora B", "USB001", "Sin papel"}},
        new Object[] {"Impresora", "Puerto", "Estado"})).rowHeight(28).singleSelection();

    return Scroll.of(Col.gap(10).padding(8, 0, 12, 0).children(
        Card.section("Botones y selectores").children(
            Row.gap(10).children(Button.of("Normal"), Button.primary("Principal"), Toggle.of("Lista"), Toggle.of("Tarjetas")),
            Row.gap(18).children(Check.of("Casilla").selected(true), Switch.of("Interruptor").selected(true), Switch.of("Apagado"))),
        Card.section("Campos").children(
            Form.create()
                .row("Nombre", Input.text().placeholder("Nombre completo"))
                .row("Correo", Input.text().placeholder("nombre@ejemplo.com")),
            Hint.of("Los textos de ayuda se parten solos al ancho disponible.")),
        Card.section("Tabla y lista").children(Scroll.of(table).height(100), Scroll.of(list).height(80))
    )).noBorder();
  }

  private static JComponent notifications() {
    final Button notify = Button.primary("Notification").onClick(() ->
        new Notification.Builder("demo.saved")
            .setTitle("Guardado")
            .setMessage("El perfil se guardo correctamente.")
            .setStyle(Notification.Style.SUCCESS)
            .setDuration(Notification.LENGTH_LONG)
            .setOnAction("Ver", n -> n.dismiss())
            .show());

    final Button error = Button.of("Error").onClick(() ->
        new Notification.Builder("demo.error")
            .setTitle("Error")
            .setMessage("No se pudo conectar con el servidor.")
            .setStyle(Notification.Style.ERROR)
            .show());

    final Button toast = Button.of("Toast").onClick(() ->
        Toast.makeToast(SwingUtilities.getWindowAncestor(error), "Copiado", Toast.Style.SUCCESS).show());

    return Col.gap(10).padding(8, 0, 12, 0).children(
        Card.section("Avisos").children(
            Hint.of("Notification: tarjetas que se apilan en la esquina de la pantalla (una por id). "
                + "Toast: mensaje breve centrado sobre la ventana."),
            Row.gap(10).children(notify, error, toast)));
  }
}
