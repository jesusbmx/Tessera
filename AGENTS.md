# Tessera: guia para IA y devs nuevos

Libreria de UI para Swing (Java 8, FlatLaf 3.7 + MigLayout). Lee este archivo en vez de recorrer los fuentes:
es la referencia completa de la API. Detalle y ejemplos: Javadoc de cada clase (cortas, con `<pre>` de uso).

## Reglas del proyecto
- Java 8: sin `var`, sin `List.of`, sin records. Compilar: `./build.sh` (o `./build.ps1`). No hay tests; se verifica con `./build.sh --demo` (Showcase).
- Sin dependencias externas fuera de FlatLaf, JSVG y MigLayout. Nada especifico de una app (nombres, rutas, claves de idioma) dentro de la libreria.
- Codigo y comentarios en espanol; mantener el estilo del archivo (2 espacios de indentacion).
- Sin datos sensibles (nombres de empresas, correos, URLs, claves) en codigo, docs ni ejemplos: el proyecto puede ser open source.
- Al agregar un widget o contenedor: Javadoc corto con ejemplo, metodos encadenables, sin cambiar la firma de lo que ya existe (las apps lo usan).

## Mapa
```
tessera.kit         contenedores y widgets (lo que mas se usa)
tessera.theme       Theme (instala el tema), Icons (SVG, switch)
tessera.notify      Notification (apiladas), Toast (una a la vez)
tessera.app         TrayDock (bandeja + Dock macOS), WindowPrefs (tamano de ventanas)
tessera.components  modelos/renderers/bordes/validacion sueltos
```

## Tema (una vez, antes de crear ventanas)
```java
Theme.builder().accent("#1976D2").primary("#1976D2").panelBackground("#F7F6F3")
     .surface("#FFFFFF").cardLine("#E4E1D9").arc(12).install();
```
Fuente Inter empaquetada por defecto; `.font("Segoe UI")` usa una del sistema. `Theme.fontFamily()`, `Theme.accent()` para codigo propio. `arc` afecta botones, campos y barras, **no** a `Card` (fijo en 12). `accent` hoy no cambia el color de radios, casillas ni barras de progreso (workaround y detalle en docs/guides/customizing.md).
Despues de `install()` la app ajusta lo suyo con `UIManager.put(...)`. La paleta del kit (`Kit.PRIMARY`, `Kit.SURFACE`, `Kit.CARD_LINE`, `Kit.DARK`,
`Kit.ON_DARK`, `Kit.MUTED`, `Kit.LINE`, `Kit.ROW_ODD/ROW_EVEN/ROW_SELECTED`) son campos `static` no finales: se pueden asignar tambien a mano.

## Kit: contenedores
Todos son `JPanel` transparentes con MigLayout adentro (no hace falta saber MigLayout). Todos: `.padding(todo)`, `.padding(vert, horiz)`,
`.padding(arriba, izq, abajo, der)`, `.children(...)`, `.build()`.

| Clase | Uso |
|---|---|
| `Row.gap(n)` | hijos en fila. `.fill(c...)` ocupan el ancho que sobra, `.fillBoth`, `.fillEqual`, `.glue()` empuja lo siguiente a la derecha, `.top()`, `.center()` |
| `Col.gap(n)` | hijos en columna; todo ocupa el ancho salvo botones y JLabel. `.start(c)` ancho natural, `.fill(c)`, `.fillBoth(c)` ocupa tambien el alto, `.glue()` |
| `Stack.fill/start/end/center(c...)` | alternativas una encima de otra; los ocultos no ocupan espacio |
| `Grid.gap(n)` | `.row(celdas...)` filas de columnas alineadas; `.columnGap(px)`, `.columnGap(despues, px)` |
| `Form.create()` | `.row("Etiqueta", campo)`, `.row(JComponent, campo)`, `.rowTop(..)`, `.wide(c)`, `.field(c)`, `.space(px)`, `.labelGap(px)`, `.labelsRight()` |
| `Card.create(titulo)` | bloque con fondo suave y borde redondeado: `.icon(res)`, `.subtitle(txt)`, `.gap(n)`; `Card.section(titulo)` = compacto; `Card.create()` sin titulo; `Card.create(JComponent)` titulo propio |
| `Header.create(titulo, descripcion)` | banner oscuro de pasos de asistente; `.subtitle(..)`, `.corner(comp)` componente a la derecha del titulo |
| `Footer.of(botones...)` | barra de botones alineada a la derecha |
| `Page.create()` | `.top() .center() .bottom() .left() .right()`, `.minWidth(px)`, `.build()` (BorderLayout) |
| `Pages.create()` | `.page(nombre, c)`, `.show(nombre)` (CardLayout) |
| `Scroll.of(c)` | `.noBorder()`, `.vertical()`, `.step(px)` |
| `Tabs.create()` | `.tab(titulo, c)`, `.tab(titulo, icono, c)` |
| `Split.horizontal(a, b)` / `.vertical` | `.divider(px)`, `.weight(0..1)` |
| `Separator.horizontal()/vertical()` | linea fina |

## Kit: widgets
Extienden el de Swing (`JTextField txt = Input.text()...` sigue siendo un JTextField), con metodos encadenables.
- **Todos (`Styled`)**: `.tip(txt)` `.bold()` `.fontSize(n)` `.font(f)` `.color(c)` `.background(c)` `.enabled(b)` `.visible(b)` `.name(s)` `.width(px)` `.height(px)`
- **Botones y casillas (`Clickable`)**: `.onClick(Runnable|ActionListener)` `.icon(res, size)|.icon(Icon)` `.iconGap(n)` `.selected(b)`
- `Label.text/title/field/muted(txt)` (+ `.icon .small() .center() .right() .padding(t,l,b,r) .onClick(Runnable)`) · `Hint.of(txt)` / `Hint.ofHtml(txt)` ayuda gris que se parte al ancho que reciba (`.width(px)` es solo el ancho que *pide*: en `Col`/`Card`/`Form`/`Row` se estira)
- `Button.of(txt)` / `Button.primary(txt)` · `Check.of` · `Radio.of` (+ `Radio.group(...)`) · `Toggle.of` (+ `Toggle.group(...)`) · `Switch.of` interruptor estilo iOS
- `Input.text()` / `Input.password()` / `Input.area(filas)` → `.placeholder(txt) .columns(n) .editable(b) .onChange(Runnable) .onEnter(listener)`; `TextArea.wrap()`
- `Combo.of(items...)` / `Combo.empty()` → `.onChange .maxRows .editable` · `Spinner.integer(v,min,max,paso)` / `.decimal(...)` → `.onChange`
- **Botones que abren un menu** (extienden `PopupButton`: `.color .arrowLeft .noArrow .icon(res,tam) .iconGap .menu()`): `ChoiceButton.<T>of()` elige **una** y el boton la muestra → `.placeholder .option(valor, texto[, ayuda]) .optionEnabled(valor, b[, ayuda]) .onChange(Consumer<T>|Runnable)`, `getValue() setValue(v) is(v) hasValue() getValueTip()` · `MultiChoiceButton.<T>of(texto)` marca **varias** → `.option .checked(v) .onChange(Consumer<Set<T>>|Runnable)`, `getValues() isChecked(v) setChecked(v,b) setValues(c)` · `MenuButton.of(texto)` **acciones**, no queda nada elegido → `.item(texto, accion) .item(MenuItem) .title .separator .clear .onOpen(Consumer<Menu>)`. Sin boton (clic derecho): `Menu.create().item(..).title(..).separator()` + `menu.show(comp, x, y)` / `menu.showBelow(comp)`; `MenuItem.of(texto, accion)` → `.icon .iconRight .padding .accelerator(KeyStroke) .onClick`, `MenuItem.title(texto)`. `Menu` y `MenuItem` chocan con `java.awt`: imports explicitos. Cual usar y ejemplos con imagenes: [docs/guides/menus.md](docs/guides/menus.md). Que widget elegir (Check/Switch/Toggle, Radio/Combo/ChoiceButton, Input/TextArea/Spinner...) y que widget avisa de un cambio hecho por codigo: [docs/guides/widgets.md](docs/guides/widgets.md)
- `Table.of(model)` → `.rowHeight(px) .singleSelection() .onSelect(Runnable) .columnWidth(col, px)` (dentro de un `Scroll`)
- Utilidades en `Kit`: `Kit.px(n)` (escala con el zoom), `Kit.font(size, bold)`, `Kit.icon(recurso, size[, color])`, `Kit.group(botones...)`, `Kit.makePrimary(boton)`.
- Si algo es muy puntual: `contenedor.add(comp, "growx, wrap")` acepta restricciones MigLayout crudas (evitarlo).

## Patron de una pantalla
```java
public class MiPanel extends JPanel {
  // ... logica arriba (campos de estado, metodos, handlers) ...

  // ---------------------------------------------------------------------
  // UI: widgets y construcción de la pantalla
  // Cada bloque lleva sus campos (widgets) justo encima del método que lo arma:
  // campos del bloque -> método del bloque. Empieza en buildUI() y sigue el orden
  // en que se usan. La lógica (eventos, datos, etc.) va fuera de esta zona.
  // ---------------------------------------------------------------------

  private final Input txtName = Input.text().placeholder("Nombre");
  private final Button btnSave = Button.primary("Guardar").onClick(this::onSave);

  private void buildUI() {
    setLayout(new BorderLayout());
    add(Page.create().top(Header.create("Titulo", "Descripcion"))
        .center(Card.create("Datos").children(Form.create().row("Nombre", txtName)))
        .bottom(Footer.of(btnSave)).build());
  }
}
```
Reglas: los campos de cada bloque van justo encima del metodo que lo arma (si hay varios bloques: campos -> metodo, campos -> metodo);
un campo que usa otro va despues de el (orden de inicializacion de Java); eventos con `.onClick(this::metodo)`; si el original reaccionaba a `setSelected` por codigo usa `ItemListener`
(onClick solo atiende clics del usuario) y registralo despues de armar la pantalla; iconos SVG con `Kit.icon`/`Icons.svg`;
textos de la app con su propio sistema de idiomas (la libreria no trae uno).

## Avisos
```java
new Notification.Builder("id.unico").setTitle("Guardado").setMessage("Listo")
    .setStyle(Notification.Style.SUCCESS).setDuration(Notification.LENGTH_LONG)
    .setOnAction("Ver", n -> ...).setProgress(valor, max).show();      // mismo id = reemplaza
Notification.setAppName("Mi App");                                      // texto chico sobre el titulo
Toast.makeToast(ventana, "Copiado", Toast.Style.SUCCESS).show();       // uno a la vez, con cola
```
Estilos: `NORMAL SUCCESS ERROR WARNING`. Duraciones: `LENGTH_SHORT LENGTH_LONG LENGTH_INDEFINITE`.
`notification.setProgress(v, max)`, `.complete(titulo, msg, estilo, duracion)`, `.dismiss()`.

## Bandeja y ventanas
```java
TrayDock dock = TrayDock.create(imagen, "Mi App").onOpen(this::mostrar)
    .menu(() -> Arrays.asList(TrayDock.Item.title("Mi App"), TrayDock.Item.separator(), TrayDock.Item.of("Salir", this::salir)));
dock.install();  dock.refresh();  dock.remove();
TrayDock.onAppReopened(this::mostrar);                // macOS: clic en el Dock (Java 9+)

WindowPrefs.install("com.miapp.");                    // recuerda tamano de las ventanas del paquete
WindowPrefs.install(miStore, "com.miapp.");           // con almacen propio (interfaz WindowPrefs.Store)
```

## Componentes sueltos (`tessera.components`)
`BaseTableModel<T>` (modelo por columnas con lista de T) · `CheckableListModel<T>` (+ `CheckBoxCellRenderer` interno) · `ComboItem<K>` (etiqueta + valor)
· `CheckGroup<K>` (clave ↔ radios/checks) · `HighlightColumnRenderer`, `FlexibleColumnRenderer` (resaltar columnas) · `LabelListCell` (filas alternas)
· `ComboBoxRenderer` · `RoundedBorder`, `DashedBorder` · `ErrorProvider` (borde rojo) / `FloatingErrorProvider` (globo) con `setError(c, msg)`, `clear()`
(detalle y ejemplos de todos: [Componentes sueltos](docs/reference/components/README.md))
· `DragAndDrop` (soltar archivos) · `TextUndoRedoManager` · `TextPlaceholder` (placeholder para JTextArea de Swing puro; `Input.area(n).placeholder(..)` ya lo usa) · `PopupMenuExtension` · `WindowDragListener` · `InteractiveNavbar`.

## Recursos de la libreria
`tessera/fonts/Inter-*.ttf`, `tessera/img/switch_*.svg`, `info|warning_amber|success_green|error_red|arrow_down.svg`. Los iconos de la app van en los recursos
de la app (`"com/miapp/img/x.svg"`), no aqui.

## Trampas conocidas (verificadas)
- Imports **explicitos**: `Label`, `Button`, `List`, `TextArea` chocan con `java.awt` y `Stack` con `java.util`.
- `Hint.of(txt)` y `Card.subtitle(txt)` meten el texto en `<html>`: escapar `<` y `&`.
- `Toast.LENGTH_INDEFINITE` solo se cierra con `dismiss()` o su accion (un clic no lo cierra).
- `ErrorProvider.setError` mueve el foco y muestra un `Toast`: marcar un solo error por pasada. `notIsEmail` devuelve `true` cuando **no** es correo.
- `CheckableListModel.configureList` quita los `MouseListener` previos de la lista. `CheckBoxCellRenderer` es interno (no publico).
- `WindowPrefs.install(store, paquetes...)`: el almacen va **primero**. Un `JDialog` suelto no se detecta: `WindowPrefs.remember(ventana, clave)`.

## Documentacion completa
Todo en Markdown dentro de `docs/` (sin sitio web); indice en [docs/README.md](docs/README.md).
- **Referencia, una pagina por componente** (imagen, cuando usarlo, todos los metodos, errores comunes): [docs/reference/README.md](docs/reference/README.md). Contenedores y widgets de `tessera.kit` en `docs/reference/kit/containers/<nombre>.md` y `docs/reference/kit/widgets/<nombre>.md` (por ejemplo `row.md`, `button.md`, `check.md`); `Theme`/`Icons` en `reference/theme/`; avisos en `reference/notify/`; bandeja y ventanas en `reference/app/`; el resto de piezas sueltas en `reference/components/`.
- **Guias**: [primeros pasos](docs/guides/getting-started.md) · [pantallas: guia visual de disposiciones](docs/guides/screens.md) · [recetas](docs/guides/recipes.md) · [personalizar](docs/guides/customizing.md) · [migrar](docs/guides/migrating.md) · [FAQ](docs/guides/faq.md).

## Personalizar y modificar
- Cambiar colores, esquinas, fuente, y que **no** se puede cambiar desde la app (ej. el radio de `Card` es fijo): [docs/guides/customizing.md](docs/guides/customizing.md).
- Convencion de pantallas con ejemplo completo: [docs/guides/screens.md](docs/guides/screens.md).
- Como esta hecha la libreria y como agregar o abrir cosas: [docs/guides/contributing.md](docs/guides/contributing.md).

## Pasar una app existente
Ver [docs/guides/migrating.md](docs/guides/migrating.md).
