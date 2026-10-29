package tessera.demo;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;
import tessera.components.BaseTableModel;
import tessera.kit.Button;
import tessera.kit.Card;
import tessera.kit.Check;
import tessera.kit.ChoiceButton;
import tessera.kit.Col;
import tessera.kit.Combo;
import tessera.kit.Hint;
import tessera.kit.Input;
import tessera.kit.Label;
import tessera.kit.Menu;
import tessera.kit.MenuButton;
import tessera.kit.MenuItem;
import tessera.kit.MultiChoiceButton;
import tessera.kit.Password;
import tessera.kit.PopupButton;
import tessera.kit.Radio;
import tessera.kit.Row;
import tessera.kit.Scroll;
import tessera.kit.Separator;
import tessera.kit.Spinner;
import tessera.kit.Switch;
import tessera.kit.Table;
import tessera.kit.TextArea;
import tessera.kit.Toggle;
import tessera.theme.Icons;
import tessera.theme.Theme;
import tessera.theme.Variant;

/**
 * Genera las imagenes de widgets de docs/img/ (widget-*.png) que ilustran las paginas de docs/reference/kit.
 * El titulo de cada tarjeta es el codigo que la arma. Salen todas en una sola ejecucion
 * (scripts/doc-images.sh):
 *
 * <pre>
 * java ... tessera.demo.WidgetImages [carpeta]        (por defecto docs/img)
 * </pre>
 */
public final class WidgetImages {

  private static final int W = 480;

  private WidgetImages() {
  }

  public static void main(String[] args) throws Exception {
    final String dir = args.length > 0 ? args[0] : "docs/img";
    Theme.builder().accent("#1976D2").panelBackground("#F7F6F3").install();

    SwingUtilities.invokeAndWait(() -> {
      LayoutImages.save(dir, "widget-label", label(), W, 0);
      LayoutImages.save(dir, "widget-hint", hint(), W, 0);
      LayoutImages.save(dir, "widget-button", button(), W, 0);
      LayoutImages.save(dir, "widget-button-variants", buttonVariants(), W, 0);
      LayoutImages.save(dir, "widget-label-variants", labelVariants(), W, 0);
      LayoutImages.save(dir, "widget-check", check(), W, 0);
      LayoutImages.save(dir, "widget-radio", radio(), W, 0);
      LayoutImages.save(dir, "widget-toggle", toggle(), W, 0);
      LayoutImages.save(dir, "widget-switch", switchWidget(), W, 0);
      LayoutImages.save(dir, "widget-input", input(), W, 0);
      LayoutImages.save(dir, "widget-password", password(), W, 0);
      LayoutImages.save(dir, "widget-textarea", textArea(), W, 0);
      LayoutImages.save(dir, "widget-combo", combo(), W, 0);
      LayoutImages.save(dir, "widget-choicebutton", choiceButton(), W, 0);
      LayoutImages.save(dir, "widget-multichoicebutton", multiChoiceButton(), W, 0);
      LayoutImages.save(dir, "widget-menubutton", menuButton(), W, 0);
      LayoutImages.save(dir, "widget-menu", menu(), W, 0);
      LayoutImages.save(dir, "widget-spinner", spinner(), W, 0);
      LayoutImages.save(dir, "widget-table", table(), W, 0);
      LayoutImages.save(dir, "container-scroll", scroll(), W, 0);
      LayoutImages.save(dir, "container-separator", separator(), W, 0);
    });
    System.exit(0);
  }

  private static JComponent label() {
    return Col.gap(10).padding(12, 16, 12, 16).children(
        Card.section("Label.text / title / field / muted").children(
            Col.gap(4).children(
                Label.text("Label.text(\"...\")  texto normal"),
                Label.title("Label.title(\"...\")  titulo"),
                Label.field("Label.field(\"...\")  nombre de un campo"),
                Label.muted("Label.muted(\"...\")  texto secundario"))),
        Card.section(".small()  .center()  .right()").children(
            Col.gap(4)
                .fill(Label.text("Label.text(\"...\").small()").small())
                .fill(Label.text("center()").center())
                .fill(Label.text("right()").right())),
        Card.section(".icon(...).iconGap(6).onClick(...)  ->  un enlace").children(
            Label.text("Ayuda").icon("tessera/img/info.svg", 16).iconGap(6).color(Theme.accent()).onClick(() -> { })));
  }

  private static JComponent hint() {
    final String text = "Un texto de ayuda: pequeno, gris, y se parte solo en varias lineas cuando es largo.";
    return Col.gap(10).padding(12, 16, 12, 16).children(
        Card.section("Hint.of(texto)  -> se parte al ancho que reciba").children(Hint.of(text)),
        Row.gap(12).top()
            .children(Card.section("Un bloque de 180 px").children(Hint.of(text)).width(180))
            .fill(Card.section("...y uno que ocupa lo que sobra").children(Hint.of(text))));
  }

  private static JComponent button() {
    return Col.gap(10).padding(12, 16, 12, 16).children(
        Card.section("Button.of(texto)  /  Button.primary(texto)").children(
            Row.gap(10).children(Button.of("Cancelar"), Button.primary("Guardar"))),
        Card.section(".icon(recurso, tam).iconGap(8)").children(
            Row.gap(10).children(
                Button.of("Buscar").icon("tessera/img/info.svg", 16).iconGap(8),
                Button.primary("Guardar").icon(Icons.of("tessera/img/info.svg", 16, Color.WHITE)).iconGap(8))),
        Card.section(".enabled(false)  /  boton.primary()").children(
            Row.gap(10).children(
                Button.of("Deshabilitado").enabled(false),
                Button.primary("Principal deshabilitado").enabled(false),
                Button.of("Convertido").primary())));
  }

  private static JComponent buttonVariants() {
    return Col.gap(10).padding(12, 16, 12, 16).children(
        Card.section("Button.primary / accent / secondary / inverse").children(
            Row.gap(8).children(Button.primary("Primary"), Button.accent("Accent"), Button.secondary("Secondary"), Button.inverse("Inverse"))),
        Card.section("Button.success / danger / warning / info").children(
            Row.gap(8).children(Button.success("Success"), Button.danger("Danger"), Button.warning("Warning"), Button.info("Info"))),
        Card.section("Button.outline(Variant.X, texto)").children(
            Row.gap(8).children(Button.outline(Variant.PRIMARY, "Primary"), Button.outline(Variant.SUCCESS, "Success"),
                Button.outline(Variant.DANGER, "Danger"), Button.outline(Variant.WARNING, "Warning"))),
        Card.section("boton.variant(v)  /  .enabled(false)  /  Toggle.variant(v)").children(
            Row.gap(8).children(Button.of("Normal").variant(Variant.DANGER), Button.danger("Deshabilitado").enabled(false),
                Toggle.of("Lista").variant(Variant.SUCCESS).selected(true), Toggle.of("Tarjetas"))));
  }

  private static JComponent labelVariants() {
    return Col.gap(10).padding(12, 16, 12, 16).children(
        Card.section("Label.success / danger / warning / info ...").children(
            Col.gap(4).children(
                Label.success("Label.success(\"Guardado\")"),
                Label.danger("Label.danger(\"No se pudo conectar\")"),
                Label.warning("Label.warning(\"Quedan 2 dias\")"),
                Label.info("Label.info(\"Hay una version nueva\")"),
                Label.primary("Label.primary / accent / secondary / inverse"))),
        Card.section("Hint.of(texto).variant(Variant.DANGER)").children(
            Hint.of("Campo obligatorio").variant(Variant.DANGER)));
  }

  private static JComponent check() {
    return Col.gap(10).padding(12, 16, 12, 16).children(
        Card.section("Check.of(texto)  /  .selected(true)  /  .enabled(false)").children(
            Col.gap(6).children(
                Check.of("Sin marcar"),
                Check.of("Marcada").selected(true),
                Check.of("Deshabilitada").enabled(false),
                Check.of("Marcada y deshabilitada").selected(true).enabled(false))));
  }

  private static JComponent radio() {
    final Radio driver = Radio.of("Driver").selected(true);
    final Radio red = Radio.of("Red");
    final Radio usb = Radio.of("USB");
    Radio.group(driver, red, usb);
    return Col.gap(10).padding(12, 16, 12, 16).children(
        Card.section("Radio.of(texto)  +  Radio.group(a, b, c)  -> solo una marcada").children(
            Row.gap(14).children(driver, red, usb)),
        Card.section("Radio.of(texto).enabled(false)  ->  una opcion no disponible").children(
            Row.gap(14).children(Radio.of("Disponible").selected(true), Radio.of("No disponible").enabled(false))));
  }

  private static JComponent toggle() {
    final Toggle list = Toggle.of("Lista").selected(true);
    final Toggle cards = Toggle.of("Tarjetas");
    Toggle.group(list, cards);
    return Col.gap(10).padding(12, 16, 12, 16).children(
        Card.section("Toggle.of(texto)  +  Toggle.group(a, b)  -> modo de vista").children(
            Row.gap(8).children(list, cards)),
        Card.section("Toggle suelto: un filtro que se activa y desactiva").children(
            Row.gap(8).children(Toggle.of("Solo activos").selected(true), Toggle.of("Con errores"))));
  }

  private static JComponent switchWidget() {
    return Col.gap(10).padding(12, 16, 12, 16).children(
        Card.section("Switch.of(texto)  ->  un si/no que se aplica al instante").children(
            Col.gap(6).children(
                Switch.of("Iniciar con el sistema").selected(true),
                Switch.of("Apagado"),
                Switch.of("Deshabilitado").enabled(false))));
  }

  private static JComponent input() {
    final Input readOnly = Input.text().editable(false);
    readOnly.setText("Solo lectura");
    final Input typed = Input.text();
    typed.setText("Texto escrito");
    return Col.gap(10).padding(12, 16, 12, 16).children(
        Card.section("Input.text().placeholder(texto)").children(
            Input.text().placeholder("Nombre del cliente")),
        Card.section("Con texto  /  .editable(false)  /  .enabled(false)").children(
            Col.gap(6).children(typed, readOnly, Input.text().placeholder("Deshabilitado").enabled(false))),
        Card.section(".columns(12)  /  .width(120)  -> ancho propio").children(
            Row.gap(10).children(
                Input.text().placeholder("12 columnas").columns(12),
                Input.text().placeholder("120 px").width(120))));
  }

  private static JComponent password() {
    final Password typed = Input.password().columns(20);
    typed.setText("secreto123");
    return Col.gap(10).padding(12, 16, 12, 16).children(
        Card.section("Input.password().placeholder(texto)").children(
            Col.gap(6).children(
                Input.password().placeholder("Clave").columns(20),
                typed)));
  }

  private static JComponent textArea() {
    final TextArea filled = Input.area(3).wrap();
    filled.setText("Un texto largo que se parte en lineas en vez de salirse del area, porque se uso wrap().");
    return Col.gap(10).padding(12, 16, 12, 16).children(
        Card.section("Input.area(3).placeholder(texto)  -> dentro de un Scroll").children(
            Scroll.of(Input.area(3).wrap().placeholder("Escribe aqui las notas..."))),
        Card.section("Input.area(3).wrap()  -> las lineas largas se parten").children(
            Scroll.of(filled)));
  }

  private static JComponent combo() {
    final Combo<String> editable = Combo.of("203", "300", "600").editable(true);
    return Col.gap(10).padding(12, 16, 12, 16).children(
        Card.section("Combo.of(\"USB\", \"Red\", \"Archivo\")").children(
            Combo.of("USB", "Red", "Archivo")),
        Card.section(".editable(true)  -> el usuario puede escribir otro valor").children(editable),
        Card.section(".enabled(false)").children(Combo.of("Deshabilitado").enabled(false)));
  }

  /** Boton y, debajo, su menu abierto (un menu emergente no se dibuja dentro de otra ventana: se pinta aparte). */
  private static JComponent withMenu(PopupButton<?> button) {
    final Menu menu = button.menu();
    final Dimension size = menu.getPreferredSize();
    menu.setSize(size);
    menu.doLayout();
    final BufferedImage image = new BufferedImage(size.width, size.height, BufferedImage.TYPE_INT_ARGB);
    final Graphics2D g = image.createGraphics();
    menu.paint(g);
    g.dispose();
    return Col.gap(2).children(button, new JLabel(new ImageIcon(image)));
  }

  private static JComponent choiceButton() {
    final ChoiceButton<Integer> empty = ChoiceButton.<Integer>of()
        .placeholder("Elegir metodo")
        .option(0, "Driver", "Imprime con el driver del sistema")
        .option(1, "Red", "Imprime por IP")
        .option(2, "USB", "Imprime directo al puerto USB");
    final ChoiceButton<Integer> chosen = ChoiceButton.<Integer>of()
        .option(0, "Driver").option(1, "Red").option(2, "USB").bold();
    chosen.setValue(1);
    final ChoiceButton<Integer> left = ChoiceButton.<Integer>of().option(0, "Driver").option(1, "Red").arrowLeft();
    left.setValue(0);
    return Col.gap(10).padding(12, 16, 12, 16).children(
        Card.section("ChoiceButton.<Integer>of().placeholder(texto).option(valor, texto, ayuda)").children(withMenu(empty)),
        Card.section("setValue(1)  .bold()  -> el boton muestra la opcion elegida").children(withMenu(chosen)),
        Card.section(".arrowLeft()  -> la flecha a la izquierda").children(left));
  }

  private static JComponent multiChoiceButton() {
    final MultiChoiceButton<String> unzip = MultiChoiceButton.<String>of("Configuracion para descomprimir")
        .option("residues", "Borrar los residuos del zip").checked("residues")
        .option("sameFolder", "Extraer en la misma carpeta");
    return Col.gap(10).padding(12, 16, 12, 16).children(
        Card.section("MultiChoiceButton.<String>of(texto).option(valor, texto).checked(valor)").children(withMenu(unzip)),
        Card.section("getValues()  isChecked(valor)  setChecked(valor, true)  onChange(...)").children(
            Label.muted("Las marcadas: " + unzip.getValues()).small()));
  }

  private static JComponent menuButton() {
    final MenuButton add = MenuButton.of("Agregar")
        .item("Crear servicio", () -> { })
        .separator()
        .item("Importar de un archivo", () -> { });
    final MenuButton more = MenuButton.of("").icon("tessera/img/info.svg", 24)
        .tip("Mas opciones")
        .title("Mas opciones")
        .item("Exportar", () -> { });
    return Col.gap(10).padding(12, 16, 12, 16).children(
        Card.section("MenuButton.of(texto).item(texto, accion).separator()").children(withMenu(add)),
        Card.section(".icon(recurso, tam)  -> un icono en lugar de la flecha").children(withMenu(more)));
  }

  private static JComponent menu() {
    final Menu menu = Menu.create()
        .title("Servicio")
        .item("Editar", () -> { })
        .item(MenuItem.of("Duplicar", () -> { }).enabled(false))
        .separator()
        .item(MenuItem.of("Importar", () -> { }).icon("tessera/img/info.svg", 16).iconRight());
    final Dimension size = menu.getPreferredSize();
    menu.setSize(size);
    menu.doLayout();
    final BufferedImage image = new BufferedImage(size.width, size.height, BufferedImage.TYPE_INT_ARGB);
    final Graphics2D g = image.createGraphics();
    menu.paint(g);
    g.dispose();
    return Col.gap(10).padding(12, 16, 12, 16).children(
        Card.section("Menu.create().title(texto).item(texto, accion).separator()").children(new JLabel(new ImageIcon(image))),
        Card.section("menu.show(lista, x, y)  clic derecho   |   menu.showBelow(boton)").children(
            Label.muted("MenuItem.of(texto, accion).icon(recurso, tam).iconRight().enabled(false)").small()));
  }

  private static JComponent spinner() {
    return Col.gap(10).padding(12, 16, 12, 16).children(
        Card.section("Spinner.integer(valor, min, max, paso)").children(
            Row.gap(10).children(Spinner.integer(1, 1, 99, 1).width(80), Label.muted("copias (1 a 99)").small())),
        Card.section("Spinner.decimal(valor, min, max, paso)").children(
            Row.gap(10).children(Spinner.decimal(1.5, 0.0, 10.0, 0.1).width(80), Label.muted("de 0.0 a 10.0, paso 0.1").small())));
  }

  private static JComponent table() {
    final BaseTableModel.TArray model = new BaseTableModel.TArray("Impresora", "Puerto", "Estado");
    model.addRow(new Object[] {"Zebra ZD420", "192.168.0.10", "Listo"});
    model.addRow(new Object[] {"TSC TE200", "USB001", "Sin papel"});
    model.addRow(new Object[] {"Brother QL-800", "USB002", "Listo"});
    final Table table = Table.of(model).rowHeight(28).singleSelection();
    table.setRowSelectionInterval(1, 1);
    table.setPreferredScrollableViewportSize(new Dimension(300, 100));
    return Col.gap(10).padding(12, 16, 12, 16).children(
        Card.section("Scroll.of(Table.of(model).rowHeight(28).singleSelection())").children(
            Scroll.of(table)));
  }

  private static JComponent scroll() {
    final Col content = Col.gap(6);
    for (int i = 1; i <= 12; i++) {
      content.children(Check.of("Etiqueta de producto " + i));
    }
    return Col.gap(10).padding(12, 16, 12, 16).children(
        Card.section("Scroll.of(contenido).vertical()  -> solo barra vertical").children(
            Scroll.of(content).vertical().height(130)));
  }

  private static JComponent separator() {
    return Col.gap(10).padding(12, 16, 12, 16).children(
        Card.section("Separator.horizontal()  -> una linea fina entre secciones").children(
            Label.text("Seccion de arriba"),
            Separator.horizontal(),
            Label.text("Seccion de abajo")),
        Card.section("Separator.vertical().height(24)  -> entre elementos de una fila").children(
            Row.gap(10).children(
                Button.of("Cortar"), Button.of("Copiar"), Separator.vertical().height(24), Button.of("Eliminar"))));
  }
}
