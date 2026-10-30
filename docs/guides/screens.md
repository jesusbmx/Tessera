# Como armar una pantalla

Convencion para las clases de pantalla (paneles y dialogos) que usan Tessera. Es la que siguen Text2Barcode, Codaly Sync y PDFLabelCut,
y la que debe seguir cualquier app nueva. Sirve para que un dev, o una IA, encuentre en segundos donde esta la logica y donde esta la UI.

Despues de la convencion hay [pantallas completas](#pantallas-completas) listas para copiar: cada una dice **que contenedores y widgets usa**, con enlace a su
pagina, donde se explica a fondo con una imagen de cada cosa que se puede hacer. Para elegir un contenedor, empieza por
[Contenedores: que uso para cada cosa](../reference/kit/containers/README.md#que-uso-para-cada-cosa).

## La idea

1. **La logica va arriba** de la clase: estado, metodos, eventos, acceso a datos.
2. **La UI va al final**, en una sola zona marcada con un encabezado fijo.
3. Dentro de esa zona, **cada bloque lleva sus campos (widgets) justo encima del metodo que lo arma**. Se lee de arriba hacia abajo en el
   mismo orden en que la pantalla se construye.

## Esqueleto

```java
public class MiPanel extends JPanel {

  // ... logica arriba: campos de estado, constructor, metodos, handlers ...

  // ---------------------------------------------------------------------
  // UI: widgets y construcción de la pantalla
  // Cada bloque lleva sus campos (widgets) justo encima del método que lo arma:
  // campos del bloque -> método del bloque. Empieza en buildUI() y sigue el orden
  // en que se usan. La lógica (eventos, datos, etc.) va fuera de esta zona.
  // ---------------------------------------------------------------------

  // campos del pie: los coloca el propio buildUI()
  private final Button btnCancel = Button.of("Cancelar").onClick(this::dispose);
  private final Button btnSave = Button.primary("Guardar").onClick(this::save);

  private void buildUI() {
    setLayout(new BorderLayout());
    add(Page.create()
        .top(Header.create("Impresora", "Elige como se conecta"))
        .center(Col.gap(10).padding(10, 12, 10, 12).children(connectionBlock(), qualityBlock()))
        .bottom(Footer.of(btnCancel, btnSave))
        .build());
  }

  // campos de connectionBlock()
  private final Radio rbtnDriver = Radio.of("Driver").selected(true);
  private final Radio rbtnIp = Radio.of("Red");
  private final Input txtIp = Input.text().placeholder("192.168.0.10");
  private final Button btnScan = Button.of("Buscar").onClick(this::scan);

  private Card connectionBlock() {
    return Card.create("Conexion")
        .children(
            Row.gap(14).children(rbtnDriver, rbtnIp),
            Row.gap(10).fill(txtIp).children(btnScan));
  }

  // campos de qualityBlock()
  private final Combo<String> cbDpi = Combo.of("203", "300", "600");

  private Card qualityBlock() {
    return Card.create("Calidad").children(Form.create().row("DPI", cbDpi));
  }
}
```

Asi se ve esa pantalla (el codigo de arriba esta compilado en `src/demo/java/tessera/demo/ScreenExample.java`):

![Pantalla de ejemplo](../img/screen-example.png)

## Reglas

- **Un solo encabezado** por clase, el texto exacto de arriba. Si el método que arranca la pantalla no se llama `buildUI()`
  (por ejemplo `initUI()`), se cambia ese nombre en la frase "Empieza en ...". Buscar `UI: widgets y construcción` encuentra todas las pantallas.
- **Un campo va encima del metodo que lo coloca en el layout**, no del que solo lo configura. Si lo usan varios bloques, encima del primero.
- **Dependencias entre campos**: Java inicializa los campos en el orden en que aparecen. Si un campo usa otro (`btnMore` usa `popupMenu` en su
  lambda, un `Scroll.of(listView)` usa `listView`), el usado va **antes**; de lo contrario no compila ("illegal forward reference") o, si se accede con `this.campo`,
  queda en `null`.
- **Eventos con referencia a metodo**: `.onClick(this::guardar)`; el cuerpo del metodo vive en la zona de logica, arriba.
- **Casillas/radios que reaccionan a `setSelected` por codigo**: usa `ItemListener` (`onClick` solo atiende clics del usuario) y registralo
  despues de armar la pantalla.
- **Un solo metodo de arranque** (`buildUI()` o `initUI()`) llamado desde el constructor.
- **Si la pantalla es pequena** (un solo bloque, pocos campos) no hace falta partirla en varios metodos: los campos van juntos encima de `buildUI()`.
- Imports explicitos: `Label`, `Button`, `List`, `TextArea` chocan con `java.awt`.
- Textos de la app con el sistema de idiomas de la propia app; la libreria no trae uno.

## Que contenedor usar

Cada contenedor tiene su propia pagina con **una imagen por cada cosa que se puede hacer** y el codigo que la produce. En corto:

| Necesito | Contenedor |
|---|---|
| widgets uno al lado del otro | [`Row`](../reference/kit/containers/row.md): `Row.gap(n).children(...)`; el que debe estirarse: `.fill(c)` |
| widgets uno debajo del otro | [`Col`](../reference/kit/containers/col.md): `Col.gap(n).children(...)`; uno que ocupe el alto sobrante: `.fillBoth(c)` |
| etiqueta + campo alineados | [`Form`](../reference/kit/containers/form.md): `Form.create().row("Nombre", txtName)` |
| tabla de celdas alineadas por columna | [`Grid`](../reference/kit/containers/grid.md): `Grid.gap(n).row(...)` |
| bloque con titulo y borde redondeado | [`Card`](../reference/kit/containers/card.md): `Card.create("Titulo")` / `Card.section("Titulo")` |
| ventana con cabecera, centro y pie | [`Page`](../reference/kit/containers/page.md) con [`Header`](../reference/kit/containers/header.md) y [`Footer`](../reference/kit/containers/footer.md) |
| alternar paneles | [`Pages`](../reference/kit/containers/pages.md) (uno visible, lo decide el programa), [`Tabs`](../reference/kit/containers/tabs.md) (lo decide el usuario) o [`Stack`](../reference/kit/containers/stack.md) (alternativas pequenas) |
| separar en dos zonas redimensionables | [`Split`](../reference/kit/containers/split.md): `Split.horizontal(a, b)` |
| contenido con scroll | [`Scroll`](../reference/kit/containers/scroll.md): `Scroll.of(c)` |

La tabla completa, con mas casos, esta en [Contenedores](../reference/kit/containers/README.md#que-uso-para-cada-cosa). Lo que comparten todos (margen, tamano fijo, ocultar) esta en
[`Box`](../reference/kit/containers/box.md). La referencia completa de metodos esta en [AGENTS.md](../../AGENTS.md).

## Pantallas completas

Las piezas combinadas en **disposiciones tipicas** de una aplicacion. Cada una va con el codigo que la arma (en tu clase de pantalla, ese `return` es lo que se pasa a
`add(...)` desde `buildUI()`; los campos van arriba, como se explica en [La idea](#la-idea)).

### Dialogo clasico

**Usa:** [`Row`](../reference/kit/containers/row.md) · [`Col`](../reference/kit/containers/col.md) · [`Form`](../reference/kit/containers/form.md) · [`Card`](../reference/kit/containers/card.md) · [`Header`](../reference/kit/containers/header.md) · [`Footer`](../reference/kit/containers/footer.md) · [`Page`](../reference/kit/containers/page.md) · [`Button`](../reference/kit/widgets/button.md) · [`Radio`](../reference/kit/widgets/radio.md) · [`Input`](../reference/kit/widgets/input.md) · [`Combo`](../reference/kit/widgets/combo.md)

Cabecera, bloques con titulo y barra de botones: el esqueleto de [este mismo documento](#esqueleto).

![Dialogo clasico](../img/screen-example.png)

### Barra de herramientas, tabla y barra de estado

**Usa:** [`Row`](../reference/kit/containers/row.md) · [`Col`](../reference/kit/containers/col.md) · [`Header`](../reference/kit/containers/header.md) · [`Page`](../reference/kit/containers/page.md) · [`Scroll`](../reference/kit/containers/scroll.md) · [`Label`](../reference/kit/widgets/label.md) · [`Button`](../reference/kit/widgets/button.md) · [`Input`](../reference/kit/widgets/input.md) · [`Table`](../reference/kit/widgets/table.md)

Una pantalla de **listado**: cabecera, una fila de acciones con un buscador a la derecha, la tabla ocupando el centro y una barra de estado abajo.

![Barra de herramientas, tabla y estado](../img/app-toolbar-table.png)

```java
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
```

Notas: la tabla va dentro de un `Scroll`, dentro de un `Col` con `fillBoth` para que ocupe el alto; el buscador usa `.glue()` antes para quedar a la derecha, con
`.width(200)` fijo; la barra de estado es una `Row` con `background(...)`.

### Lista y detalle

**Usa:** [`Row`](../reference/kit/containers/row.md) · [`Col`](../reference/kit/containers/col.md) · [`Form`](../reference/kit/containers/form.md) · [`Card`](../reference/kit/containers/card.md) · [`Header`](../reference/kit/containers/header.md) · [`Page`](../reference/kit/containers/page.md) · [`Scroll`](../reference/kit/containers/scroll.md) · [`Split`](../reference/kit/containers/split.md) · [`Label`](../reference/kit/widgets/label.md) · [`Button`](../reference/kit/widgets/button.md) · [`Table`](../reference/kit/widgets/table.md)

Una lista a la izquierda y el detalle de lo elegido a la derecha, con una barra para ajustar el reparto.

![Lista y detalle](../img/app-master-detail.png)

```java
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
```

Notas: `Split.horizontal(...).divider(220)`; la lista va en un `Scroll` sin borde (`noBorder()`); el detalle es un `Col` con `Card` y una fila de botones a la derecha (`glue()`).
La logica de seleccionar y actualizar el detalle esta en la [receta de lista y detalle](recipes.md#2-lista-y-detalle).

### Menu lateral

**Usa:** [`Row`](../reference/kit/containers/row.md) · [`Col`](../reference/kit/containers/col.md) · [`Form`](../reference/kit/containers/form.md) · [`Card`](../reference/kit/containers/card.md) · [`Page`](../reference/kit/containers/page.md) · [`Label`](../reference/kit/widgets/label.md) · [`Button`](../reference/kit/widgets/button.md)

Un menu oscuro a la izquierda y el contenido a la derecha: `Page.left(...)` y `Page.center(...)`.

![Menu lateral](../img/app-sidebar.png)

```java
private static Label navItem(String text, boolean selected) {
  return Label.text(text)
      .color(selected ? Color.WHITE : Kit.ON_DARK)
      .padding(8, 10, 8, 10)
      .background(selected ? new Color(0x35474F) : Kit.DARK);
}
```

```java
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
```

Notas: el menu es un `Col` con `background(Kit.DARK)`; los elementos van con `.fill(...)` para que el fondo del seleccionado ocupe todo el ancho; el `.glue()` empuja "Ajustes"
al fondo. Para que cada elemento cambie el contenido, junta esto con un `Pages` en el centro (`pages.show("inicio")`).

### Asistente de pasos

**Usa:** [`Row`](../reference/kit/containers/row.md) · [`Col`](../reference/kit/containers/col.md) · [`Form`](../reference/kit/containers/form.md) · [`Card`](../reference/kit/containers/card.md) · [`Header`](../reference/kit/containers/header.md) · [`Footer`](../reference/kit/containers/footer.md) · [`Page`](../reference/kit/containers/page.md) · [`Pages`](../reference/kit/containers/pages.md) · [`Button`](../reference/kit/widgets/button.md) · [`Radio`](../reference/kit/widgets/radio.md) · [`Spinner`](../reference/kit/widgets/spinner.md)

Cabecera, un panel por paso y botones Atras / Siguiente. `Pages` mide la pagina mas grande, asi la ventana no cambia de tamano entre pasos.

![Asistente de pasos](../img/app-wizard.png)

```java
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
```

La logica de Atras / Siguiente esta en la [receta del asistente](recipes.md#3-asistente-de-pasos).

### Tablero con indicadores

**Usa:** [`Row`](../reference/kit/containers/row.md) · [`Col`](../reference/kit/containers/col.md) · [`Grid`](../reference/kit/containers/grid.md) · [`Card`](../reference/kit/containers/card.md) · [`Header`](../reference/kit/containers/header.md) · [`Page`](../reference/kit/containers/page.md) · [`Label`](../reference/kit/widgets/label.md)

Una fila de tarjetas del **mismo ancho** (`fillEqual`) con una cifra grande, y debajo un bloque de actividad.

![Tablero con indicadores](../img/app-dashboard.png)

```java
private static Card stat(String title, String value, Color color) {
  return Card.create().gap(2).children(
      Label.muted(title).small(),
      Label.title(value).fontSize(28).color(color));
}
```

```java
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
```

### Dos columnas de bloques

**Usa:** [`Row`](../reference/kit/containers/row.md) · [`Col`](../reference/kit/containers/col.md) · [`Form`](../reference/kit/containers/form.md) · [`Card`](../reference/kit/containers/card.md) · [`Header`](../reference/kit/containers/header.md) · [`Footer`](../reference/kit/containers/footer.md) · [`Page`](../reference/kit/containers/page.md) · [`Button`](../reference/kit/widgets/button.md) · [`Input`](../reference/kit/widgets/input.md) · [`Combo`](../reference/kit/widgets/combo.md) · [`Spinner`](../reference/kit/widgets/spinner.md)

Dos columnas del mismo ancho, cada una con sus tarjetas apiladas: `Row.fillEqual(Col, Col)`.

![Dos columnas](../img/app-two-columns.png)

```java
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
```

### Formulario centrado (inicio de sesion)

**Usa:** [`Row`](../reference/kit/containers/row.md) · [`Col`](../reference/kit/containers/col.md) · [`Form`](../reference/kit/containers/form.md) · [`Card`](../reference/kit/containers/card.md) · [`Page`](../reference/kit/containers/page.md) · [`Button`](../reference/kit/widgets/button.md) · [`Input`](../reference/kit/widgets/input.md) · [`Password`](../reference/kit/widgets/password.md)

Un bloque **centrado** en la ventana, de ancho fijo. El truco: un `glue()` a cada lado, en horizontal (dentro de una `Row`) y en vertical (dentro de una `Col`).

![Inicio de sesion centrado](../img/app-login-centered.png)

```java
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
```

La version modal de este dialogo, con validacion, esta en la [receta de inicio de sesion](recipes.md#1-dialogo-de-inicio-de-sesion).

### Editor y consola

**Usa:** [`Row`](../reference/kit/containers/row.md) · [`Page`](../reference/kit/containers/page.md) · [`Scroll`](../reference/kit/containers/scroll.md) · [`Split`](../reference/kit/containers/split.md) · [`Button`](../reference/kit/widgets/button.md) · [`TextArea`](../reference/kit/widgets/textarea.md) · [`Combo`](../reference/kit/widgets/combo.md)

Una barra de acciones y, debajo, un editor y una consola **una sobre otra** con una barra que se arrastra: `Split.vertical`. `weight(0.7)` hace que el editor se quede con el 70% al
crecer la ventana.

![Editor y consola](../img/app-editor-console.png)

```java
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
```

### Ajustes con pestanas

**Usa:** [`Col`](../reference/kit/containers/col.md) · [`Form`](../reference/kit/containers/form.md) · [`Card`](../reference/kit/containers/card.md) · [`Header`](../reference/kit/containers/header.md) · [`Footer`](../reference/kit/containers/footer.md) · [`Page`](../reference/kit/containers/page.md) · [`Tabs`](../reference/kit/containers/tabs.md) · [`Button`](../reference/kit/widgets/button.md) · [`Check`](../reference/kit/widgets/check.md) · [`Switch`](../reference/kit/widgets/switch.md) · [`Combo`](../reference/kit/widgets/combo.md) · [`Spinner`](../reference/kit/widgets/spinner.md)

Una pantalla de preferencias: cabecera, `Tabs` en el centro y botones al pie.

![Ajustes con pestanas](../img/app-settings-tabs.png)

```java
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
```

## Errores de disposicion que se repiten

| Sintoma | Causa y arreglo |
|---|---|
| Un boton o etiqueta dentro de un `Col` **no se estira** | es lo esperado; usa `.fill(boton)` |
| La **tabla** no muestra el encabezado, o no ocupa el alto | falta `Scroll.of(table)`, o el `Scroll` esta en un `Col` sin `fillBoth` |
| `.width(200)` **no hace nada** | solo lo entienden los contenedores del kit; dentro de un `Page` o un `JPanel` suelto se ignora |
| Un bloque no queda **centrado** | para centrar necesitas el `glue()` a ambos lados **y** un ancho fijo (`.width(...)`) en el bloque |
| Al ocultar una fila de un `Form` queda la **etiqueta** | una fila son dos componentes: oculta los dos |
| El menu lateral queda **muy angosto** | `Page.left` mide lo que pide su contenido: dale `padding` o elementos mas anchos |
| Un campo **no se estira** junto a su boton | falta `.fill(campo)` en el `Row` (con `.children(campo)` queda con su ancho natural) |
| Los hijos del `Row` quedan **a la izquierda** y quieres uno a la derecha | pon `.glue()` antes del que debe ir a la derecha |
| Dos bloques lado a lado miden **distinto** | usa `fillEqual(a, b)` en vez de `fill(a, b)` |

Mas errores, uno por uno, en la seccion **Errores comunes** de la pagina de cada contenedor ([`Row`](../reference/kit/containers/row.md#errores-comunes), [`Col`](../reference/kit/containers/col.md#errores-comunes), [`Form`](../reference/kit/containers/form.md#errores-comunes)...).

## Buscar rapido (para devs e IA)

- Donde esta la UI de una pantalla: buscar `UI: widgets y construcción` en el archivo y bajar desde ahi.
- Todas las pantallas del proyecto: `grep -rl "UI: widgets y construcción" src`.
- Donde se arma un widget: el campo esta declarado en la zona de UI, justo sobre el metodo del bloque al que pertenece.
- Donde esta su comportamiento: el `.onClick(this::metodo)` apunta al metodo, que esta en la zona de logica.
