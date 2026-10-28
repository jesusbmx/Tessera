package tessera.demo;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.function.Function;
import javax.imageio.ImageIO;
import javax.swing.DefaultListModel;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JToolTip;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import tessera.kit.Button;
import tessera.kit.Card;
import tessera.kit.Check;
import tessera.kit.ChoiceButton;
import tessera.kit.Col;
import tessera.kit.Form;
import tessera.kit.Hint;
import tessera.kit.Input;
import tessera.kit.Kit;
import tessera.kit.Label;
import tessera.kit.Menu;
import tessera.kit.MenuButton;
import tessera.kit.MenuItem;
import tessera.kit.MultiChoiceButton;
import tessera.kit.Row;
import tessera.kit.Scroll;
import tessera.kit.TextArea;
import tessera.theme.Theme;

/**
 * Genera las imagenes de la guia docs/guides/menus.md (menus-*.png): pantallas reales con el menu abierto encima
 * del boton, armadas con el mismo codigo que muestra la guia. Salen todas en una sola ejecucion
 * (scripts/doc-images.sh):
 *
 * <pre>
 * java ... tessera.demo.MenuImages [carpeta]        (por defecto docs/img)
 * </pre>
 */
public final class MenuImages {

  private static final int DRIVER = 0;
  private static final int NETWORK = 1;
  private static final int USB = 2;

  private MenuImages() {
  }

  public static void main(String[] args) throws Exception {
    final String dir = args.length > 0 ? args[0] : "docs/img";
    Theme.builder().accent("#1976D2").panelBackground("#F7F6F3").install();

    SwingUtilities.invokeAndWait(() -> {
      overview(dir);
      choice(dir);
      choiceDisabled(dir);
      multi(dir);
      actions(dir);
      context(dir);
      dynamic(dir);
    });
    System.exit(0);
  }

  // ---------------------------------------------------------------------- //
  //   L A S   T R E S   P I E Z A S,   L A D O   A   L A D O
  // ---------------------------------------------------------------------- //

  private static void overview(String dir) {
    final ChoiceButton<Integer> choice = ChoiceButton.<Integer>of()
        .option(DRIVER, "Driver").option(NETWORK, "Red").option(USB, "USB")
        .bold();
    choice.setValue(NETWORK);

    final MultiChoiceButton<String> multi = MultiChoiceButton.<String>of("Ajustes")
        .option("residues", "Borrar residuos").checked("residues")
        .option("sameFolder", "Misma carpeta");

    final MenuButton action = MenuButton.of("Agregar")
        .item("Crear servicio", () -> { })
        .item("Importar de un archivo", () -> { });

    // el texto va arriba del boton: el menu se abre hacia abajo y no tapa nada
    final JComponent screen = Row.gap(12).padding(12, 16, 90, 16).top().fillEqual(
        Card.create("Elegir una").subtitle("ChoiceButton").children(
            Hint.of("El boton muestra la opcion elegida. getValue() devuelve su valor."), choice),
        Card.create("Marcar varias").subtitle("MultiChoiceButton").children(
            Hint.of("El texto no cambia. getValues() devuelve las marcadas."), multi),
        Card.create("Hacer algo").subtitle("MenuButton").children(
            Hint.of("No queda nada elegido: cada opcion corre su accion."), action));

    save(dir, "menus-overview", screen, 720,
        below(choice, choice.menu()), below(multi, multi.menu()), below(action, action.menu()));
  }

  // ---------------------------------------------------------------------- //
  //   1.   E L E G I R   U N A   O P C I O N
  // ---------------------------------------------------------------------- //

  /** Formulario de impresion: el selector del metodo y lo que cambia en la pantalla al elegir. */
  private static final class PrintForm {
    final Label lblHint = Label.muted("").small();
    final Label lblIp = Label.field("Direccion IP");
    final Input txtIp = Input.text().placeholder("192.168.0.10");

    final ChoiceButton<Integer> method = ChoiceButton.<Integer>of()
        .placeholder("Elegir metodo")
        .option(DRIVER, "Driver del sistema", "Imprime con el driver instalado en el sistema.")
        .option(NETWORK, "Red (IP)", "Imprime directo a la impresora por su direccion IP.")
        .option(USB, "USB directo", "Imprime directo al puerto USB, sin driver.")
        .bold().fontSize(14);

    PrintForm(int initial) {
      // se registra antes de elegir el valor inicial: onChange tambien corre al elegir por codigo
      method.onChange(() -> {
        lblHint.setText(method.getValueTip());
        lblIp.setVisible(method.is(NETWORK));
        txtIp.setVisible(method.is(NETWORK));
      });
      method.setValue(initial);
    }

    JComponent form() {
      return Form.gap(6).row("Metodo", Col.gap(2).children(method, lblHint)).row(lblIp, txtIp);
    }
  }

  /** Antes y despues de elegir: el menu abierto y, debajo, la pantalla cuando ya se eligio "Red (IP)". */
  private static void choice(String dir) {
    final PrintForm opened = new PrintForm(DRIVER);
    final PrintForm chosen = new PrintForm(NETWORK);

    // el espacio entre las tarjetas deja ver el menu abierto sin que tape la segunda
    final JComponent screen = Col.gap(84).padding(12, 16, 12, 16).children(
        Card.create("1. El usuario abre el menu y elige \"Red (IP)\"").children(opened.form()),
        Card.create("2. El boton muestra la opcion y la pantalla reacciona").children(chosen.form()));

    save(dir, "menus-choice", screen, 600, below(opened.method, opened.method.menu()));
  }

  /** Una opcion deshabilitada con su explicacion al dejar el mouse encima. */
  private static void choiceDisabled(String dir) {
    final PrintForm form = new PrintForm(NETWORK);
    form.method.optionEnabled(USB, false, "No disponible en este equipo");

    final JComponent screen = Col.gap(0).padding(12, 16, 100, 16).children(
        Card.create("Impresion").children(form.form()));

    final Overlay menu = below(form.method, form.method.menu());
    save(dir, "menus-choice-disabled", screen, 600, menu,
        tooltipNextTo(menu, form.method.menu().getComponent(2), "No disponible en este equipo"));
  }

  // ---------------------------------------------------------------------- //
  //   2.   M A R C A R   V A R I A S
  // ---------------------------------------------------------------------- //

  private static void multi(String dir) {
    final MultiChoiceButton<String> unzip = MultiChoiceButton.<String>of("Configuracion para descomprimir")
        .option("residues", "Borrar los residuos del zip").checked("residues")
        .option("sameFolder", "Extraer en la misma carpeta");

    final Check chkUnzip = Check.of("Descomprimir automaticamente").selected(true);
    chkUnzip.addItemListener(e -> unzip.setEnabled(chkUnzip.isSelected()));

    final JComponent screen = Col.gap(0).padding(12, 16, 80, 16).children(
        Card.create("Archivos ZIP").children(
            Label.muted("unzip.getValues()  ->  " + unzip.getValues()).small(),
            Row.gap(10).children(chkUnzip, unzip)));

    save(dir, "menus-multi", screen, 520, below(unzip, unzip.menu()));
  }

  // ---------------------------------------------------------------------- //
  //   3.   H A C E R   A L G O
  // ---------------------------------------------------------------------- //

  private static void actions(String dir) {
    final MenuButton more = MenuButton.of("Mas opciones")
        .item("Establecer impresora para todos", () -> { })
        .item("Exportar servicios", () -> { });

    final MenuButton add = MenuButton.of("Agregar")
        .color(Color.WHITE)
        .item("Crear servicio", () -> { })
        .separator()
        .item("Importar de la web", () -> { })
        .item("Importar de un archivo", () -> { });
    Kit.makePrimary(add);

    final JComponent screen = Col.gap(0).padding(12, 16, 110, 16).children(
        Row.gap(10).children(Label.title("Servicios")).glue().children(more, add));

    save(dir, "menus-action", screen, 520, belowRight(add, add.menu()));
  }

  // ---------------------------------------------------------------------- //
  //   4.   M E N U   S I N   B O T O N :   C L I C   D E R E C H O
  // ---------------------------------------------------------------------- //

  private static void context(String dir) {
    final DefaultListModel<String> model = new DefaultListModel<>();
    model.addElement("Etiquetas de envio");
    model.addElement("Codigos de barras");
    model.addElement("Facturas en PDF");
    final JList<String> list = new JList<>(model);
    list.setSelectedIndex(2);

    // las opciones que cambian despues (texto, habilitada) se guardan en un campo
    final MenuItem miTitle = MenuItem.title("Facturas en PDF").fontSize(15);
    final MenuItem miToggle = MenuItem.of("Deshabilitar", () -> { });
    final MenuItem miUp = MenuItem.of("Subir", () -> { });
    final MenuItem miDown = MenuItem.of("Bajar", () -> { });
    miDown.setEnabled(false);           // es la ultima de la lista

    final Menu rowMenu = Menu.create()
        .item(miTitle)
        .item("Editar", () -> { })
        .item(miToggle)
        .separator()
        .item(miUp)
        .item(miDown)
        .item("Eliminar", () -> { });

    final JComponent screen = Col.gap(0).padding(12, 16, 150, 16).children(
        Card.create("Servicios").subtitle("clic derecho sobre un servicio").children(Scroll.of(list).height(100)));

    // en la app: rowMenu.show(list, evt.getX(), evt.getY())
    save(dir, "menus-context", screen, 520, at(list, 110, 70, rowMenu));
  }

  // ---------------------------------------------------------------------- //
  //   5.   M E N U   Q U E   S E   L L E N A   A L   M O M E N T O
  // ---------------------------------------------------------------------- //

  private static void dynamic(String dir) {
    final String[] columns = {"codigo", "descripcion", "precio"};
    final TextArea txtLabel = Input.area(3);
    txtLabel.setText("^XA\n^FO50,50^FD%codigo%^FS\n^XZ");

    final MenuButton btnInsert = MenuButton.of("Insertar columna")
        .noArrow()
        .onOpen(menu -> fillColumns(menu, columns, txtLabel));

    final JComponent screen = Col.gap(0).padding(12, 16, 100, 16).children(
        Card.create("Etiqueta").children(
            Row.gap(6).children(Button.of("Elegir datos"), btnInsert),
            Scroll.of(txtLabel).height(80)));

    // en la app el menu se llena solo al hacer clic; aqui se llena a mano para dibujarlo abierto
    fillColumns(btnInsert.menu(), columns, txtLabel);
    save(dir, "menus-dynamic", screen, 520, below(btnInsert, btnInsert.menu()));
  }

  /** Vacia el menu y lo vuelve a llenar con las columnas de los datos: cada una se inserta en el texto. */
  private static void fillColumns(Menu menu, String[] columns, TextArea target) {
    menu.clear();
    for (String column : columns) {
      menu.item(MenuItem.of(column, () -> target.append("%" + column + "%")).tip("Inserta %" + column + "%"));
    }
  }

  // ---------------------------------------------------------------------- //
  //   P I N T A R   L A   P A N T A L L A   C O N   E L   M E N U   A B I E R T O
  // ---------------------------------------------------------------------- //

  /** Algo que se pinta encima de la pantalla: un menu abierto o un tooltip. La posicion se calcula al pintar. */
  private static final class Overlay {
    final JComponent component;
    private final Function<JComponent, Point> origin;

    Overlay(JComponent component, Function<JComponent, Point> origin) {
      this.component = component;
      this.origin = origin;
      component.setSize(component.getPreferredSize());
      component.doLayout();
    }

    /** Esquina de arriba a la izquierda dentro de la pantalla pintada. */
    Point origin(JComponent screen) {
      return origin.apply(screen);
    }
  }

  /** Menu abierto debajo del boton, alineado a su borde izquierdo (como lo abre Tessera). */
  private static Overlay below(Component button, Menu menu) {
    return new Overlay(menu, screen -> SwingUtilities.convertPoint(button, 0, button.getHeight(), screen));
  }

  /** Igual, alineado al borde derecho: Swing corre el menu solo cuando no cabe en la pantalla. */
  private static Overlay belowRight(Component button, Menu menu) {
    return new Overlay(menu, screen ->
        SwingUtilities.convertPoint(button, button.getWidth() - menu.getWidth(), button.getHeight(), screen));
  }

  /** Menu abierto en un punto del componente: el clic derecho. */
  private static Overlay at(Component component, int x, int y, Menu menu) {
    return new Overlay(menu, screen -> SwingUtilities.convertPoint(component, x, y, screen));
  }

  /** Tooltip debajo de una opcion de un menu abierto. */
  private static Overlay tooltipNextTo(Overlay menu, Component item, String text) {
    final JToolTip tip = new JToolTip();
    tip.setTipText(text);
    return new Overlay(tip, screen -> {
      final Point p = menu.origin(screen);
      return new Point(p.x + item.getX() + 30, p.y + item.getY() + item.getHeight() + 2);
    });
  }

  private static void save(String dir, String name, JComponent screen, int width, Overlay... overlays) {
    final JFrame frame = new JFrame();
    frame.setContentPane(screen);
    frame.pack();
    screen.setSize(width, 10);
    screen.validate();
    final Dimension size = new Dimension(width, screen.getPreferredSize().height);
    screen.setSize(size);
    screen.validate();

    // el menu abierto puede salirse por abajo de la pantalla: la imagen se alarga
    int height = size.height;
    for (Overlay overlay : overlays) {
      height = Math.max(height, overlay.origin(screen).y + overlay.component.getHeight() + 8);
    }

    final BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
    final Graphics2D g = image.createGraphics();
    g.setColor(UIManager.getColor("Panel.background"));
    g.fillRect(0, 0, width, height);
    screen.paint(g);
    for (Overlay overlay : overlays) {
      final Point p = overlay.origin(screen);
      final Graphics2D og = (Graphics2D) g.create();
      og.translate(p.x, p.y);
      overlay.component.paint(og);
      og.dispose();
    }
    g.dispose();

    try {
      final File file = new File(dir, name + ".png").getAbsoluteFile();
      file.getParentFile().mkdirs();
      ImageIO.write(image, "png", file);
      System.out.println(dir + "/" + name + ".png");
    } catch (Exception e) {
      throw new IllegalStateException(e);
    }
  }
}
