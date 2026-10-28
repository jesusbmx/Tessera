# Componentes sueltos (`tessera.components`)

Piezas independientes que se usan con widgets de Swing (o del kit): modelos de tabla y lista, renderizadores, bordes, validacion de
formularios, arrastrar y soltar, deshacer/rehacer y otras utilidades. Ninguna depende de otra: se toma la que haga falta.

## Cual uso para...

| Quiero... | Clase |
|---|---|
| llenar una tabla con una lista de objetos | [`BaseTableModel`](#basetablemodel) |
| una lista con casillas | [`CheckableListModel`](#checkablelistmodel) |
| un combo que muestra un texto y guarda otro valor (enum, id) | [`ComboItem`](#comboitem) |
| leer cual radio / casilla de un grupo esta marcado, como una clave de mi dominio | [`CheckGroup`](#checkgroup) |
| filas alternas en una lista | [`LabelListCell`](#labellistcell) |
| resaltar columnas de una tabla | [`HighlightColumnRenderer`](#highlightcolumnrenderer), [`FlexibleColumnRenderer`](#flexiblecolumnrenderer) |
| marcar campos con error al validar un formulario | [`ErrorProvider`](#errorprovider), [`FloatingErrorProvider`](#floatingerrorprovider) |
| recibir archivos arrastrados | [`DragAndDrop`](#draganddrop) |
| Ctrl+Z / Ctrl+Y en un campo de texto | [`TextUndoRedoManager`](#textundoredomanager) |
| texto de ayuda dentro de un `JTextArea` | [`TextPlaceholder`](#textplaceholder) |
| un campo de texto con opciones marcables (extensiones, etiquetas) | [`PopupMenuExtension`](#popupmenuextension) |
| barra de navegacion hecha con etiquetas | [`InteractiveNavbar`](#interactivenavbar) |
| mover una ventana sin decoracion | [`WindowDragListener`](#windowdraglistener) |
| bordes redondeados o punteados | [`RoundedBorder`](#roundedborder), [`DashedBorder`](#dashedborder) |
| elementos especiales en un combo (separador, "boton") | [`ComboBoxRenderer`](#comboboxrenderer) |

## Tablas

### `BaseTableModel`

Modelo de tabla **por columnas, con una lista de objetos T** (cada fila es un `T`). Es abstracto: solo se escribe `getValueAt`.

```java
class PrinterModel extends BaseTableModel<Printer> {
  PrinterModel() {
    super("Impresora", "Puerto", "Estado");                  // nombres de las columnas
  }

  @Override
  public Object getValueAt(int row, int col) {
    final Printer p = getRow(row);
    switch (col) {
      case 0: return p.name;
      case 1: return p.port;
      default: return p.status;
    }
  }
}

final PrinterModel model = new PrinterModel();
model.setItems(printers);                                    // llena y avisa a la tabla
final Table table = Table.of(model).rowHeight(28);
Scroll.of(table)
```

| Metodo | Que hace |
|---|---|
| `getRow(i)` / `getRows()` | la fila `i` / una **copia** de todas las filas |
| `addRow(item)` / `addRow(indice, item)` | agrega al final / en esa posicion |
| `setRow(indice, item)` | reemplaza esa fila |
| `removeRow(indice)` / `removeRow(item)` | quita una fila (devuelve el item; con `item` devuelve `null` si no estaba) |
| `removeAllRows(Collection<T>)` | quita varios items |
| `clearRows()` | vacia la tabla |
| `setItems(Collection<T>)` / `setItems(T...)` | **reemplaza** todo el contenido |
| `containsRow(item)` | si el item esta en la tabla |
| `getColumns()` / `addColumn(nombre)` / `setColumnIdentifiers(...)` / `clearColumns()` | manejo de columnas (`String...` o `List<String>`) |
| `rowsToArray(Class<T>)` | las filas como arreglo `T[]` |
| `iterator()` | se puede recorrer con `for (T item : model)` (sobre una copia) |
| `BaseTableModel.adjustColumnPreferredWidths(JTable)` | **estatico**: ajusta el ancho de cada columna al de su contenido mas ancho (mas 10 px) |

Cada cambio avisa a la tabla (`fireTable...`): no hay que llamar nada mas. El tipo de cada columna (`getColumnClass`) se deduce del valor de la primera
fila, asi que las columnas numericas se ordenan como numeros.

**Sin crear una clase**, hay dos modelos listos:

| Clase | Cada fila es... | Ejemplo |
|---|---|---|
| `BaseTableModel.TArray` | un `Object[]` | `model.addRow(new Object[] {"Zebra", "USB001"})` |
| `BaseTableModel.TList<T>` | una `List<T>` | `model.addRow(Arrays.asList("Zebra", "USB001"))` |

```java
final BaseTableModel.TArray model = new BaseTableModel.TArray("Impresora", "Puerto");
model.addRow(new Object[] {"Zebra ZD420", "192.168.0.10"});
final Object port = model.getValueAt(0, "Puerto");          // tambien se lee y escribe por nombre de columna
```

### `HighlightColumnRenderer`

Pinta de **amarillo suave** las columnas que le indiques **por nombre** (el del encabezado). La fila seleccionada usa los colores normales de
seleccion y el resto queda en blanco.

```java
final HighlightColumnRenderer renderer = new HighlightColumnRenderer("Estado", "Total");
renderer.applyCellRenderer(table);                           // lo pone en todas las columnas
```

| Metodo | Que hace |
|---|---|
| `new HighlightColumnRenderer(String...)` / `(List<String>)` / `()` | las columnas a resaltar |
| `addHighlightedColumn(nombre)` / `removeHighlightedColumn(nombre)` | cambia la lista |
| `setHighlightedColumns(List<String>)` | reemplaza la lista |
| `applyCellRenderer(JTable)` | lo asigna como renderer de **todas** las columnas |

> El color de resaltado es fijo. Para colores propios, usa `FlexibleColumnRenderer`.

### `FlexibleColumnRenderer`

Como el anterior, pero con **fondo, texto y borde propios por columna**.

```java
final FlexibleColumnRenderer renderer = new FlexibleColumnRenderer("Estado");
renderer.setColumnBackground("Estado", new Color(220, 252, 231));
renderer.setColumnForeground("Estado", new Color(22, 101, 52));
renderer.applyToTable(table);
```

| Metodo | Que hace |
|---|---|
| `new FlexibleColumnRenderer(String... columnas)` | las columnas a resaltar |
| `addHighlightedColumn(c)` / `removeHighlightedColumn(c)` | cambia la lista |
| `setColumnBackground(c, color)` / `setColumnForeground(c, color)` / `setColumnBorder(c, borde)` | estilo de **una** columna |
| `setDefaultBackground(color)` / `setDefaultForeground(color)` / `setDefaultBorder(borde)` | estilo de las columnas resaltadas que no tengan el suyo (amarillo suave y texto negro por defecto) |
| `applyToTable(JTable)` | lo asigna a todas las columnas |

## Listas y combos

### `CheckableListModel`

Modelo de lista (`DefaultListModel<T>`) **con casilla por elemento**.

```java
final CheckableListModel<String> model = new CheckableListModel<>();
model.addElement("Etiqueta de producto");
model.addElement("Etiqueta de envio");

final JList<String> list = new JList<>();
model.configureList(list);                                   // renderer con casillas + clic que alterna

final List<String> elegidos = model.getCheckedItems();
```

| Metodo | Que hace |
|---|---|
| `configureList(JList<T>)` | deja la lista lista: casillas, seleccion simple, un clic alterna la casilla y asigna este modelo |
| `setChecked(indice, boolean)` / `isChecked(indice)` | marcar / leer un elemento |
| `checkAll(boolean)` | marcar o desmarcar todos |
| `clearChecks()` | desmarcar todo |
| `getCheckedItems()` | los elementos marcados (el orden **no esta garantizado**) |

Detalles: el estado se guarda por **elemento** (segun `equals`/`hashCode`): dos elementos iguales comparten casilla. `configureList` **quita los
`MouseListener` que la lista ya tuviera**, incluidos los del propio look and feel: es el precio de que un clic alterne la casilla; ponle a la lista un
`ListSelectionListener` si necesitas reaccionar a la seleccion.

### `ComboItem`

Un elemento para un combo que separa **lo que ve el usuario** (la etiqueta) del **valor real** (un enum, un id). El combo muestra `toString()` (la
etiqueta) y tu recuperas el valor ya tipado.

```java
enum Mode { DIRECT, TRANSFER }

final Combo<ComboItem<Mode>> cbMode = Combo.empty();
final ComboItem.Binder<Mode> mode = new ComboItem.Binder<>(
    new ComboItem<>("Termico directo", Mode.DIRECT),
    new ComboItem<>("Transferencia", Mode.TRANSFER));
mode.setup(cbMode);                                          // llena el combo
mode.selectValue(Mode.TRANSFER);                             // elige por valor
final Mode elegido = mode.getSelectedValue(Mode.DIRECT);     // lee el valor (o el de respaldo)
```

| Metodo | Que hace |
|---|---|
| `new ComboItem<>(etiqueta, valor)` / `getValue()` | el elemento |
| `ComboItem.getSelectedValue(combo)` / `(combo, porDefecto)` | el valor seleccionado (o `null` / el de respaldo si no hay seleccion) |
| `ComboItem.selectValue(combo, valor)` | elige el primer item con ese valor; si no esta, no cambia nada |
| `ComboItem.setupModel(combo, lista)` | le pone el modelo al combo |
| `ComboItem.itemsOf(items...)` | una lista mutable de items |
| `ComboItem.valuesOf(items...)` | la lista de sus valores |
| `ComboItem.fromEnum(valores, e -> etiqueta)` | un arreglo de items a partir de un enum |
| `ComboItem.Binder<T>` | une la lista de items a un combo: `setup(combo)`, `selectValue(v)`, `getSelectedValue()`, `getSelectedValue(porDefecto)`. Lanza `IllegalStateException` si se usa antes de `setup` |

### `CheckGroup`

Une **claves de tu dominio** con los botones que las representan (`Radio`, `Check`, `Toggle`), para leer y fijar la seleccion sin `if` en cadena.

```java
final CheckGroup<Mode> modes = new CheckGroup<Mode>()
    .add(Mode.DIRECT, rbtnDirect)
    .add(Mode.TRANSFER, rbtnTransfer);

modes.select(Mode.TRANSFER);                                 // marca ese y desmarca el resto
final Mode actual = modes.selectedKeyOr(Mode.DIRECT);        // la clave marcada, o la de respaldo
```

| Metodo | Que hace |
|---|---|
| `add(clave, boton)` / `addAll(Map)` | agrega elementos (no admite `null`) |
| `select(clave)` | marca esa clave y desmarca las demas; si no existe, no marca ninguna |
| `selectedKey()` | un `Optional<K>` con la clave marcada |
| `selectedKeyOr(porDefecto)` | la clave marcada o el valor de respaldo |
| `clearSelection()` | desmarca todo |
| `size()` / `isEmpty()` / `asMap()` | tamano y una vista inmutable |

`CheckGroup` **no** agrupa los botones para que sean excluyentes: para eso usa `Radio.group(...)` o `Kit.group(...)`.

### `LabelListCell`

Renderizador de filas de lista con **colores alternos** (`Kit.ROW_ODD`, `Kit.ROW_EVEN`) y fila seleccionada en `Kit.ROW_SELECTED`, con 8 px de margen.

```java
list.setCellRenderer(new LabelListCell());                   // en una lista
UIManager.put("List.cellRenderer", new LabelListCell());     // o en todas las listas de la app (antes de crearlas)
```

### `ComboBoxRenderer`

Renderizador de combos: pinta el `toString()` del elemento, o **el propio componente si el elemento ya es un `Component`**. Trae dos
elementos listos para meter en un combo:

| Clase | Que es |
|---|---|
| `ComboBoxRenderer.Separator` | una linea divisoria entre elementos |
| `ComboBoxRenderer.Button(int id, String texto)` / `(int id, Icon icono)` | un elemento con aspecto de enlace (color de acento) y un `id` para distinguirlo ("Actualizar lista...") |

```java
final JComboBox<Object> cb = new JComboBox<>(new Object[] {
    "Impresora A", "Impresora B", new ComboBoxRenderer.Separator(), new ComboBoxRenderer.Button(1, "Actualizar...")});
cb.setRenderer(new ComboBoxRenderer());
cb.addActionListener(e -> {
  if (cb.getSelectedItem() instanceof ComboBoxRenderer.Button) {
    final int id = ((ComboBoxRenderer.Button) cb.getSelectedItem()).id;
    // atender la accion `id` y volver a elegir un valor real
  }
});
```

## Formularios

### `ErrorProvider`

Marca con **borde rojo y tooltip** los campos con error y los **restaura** con `clear()`.

```java
private final ErrorProvider errors = new ErrorProvider();

private boolean validate() {
  errors.clear();                                            // quita los errores de la vez anterior
  if (txtName.getText().trim().isEmpty()) {
    errors.setError(txtName, "El nombre es obligatorio");
    return false;
  }
  if (errors.notIsEmail(txtEmail, "Correo no valido")) {     // true cuando NO es un correo
    return false;
  }
  return true;
}
```

| Metodo | Que hace |
|---|---|
| `setError(componente, mensaje)` | borde rojo + tooltip con el mensaje + **da el foco** al campo + muestra un [`Toast`](NOTIFICATIONS.md#toast) de error de 3 s |
| `clear()` | restaura bordes y tooltips originales de todos los campos marcados |
| `size()` | cuantos campos estan marcados |
| `notIsEmail(JTextComponent, mensaje)` | recorta el texto y, si **no** tiene forma de correo, marca el error y devuelve `true` (ojo con el nombre: `true` = hay error) |
| `getError(componente)` | el mensaje del error de ese campo, o `null` si no esta marcado |

Como `setError` **mueve el foco**, marca un solo error por pasada (el primero) y sal con `return`, como en el ejemplo. Si llamas a `setError` otra
vez sobre el mismo campo, solo cambia el mensaje (el borde no se acumula) y `clear()` lo deja como estaba.

### `FloatingErrorProvider`

La misma idea con un **globo flotante debajo del campo** en vez de borde rojo y toast.

```java
private final FloatingErrorProvider errors = new FloatingErrorProvider();
// igual que ErrorProvider: errors.clear(); errors.setError(txtName, "..."); errors.notIsEmail(txtEmail, "...");
```

| Metodo | Que hace |
|---|---|
| `setError(componente, mensaje)` | muestra el globo bajo el campo y le da el foco. **No hace nada si el componente esta oculto** (`isVisible()` es `false`) |
| `removeError(componente)` | cierra el globo de ese campo |
| `clear()` | cierra todos |
| `getError(componente)` | el mensaje mostrado, o `null` |
| `size()` | cuantos globos hay |
| `notIsEmail(JTextComponent, mensaje)` | igual que en `ErrorProvider` (`true` = no es correo) |

El globo mide unos 250 px de ancho y se parte en lineas. El mensaje se inserta como HTML: **no incluyas texto del usuario sin escapar**.

## Texto

### `TextPlaceholder`

Texto gris de ayuda **dentro de un `JTextArea`** (o cualquier `JTextComponent`) mientras esta vacio; desaparece en cuanto el usuario escribe.

```java
final JTextArea area = new JTextArea(4, 30);                          // un JTextArea de Swing puro
TextPlaceholder.install(area, "Escribe aqui las notas...");

final TextArea notes = Input.area(4).wrap().placeholder("Escribe aqui las notas...");   // con el kit
```

Existe porque FlatLaf no pinta `placeholderText` en un `JTextArea`. **Con el kit no hace falta llamarla**: `Input.area(n).placeholder("...")` la usa por
dentro, y para `Input` y `Password` se usa `.placeholder(...)` (que si lo soporta FlatLaf). Es para un `JTextArea` o `JTextComponent` de Swing puro. Llamarla de
nuevo sobre el mismo componente solo cambia el texto. **Cambia el layout del componente** a `BorderLayout` para alojar la etiqueta.

### `TextUndoRedoManager`

Deshacer y rehacer con **Ctrl+Z** y **Ctrl+Y** en un campo de texto.

```java
TextUndoRedoManager.setupUndo(txtNotes);                     // una linea
```

| Metodo | Que hace |
|---|---|
| `TextUndoRedoManager.setupUndo(JTextComponent)` | estatico: crea el manager y lo conecta |
| `new TextUndoRedoManager()` + `addUndoableEditListener(JTextComponent)` | lo mismo con una instancia que puedes conservar |
| `undo()` / `redo()` | por codigo, p. ej. desde un boton |
| `getUndoManager()` | el `UndoManager` de Swing |

Los atajos son `Ctrl` tambien en macOS (no `Cmd`).

### `PopupMenuExtension`

Convierte un **campo de texto** en un selector con **opciones marcables**: al hacer clic se despliega un menu de casillas, y el texto del campo pasa
a ser la lista de lo marcado **separada por comas**. Sirve para extensiones de archivo, etiquetas, filtros.

```java
final Input txtExt = Input.text();
final PopupMenuExtension ext = new PopupMenuExtension(txtExt);
ext.setChips("png,jpg,gif,pdf");                             // las opciones disponibles
// el usuario marca "png" y "pdf"  ->  txtExt.getText() es "png,pdf"
```

| Metodo | Que hace |
|---|---|
| `new PopupMenuExtension(JTextField)` | conecta el campo (le pone el icono de flecha y el clic que despliega el menu) |
| `setChips(String)` / `setChips(List<String>)` | las opciones (la version `String` separa por comas, **sin espacios**) |
| `getChips()` | las opciones |
| `showPopup()` / `closePopup()` | abrir / cerrar el menu por codigo |

Escribe las opciones en **minusculas**: el texto del campo se compara y se guarda en minusculas, y una opcion se ve marcada si el texto la
**contiene** (como subcadena).

## Ventanas y navegacion

### `InteractiveNavbar`

Barra de navegacion hecha con **etiquetas** (o cualquier componente): cursor de mano, color al pasar el mouse y color para el elemento seleccionado. Se
arma con un `Builder`.

```java
final InteractiveNavbar nav = new InteractiveNavbar.Builder()
    .foreground(Kit.MUTED)
    .selectedForeground(Theme.accent())
    .items(lblHome, lblSettings, lblAbout)
    .build();
nav.setSelectedItem(lblHome);                                // aplica los colores iniciales
```

| Metodo del `Builder` | Que hace |
|---|---|
| `foreground(Color)` | color normal (negro por defecto) |
| `selectedForeground(Color)` | color del seleccionado (azul por defecto) |
| `hoverForeground(Color)` / `hoverSelectedForeground(Color)` | color al pasar el mouse (por defecto, el normal / el del seleccionado, 1.5 veces mas claro) |
| `items(Component...)` | los elementos |
| `build()` | crea la barra |

Instancia: `setSelectedItem(Component)` y `updateComponents()`. La barra **solo cambia colores**: la accion de cada elemento se la das tu
(`Label.onClick(...)`). Llama a `setSelectedItem` despues de `build()` para que los colores iniciales se apliquen.

### `WindowDragListener`

Mueve una **ventana sin decoracion** arrastrando un componente (tu propia barra de titulo).

```java
final JFrame frame = new JFrame();
frame.setUndecorated(true);
new WindowDragListener(frame).setup(titleBar);               // al arrastrar titleBar se mueve la ventana
```

## Bordes

### `RoundedBorder`

Borde redondeado que ademas **rellena** con el fondo actual del componente. El componente debe ser **no opaco** (`setOpaque(false)`), si no el
fondo rectangular de Swing asoma en las esquinas.

| Constructor / metodo | Resultado |
|---|---|
| `new RoundedBorder()` | radio 20, gris claro |
| `new RoundedBorder(radio)` | gris claro |
| `new RoundedBorder(radio, color)` | con ese color de trazo |
| `RoundedBorder.createCardBorder(colorMargen, radio)` | borde de **tarjeta de lista**: margen exterior relleno de `colorMargen` (separa tarjetas) + relleno redondeado + trazo fino |

```java
panel.setOpaque(false);
panel.setBorder(new RoundedBorder(12, new Color(200, 200, 200)));
```

### `DashedBorder`

Borde **punteado** con esquinas redondeadas; da la pista de "clic para elegir" en campos de solo lectura que abren un selector de archivo o carpeta.

```java
txtPath.setEditable(false);
txtPath.setBorder(new DashedBorder(8, Color.GRAY));                      // radio y color
txtPath.setBorder(new DashedBorder(8, Color.GRAY, 4f, 3f));              // ademas largo del trazo y del hueco
```

## Arrastrar y soltar

### `DragAndDrop`

Receptor de **archivos (o texto) soltados** sobre un componente: sobrescribe el metodo que te interese.

```java
new DropTarget(panelDrop, new DragAndDrop() {
  @Override
  public void dropFile(File file) {                          // se llama una vez por archivo
    txtPath.setText(file.getAbsolutePath());
  }
});
```

| Metodo a sobrescribir | Se llama cuando |
|---|---|
| `dropFile(File)` | se suelta un archivo (una vez por cada uno) |
| `dropFiles(List<File>)` | se sueltan archivos; por defecto llama a `dropFile` con cada uno |
| `dropString(String)` | se suelta texto |

Si lo soltado no es ni archivo ni texto, o algo falla durante el procesamiento, **se ignora en silencio** (no hay excepcion ni aviso).

## Interno

`CheckBoxCellRenderer` (el dibujo de las casillas de `CheckableListModel`) es de uso interno de la libreria (visibilidad de paquete): no se usa directamente.
