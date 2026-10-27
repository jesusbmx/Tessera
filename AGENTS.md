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
Fuente Inter empaquetada por defecto; `.font("Segoe UI")` usa una del sistema. `Theme.fontFamily()`, `Theme.accent()` para codigo propio. `arc` afecta botones, campos y barras, **no** a `Card` (fijo en 12). `accent` hoy no cambia el color de radios, casillas ni barras de progreso (workaround y detalle en docs/CUSTOMIZING.md).
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
- `Label.text/title/field/muted(txt)` (+ `.icon .small() .center() .right() .padding(t,l,b,r) .onClick(Runnable)`) · `Hint.of(txt)` / `Hint.ofHtml(txt)` ayuda gris que se parte (`.width(px)`)
- `Button.of(txt)` / `Button.primary(txt)` · `Check.of` · `Radio.of` (+ `Radio.group(...)`) · `Toggle.of` (+ `Toggle.group(...)`) · `Switch.of` interruptor estilo iOS
- `Input.text()` / `Input.password()` / `Input.area(filas)` → `.placeholder(txt) .columns(n) .editable(b) .onChange(Runnable) .onEnter(listener)`; `TextArea.wrap()`
- `Combo.of(items...)` / `Combo.empty()` → `.onChange .maxRows .editable` · `Spinner.integer(v,min,max,paso)` / `.decimal(...)` → `.onChange`
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
`BaseTableModel<T>` (modelo por columnas con lista de T) · `CheckableListModel<T>` + `CheckBoxCellRenderer` · `ComboItem<K>` (etiqueta + valor)
· `CheckGroup<K>` (clave ↔ radios/checks) · `HighlightColumnRenderer`, `FlexibleColumnRenderer` (resaltar columnas) · `LabelListCell` (filas alternas)
· `ComboBoxRenderer` · `RoundedBorder`, `DashedBorder` · `ErrorProvider` (borde rojo) / `FloatingErrorProvider` (globo) con `setError(c, msg)`, `clear()`
· `DragAndDrop` (soltar archivos) · `TextUndoRedoManager` · `TextPlaceholder` (placeholder para JTextArea) · `PopupMenuExtension` · `WindowDragListener` · `InteractiveNavbar`.

## Recursos de la libreria
`tessera/fonts/Inter-*.ttf`, `tessera/img/switch_*.svg`, `info|warning_amber|success_green|error_red|arrow_down.svg`. Los iconos de la app van en los recursos
de la app (`"com/miapp/img/x.svg"`), no aqui.

## Personalizar y modificar
- Cambiar colores, esquinas, fuente, y que **no** se puede cambiar desde la app (ej. el radio de `Card` es fijo): [docs/CUSTOMIZING.md](docs/CUSTOMIZING.md).
- Convencion de pantallas con ejemplo completo: [docs/SCREENS.md](docs/SCREENS.md).
- Como esta hecha la libreria y como agregar o abrir cosas: [docs/CONTRIBUTING.md](docs/CONTRIBUTING.md).

## Pasar una app existente
Ver [docs/MIGRATING.md](docs/MIGRATING.md).
