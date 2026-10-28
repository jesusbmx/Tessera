package tessera.demo;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import tessera.components.BaseTableModel;
import tessera.components.DashedBorder;
import tessera.kit.Button;
import tessera.kit.Card;
import tessera.kit.Check;
import tessera.kit.Col;
import tessera.kit.Combo;
import tessera.kit.Footer;
import tessera.kit.Form;
import tessera.kit.Grid;
import tessera.kit.Header;
import tessera.kit.Hint;
import tessera.kit.Input;
import tessera.kit.Kit;
import tessera.kit.Label;
import tessera.kit.Page;
import tessera.kit.Pages;
import tessera.kit.Password;
import tessera.kit.Radio;
import tessera.kit.Row;
import tessera.kit.Scroll;
import tessera.kit.Spinner;
import tessera.kit.Split;
import tessera.kit.Stack;
import tessera.kit.Switch;
import tessera.kit.Table;
import tessera.kit.Tabs;
import tessera.kit.TextArea;
import tessera.theme.Theme;

/**
 * Genera las imagenes de la guia visual de disposiciones de docs/guides/screens.md (docs/img/layout-*.png y
 * docs/img/app-*.png). Cada metodo devuelve la expresion tal como aparece en la guia: si cambia uno, se cambia el otro.
 * Todas comparten el mismo tema, asi que salen en una sola ejecucion; scripts/doc-images.sh la lanza.
 *
 * <pre>
 * java ... tessera.demo.LayoutImages [carpeta]        (por defecto docs/img)
 * </pre>
 */
public final class LayoutImages {

  /** Ancho de las imagenes de piezas sueltas. */
  private static final int W = 560;

  private LayoutImages() {
  }

  public static void main(String[] args) throws Exception {
    final String dir = args.length > 0 ? args[0] : "docs/img";
    Theme.builder().accent("#1976D2").panelBackground("#F7F6F3").install();

    SwingUtilities.invokeAndWait(() -> {
      // piezas
      save(dir, "layout-row-fill", rowFill(), W, 0);
      save(dir, "layout-row-align", rowAlign(), W, 0);
      save(dir, "layout-col-rules", colRules(), W, 0);
      save(dir, "layout-col-fill", colFill(), W, 0);
      save(dir, "layout-form", formVariants(), W, 0);
      save(dir, "layout-form-labels", formLabels(), W, 0);
      save(dir, "layout-grid", gridColumns(), W, 0);
      save(dir, "layout-card", cardVariants(), W, 0);
      save(dir, "layout-header", headerVariants(), W, 0);
      save(dir, "layout-footer", footerVariants(), W, 0);
      save(dir, "layout-stack", stackAlternatives(), W, 0);
      save(dir, "layout-stack-align", stackAlign(), W, 0);
      save(dir, "layout-stack-patterns", stackPatterns(), W, 0);
      save(dir, "layout-page", pageRegions(), W, 300);
      save(dir, "layout-tabs-split", tabsAndSplit(), W, 0);
      save(dir, "layout-gap-padding", gapAndPadding(), W, 0);
      save(dir, "layout-visibility", visibility(), W, 0);
      // aplicaciones completas
      save(dir, "app-toolbar-table", appToolbarTable(), 680, 380);
      save(dir, "app-master-detail", appMasterDetail(), 680, 360);
      save(dir, "app-sidebar", appSidebar(), 700, 380);
      save(dir, "app-wizard", appWizard(), 560, 360);
      save(dir, "app-dashboard", appDashboard(), 700, 0);
      save(dir, "app-two-columns", appTwoColumns(), 680, 0);
      save(dir, "app-login-centered", appLoginCentered(), 560, 340);
      save(dir, "app-editor-console", appEditorConsole(), 680, 420);
      save(dir, "app-settings-tabs", appSettingsTabs(), 640, 380);
    });
    System.exit(0);
  }

  // ------------------------------------------------------------------------ //
  //   A y u d a s   s o l o   p a r a   i l u s t r a r
  // ------------------------------------------------------------------------ //

  /** Bloque de color con texto: sirve para dibujar "una zona" sin que importe su contenido. */
  private static Label zone(String text) {
    return zone(text, 8, 14);
  }

  private static Label zone(String text, int vertical, int horizontal) {
    return zone(text, new Color(0xDBEAFE), vertical, horizontal);
  }

  private static Label zone(String text, Color color, int vertical, int horizontal) {
    final Label label = Label.text(text).center().background(color);
    label.setBorder(BorderFactory.createCompoundBorder(
        BorderFactory.createLineBorder(color.darker()),
        BorderFactory.createEmptyBorder(Kit.px(vertical), Kit.px(horizontal), Kit.px(vertical), Kit.px(horizontal))));
    return label;
  }

  /** Dibuja con linea punteada el borde de un contenedor, para ver hasta donde llega. */
  private static <T extends JComponent> T outline(T container) {
    container.setBorder(new DashedBorder(8, new Color(0x94A3B8)));
    return container;
  }

  /** Un titulo pequeno encima del componente (el componente ocupa el resto). */
  private static Col captioned(String caption, JComponent component) {
    return Col.gap(4).children(Label.muted(caption).small()).fillBoth(component);
  }

  // ------------------------------------------------------------------------ //
  //   P I E Z A S
  // ------------------------------------------------------------------------ //

  private static JComponent rowFill() {
    return Col.gap(10).padding(12, 16, 12, 16).children(
        Card.section("Row.gap(10).children(a, b, c)").children(
            Row.gap(10).children(Button.of("Uno"), Button.of("Dos"), Button.of("Tres"))),
        Card.section("Row.gap(10).fill(campo).children(boton)").children(
            Row.gap(10).fill(Input.text().placeholder("Ruta del archivo")).children(Button.of("Buscar"))),
        Card.section("Row.gap(10).fill(campo1, campo2)").children(
            Row.gap(10).fill(Input.text().placeholder("Nombre"), Input.text().placeholder("Apellido"))),
        Card.section("Row.gap(8).children(info).glue().children(botones)").children(
            Row.gap(8).children(Label.muted("3 cambios sin guardar"))
                .glue()
                .children(Button.of("Cancelar"), Button.primary("Guardar"))),
        Card.section("Row.gap(10).fillEqual(a, b)").children(
            Row.gap(10).fillEqual(zone("A (corto)"), zone("B (un texto bastante mas largo)"))));
  }

  private static JComponent rowAlign() {
    return Col.gap(10).padding(12, 16, 12, 16).children(
        Card.section("Row.gap(10).children(...)  -> centrados (por defecto)").children(
            Row.gap(10).children(
                Label.field("Notas"), Scroll.of(Input.area(3).wrap()).width(260), Button.of("Guardar"))),
        Card.section("Row.gap(10).top().children(...)  -> alineados arriba").children(
            Row.gap(10).top().children(
                Label.field("Notas"), Scroll.of(Input.area(3).wrap()).width(260), Button.of("Guardar"))));
  }

  private static JComponent colRules() {
    return Col.gap(10).padding(12, 16, 12, 16).children(
        Row.gap(12).fillEqual(
            Card.section("Col.gap(6).children(...)").children(
                Col.gap(6).children(
                    Label.text("Etiqueta: ancho natural"),
                    Button.of("Boton: ancho natural"),
                    Input.text().placeholder("Campo: todo el ancho"),
                    Combo.of("Combo: todo el ancho"))),
            Card.section("Col.gap(6).fill(boton).start(campo)").children(
                Col.gap(6)
                    .fill(Button.of("fill: el boton ocupa todo el ancho"))
                    .start(Input.text().placeholder("start").columns(10)))));
  }

  private static JComponent colFill() {
    return Col.gap(10).padding(12, 16, 12, 16).children(
        Row.gap(12).fillEqual(
            captioned("Col.children(...).glue().children(boton)",
                outline(Col.gap(8).padding(8)
                    .children(Label.title("Titulo"), Input.text().placeholder("Campo"))
                    .glue()
                    .children(Button.primary("Pegado abajo"))).height(190)),
            captioned("Col.children(campo).fillBoth(scroll)",
                outline(Col.gap(8).padding(8)
                    .children(Input.text().placeholder("Buscar"))
                    .fillBoth(Scroll.of(Input.area(4)))).height(190))));
  }

  private static JComponent formVariants() {
    return Col.gap(10).padding(12, 16, 12, 16).children(
        Card.section("Form.create().row(...).rowTop(...).wide(...)").children(
            Form.create()
                .row("Nombre", Input.text().placeholder("Nombre completo"))
                .row("Carpeta", Row.gap(8).fill(Input.text().placeholder("Carpeta de salida")).children(Button.of("...")))
                .row("Tipo", Combo.of("Gap", "Continuo"))
                .row("Copias", Spinner.integer(1, 1, 99, 1).width(80))
                .rowTop("Notas", Scroll.of(Input.area(3).wrap()))
                .wide(Check.of("Imprimir al guardar"), 6)
                .wide(Hint.of("Lo que ocupa las dos columnas, como esta ayuda, se pone con wide(...)."))));
  }

  private static JComponent formLabels() {
    return Col.gap(10).padding(12, 16, 12, 16).children(
        Row.gap(12).fillEqual(
            Card.section("Form.create()").children(
                Form.create()
                    .row("Nombre", Input.text().placeholder("Nombre"))
                    .row("Direccion", Input.text().placeholder("Direccion"))
                    .row("Ciudad", Input.text().placeholder("Ciudad"))),
            Card.section("Form.create().labelsRight()").children(
                Form.create().labelsRight()
                    .row("Nombre", Input.text().placeholder("Nombre"))
                    .row("Direccion", Input.text().placeholder("Direccion"))
                    .row("Ciudad", Input.text().placeholder("Ciudad")))),
        Card.section("Form.gap(4).labelGap(30)  -> menos espacio entre filas, mas entre etiqueta y campo").children(
            Form.gap(4).labelGap(30)
                .row("Usuario", Input.text().placeholder("Usuario"))
                .row("Correo", Input.text().placeholder("Correo"))));
  }

  private static JComponent gridColumns() {
    return Col.gap(10).padding(12, 16, 12, 16).children(
        Card.section("Grid.gap(6).row(celdas...)  -> cada columna mide lo que su celda mas ancha").children(
            Grid.gap(6)
                .row(Label.field("Producto"), Label.field("Cantidad"), Label.field("Precio"))
                .row(Label.text("Etiqueta"), Label.text("100"), Label.text("12.50"))
                .row(Label.text("Cinta de impresion"), Label.text("4"), Label.text("30.00"))),
        Card.section("Grid.gap(8).columnGap(1, 40)  -> dos pares etiqueta / campo por fila").children(
            Grid.gap(8).columnGap(1, 40)
                .row(Label.field("DPI"), Combo.of("203", "300").width(120),
                    Label.field("Ancho"), Spinner.integer(100, 10, 200, 1).width(80))
                .row(Label.field("Tipo"), Combo.of("Gap", "Continuo").width(120),
                    Label.field("Velocidad"), Combo.of("2 ips", "4 ips").width(80))));
  }

  private static JComponent cardVariants() {
    return Col.gap(10).padding(12, 16, 12, 16).children(
        Card.create("Card.create(titulo).icon(..).subtitle(..)")
            .icon("tessera/img/info.svg")
            .subtitle("Un icono junto al titulo y un texto de ayuda debajo.")
            .children(Input.text().placeholder("Un campo")),
        Card.section("Card.section(titulo): compacto").children(
            Row.gap(10).children(Check.of("Opcion A").selected(true), Check.of("Opcion B"))),
        Card.create().children(Label.text("Card.create(): sin titulo, solo el bloque")),
        Card.create(Row.gap(8).fill(Label.text("Card.create(componente): titulo propio").bold())
                .children(Switch.of("").selected(true)))
            .children(Hint.of("El titulo puede ser cualquier componente, por ejemplo un interruptor.")));
  }

  private static JComponent headerVariants() {
    return Col.gap(10).children(
        Header.create("Header.create(titulo)"),
        Header.create("Header.create(titulo, descripcion)", "Elige como se conecta"),
        Header.create("Header.corner(componente)", "Un componente a la derecha del titulo")
            .corner(Label.text("Ayuda").color(Kit.ON_DARK).onClick(() -> { })));
  }

  private static JComponent footerVariants() {
    return Col.gap(10).padding(12, 16, 12, 16).children(
        Card.section("Footer.of(btnCancel, btnSave)").children(
            Footer.of(Button.of("Cancelar"), Button.primary("Guardar"))),
        Card.section("Footer.of(btnClose)").children(
            Footer.of(Button.primary("Cerrar"))),
        Card.section("Row propia: una casilla a la izquierda, botones a la derecha").children(
            Row.gap(8).padding(12, 14)
                .children(Check.of("No volver a mostrar"))
                .glue()
                .children(Button.of("Cancelar"), Button.primary("Aceptar"))),
        Card.section("Row propia: ayuda a la izquierda, navegacion a la derecha").children(
            Row.gap(8).padding(12, 14)
                .children(Button.of("Ayuda"))
                .glue()
                .children(Button.of("Atras"), Button.primary("Siguiente"))));
  }

  /** Un Stack con tres alternativas de las que se ve una: la que elige quien arma la pantalla. */
  private static JComponent stackState(String title, int visible) {
    final Combo<String> cbPrinters = Combo.of("Zebra ZD420", "TSC TE200");
    final Input txtIp = Input.text().placeholder("192.168.0.10");
    final Combo<String> cbUsb = Combo.of("USB001", "USB002");
    cbPrinters.setVisible(visible == 0);
    txtIp.setVisible(visible == 1);
    cbUsb.setVisible(visible == 2);

    return Card.section(title).children(
        Row.gap(14).children(
            Radio.of("Driver").selected(visible == 0),
            Radio.of("Red").selected(visible == 1),
            Radio.of("USB").selected(visible == 2)),
        Row.gap(10).children(
            Stack.fill(cbPrinters, txtIp, cbUsb),
            Button.of("Buscar")));
  }

  private static JComponent stackAlternatives() {
    return Col.gap(10).padding(12, 16, 12, 16).children(
        stackState("Stack.fill(cbPrinters, txtIp, cbUsb)  -> se ve cbPrinters", 0),
        stackState("...  -> se ve txtIp (los otros dos no ocupan espacio)", 1),
        stackState("...  -> se ve cbUsb", 2));
  }

  private static JComponent stackAlign() {
    return Col.gap(10).padding(12, 16, 12, 16).children(
        Card.section("Stack.fill(boton)  -> ocupa todo el ancho").children(
            Stack.fill(Button.of("Boton"))),
        Card.section("Stack.start(boton)  -> ancho natural, a la izquierda").children(
            Stack.start(Button.of("Boton"))),
        Card.section("Stack.center(boton)  -> ancho natural, centrado").children(
            Stack.center(Button.of("Boton"))),
        Card.section("Stack.end(boton)  -> ancho natural, a la derecha").children(
            Stack.end(Button.of("Boton"))));
  }

  /** Dos botones en el mismo lugar: se ve "Iniciar" o "Detener" segun el estado. */
  private static JComponent stackToggle(String title, boolean running) {
    final Button start = Button.primary("Iniciar").visible(!running);
    final Button stop = Button.of("Detener").visible(running);
    return Card.section(title).children(
        Row.gap(10)
            .children(Label.text(running ? "Servicio en marcha" : "Servicio detenido"))
            .glue()
            .children(Stack.start(start, stop)));
  }

  /** Un mensaje de "no hay datos" y la lista en el mismo lugar: se ve uno u otro. */
  private static JComponent stackEmptyState(String title, boolean empty) {
    final BaseTableModel.TArray model = new BaseTableModel.TArray("Impresora", "Puerto");
    if (!empty) {
      model.addRow(new Object[] {"Zebra ZD420", "192.168.0.10"});
      model.addRow(new Object[] {"TSC TE200", "USB001"});
    }
    final Table table = Table.of(model).rowHeight(26);
    table.setPreferredScrollableViewportSize(new Dimension(200, 80));

    final Hint emptyMessage = Hint.of("Todavia no hay impresoras. Presiona Nueva para agregar una.").visible(empty);
    final Scroll list = Scroll.of(table).visible(!empty);
    return Card.section(title).children(Stack.fill(emptyMessage, list));
  }

  private static JComponent stackPatterns() {
    return Col.gap(10).padding(12, 16, 12, 16).children(
        Row.gap(12).fillEqual(
            stackToggle("Detenido: se ve Iniciar", false),
            stackToggle("En marcha: se ve Detener", true)),
        Row.gap(12).fillEqual(
            stackEmptyState("Sin datos: se ve el mensaje", true),
            stackEmptyState("Con datos: se ve la lista", false)));
  }

  private static JComponent pageRegions() {
    return Page.create()
        .top(zone("top  ->  Header, barra de herramientas", new Color(0xDBEAFE), 14, 16))
        .left(zone("left", new Color(0xD1FAE5), 20, 30))
        .right(zone("right", new Color(0xFEF3C7), 20, 30))
        .center(zone("center  ->  ocupa todo el espacio que sobra", new Color(0xE5E7EB), 20, 16))
        .bottom(zone("bottom  ->  Footer, barra de estado", new Color(0xFFE4E6), 14, 16));
  }

  private static JComponent tabsAndSplit() {
    return Col.gap(10).padding(12, 16, 12, 16).children(
        Card.section("Tabs.create().tab(\"General\", a).tab(\"Avanzado\", b)").children(
            Tabs.create()
                .tab("General", Col.gap(6).padding(10).children(Check.of("Opcion 1").selected(true), Check.of("Opcion 2")))
                .tab("Avanzado", Col.gap(6).padding(10).children(Check.of("Opcion avanzada")))),
        Card.section("Split.horizontal(lista, detalle).divider(160)").children(
            Split.horizontal(zone("lista", 22, 10), zone("detalle", new Color(0xE5E7EB), 22, 10)).divider(160)),
        Card.section("Split.vertical(editor, consola).weight(0.7)").children(
            Split.vertical(zone("editor", 22, 10), zone("consola", new Color(0xE5E7EB), 22, 10)).weight(0.7)));
  }

  private static JComponent gapAndPadding() {
    return Col.gap(10).padding(12, 16, 12, 16).children(
        Row.gap(12).fillEqual(
            captioned("Col.gap(0)", Col.gap(0).children(zone("a", 4, 10), zone("b", 4, 10), zone("c", 4, 10))),
            captioned("Col.gap(8)", Col.gap(8).children(zone("a", 4, 10), zone("b", 4, 10), zone("c", 4, 10))),
            captioned("Col.gap(20)", Col.gap(20).children(zone("a", 4, 10), zone("b", 4, 10), zone("c", 4, 10)))),
        Row.gap(12).fillEqual(
            captioned("padding(0)", outline(Col.gap(6).children(zone("a", 4, 10), zone("b", 4, 10)))),
            captioned("padding(12)", outline(Col.gap(6).padding(12).children(zone("a", 4, 10), zone("b", 4, 10)))),
            captioned("padding(4, 24)", outline(Col.gap(6).padding(4, 24).children(zone("a", 4, 10), zone("b", 4, 10))))));
  }

  private static JComponent visibility() {
    final Button hidden = Button.of("Dos").visible(false);
    final Label lblPass = Label.field("Clave").visible(false);
    final Password txtPass = Input.password().placeholder("Clave").visible(false);

    return Col.gap(10).padding(12, 16, 12, 16).children(
        Card.section("Row: todos visibles").children(
            Row.gap(8).children(Button.of("Uno"), Button.of("Dos"), Button.of("Tres"))),
        Card.section("Row: Button.of(\"Dos\").visible(false)  -> no deja hueco").children(
            Row.gap(8).children(Button.of("Uno"), hidden, Button.of("Tres"))),
        Row.gap(12).fillEqual(
            Card.section("Form: dos filas visibles").children(
                Form.create()
                    .row("Usuario", Input.text().placeholder("Usuario"))
                    .row("Clave", Input.password().placeholder("Clave"))),
            Card.section("Form: etiqueta y campo ocultos").children(
                Form.create()
                    .row("Usuario", Input.text().placeholder("Usuario"))
                    .row(lblPass, txtPass))));
  }

  // ------------------------------------------------------------------------ //
  //   A P L I C A C I O N E S   C O M P L E T A S
  // ------------------------------------------------------------------------ //

  private static JComponent appToolbarTable() {
    final BaseTableModel.TArray model = new BaseTableModel.TArray("Impresora", "Puerto", "Estado");
    model.addRow(new Object[] {"Zebra ZD420", "192.168.0.10", "Listo"});
    model.addRow(new Object[] {"TSC TE200", "USB001", "Sin papel"});
    model.addRow(new Object[] {"Brother QL-800", "USB002", "Listo"});
    final Table table = Table.of(model).rowHeight(28).singleSelection();
    table.setRowSelectionInterval(0, 0);

    return Page.create()
        .top(Col.gap(0).children(
            Header.create("Impresoras", "Administra las impresoras conectadas"),
            Row.gap(8).padding(10, 12)
                .children(Button.primary("Nuevo"), Button.of("Editar"), Button.of("Eliminar"))
                .glue()
                .children(Input.text().placeholder("Buscar...").width(200))))
        .center(Col.gap(0).padding(0, 12, 0, 12).fillBoth(Scroll.of(table)))
        .bottom(Row.gap(12).padding(6, 14).background(Kit.SURFACE)
            .children(Label.muted("3 impresoras").small())
            .glue()
            .children(Label.muted("Conectado").small()));
  }

  private static JComponent appMasterDetail() {
    final BaseTableModel.TArray model = new BaseTableModel.TArray("Cliente");
    model.addRow(new Object[] {"Ana Torres"});
    model.addRow(new Object[] {"Luis Prado"});
    model.addRow(new Object[] {"Marta Rios"});
    model.addRow(new Object[] {"Pedro Soto"});
    final Table list = Table.of(model).rowHeight(30).singleSelection();
    list.setRowSelectionInterval(0, 0);

    return Page.create()
        .top(Header.create("Clientes", "Elige uno para ver su detalle"))
        .center(Split.horizontal(
            Scroll.of(list).noBorder(),
            Col.gap(10).padding(12).children(
                Card.create("Ana Torres").subtitle("Cliente desde 2021").children(
                    Form.create()
                        .row("Ciudad", Label.text("Lima"))
                        .row("Correo", Label.text("ana@ejemplo.com"))
                        .row("Telefono", Label.text("555-0101"))),
                Row.gap(8).glue().children(Button.of("Editar"), Button.primary("Nuevo pedido")))).divider(220));
  }

  /** Un elemento del menu lateral. */
  private static Label navItem(String text, boolean selected) {
    return Label.text(text)
        .color(selected ? Color.WHITE : Kit.ON_DARK)
        .padding(8, 10, 8, 10)
        .background(selected ? new Color(0x35474F) : Kit.DARK);
  }

  private static JComponent appSidebar() {
    return Page.create()
        .left(Col.gap(2).padding(16, 10, 16, 10).background(Kit.DARK)
            .children(Label.title("Mi App").color(Color.WHITE).padding(4, 10, 12, 10))
            .fill(navItem("Inicio", true), navItem("Impresoras", false), navItem("Reportes", false))
            .glue()
            .fill(navItem("Ajustes", false)))
        .center(Col.gap(10).padding(14).children(
            Label.title("Inicio"),
            Card.create("Resumen").children(
                Form.create()
                    .row("Impresoras", Label.text("12 conectadas"))
                    .row("Ultimo trabajo", Label.text("Hace 5 minutos"))),
            Card.create("Atajos").children(
                Row.gap(8).children(Button.primary("Nueva etiqueta"), Button.of("Ver reportes")))));
  }

  private static JComponent appWizard() {
    final Pages pages = Pages.create()
        .page("name", Col.gap(10).padding(12).children(Card.create("Nombre")))
        .page("type", Col.gap(10).padding(12).children(
            Card.create("Tipo de servicio").subtitle("Como arranca el servicio").children(
                Row.gap(14).children(Radio.of("Manual").selected(true), Radio.of("Automatico")),
                Form.create().row("Reintentos", Spinner.integer(3, 0, 10, 1).width(80)))))
        .page("done", Col.gap(10).padding(12).children(Card.create("Listo")));
    pages.show("type");

    return Page.create()
        .top(Header.create("Nuevo servicio", "Paso 2 de 3"))
        .center(pages)
        .bottom(Footer.of(Button.of("Atras"), Button.primary("Siguiente")));
  }

  /** Una tarjeta con una cifra grande: un indicador del tablero. */
  private static Card stat(String title, String value, Color color) {
    return Card.create().gap(2).children(
        Label.muted(title).small(),
        Label.title(value).fontSize(28).color(color));
  }

  private static JComponent appDashboard() {
    return Page.create()
        .top(Header.create("Panel", "Resumen de hoy"))
        .center(Col.gap(12).padding(12).children(
            Row.gap(12).fillEqual(
                stat("Impresoras", "12", Kit.DARK),
                stat("En linea", "9", new Color(0x14A44D)),
                stat("Con error", "1", new Color(0xDC4C64))),
            Card.create("Actividad reciente").children(
                Grid.gap(6).columnGap(24)
                    .row(Label.muted("10:32"), Label.text("Zebra ZD420"), Label.text("Etiqueta impresa"))
                    .row(Label.muted("10:28"), Label.text("TSC TE200"), Label.text("Sin papel"))
                    .row(Label.muted("10:15"), Label.text("Brother QL-800"), Label.text("Etiqueta impresa")))));
  }

  private static JComponent appTwoColumns() {
    return Page.create()
        .top(Header.create("Etiqueta", "Disena y configura la impresion"))
        .center(Row.gap(12).padding(12).fillEqual(
            Col.gap(10).children(
                Card.section("Contenido").children(
                    Form.create()
                        .row("Texto", Input.text().placeholder("Texto de la etiqueta"))
                        .row("Codigo", Input.text().placeholder("123456789"))),
                Card.section("Codigo de barras").children(
                    Form.create()
                        .row("Tipo", Combo.of("Code 128", "EAN-13", "QR"))
                        .row("Alto", Spinner.integer(40, 10, 200, 5).width(80)))),
            Col.gap(10).children(
                Card.section("Papel").children(
                    Form.create()
                        .row("Ancho (mm)", Spinner.integer(100, 10, 300, 1).width(80))
                        .row("Tipo", Combo.of("Gap", "Continuo"))),
                Card.section("Impresion").children(
                    Form.create()
                        .row("Copias", Spinner.integer(1, 1, 99, 1).width(80))
                        .row("Velocidad", Combo.of("2 ips", "4 ips"))))))
        .bottom(Footer.of(Button.of("Cancelar"), Button.primary("Imprimir")));
  }

  private static JComponent appLoginCentered() {
    return Page.create().center(
        Col.gap(0).glue().children(
            Row.gap(0).glue().children(
                Card.create("Iniciar sesion").subtitle("Usa tu cuenta para continuar").width(320).children(
                    Form.create()
                        .row("Usuario", Input.text().placeholder("Usuario"))
                        .row("Clave", Input.password().placeholder("Clave")),
                    Row.gap(8).glue().children(Button.of("Olvide mi clave"), Button.primary("Entrar"))))
                .glue())
            .glue());
  }

  private static JComponent appEditorConsole() {
    final Font mono = new Font(Font.MONOSPACED, Font.PLAIN, 13);
    final TextArea editor = Input.area(10).font(mono);
    editor.setText("^XA\n^FO50,50^A0N,40,40^FDHola Tessera^FS\n^XZ");
    final TextArea console = Input.area(4).font(mono).editable(false);
    console.setText("> Enviando a Zebra ZD420...\n> Listo (1 etiqueta)");

    return Page.create()
        .top(Row.gap(8).padding(8, 12)
            .children(Button.primary("Ejecutar"), Button.of("Detener"))
            .glue()
            .children(Combo.of("ZPL", "EPL").width(100)))
        .center(Split.vertical(Scroll.of(editor), Scroll.of(console)).weight(0.7).divider(250));
  }

  private static JComponent appSettingsTabs() {
    return Page.create()
        .top(Header.create("Ajustes", "Preferencias de la aplicacion"))
        .center(Col.gap(0).padding(12).children(
            Tabs.create()
                .tab("General", Col.gap(10).padding(12).children(
                    Card.section("Inicio").children(
                        Switch.of("Iniciar con el sistema").selected(true),
                        Check.of("Buscar actualizaciones al iniciar").selected(true))))
                .tab("Impresion", Col.gap(10).padding(12).children(
                    Card.section("Predeterminados").children(
                        Form.create()
                            .row("Copias", Spinner.integer(1, 1, 99, 1).width(80))
                            .row("Velocidad", Combo.of("2 ips", "4 ips")))))
                .tab("Avanzado", Col.gap(10).padding(12).children(
                    Card.section("Depuracion").children(Check.of("Guardar registro de eventos"))))))
        .bottom(Footer.of(Button.of("Cancelar"), Button.primary("Guardar")));
  }

  // ------------------------------------------------------------------------ //
  //   G U A R D A R
  // ------------------------------------------------------------------------ //

  /** Pinta el componente fuera de pantalla con ese ancho (y ese alto; 0 = el que pida) y lo guarda como PNG. */
  static void save(String dir, String name, JComponent content, int width, int height) {
    final JFrame frame = new JFrame();
    frame.setContentPane(content);
    frame.pack();
    // el alto depende del ancho (textos que se parten): se calcula con el ancho final
    content.setSize(width, 10);
    content.validate();
    final Dimension size = new Dimension(width, height > 0 ? height : content.getPreferredSize().height);
    content.setSize(size);
    content.validate();

    final BufferedImage image = new BufferedImage(size.width, size.height, BufferedImage.TYPE_INT_RGB);
    final Graphics2D g = image.createGraphics();
    g.setColor(UIManager.getColor("Panel.background"));
    g.fillRect(0, 0, size.width, size.height);
    content.paint(g);
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
