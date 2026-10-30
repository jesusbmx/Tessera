package tessera.demo;

import java.awt.Color;
import java.awt.Dimension;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.SwingUtilities;
import tessera.components.BaseTableModel;
import tessera.components.DashedBorder;
import tessera.kit.Button;
import tessera.kit.Card;
import tessera.kit.Check;
import tessera.kit.Col;
import tessera.kit.Combo;
import tessera.kit.Hint;
import tessera.kit.Input;
import tessera.kit.Kit;
import tessera.kit.Label;
import tessera.kit.Password;
import tessera.kit.Radio;
import tessera.kit.Row;
import tessera.kit.Scroll;
import tessera.kit.Spinner;
import tessera.kit.Stack;
import tessera.kit.Switch;
import tessera.kit.Table;
import tessera.theme.Theme;

/**
 * Genera las imagenes de docs/guides/layout.md (docs/img/box-*.png): todo lo que se puede hacer con Row, Col y Stack.
 * Cada metodo devuelve la expresion tal como aparece en la guia (el titulo de cada tarjeta es el codigo): si cambia
 * uno, se cambia el otro. Todas comparten el mismo tema, asi que salen en una sola ejecucion; scripts/doc-images.sh la lanza.
 *
 * <pre>
 * java ... tessera.demo.BoxImages [carpeta]        (por defecto docs/img)
 * </pre>
 */
public final class BoxImages {

  /** Ancho de las imagenes. */
  private static final int W = 620;

  static final Color BLUE = new Color(0xDBEAFE);
  static final Color GREEN = new Color(0xD1FAE5);
  static final Color AMBER = new Color(0xFEF3C7);
  static final Color ROSE = new Color(0xFFE4E6);
  static final Color GRAY = new Color(0xE5E7EB);

  private BoxImages() {
  }

  public static void main(String[] args) throws Exception {
    final String dir = args.length > 0 ? args[0] : "docs/img";
    Theme.builder().accent("#1976D2").panelBackground("#F7F6F3").install();

    SwingUtilities.invokeAndWait(() -> {
      // Row
      save(dir, "box-row-gap", rowGap());
      save(dir, "box-row-fill", rowFill());
      save(dir, "box-row-equal", rowEqual());
      save(dir, "box-row-fillboth", rowFillBoth());
      save(dir, "box-row-glue", rowGlue());
      save(dir, "box-row-align", rowAlign());
      save(dir, "box-row-padding", rowPadding());
      save(dir, "box-row-width", rowWidth());
      save(dir, "box-row-hidden", rowHidden());
      save(dir, "box-row-patterns", rowPatterns());
      // Col
      save(dir, "box-col-basic", colBasic());
      save(dir, "box-col-startfill", colStartFill());
      save(dir, "box-col-gap", colGap());
      save(dir, "box-col-glue", colGlue());
      save(dir, "box-col-fillboth", colFillBoth());
      save(dir, "box-col-padding", colPadding());
      save(dir, "box-col-hidden", colHidden());
      save(dir, "box-col-nested", colNested());
      save(dir, "box-col-patterns", colPatterns());
      // Stack
      save(dir, "box-stack-align", stackAlign());
      save(dir, "box-stack-switch", stackSwitch());
      save(dir, "box-stack-swap", stackSwap());
      save(dir, "box-stack-layouts", stackLayouts());
      // Juntos
      save(dir, "box-mix-form", mixForm());
      save(dir, "box-mix-layout", mixLayout());
      save(dir, "box-mix-dashboard", mixDashboard());
      save(dir, "box-mix-toolbar", mixToolbar());
    });
    System.exit(0);
  }

  static void save(String dir, String name, JComponent content) {
    LayoutImages.save(dir, name, content, W, 0);
  }

  // ------------------------------------------------------------------------ //
  //   A y u d a s   s o l o   p a r a   i l u s t r a r
  // ------------------------------------------------------------------------ //

  /** Bloque de color con texto: sirve para dibujar "una pieza" sin que importe su contenido. */
  static Label zone(String text) {
    return zone(text, BLUE, 6, 12);
  }

  static Label zone(String text, Color color) {
    return zone(text, color, 6, 12);
  }

  static Label zone(String text, Color color, int vertical, int horizontal) {
    final Label label = Label.text(text).center().background(color);
    label.setBorder(BorderFactory.createCompoundBorder(
        BorderFactory.createLineBorder(color.darker()),
        BorderFactory.createEmptyBorder(Kit.px(vertical), Kit.px(horizontal), Kit.px(vertical), Kit.px(horizontal))));
    return label;
  }

  /** Dibuja con linea punteada el borde de un contenedor, para ver hasta donde llega. */
  static <T extends JComponent> T outline(T container) {
    container.setBorder(new DashedBorder(8, new Color(0x94A3B8)));
    return container;
  }

  /** Un titulo pequeno encima del componente (el componente ocupa el resto). */
  static Col captioned(String caption, JComponent component) {
    return Col.gap(4).children(Label.muted(caption).small()).fillBoth(component);
  }

  /** Tarjeta con el codigo como titulo. */
  static Card card(String title, JComponent... children) {
    return Card.section(title).children(children);
  }

  /** Pagina de la imagen: tarjetas una debajo de otra. */
  static Col page(JComponent... children) {
    return Col.gap(10).padding(12, 16, 12, 16).children(children);
  }

  // ------------------------------------------------------------------------ //
  //   R O W
  // ------------------------------------------------------------------------ //

  private static JComponent rowGap() {
    return page(
        card("Row.gap(0)",
            Row.gap(0).children(zone("A"), zone("B"), zone("C"))),
        card("Row.create()  ->  gap(8)",
            Row.create().children(zone("A"), zone("B"), zone("C"))),
        card("Row.gap(24)",
            Row.gap(24).children(zone("A"), zone("B"), zone("C"))));
  }

  private static JComponent rowFill() {
    return page(
        card("Row.gap(10).children(a, b, c)  ->  cada uno con su ancho natural",
            Row.gap(10).children(Button.of("Uno"), Button.of("Dos"), Button.of("Tres"))),
        card("Row.gap(10).fill(campo).children(boton)",
            Row.gap(10).fill(Input.text().placeholder("Ruta del archivo")).children(Button.of("Buscar"))),
        card("Row.gap(10).children(etiqueta).fill(campo).children(boton)",
            Row.gap(10).children(Label.field("Carpeta")).fill(Input.text().placeholder("Carpeta de salida"))
                .children(Button.of("..."))),
        card("Row.gap(10).fill(campo1, campo2)  ->  se reparten el ancho",
            Row.gap(10).fill(Input.text().placeholder("Nombre"), Input.text().placeholder("Apellido"))),
        card("Row.gap(10).fill(campo).children(combo, boton)",
            Row.gap(10).fill(Input.text().placeholder("Buscar"))
                .children(Combo.of("Todos", "Activos"), Button.primary("Buscar"))));
  }

  private static JComponent rowEqual() {
    return page(
        card("Row.gap(10).fill(a, b)  ->  se reparten lo que sobra, cada uno suma su ancho",
            Row.gap(10).fill(zone("A (corto)"), zone("B (un texto bastante mas largo)"))),
        card("Row.gap(10).fillEqual(a, b)  ->  miden exactamente lo mismo",
            Row.gap(10).fillEqual(zone("A (corto)"), zone("B (un texto bastante mas largo)"))),
        card("Row.gap(10).fillEqual(a, b, c)",
            Row.gap(10).fillEqual(zone("1", GREEN), zone("2", AMBER), zone("3", ROSE))),
        card("Row.gap(10).fillEqual(a, b).children(c)  ->  c conserva su ancho natural",
            Row.gap(10).fillEqual(zone("A"), zone("B")).children(Button.primary("Aceptar"))));
  }

  private static JComponent rowFillBoth() {
    return page(
        Row.gap(12).fillEqual(
            captioned("Row.fill(area)  ->  solo el ancho",
                outline(Row.gap(8).padding(8)
                    .fill(Scroll.of(Input.area(2)))
                    .children(Button.of("Ok"))).height(130)),
            captioned("Row.fillBoth(area)  ->  ancho y alto",
                outline(Row.gap(8).padding(8)
                    .fillBoth(Scroll.of(Input.area(2)))
                    .children(Button.of("Ok"))).height(130))),
        Row.gap(12).fillEqual(
            captioned("Row.top().fillBoth(area).children(col)",
                outline(Row.gap(8).padding(8).top()
                    .fillBoth(Scroll.of(Input.area(2)))
                    .children(Col.gap(6).children(Button.of("Abrir"), Button.of("Guardar")))).height(130)),
            captioned("Row.fillBoth(a, b)  ->  se reparten el alto",
                outline(Row.gap(8).padding(8)
                    .fillBoth(Scroll.of(Input.area(2)), Scroll.of(Input.area(2)))).height(130))));
  }

  private static JComponent rowGlue() {
    return page(
        card("Row.gap(8).glue().children(cancelar, guardar)  ->  a la derecha",
            Row.gap(8).glue().children(Button.of("Cancelar"), Button.primary("Guardar"))),
        card("Row.gap(8).children(info).glue().children(boton)  ->  uno a cada lado",
            Row.gap(8).children(Label.muted("3 cambios sin guardar")).glue().children(Button.primary("Guardar"))),
        card("Row.gap(8).glue().children(a, b).glue()  ->  centrados",
            Row.gap(8).glue().children(Button.of("Anterior"), Button.of("Siguiente")).glue()),
        card("Row.gap(8).children(a).glue().children(b).glue().children(c)  ->  tres grupos",
            Row.gap(8).children(Button.of("Atras")).glue().children(Label.muted("Paso 2 de 4")).glue()
                .children(Button.primary("Siguiente"))));
  }

  private static JComponent rowAlign() {
    return page(
        card("Row.gap(10).children(...)  ->  centrados en vertical (por defecto)",
            outline(Row.gap(10).padding(6).children(
                Label.field("Notas"), Scroll.of(Input.area(3).wrap()).width(260), Button.of("Guardar")))),
        card("Row.gap(10).top().children(...)  ->  alineados arriba",
            outline(Row.gap(10).padding(6).top().children(
                Label.field("Notas"), Scroll.of(Input.area(3).wrap()).width(260), Button.of("Guardar")))),
        card("Row.gap(10).top().center()  ->  center() vuelve al centro",
            outline(Row.gap(10).padding(6).top().center().children(
                Label.field("Notas"), Scroll.of(Input.area(3).wrap()).width(260), Button.of("Guardar")))));
  }

  private static JComponent rowPadding() {
    return page(
        Row.gap(12).fillEqual(
            captioned("padding(0)", outline(Row.gap(6).children(zone("a", BLUE, 4, 10), zone("b", BLUE, 4, 10)))),
            captioned("padding(12)", outline(Row.gap(6).padding(12).children(zone("a", BLUE, 4, 10), zone("b", BLUE, 4, 10)))),
            captioned("padding(4, 24)", outline(Row.gap(6).padding(4, 24).children(zone("a", BLUE, 4, 10), zone("b", BLUE, 4, 10))))),
        Row.gap(12).fillEqual(
            captioned("paddingVertical(16)",
                outline(Row.gap(6).paddingVertical(16).children(zone("a", GREEN, 4, 10), zone("b", GREEN, 4, 10)))),
            captioned("paddingHorizontal(32)",
                outline(Row.gap(6).paddingHorizontal(32).children(zone("a", GREEN, 4, 10), zone("b", GREEN, 4, 10)))),
            captioned("padding(0, 0, 0, 40)",
                outline(Row.gap(6).padding(0, 0, 0, 40).children(zone("a", GREEN, 4, 10), zone("b", GREEN, 4, 10))))));
  }

  private static JComponent rowWidth() {
    return page(
        card("Input.text().width(80)  ->  ancho fijo de cada hijo",
            Row.gap(8).children(
                Input.text().placeholder("CP").width(80),
                Input.text().placeholder("Ciudad").width(160),
                Combo.of("Mexico", "Chile").width(110))),
        card("ancho fijo + fill  ->  el campo toma lo que sobra",
            Row.gap(8).children(Combo.of("A4", "Carta").width(100))
                .fill(Input.text().placeholder("Descripcion"))
                .children(Spinner.integer(1, 1, 99, 1).width(70))),
        card("etiqueta y control, uno al lado del otro",
            Row.gap(8).children(
                Label.field("Copias"), Spinner.integer(1, 1, 99, 1).width(70),
                Label.field("Tamano"), Combo.of("Chico", "Grande").width(110))));
  }

  private static JComponent rowHidden() {
    return page(
        card("Row: todos visibles",
            Row.gap(8).children(Button.of("Uno"), Button.of("Dos"), Button.of("Tres"))),
        card("Button.of(\"Dos\").visible(false)  ->  no deja hueco",
            Row.gap(8).children(Button.of("Uno"), Button.of("Dos").visible(false), Button.of("Tres"))),
        card("fill(campo) con el boton oculto  ->  el campo toma su lugar",
            Row.gap(8).fill(Input.text().placeholder("Direccion IP"))
                .children(Button.of("Buscar en red").visible(false))));
  }

  private static JComponent rowPatterns() {
    return page(
        card("barra de herramientas",
            Row.gap(8).fill(Input.text().placeholder("Buscar..."))
                .children(Combo.of("Todos", "Activos"), Button.of("Buscar"), Button.primary("Nuevo"))),
        card("barra de estado",
            Row.gap(8).children(Label.muted("Listo")).glue().children(Label.muted("3 de 25"), Label.muted("v1.0"))),
        card("paginacion",
            Row.gap(6).glue()
                .children(Button.of("<"), Button.of("1"), Button.primary("2"), Button.of("3"), Button.of(">")).glue()),
        card("opciones una al lado de otra",
            Row.gap(14).children(Radio.of("Driver").selected(true), Radio.of("Red"), Radio.of("USB"))),
        card("etiquetas de color",
            Row.gap(6).children(zone("nuevo", GREEN, 2, 8), zone("urgente", ROSE, 2, 8), zone("revision", AMBER, 2, 8))));
  }

  // ------------------------------------------------------------------------ //
  //   C O L
  // ------------------------------------------------------------------------ //

  private static JComponent colBasic() {
    return page(
        card("Col.gap(6).children(...)  ->  cada hijo en su fila; botones y etiquetas con su ancho natural",
            Col.gap(6).children(
                Label.text("Etiqueta: ancho natural"),
                Button.of("Boton: ancho natural"),
                Check.of("Casilla: ancho natural"),
                Input.text().placeholder("Campo: todo el ancho"),
                Combo.of("Combo: todo el ancho"),
                Scroll.of(Input.area(2)))));
  }

  private static JComponent colStartFill() {
    return page(
        Row.gap(12).top().fillEqual(
            captioned("children(boton, campo)",
                Col.gap(6).children(Button.of("Guardar"), Input.text().placeholder("Campo"))),
            captioned("fill(boton).children(campo)",
                Col.gap(6).fill(Button.of("Guardar")).children(Input.text().placeholder("Campo"))),
            captioned("start(campo)",
                Col.gap(6).children(Button.of("Guardar")).start(Input.text().placeholder("Campo").columns(6)))));
  }

  private static JComponent colGap() {
    return page(
        Row.gap(12).top().fillEqual(
            captioned("Col.gap(0)", Col.gap(0).children(zone("a", BLUE, 4, 10), zone("b", BLUE, 4, 10), zone("c", BLUE, 4, 10))),
            captioned("Col.gap(4)", Col.gap(4).children(zone("a", BLUE, 4, 10), zone("b", BLUE, 4, 10), zone("c", BLUE, 4, 10))),
            captioned("Col.gap(12)", Col.gap(12).children(zone("a", BLUE, 4, 10), zone("b", BLUE, 4, 10), zone("c", BLUE, 4, 10))),
            captioned("Col.gap(24)", Col.gap(24).children(zone("a", BLUE, 4, 10), zone("b", BLUE, 4, 10), zone("c", BLUE, 4, 10)))));
  }

  private static JComponent colGlue() {
    return page(
        Row.gap(12).fillEqual(
            captioned("sin glue",
                outline(Col.gap(8).padding(8)
                    .children(Label.field("Titulo"), Input.text().placeholder("Campo"), Button.primary("Guardar"))).height(170)),
            captioned(".glue() antes del boton",
                outline(Col.gap(8).padding(8)
                    .children(Label.field("Titulo"), Input.text().placeholder("Campo"))
                    .glue()
                    .children(Button.primary("Guardar"))).height(170)),
            captioned(".glue() al principio",
                outline(Col.gap(8).padding(8)
                    .glue()
                    .children(Label.field("Titulo"), Button.primary("Guardar"))).height(170)),
            captioned("dos .glue()",
                outline(Col.gap(8).padding(8)
                    .children(Label.field("Arriba"))
                    .glue()
                    .children(Label.field("Medio"))
                    .glue()
                    .children(Label.field("Abajo"))).height(170))));
  }

  private static JComponent colFillBoth() {
    return page(
        Row.gap(12).fillEqual(
            captioned("campo + fillBoth(area)",
                outline(Col.gap(8).padding(8)
                    .children(Input.text().placeholder("Buscar"))
                    .fillBoth(Scroll.of(Input.area(4)))).height(190)),
            captioned("fillBoth(a, b)  ->  se reparten el alto",
                outline(Col.gap(8).padding(8)
                    .fillBoth(Scroll.of(Input.area(2)), Scroll.of(Input.area(2)))).height(190)),
            captioned("titulo + fillBoth + botones",
                outline(Col.gap(8).padding(8)
                    .children(Label.field("Notas"))
                    .fillBoth(Scroll.of(Input.area(4)))
                    .children(Row.gap(6).glue().children(Button.primary("Guardar")))).height(190))));
  }

  private static JComponent colPadding() {
    return page(
        Row.gap(12).fillEqual(
            captioned("padding(0)", outline(Col.gap(6).children(zone("a", BLUE, 4, 10), zone("b", BLUE, 4, 10)))),
            captioned("padding(12)", outline(Col.gap(6).padding(12).children(zone("a", BLUE, 4, 10), zone("b", BLUE, 4, 10)))),
            captioned("padding(4, 24)", outline(Col.gap(6).padding(4, 24).children(zone("a", BLUE, 4, 10), zone("b", BLUE, 4, 10))))),
        Row.gap(12).fillEqual(
            captioned("paddingVertical(16)",
                outline(Col.gap(6).paddingVertical(16).children(zone("a", GREEN, 4, 10), zone("b", GREEN, 4, 10)))),
            captioned("paddingHorizontal(32)",
                outline(Col.gap(6).paddingHorizontal(32).children(zone("a", GREEN, 4, 10), zone("b", GREEN, 4, 10)))),
            captioned("padding(0, 0, 0, 40)",
                outline(Col.gap(6).padding(0, 0, 0, 40).children(zone("a", GREEN, 4, 10), zone("b", GREEN, 4, 10))))));
  }

  private static JComponent colHidden() {
    return page(
        Row.gap(12).fillEqual(
            card("todos visibles",
                Col.gap(6).children(
                    Input.text().placeholder("Usuario"),
                    Input.password().placeholder("Clave"),
                    Button.primary("Entrar"))),
            card("Input.password()...visible(false)",
                Col.gap(6).children(
                    Input.text().placeholder("Usuario"),
                    Input.password().placeholder("Clave").visible(false),
                    Button.primary("Entrar")))));
  }

  private static JComponent colNested() {
    return page(
        card("Row.gap(12).top().children(Col menu).fill(Col contenido)  ->  menu lateral y contenido",
            Row.gap(12).top()
                .children(Col.gap(4).fill(
                    Button.of("Servicios"), Button.of("Archivo"), Button.of("Historial"), Button.of("Ajustes")))
                .fill(Col.gap(6).children(
                    Label.title("Servicios"), Input.text().placeholder("Nombre"), Combo.of("Gap", "Continuo")))),
        card("Row.gap(12).top().fillEqual(Col, Col)  ->  dos columnas iguales",
            Row.gap(12).top().fillEqual(
                Col.gap(6).children(Label.field("Origen"), Input.text().placeholder("Carpeta"), Check.of("Subcarpetas")),
                Col.gap(6).children(Label.field("Destino"), Input.text().placeholder("Carpeta"), Check.of("Sobrescribir")))),
        card("Col.gap(14) con Col.gap(4) dentro  ->  grupos con su propio espacio",
            Col.gap(14).children(
                Col.gap(4).children(Label.field("Conexion"), Input.text().placeholder("192.168.0.10")),
                Col.gap(4).children(Label.field("Papel"), Combo.of("Continuo", "Gap")))));
  }

  private static JComponent colPatterns() {
    return page(
        Row.gap(12).top().fillEqual(
            Card.create("Iniciar sesion").children(
                Col.gap(8)
                    .children(Input.text().placeholder("Usuario"), Input.password().placeholder("Clave"), Check.of("Recordarme"))
                    .fill(Button.primary("Entrar"))),
            Card.create("Ajustes").children(
                Col.gap(10).children(
                    setting("Notificaciones", true), setting("Sonido", false), setting("Modo oscuro", false)))));
  }

  /** Una linea de ajustes: el nombre a la izquierda y el interruptor a la derecha. */
  private static Row setting(String name, boolean on) {
    return Row.gap(8).children(Label.text(name)).glue().children(Switch.of(on ? "Si" : "No").selected(on));
  }

  // ------------------------------------------------------------------------ //
  //   S T A C K
  // ------------------------------------------------------------------------ //

  private static JComponent stackAlign() {
    return page(
        card("Stack.fill(boton)  ->  ocupa todo el ancho",
            outline(Stack.fill(Button.of("Boton")))),
        card("Stack.start(boton)  ->  ancho natural, a la izquierda",
            outline(Stack.start(Button.of("Boton")))),
        card("Stack.center(boton)  ->  ancho natural, centrado",
            outline(Stack.center(Button.of("Boton")))),
        card("Stack.end(boton)  ->  ancho natural, a la derecha",
            outline(Stack.end(Button.of("Boton")))));
  }

  /** La conexion de una impresora: lo que se muestra al lado depende de la opcion elegida. */
  private static Card connection(String title, int mode) {
    final Combo<String> cbDriver = Combo.of("Zebra ZD420", "TSC TE200").visible(mode == 0);
    final Input txtIp = Input.text().placeholder("192.168.0.10").visible(mode == 1);
    final Combo<String> cbUsb = Combo.of("USB001 (Zebra)").visible(mode == 2);
    final Button btnScan = Button.of("Buscar").visible(mode == 1);
    final Button btnRefresh = Button.of("Actualizar").visible(mode == 2);

    return Card.section(title).children(
        Row.gap(14).children(
            Radio.of("Driver").selected(mode == 0), Radio.of("Red").selected(mode == 1), Radio.of("USB").selected(mode == 2)),
        Row.gap(8).children(Stack.fill(cbDriver, txtIp, cbUsb), Stack.start(btnScan, btnRefresh)));
  }

  private static JComponent stackSwitch() {
    return page(
        connection("Stack.fill(cbDriver, txtIp, cbUsb)  ->  se ve el combo de impresoras", 0),
        connection("la misma pantalla con Red elegido  ->  se ve el campo y su boton", 1),
        connection("la misma pantalla con USB elegido  ->  se ve el otro combo", 2));
  }

  /** Dos botones en el mismo lugar: se ve "Iniciar" o "Detener" segun el estado. */
  private static Card startStop(String title, boolean running) {
    final Button start = Button.primary("Iniciar").visible(!running);
    final Button stop = Button.of("Detener").visible(running);
    return Card.section(title).children(
        Row.gap(10)
            .children(Label.text(running ? "En marcha" : "Detenido"))
            .glue()
            .children(Stack.start(start, stop)));
  }

  /** Clave oculta o a la vista: dos campos en el mismo lugar. */
  private static Card showPassword(String title, boolean show) {
    final Password hidden = Input.password().placeholder("Clave").visible(!show);
    final Input visible = Input.text().placeholder("Clave").visible(show);
    return Card.section(title).children(
        Row.gap(10).children(Stack.fill(hidden, visible), Check.of("Mostrar").selected(show)));
  }

  /** Un mensaje de "no hay datos" y la lista en el mismo lugar: se ve uno u otro. */
  private static Card emptyOrList(String title, boolean empty) {
    final BaseTableModel.TArray model = new BaseTableModel.TArray("Impresora", "Puerto");
    if (!empty) {
      model.addRow(new Object[] {"Zebra ZD420", "192.168.0.10"});
      model.addRow(new Object[] {"TSC TE200", "USB001"});
    }
    final Table table = Table.of(model).rowHeight(26);
    table.setPreferredScrollableViewportSize(new Dimension(200, 80));

    final Hint message = Hint.of("Todavia no hay impresoras. Presiona Nueva para agregar una.").visible(empty);
    final Scroll list = Scroll.of(table).visible(!empty);
    return Card.section(title).children(Stack.fill(message, list));
  }

  private static JComponent stackSwap() {
    return page(
        Row.gap(12).fillEqual(
            startStop("Detenido: se ve Iniciar", false),
            startStop("En marcha: se ve Detener", true)),
        Row.gap(12).fillEqual(
            showPassword("Oculta: se ve el campo de clave", false),
            showPassword("A la vista: se ve el campo de texto", true)),
        Row.gap(12).fillEqual(
            emptyOrList("Sin datos: se ve el mensaje", true),
            emptyOrList("Con datos: se ve la lista", false)));
  }

  private static JComponent stackLayouts() {
    return page(
        card("Row con Stack.fill(campo)  ->  el Stack se estira",
            Row.gap(8).children(Label.field("Valor"), Stack.fill(Input.text().placeholder("campo")), Stack.start(Button.of("Ok")))),
        card("Row con Stack.start(campo)  ->  el Stack no se estira",
            Row.gap(8).children(
                Label.field("Valor"), Stack.start(Input.text().placeholder("campo").columns(12)), Stack.start(Button.of("Ok")))),
        card("Col con Stack.end(boton)  ->  el boton queda a la derecha",
            Col.gap(8).children(Input.text().placeholder("Nombre"), Stack.end(Button.primary("Guardar")))),
        card("Col.glue().children(Stack.center(mensaje)).glue()  ->  centrado en un area alta",
            outline(Col.gap(0).padding(8).glue()
                .children(Stack.center(Label.muted("No hay datos para mostrar")))
                .glue()).height(110)));
  }

  // ------------------------------------------------------------------------ //
  //   J U N T O S
  // ------------------------------------------------------------------------ //

  private static JComponent mixForm() {
    return page(
        Card.create("Nueva impresora").children(
            Col.gap(10)
                .children(
                    Row.gap(10).fill(Input.text().placeholder("Nombre")).children(Combo.of("Zebra", "TSC")),
                    Row.gap(10).fill(Input.text().placeholder("192.168.0.10")).children(Button.of("Buscar")),
                    Check.of("Imprimir una etiqueta de prueba"))
                .glue()
                .children(Row.gap(8).glue().children(Button.of("Cancelar"), Button.primary("Guardar")))));
  }

  private static JComponent mixLayout() {
    return page(
        outline(Col.gap(6).padding(6)
            .fill(zone("Row.gap(8)  ->  barra de arriba", BLUE, 8, 12))
            .fillBoth(zone("Col.fillBoth(...)  ->  ocupa todo lo que sobra", GRAY, 8, 12))
            .fill(zone("Row.gap(8)  ->  barra de abajo", ROSE, 8, 12))).height(240));
  }

  private static JComponent mixDashboard() {
    return page(
        Row.gap(10).fillEqual(stat("Impresoras", "12"), stat("Trabajos hoy", "148"), stat("Errores", "3")),
        Row.gap(10).top().fillEqual(
            Card.section("Ultimos trabajos").children(
                Col.gap(4).children(Label.text("Etiquetas de envio"), Label.text("Recibo #2041"), Label.text("Codigos de barras"))),
            Card.section("Estado").children(
                Col.gap(4).children(Label.text("Zebra ZD420: lista"), Label.text("TSC TE200: sin papel"), Label.text("USB001: lista")))));
  }

  private static Card stat(String title, String value) {
    return Card.section(title).children(Label.text(value).bold().fontSize(26));
  }

  private static JComponent mixToolbar() {
    final BaseTableModel.TArray model = new BaseTableModel.TArray("Impresora", "Puerto", "Estado");
    model.addRow(new Object[] {"Zebra ZD420", "192.168.0.10", "Lista"});
    model.addRow(new Object[] {"TSC TE200", "USB001", "Sin papel"});
    model.addRow(new Object[] {"Brother QL-800", "USB002", "Lista"});
    final Table table = Table.of(model).rowHeight(26);

    return page(
        outline(Col.gap(8).padding(8)
            .children(Row.gap(8).fill(Input.text().placeholder("Buscar impresora"))
                .children(Combo.of("Todas", "Listas"), Button.primary("Nueva")))
            .fillBoth(Scroll.of(table))
            .children(Row.gap(8).children(Label.muted("3 impresoras")).glue()
                .children(Button.of("Editar"), Button.of("Borrar")))).height(260));
  }
}
