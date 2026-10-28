package tessera.demo;

import java.awt.Dimension;
import javax.swing.JComponent;
import javax.swing.SwingUtilities;
import tessera.components.BaseTableModel;
import tessera.components.ComboItem;
import tessera.kit.Button;
import tessera.kit.Card;
import tessera.kit.Check;
import tessera.kit.ChoiceButton;
import tessera.kit.Col;
import tessera.kit.Combo;
import tessera.kit.Footer;
import tessera.kit.Form;
import tessera.kit.Hint;
import tessera.kit.Input;
import tessera.kit.Label;
import tessera.kit.MenuButton;
import tessera.kit.MultiChoiceButton;
import tessera.kit.Password;
import tessera.kit.Radio;
import tessera.kit.Row;
import tessera.kit.Scroll;
import tessera.kit.Spinner;
import tessera.kit.Switch;
import tessera.kit.Table;
import tessera.kit.TextArea;
import tessera.kit.Toggle;
import tessera.theme.Theme;

/**
 * Los ejemplos de las paginas de widgets (docs/reference/kit/widgets) y de la guia docs/guides/widgets.md: cada uno es una pantalla
 * pequena y real, y el codigo entre las marcas {@code // ejemplo:nombre} y {@code // fin} es el que muestra la documentacion
 * (por eso compila contra la libreria). Genera las imagenes example-*.png y widgets-*.png (scripts/doc-images.sh):
 *
 * <pre>
 * java ... tessera.demo.WidgetExamples [carpeta]        (por defecto docs/img)
 * </pre>
 */
public final class WidgetExamples {

  private static final int W = 520;

  private WidgetExamples() {
  }

  public static void main(String[] args) throws Exception {
    final String dir = args.length > 0 ? args[0] : "docs/img";
    Theme.builder().accent("#1976D2").panelBackground("#F7F6F3").install();

    SwingUtilities.invokeAndWait(() -> {
      LayoutImages.save(dir, "example-label", label(), W, 0);
      LayoutImages.save(dir, "example-hint", hint(), W, 0);
      LayoutImages.save(dir, "example-button", button(), W, 0);
      LayoutImages.save(dir, "example-check", check(), W, 0);
      LayoutImages.save(dir, "example-radio", radio(), W, 0);
      LayoutImages.save(dir, "example-toggle", toggle(), W, 0);
      LayoutImages.save(dir, "example-switch", switchExample(), W, 0);
      LayoutImages.save(dir, "example-input", input(), W, 0);
      LayoutImages.save(dir, "example-password", password(), W, 0);
      LayoutImages.save(dir, "example-textarea", textArea(), W, 0);
      LayoutImages.save(dir, "example-combo", combo(), W, 0);
      LayoutImages.save(dir, "example-spinner", spinner(), W, 0);
      LayoutImages.save(dir, "example-table", table(), W, 0);

      LayoutImages.save(dir, "widgets-overview", overview(), 760, 0);
      LayoutImages.save(dir, "widgets-yesno", yesNo(), 760, 0);
      LayoutImages.save(dir, "widgets-pickone", pickOne(), 760, 0);
      LayoutImages.save(dir, "widgets-write", write(), 760, 0);
    });
    System.exit(0);
  }

  /** Margen de la pantalla de muestra. */
  private static JComponent screen(JComponent... content) {
    return Col.gap(10).padding(12, 16, 12, 16).children(content);
  }

  // ---------------------------------------------------------------------- //
  //   U N O   P O R   W I D G E T
  // ---------------------------------------------------------------------- //

  private static JComponent label() {
    // ejemplo:label
    final Card profile = Card.create().children(
        Label.title("Perfil de usuario"),
        Label.muted("Estos datos aparecen en tus documentos.").small(),
        Form.create().row("Nombre", Input.text().placeholder("Nombre completo")),
        Label.text("Ver ayuda").icon("tessera/img/info.svg", 16).iconGap(6).color(Theme.accent())
            .onClick(() -> { }));                   // un enlace: cursor de mano y una accion
    // fin
    return screen(profile);
  }

  private static JComponent hint() {
    // ejemplo:hint
    final Card address = Card.create("Direccion de la impresora").children(
        Input.text().placeholder("192.168.0.10"),
        Hint.of("Escribe la IP que aparece en la etiqueta de configuracion de la impresora. "
            + "Si no la conoces, imprime esa etiqueta manteniendo presionado el boton de avance."));
    // fin
    return screen(address);
  }

  private static JComponent button() {
    // ejemplo:button
    final Card connection = Card.create("Conexion").children(
        Row.gap(8).fill(Input.text().placeholder("192.168.0.10")).children(
            Button.of("Buscar").icon("tessera/img/info.svg", 16).iconGap(8)));

    final Row footer = Footer.of(
        Button.of("Cancelar").onClick(() -> { }),
        Button.primary("Guardar").onClick(() -> { }));          // un solo primary por pantalla: el ultimo de la derecha
    // fin
    return screen(connection, footer);
  }

  private static JComponent check() {
    // ejemplo:check
    final Spinner spnCopies = Spinner.integer(2, 1, 99, 1).width(80);
    final Check chkCopies = Check.of("Imprimir varias copias").selected(true);
    chkCopies.addItemListener(e -> spnCopies.setEnabled(chkCopies.isSelected()));
    chkCopies.setSelected(false);       // por codigo: el ItemListener corre y el Spinner se deshabilita (onClick no)

    final Card options = Card.create("Opciones de impresion").children(
        Check.of("Aceleracion por hardware").selected(true),
        Check.of("Imprimir en color"),
        Row.gap(10).children(chkCopies, spnCopies));
    // fin
    return screen(options);
  }

  private static JComponent radio() {
    // ejemplo:radio
    final Radio rbtnDriver = Radio.of("Driver del sistema").selected(true);
    final Radio rbtnIp = Radio.of("Red (IP)");
    final Radio rbtnUsb = Radio.of("USB directo");
    Radio.group(rbtnDriver, rbtnIp, rbtnUsb);                   // solo una marcada a la vez

    final Input txtIp = Input.text().placeholder("192.168.0.10");
    rbtnIp.addItemListener(e -> txtIp.setVisible(rbtnIp.isSelected()));
    rbtnIp.setSelected(true);

    final Card connection = Card.create("Conexion").children(
        Row.gap(14).children(rbtnDriver, rbtnIp, rbtnUsb), txtIp);
    // fin
    return screen(connection);
  }

  private static JComponent toggle() {
    // ejemplo:toggle
    final Toggle tglList = Toggle.of("Lista").selected(true);
    final Toggle tglCards = Toggle.of("Tarjetas");
    Toggle.group(tglList, tglCards);                            // modos excluyentes: al presionar uno se suelta el otro

    final Card view = Card.create("Vista y filtros").children(
        Row.gap(6).children(tglList, tglCards),
        Row.gap(6).children(Toggle.of("Solo activos").selected(true), Toggle.of("Con errores")));   // filtros independientes
    // fin
    return screen(view);
  }

  private static JComponent switchExample() {
    // ejemplo:switch
    final Card settings = Card.create("Ajustes").children(
        Switch.of("Iniciar con el sistema").selected(true).onClick(() -> { }),
        Switch.of("Mostrar avisos").selected(true),
        Switch.of("Modo oscuro"),
        Switch.of("Sincronizar con la nube (requiere cuenta)").enabled(false));
    // fin
    return screen(settings);
  }

  private static JComponent input() {
    // ejemplo:input
    final Label lblCount = Label.muted("").small();
    final Input txtName = Input.text().placeholder("Nombre del servicio");
    txtName.onChange(() -> lblCount.setText(txtName.getText().length() + " caracteres"));
    txtName.setText("Etiquetas de envio");                      // onChange tambien corre con setText

    final Card service = Card.create("Servicio").children(
        Form.gap(6)
            .row("Nombre", Col.gap(2).children(txtName, lblCount))     // el contador va justo debajo del campo
            .row("Carpeta", Input.text().editable(false)));
    // fin
    return screen(service);
  }

  private static JComponent password() {
    // ejemplo:password
    final Input txtUser = Input.text().placeholder("Usuario");
    final Password txtPass = Input.password().placeholder("Clave");
    txtPass.onEnter(e -> { });                                  // Enter en la clave entra, sin buscar el boton
    txtPass.setText("secreto123");

    final Card login = Card.create("Iniciar sesion").children(
        Form.gap(8).row("Usuario", txtUser).row("Clave", txtPass));
    // la clave se lee con new String(txtPass.getPassword()), no con getText()
    // fin
    return screen(login);
  }

  private static JComponent textArea() {
    // ejemplo:textarea
    final TextArea txtNotes = Input.area(4).wrap().placeholder("Escribe aqui las notas...");
    txtNotes.setText("Las etiquetas se imprimen al llegar el archivo a la carpeta. "
        + "Revisar el papel antes de arrancar el servicio.");

    final Card notes = Card.create("Servicio").children(
        Form.gap(6).rowTop("Notas", Scroll.of(txtNotes)));      // rowTop: la etiqueta queda arriba
    // fin
    return screen(notes);
  }

  private static JComponent combo() {
    // ejemplo:combo
    final Combo<ComboItem<Integer>> cbPaper = Combo.empty();
    ComboItem.setupModel(cbPaper, ComboItem.itemsOf(
        new ComboItem<>("Etiqueta 4 x 6 in", 0),
        new ComboItem<>("Etiqueta 2 x 1 in", 1),
        new ComboItem<>("Papel continuo", 2)));
    final Label lblValue = Label.muted("").small();
    cbPaper.addActionListener(e -> lblValue.setText("getSelectedValue()  ->  " + ComboItem.getSelectedValue(cbPaper)));
    cbPaper.setSelectedIndex(1);                                // el combo muestra el texto; el codigo recibe el valor

    final Card paper = Card.create("Papel").children(Form.gap(6).row("Tipo", Col.gap(2).children(cbPaper, lblValue)));
    // fin
    return screen(paper);
  }

  private static JComponent spinner() {
    // ejemplo:spinner
    final Label lblTotal = Label.muted("").small();
    final Spinner spnCopies = Spinner.integer(2, 1, 99, 1).width(80);
    spnCopies.onChange(() -> lblTotal.setText("Se imprimen " + spnCopies.getValue() + " etiquetas"));
    spnCopies.setValue(3);

    final Card paper = Card.create("Papel").children(
        Form.gap(6)
            .row("Copias", Col.gap(2).children(spnCopies, lblTotal))
            .row("Ancho (mm)", Spinner.decimal(101.6, 10.0, 300.0, 0.1).width(80)));
    // fin
    return screen(paper);
  }

  private static JComponent table() {
    // ejemplo:table
    final BaseTableModel.TArray model = new BaseTableModel.TArray("Impresora", "Puerto", "Estado");
    model.addRow(new Object[] {"Zebra ZD420", "192.168.0.10", "Listo"});
    model.addRow(new Object[] {"TSC TE200", "USB001", "Sin papel"});
    model.addRow(new Object[] {"Brother QL-800", "USB002", "Listo"});

    final Label lblSelected = Label.muted("Selecciona una impresora").small();
    final Table table = Table.of(model).rowHeight(28).singleSelection();
    table.onSelect(() -> {
      final int row = table.getSelectedRow();                   // -1 si no hay fila seleccionada
      lblSelected.setText(row < 0 ? "Selecciona una impresora" : "Seleccionada: " + model.getValueAt(row, 0));
    });

    final Card printers = Card.create("Impresoras").children(Scroll.of(table), lblSelected);   // siempre dentro de un Scroll
    // fin
    table.setPreferredScrollableViewportSize(new Dimension(300, 100));
    table.setRowSelectionInterval(1, 1);
    return screen(printers);
  }

  // ---------------------------------------------------------------------- //
  //   L A   G U I A :   C U A L   U S O
  // ---------------------------------------------------------------------- //

  /** Una tarjeta de la guia: que se quiere hacer, el widget y en que casos. */
  private static Card pick(String need, String widget, String when, JComponent... content) {
    final Card card = Card.create(need).subtitle(widget);
    card.children(Hint.of(when));
    card.children(content);
    return card;
  }

  private static JComponent overview() {
    final Card text = pick("Mostrar texto", "Label · Hint", "Titulos, nombres de campo y ayudas.",
        Label.title("Titulo"), Label.field("Nombre del campo"), Label.muted("Texto secundario").small());

    final Card toggles = pick("Activar o desactivar", "Check · Switch · Toggle", "Un si/no, una opcion o un modo.",
        Check.of("Casilla").selected(true), Switch.of("Interruptor").selected(true),
        Row.gap(4).children(Toggle.of("Lista").selected(true), Toggle.of("Tarjetas")));

    final Radio rbA = Radio.of("Driver").selected(true);
    final Radio rbB = Radio.of("Red");
    Radio.group(rbA, rbB);
    final ChoiceButton<Integer> choice = ChoiceButton.<Integer>of().option(0, "Con ayuda").option(1, "Menos espacio");
    choice.setValue(0);
    final Card one = pick("Elegir una", "Radio · Combo · ChoiceButton", "Pocas visibles, muchas o con ayuda.",
        Row.gap(12).children(rbA, rbB), Combo.of("Gap", "Continuo", "Marca negra"), choice);

    final Card many = pick("Elegir varias", "Check · MultiChoiceButton", "Casillas visibles o dentro de un boton.",
        Check.of("Etiquetas").selected(true), Check.of("Facturas"),
        MultiChoiceButton.<String>of("Ajustes").option("a", "Borrar residuos").option("b", "Misma carpeta"));

    final Card write = pick("Escribir", "Input · Password · TextArea · Spinner", "Una linea, clave, varias lineas o un numero.",
        Input.text().placeholder("Nombre"), Input.password().placeholder("Clave"), Spinner.integer(2, 1, 99, 1).width(80));

    final Card run = pick("Ejecutar", "Button · MenuButton", "Una accion, o varias en un menu.",
        Row.gap(8).children(Button.of("Cancelar"), Button.primary("Guardar")),
        MenuButton.of("Agregar").item("Crear servicio", () -> { }));

    return screen(
        Row.gap(10).top().fillEqual(text, toggles, one),
        Row.gap(10).top().fillEqual(many, write, run));
  }

  private static JComponent yesNo() {
    final Check chk = Check.of("Imprimir en color").selected(true);
    final Card check = pick("Se confirma despues", "Check", "Se guarda al presionar Guardar: la pantalla puede cancelarse.",
        chk, Check.of("Aceleracion por hardware"), Button.primary("Guardar"));

    final Card sw = pick("Se aplica al instante", "Switch", "No hay Guardar: el cambio ya esta hecho al moverlo.",
        Switch.of("Iniciar con el sistema").selected(true), Switch.of("Mostrar avisos"));

    final Toggle list = Toggle.of("Lista").selected(true);
    final Toggle cards = Toggle.of("Tarjetas");
    Toggle.group(list, cards);
    final Card tg = pick("Cambia una vista o un filtro", "Toggle", "Queda presionado mientras esta activo.",
        Row.gap(4).children(list, cards), Toggle.of("Solo activos").selected(true));

    return screen(Row.gap(10).top().fillEqual(check, sw, tg));
  }

  private static JComponent pickOne() {
    final Radio a = Radio.of("Driver").selected(true);
    final Radio b = Radio.of("Red (IP)");
    final Radio c = Radio.of("USB");
    Radio.group(a, b, c);
    final Card radio = pick("2 a 5 opciones siempre a la vista", "Radio", "El usuario ve todas sin hacer clic.", a, b, c);

    final Card combo = pick("Muchas opciones, o que puede escribir", "Combo", "Una lista desplegable (JComboBox).",
        Combo.of("Etiqueta 4 x 6 in", "Etiqueta 2 x 1 in", "Papel continuo"));

    final ChoiceButton<Integer> choice = ChoiceButton.<Integer>of().option(0, "Driver").option(1, "Red (IP)").option(2, "USB").bold();
    choice.setValue(1);
    final Card cb = pick("Pocas, con ayuda en cada una", "ChoiceButton", "Un boton que abre un menu: ocupa poco espacio.", choice);

    final Toggle list = Toggle.of("Lista").selected(true);
    final Toggle cards = Toggle.of("Tarjetas");
    Toggle.group(list, cards);
    final Card tg = pick("Un modo de vista", "Toggle.group", "Botones que quedan presionados.", Row.gap(4).children(list, cards));

    return screen(
        Row.gap(10).top().fillEqual(radio, combo),
        Row.gap(10).top().fillEqual(cb, tg));
  }

  private static JComponent write() {
    final Card input = pick("Una linea de texto", "Input", "Nombre, ruta, direccion.", Input.text().placeholder("Nombre del servicio"));

    final Password pass = Input.password().placeholder("Clave");
    pass.setText("secreto");
    final Card password = pick("Una clave", "Password", "El contenido no se ve; se lee con getPassword().", pass);

    final TextArea area = Input.area(2).wrap().placeholder("Notas...");
    final Card textArea = pick("Varias lineas", "TextArea", "Va dentro de un Scroll.", Scroll.of(area));

    final Card spinner = pick("Un numero acotado", "Spinner", "Copias, ancho, reintentos: con minimo y maximo.",
        Spinner.integer(2, 1, 99, 1).width(80));

    return screen(
        Row.gap(10).top().fillEqual(input, password),
        Row.gap(10).top().fillEqual(textArea, spinner));
  }
}
