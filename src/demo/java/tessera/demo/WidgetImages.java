package tessera.demo;

import java.awt.Color;
import java.awt.Dimension;
import javax.swing.JComponent;
import javax.swing.SwingUtilities;
import tessera.components.BaseTableModel;
import tessera.kit.Button;
import tessera.kit.Card;
import tessera.kit.Check;
import tessera.kit.Col;
import tessera.kit.Combo;
import tessera.kit.Dropdown;
import tessera.kit.Hint;
import tessera.kit.Input;
import tessera.kit.Kit;
import tessera.kit.Label;
import tessera.kit.Password;
import tessera.kit.Radio;
import tessera.kit.Row;
import tessera.kit.Scroll;
import tessera.kit.Separator;
import tessera.kit.Spinner;
import tessera.kit.Switch;
import tessera.kit.Table;
import tessera.kit.TextArea;
import tessera.kit.Toggle;
import tessera.theme.Theme;

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
      LayoutImages.save(dir, "widget-check", check(), W, 0);
      LayoutImages.save(dir, "widget-radio", radio(), W, 0);
      LayoutImages.save(dir, "widget-toggle", toggle(), W, 0);
      LayoutImages.save(dir, "widget-switch", switchWidget(), W, 0);
      LayoutImages.save(dir, "widget-input", input(), W, 0);
      LayoutImages.save(dir, "widget-password", password(), W, 0);
      LayoutImages.save(dir, "widget-textarea", textArea(), W, 0);
      LayoutImages.save(dir, "widget-combo", combo(), W, 0);
      LayoutImages.save(dir, "widget-dropdown", dropdown(), W, 0);
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
                Button.primary("Guardar").icon(Kit.icon("tessera/img/info.svg", 16, Color.WHITE)).iconGap(8))),
        Card.section(".enabled(false)  /  boton.primary()").children(
            Row.gap(10).children(
                Button.of("Deshabilitado").enabled(false),
                Button.primary("Principal deshabilitado").enabled(false),
                Button.of("Convertido").primary())));
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

  private static JComponent dropdown() {
    final Dropdown<Integer> empty = Dropdown.<Integer>of()
        .placeholder("Elegir metodo")
        .option(0, "Driver", "Imprime con el driver del sistema")
        .option(1, "Red", "Imprime por IP")
        .option(2, "USB", "Imprime directo al puerto USB");
    final Dropdown<Integer> chosen = Dropdown.<Integer>of()
        .option(0, "Driver").option(1, "Red").option(2, "USB").bold();
    chosen.setValue(1);
    final Dropdown<Integer> left = Dropdown.<Integer>of().option(0, "Driver").option(1, "Red").arrowLeft();
    left.setValue(0);
    return Col.gap(10).padding(12, 16, 12, 16).children(
        Card.section("Dropdown.<Integer>of().placeholder(texto).option(valor, texto, ayuda)").children(empty),
        Card.section("setValue(1)  .bold()  -> el boton muestra la opcion elegida").children(chosen),
        Card.section(".arrowLeft()  -> la flecha a la izquierda").children(left));
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
