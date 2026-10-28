# Referencia del kit (`tessera.kit`)

Todo lo que trae el kit: contenedores para acomodar, widgets para capturar y mostrar, y las utilidades de `Kit`.
Es la referencia completa, clase por clase, con todos los metodos. Para una lectura rapida de toda la API
mira [AGENTS.md](../AGENTS.md); para empezar desde cero, [GETTING-STARTED.md](GETTING-STARTED.md).

![Contenedores del kit](img/containers.png)

## Indice

- [Ideas base](#ideas-base)
- [Metodos comunes (`Styled` y `Clickable`)](#metodos-comunes)
- [Contenedores](#contenedores): [`Row`](#row) · [`Col`](#col) · [`Stack`](#stack) · [`Grid`](#grid) · [`Form`](#form) · [`Card`](#card) · [`Header`](#header) · [`Footer`](#footer) · [`Page`](#page) · [`Pages`](#pages) · [`Scroll`](#scroll) · [`Tabs`](#tabs) · [`Split`](#split) · [`Separator`](#separator)
- [Widgets](#widgets): [`Label`](#label) · [`Hint`](#hint) · [`Button`](#button) · [`Check`](#check) · [`Radio`](#radio) · [`Toggle`](#toggle) · [`Switch`](#switch) · [`Input`](#input) · [`Password`](#password) · [`TextArea`](#textarea) · [`Combo`](#combo) · [`Spinner`](#spinner) · [`Table`](#table)
- [Utilidades (`Kit`)](#utilidades-kit)
- [Mezclar con Swing puro](#mezclar-con-swing-puro)
- [Errores comunes](#errores-comunes)

## Ideas base

1. **Un widget del kit es un widget de Swing.** `Button extends JButton`, `Input extends JTextField`, `Table extends JTable`...
   Donde Swing pida un `JButton`, vale un `Button`. Nada se envuelve ni se esconde: `input.getText()`, `combo.getSelectedItem()` y
   `table.getSelectedRow()` funcionan igual que siempre.
2. **Los metodos se encadenan.** Cada configuracion devuelve el propio widget, asi se arma en una expresion:
   `Input.text().placeholder("Nombre").columns(20)`.
3. **No se escribe MigLayout.** Los contenedores (`Row`, `Col`, `Form`...) son `JPanel` transparentes con MigLayout adentro; ellos
   deciden quien se estira y quien no. Los hijos solo se listan.
4. **Medidas en px logicos.** Todo numero en pixeles que entra por la API (`gap`, `padding`, `width`, `rowHeight`...) se multiplica por el
   zoom de la pantalla (`Kit.px`). Pon `12`, no `12 * zoom`.
5. **Se crean por fabrica.** Los constructores son `protected`: `Button.of("Ok")`, `Row.gap(8)`, `Input.text()`.

```java
Card.create("Conexion").children(
    Row.gap(14).children(Radio.of("Driver").selected(true), Radio.of("Red")),
    Row.gap(10).fill(Input.text().placeholder("192.168.0.10")).children(Button.of("Buscar")));
```

## Metodos comunes

### `Styled<T>` — todos los widgets y contenedores

| Metodo | Que hace |
|---|---|
| `.tip(String)` | texto emergente (tooltip) |
| `.bold()` | la misma fuente en negrita |
| `.fontSize(int)` | tamano de fuente, escalado con el zoom |
| `.font(Font)` | una fuente concreta |
| `.color(Color)` | color del texto |
| `.background(Color)` | color de fondo (y lo hace opaco) |
| `.width(int)` / `.height(int)` | tamano **fijo** en px. Solo lo entienden los contenedores del kit: ver [Errores comunes](#errores-comunes) |
| `.enabled(boolean)` / `.visible(boolean)` | habilitar / mostrar |
| `.name(String)` | nombre del componente (depuracion y pruebas) |

### `Clickable<T>` — `Button`, `Check`, `Radio`, `Toggle`, `Switch`

Extiende `Styled`.

| Metodo | Que hace |
|---|---|
| `.onClick(Runnable)` / `.onClick(ActionListener)` | accion al hacer clic. En casillas y opciones, al cambiar de estado **por el usuario** |
| `.icon(String recurso, int tam)` / `.icon(Icon)` | icono (SVG o imagen del classpath) |
| `.iconGap(int)` | espacio entre icono y texto |
| `.selected(boolean)` | estado inicial (en `Check`, `Radio`, `Toggle`, `Switch`) |

### `Box<T>` — todos los contenedores de layout

`Row`, `Col`, `Stack`, `Grid`, `Form`, `Card` y `Header` heredan de `Box`. Es un `JPanel` transparente (no pinta fondo, salvo `Header` y
`Card`, que traen el suyo) con MigLayout dentro.

| Metodo | Que hace |
|---|---|
| `.padding(todo)` | espacio entre el borde y los hijos, igual por los cuatro lados |
| `.padding(vertical, horizontal)` | arriba/abajo y izquierda/derecha |
| `.padding(arriba, izq, abajo, der)` | el mismo orden de `java.awt.Insets` |
| `.children(Component...)` | agrega los hijos en orden, cada uno con el comportamiento por defecto del contenedor. Los `null` se ignoran |
| `.build()` | devuelve el contenedor (opcional: ya es un `JPanel`) |

El espacio entre hijos (`gap`) se da al crear el contenedor (`Row.gap(10)`) o con `.gap(n)` en `Card`.

## Contenedores

### Row

Hijos **en una fila**, de izquierda a derecha, cada uno con su ancho natural, centrados en vertical.

| Metodo | Que hace |
|---|---|
| `Row.gap(n)` / `Row.create()` | fila con `n` px entre hijos / con 8 px |
| `.fill(Component...)` | hijos que ocupan el **ancho** que sobra (se reparte entre ellos) |
| `.fillBoth(Component...)` | hijos que ocupan el ancho **y el alto** que sobran (una tabla, un area de texto) |
| `.fillEqual(Component...)` | como `fillBoth`, y miden **siempre lo mismo de ancho** entre si (dos columnas lado a lado) |
| `.glue()` | hueco elastico: lo que se agregue despues queda pegado a la derecha |
| `.top()` / `.center()` | alineacion vertical de los hijos cuando no miden lo mismo (por defecto `center`) |

```java
Row.gap(10).fill(txtPath).children(btnBrowse)            // campo + boton
Row.gap(8).glue().children(btnCancel, btnOk)             // botones a la derecha
Row.gap(14).children(rbtnPrinter, rbtnIp, rbtnUsb)       // opciones una al lado de otra
```

`children(...)` agrega con el ancho natural, salvo que el hijo ya este marcado con `Kit.fill(...)` (ver [Utilidades](#utilidades-kit)).

### Col

Hijos **en una columna**, de arriba hacia abajo. Por defecto cada hijo ocupa **todo el ancho**, **excepto** los botones y las etiquetas
(`AbstractButton` y `JLabel`), que se quedan a la izquierda con su ancho natural.

| Metodo | Que hace |
|---|---|
| `Col.gap(n)` / `Col.create()` | columna con `n` px entre hijos / con 8 px |
| `.start(Component...)` | hijos con su ancho natural, a la izquierda |
| `.fill(Component...)` | hijos que ocupan todo el ancho (incluso botones y etiquetas) |
| `.fillBoth(Component...)` | hijos que ocupan todo el ancho **y** el alto que sobra (una tabla, un `Scroll`) |
| `.glue()` | hueco elastico: lo que se agregue despues queda pegado abajo |

```java
Col.gap(6).children(lblTitle, txtName, cbType)
Col.gap(8).children(Row.gap(10).fill(txtIp).children(btnScan)).fillBoth(scrollTable)
```

`Scroll`, `Tabs` y `Split` ya vienen marcados para ocupar el espacio que sobra: dentro de un `Col` o `Row` no hace falta `fillBoth`.

### Stack

Varios componentes **en el mismo lugar**, uno encima de otro; sirve para alternativas de las que se muestra una (un combo, un campo o
otro combo segun la opcion elegida). **Quien arma la pantalla decide cual esta visible**; los ocultos no ocupan espacio.

| Fabrica | Los hijos... |
|---|---|
| `Stack.fill(c...)` | ocupan todo el ancho y el `Stack` ocupa el ancho que sobra de su contenedor |
| `Stack.start(c...)` | conservan el ancho natural, a la izquierda |
| `Stack.end(c...)` | conservan el ancho natural, a la derecha |
| `Stack.center(c...)` | conservan su tamano natural, centrados |

```java
Row.gap(10).children(
    Stack.fill(cbPrinters, txtIpPrinter, cbUsbPrinters),       // una de las tres se ve
    Stack.start(btnIpScanner, btnUsbRefresh))                   // el boton que toque
```

### Grid

Cuadricula: varias celdas por fila, **alineadas en columnas** (cada columna mide lo que su celda mas ancha). `Form` es para un par
etiqueta/campo por fila; `Grid` para varios pares por fila. Una celda oculta no ocupa espacio y una fila sin celdas visibles desaparece
con su espacio.

| Metodo | Que hace |
|---|---|
| `Grid.gap(n)` | cuadricula con `n` px entre filas y 6 px entre columnas |
| `.row(Component...)` | una fila; cada componente va en la columna que le toca por su posicion (un `null` deja la celda vacia) |
| `.columnGap(px)` | espacio entre columnas (6 por defecto) |
| `.columnGap(despues, px)` | espacio solo despues de la columna `despues` (empieza en 0) |
| `.children(...)` | cada componente en una fila propia, en la primera columna |

```java
Grid.gap(6)
    .columnGap(1, 30)                                       // mas aire entre el 2.o y el 3.er par
    .row(lblDpi, cbDpi.width(170), lblWidth, Row.gap(12).children(spinW, lblMm))
    .row(lblType, cbType.width(170), lblGap, spinGap)
```

### Form

Formulario de **dos columnas**: la etiqueta a la izquierda y el campo a la derecha, que ocupa el ancho que sobra. Las etiquetas de texto se
crean con el estilo de `Label.field` (negrita, 15).

| Metodo | Que hace |
|---|---|
| `Form.create()` / `Form.gap(n)` | formulario con 10 px / `n` px entre filas |
| `.row("Etiqueta", campo)` | una fila con etiqueta de texto |
| `.row(JComponent etiqueta, campo)` | una fila con cualquier componente como etiqueta |
| `.rowTop("Etiqueta", campo)` / `.rowTop(c, campo)` | etiqueta alineada **arriba** (junto a un campo alto: un area de texto) |
| `.wide(c)` / `.wide(c, espacioArriba)` | fila que ocupa **las dos columnas** (titulo, casilla, ayuda); el segundo parametro suma espacio encima y desaparece con la fila |
| `.field(c)` | **ultima** fila, solo con el campo (la columna de etiquetas queda vacia): un enlace bajo un area de texto |
| `.space(px)` | hueco fijo entre dos grupos de filas (se usa con `row(...)`, no con `children(...)`) |
| `.labelGap(px)` | espacio entre etiqueta y campo (14 por defecto) |
| `.labelsRight()` | etiquetas pegadas a su campo (alineadas a la derecha de su columna) |

```java
Form.create()
    .row("Nombre:", txtName)
    .row("Direccion:", Row.gap(8).fill(txtAddress).children(btnMap))
    .rowTop("Notas:", Scroll.of(areaNotes))
    .wide(chkEnabled)                                       // ocupa las dos columnas
```

Con `.children(...)` los componentes se alternan: etiqueta, campo, etiqueta, campo...

### Card

Bloque con **fondo suave y borde redondeado**: titulo (con icono opcional), texto de ayuda y debajo los hijos en columna (con las reglas
de `Col`). Relleno y borde salen de `Kit.SURFACE` y `Kit.CARD_LINE` (se cambian con `Theme`). El radio de las esquinas es fijo (12): ver
[CUSTOMIZING.md](CUSTOMIZING.md).

| Fabrica | Resultado |
|---|---|
| `Card.create("Titulo")` | titulo en negrita, un poco mas grande que el texto |
| `Card.section("Titulo")` | compacto: titulo en negrita del tamano del texto. Reemplaza a los `TitledBorder` |
| `Card.create()` | sin titulo |
| `Card.create(JComponent)` | titulo propio (un titulo con un interruptor al lado, por ejemplo) |

| Metodo | Que hace |
|---|---|
| `.subtitle(String)` | texto de ayuda bajo el titulo (se parte en lineas solo; ver [`Hint`](#hint)) |
| `.icon(String recurso)` / `.icon(Icon)` | icono junto al titulo (solo si el titulo es de texto); el recurso se carga como SVG de 24 px |
| `.gap(n)` | espacio entre hijos (6 por defecto) |
| `.title()` | el componente del titulo, para cambiarlo despues |
| `.padding(...)` | margen interior (por defecto 10, 12, 12, 12) |

```java
Card.create("Conexion")
    .icon("app/img/print.svg")
    .subtitle("Como se conecta la impresora")
    .children(
        Row.gap(14).children(rbtnPrinter, rbtnIp, rbtnUsb),
        Row.gap(10).fill(txtIp).children(btnScan))
```

### Header

Banner **oscuro** con titulo y descripcion opcional: la cabecera de cada paso de un asistente o dialogo. Colores de `Kit.DARK` y
`Kit.ON_DARK`.

| Metodo | Que hace |
|---|---|
| `Header.create(titulo)` / `Header.create(titulo, descripcion)` | banner |
| `.subtitle(String)` | descripcion bajo el titulo (si ya existe, cambia su texto) |
| `.corner(Component)` | un componente (un enlace de ayuda) a la derecha del titulo |
| `.title()` | la `Label` del titulo, para cambiarla despues |

### Footer

`Footer.of(Component... botones)` devuelve un `Row` con los botones **alineados a la derecha**, 8 px entre ellos y 12/14 px de margen.

```java
Footer.of(btnCancel, btnSave)
```

### Page

Estructura de una ventana o panel: **arriba, centro, abajo** (y a los lados si hace falta), con `BorderLayout`.

| Metodo | Que hace |
|---|---|
| `Page.create()` | pagina vacia |
| `.top(c)` / `.bottom(c)` / `.left(c)` / `.right(c)` / `.center(c)` | coloca el componente en esa zona |
| `.minWidth(px)` | ancho minimo que pide la pagina: `pack()` no abre la ventana mas angosta |
| `.build()` | devuelve la pagina |

```java
frame.setContentPane(Page.create()
    .top(Header.create("Titulo"))
    .center(content)
    .bottom(Footer.of(btnCancel, btnOk)));
```

`Page` **no** es un `Box`: no tiene `children`, `gap` ni `padding`. Para margenes pon un `Col` con `padding` en el centro.

### Pages

Varias paginas **en el mismo lugar**, de las que se ve una a la vez (pasos de un asistente, vistas de la ventana principal); usa
`CardLayout`.

```java
Pages pages = Pages.create().page("name", namePanel).page("type", typePanel);
pages.show("type");
```

### Scroll

Contenedor con barras de desplazamiento (`JScrollPane`). Dentro de un `Row` o `Col` ocupa el espacio que sobra.

| Metodo | Que hace |
|---|---|
| `Scroll.of(c)` | envuelve el componente |
| `.noBorder()` | sin borde alrededor |
| `.vertical()` | solo desplazamiento vertical: el contenido toma el ancho de la ventana (los textos largos se parten y las tarjetas no se salen por la derecha). Si el contenido ya es `Scrollable` (tabla, area de texto) se deja como esta |
| `.step(px)` | cuanto avanza la rueda del mouse |

```java
Scroll.of(table)
Scroll.of(content).noBorder().vertical()
```

### Tabs

Pestanas (`JTabbedPane`). Ocupa el espacio que sobra.

```java
Tabs.create().tab("General", general).tab("Avanzado", iconAdvanced, advanced)
```

Metodos: `.tab(titulo, contenido)` y `.tab(titulo, icono, contenido)`.

### Split

Dos zonas separadas por una **barra que se arrastra** (`JSplitPane`). Ocupa el espacio que sobra.

| Metodo | Que hace |
|---|---|
| `Split.horizontal(izq, der)` | una zona a la izquierda y otra a la derecha |
| `Split.vertical(arriba, abajo)` | una zona arriba y otra abajo |
| `.divider(px)` | posicion inicial de la barra |
| `.weight(0..1)` | cuanto del espacio extra se queda la primera zona al cambiar el tamano |

```java
Split.horizontal(list, details).divider(260)
Split.vertical(editor, console).weight(0.7)
```

### Separator

`Separator.horizontal()` / `Separator.vertical()`: linea fina. Dentro de un `Col` ocupa todo el ancho.

## Widgets

### Label

Etiqueta de texto (`JLabel`).

| Fabrica | Estilo |
|---|---|
| `Label.text(txt)` | texto normal |
| `Label.title(txt)` | titulo: negrita, 18 |
| `Label.field(txt)` | nombre de un campo: negrita, 15 |
| `Label.muted(txt)` | texto secundario: gris (`Kit.MUTED`) |

| Metodo | Que hace |
|---|---|
| `.icon(recurso, tam)` / `.icon(Icon)` | icono |
| `.iconGap(px)` | espacio entre icono y texto |
| `.small()` | un punto mas pequena que el texto normal |
| `.center()` / `.right()` | alineacion horizontal del texto |
| `.padding(arriba, izq, abajo, der)` | margen alrededor del texto |
| `.onClick(Runnable)` | la etiqueta se comporta como un **enlace**: cursor de mano y una accion al hacer clic |

```java
Label.text("Ayuda").icon("app/img/help_blue.svg", 16).onClick(this::openHelp)
```

### Hint

Texto de ayuda: pequeno, gris, y **se parte en lineas** al ancho que le toque. Extiende `JLabel`, **no** `Label` (son tipos distintos).

| Metodo | Que hace |
|---|---|
| `Hint.of(txt)` | ayuda de texto |
| `Hint.ofHtml(txt)` | igual, para un texto que ya viene en `<html>...</html>` (quita esas marcas) |
| `.width(px)` | ancho en el que parte el texto cuando hay espacio (325 por defecto) |
| `.html(txt)` | cambia el texto despues |

El texto se mete dentro de `<html>`: un `<` o un `&` sueltos se interpretan como HTML. Si el texto viene de un usuario o del sistema, escapalo.

### Button

`Button.of(txt)`: boton normal. `Button.primary(txt)` o `boton.primary()`: boton **principal** (color `Kit.PRIMARY`, texto blanco), la accion
que se espera del usuario. Hereda `Clickable`.

```java
Button.primary("Guardar").onClick(e -> save())
Button.of("Buscar").icon("app/img/search.svg", 16).iconGap(8)
```

### Check

`Check.of(txt)`: casilla de verificacion (`JCheckBox`). Hereda `Clickable`.

### Radio

`Radio.of(txt)`: opcion excluyente (`JRadioButton`). `Radio.group(Radio...)` agrupa para que solo una quede marcada.

### Toggle

`Toggle.of(txt)`: boton que queda presionado (`JToggleButton`): modo de vista, filtro. `Toggle.group(Toggle...)` los hace excluyentes.

### Switch

`Switch.of(txt)`: interruptor estilo iOS (`JCheckBox` con otros iconos), un si/no que se aplica al instante. Sus colores son SVG propios y no
siguen al acento del tema.

```java
Switch.of("Iniciar con el sistema").selected(true).onClick(this::onChanged)
```

### Input

Campo de texto de una linea (`JTextField`). Con `Input.password()` y `Input.area(filas)` salen los otros dos tipos.

| Metodo | Que hace |
|---|---|
| `Input.text()` | campo de texto normal |
| `.placeholder(txt)` | texto gris mientras el campo esta vacio |
| `.columns(n)` | ancho preferido en caracteres |
| `.editable(boolean)` | permitir o no escribir |
| `.onChange(Runnable)` | cada vez que cambia el contenido (por el usuario **o por codigo**) |
| `.onEnter(ActionListener)` | al presionar Enter |

### Password

`Input.password()` devuelve un `Password` (`JPasswordField`). Metodos: `.placeholder`, `.columns`, `.onChange`, `.onEnter`. Se lee con
`getPassword()` (un `char[]`), como en Swing.

### TextArea

`Input.area(filas)` devuelve un `TextArea` (`JTextArea`). Va dentro de un [`Scroll`](#scroll).

| Metodo | Que hace |
|---|---|
| `.placeholder(txt)` | texto gris mientras el area esta vacia (se oculta al escribir) |
| `.columns(n)` | ancho preferido en caracteres |
| `.editable(boolean)` | permitir o no escribir |
| `.wrap()` | las lineas largas se parten (por palabras) en vez de salirse |
| `.onChange(Runnable)` | cada vez que cambia el contenido |

### Combo

Lista desplegable (`JComboBox<T>`).

| Metodo | Que hace |
|---|---|
| `Combo.of(items...)` | con esos elementos |
| `Combo.empty()` | vacio, se llena despues (con un `ComboItem.Binder`, por ejemplo) |
| `.onChange(Runnable)` | cada vez que se elige un elemento (tambien si se elige por codigo) |
| `.maxRows(n)` | cuantos elementos se ven a la vez al desplegar |
| `.editable(boolean)` | el usuario puede escribir un valor que no esta en la lista |

Para mostrar una etiqueta y guardar otro valor (un enum, un id), usa [`ComboItem`](COMPONENTS.md#comboitem).

### Spinner

Selector numerico con flechas (`JSpinner`).

| Metodo | Que hace |
|---|---|
| `Spinner.integer(valor, min, max, paso)` | numeros enteros |
| `Spinner.decimal(valor, min, max, paso)` | numeros con decimales |
| `Spinner.of(SpinnerNumberModel)` | con un modelo propio (otro tipo de numero, limites abiertos con `null`) |
| `.onChange(Runnable)` | cada vez que cambia el valor |

### Table

Tabla (`JTable`). **Siempre dentro de un [`Scroll`](#scroll)**, si no el encabezado no se ve.

| Metodo | Que hace |
|---|---|
| `Table.of(TableModel)` | tabla con ese modelo (ver [`BaseTableModel`](COMPONENTS.md#basetablemodel)) |
| `.rowHeight(px)` | alto de cada fila |
| `.singleSelection()` | una sola fila seleccionada a la vez |
| `.onSelect(Runnable)` | cuando cambia la fila seleccionada (una sola vez por cambio) |
| `.columnWidth(columna, px)` | ancho preferido de una columna, por posicion en la vista |

```java
Table table = Table.of(model).rowHeight(28).singleSelection().onSelect(this::onRowSelected);
Scroll.of(table)
```

## Utilidades (`Kit`)

`Kit` tambien guarda la **paleta** del kit como campos `public static` (no finales): `PRIMARY`, `SURFACE`, `CARD_LINE`, `DARK`, `ON_DARK`, `MUTED`,
`LINE`, `ROW_ODD`, `ROW_EVEN`, `ROW_SELECTED`. Sus valores y como cambiarlos: [CUSTOMIZING.md](CUSTOMIZING.md#2-paleta-del-kit-kit).

| Metodo | Que hace |
|---|---|
| `Kit.px(int)` | escala un tamano en px con el zoom de la interfaz (igual en 100%) |
| `Kit.font(tamano, negrita)` | la fuente de la interfaz (la del tema) con ese tamano y estilo, escalada |
| `Kit.icon(recurso, tam)` | icono de un recurso del classpath: **SVG** (vectorial) o imagen (PNG, tal cual). Devuelve `null` si una imagen no existe |
| `Kit.icon(svg, tam, color)` | SVG de un solo color (blanco sobre un boton principal) |
| `Kit.makePrimary(AbstractButton)` | estilo de boton principal para cualquier boton ya creado |
| `Kit.group(AbstractButton...)` | agrupa botones: solo uno queda seleccionado |
| `Kit.fill(c, horizontal, vertical)` | marca que el componente ocupa el espacio que sobra en esos ejes dentro de un `Row`, `Col`, `Form`, `Card` o `Stack` |

`Kit.fill` es lo que usan por dentro `Scroll`, `Tabs`, `Split`, `Hint` y `Stack.fill`. En un `Row` o `Col` se prefiere `.fill(c)` / `.fillBoth(c)`.

## Mezclar con Swing puro

- Un `JPanel`, `JLabel` o cualquier `Component` de Swing se puede pasar a `children(...)`, `fill(...)`, `Page.center(...)`, etc.
- `.width(px)` / `.height(px)` y `Kit.fill` funcionan con **cualquier** `JComponent`, aunque no sea del kit: se guardan como propiedades del
  componente y el contenedor las lee.
- Si algo es muy puntual, un contenedor del kit (que es un `JPanel` con MigLayout) acepta restricciones crudas:
  `contenedor.add(comp, "growx, wrap")`. Se prefiere evitarlo; si lo necesitas seguido, mejor pide un metodo nuevo al kit.

## Errores comunes

| Sintoma | Causa y arreglo |
|---|---|
| `.width(200)` no hace nada | solo lo entienden los contenedores del kit (`Row`, `Col`, `Form`, `Card`, `Stack`, `Grid`). Dentro de un `JPanel` suelto o de `Page` no tiene efecto |
| La tabla no muestra el encabezado | falta envolverla en `Scroll.of(table)` |
| `Label`, `Button`, `List`, `TextArea` o `Stack` no compilan o toman la clase equivocada | chocan con `java.awt` (y `Stack` con `java.util`); usa imports **explicitos** (`import tessera.kit.Label;`), no `java.awt.*` ni `java.util.*` |
| Un `Button` dentro de un `Col` no se estira | es lo esperado: botones y etiquetas se quedan con su ancho natural; usa `.fill(boton)` |
| `onClick` no se dispara al hacer `setSelected(true)` por codigo | `onClick` solo atiende clics del usuario. Para reaccionar a cambios por codigo usa un `ItemListener` |
| `Combo.onChange` se dispara al llenarlo | tambien lo hace al elegir por codigo: registra el `onChange` **despues** de llenar y seleccionar |
| `illegal forward reference` al declarar campos | Java inicializa los campos en orden: el que se usa va antes que el que lo usa (ver [SCREENS.md](SCREENS.md#reglas)) |
| Un texto con `<` o `&` sale raro en un `Hint` o `Card.subtitle` | se interpreta como HTML; escapalo |
| Los colores no cambian al asignar `Kit.PRIMARY` | cada widget toma el color **al crearse**: asignalo antes de armar pantallas (mejor con `Theme.builder()`) |
