# Componentes sueltos (`tessera.components`)

Piezas independientes que se usan **junto** a los widgets de Swing o del kit: modelos de tabla y lista, renderizadores, bordes, validacion de formularios, arrastrar y soltar,
deshacer/rehacer y otras utilidades. Ninguna depende de otra: se toma la que haga falta.

> **¿Buscas `Button`, `Label`, `Check`, `Input`, `Combo`, `Table`...?** Esos son **widgets del kit** (`tessera.kit`) y tienen su pagina en [la referencia del kit](../kit/README.md#widgets):
> [`Label`](../kit/widgets/label.md) · [`Hint`](../kit/widgets/hint.md) · [`Button`](../kit/widgets/button.md) · [`Check`](../kit/widgets/check.md) · [`Radio`](../kit/widgets/radio.md) · [`Toggle`](../kit/widgets/toggle.md) · [`Switch`](../kit/widgets/switch.md) · [`Input`](../kit/widgets/input.md) · [`Password`](../kit/widgets/password.md) · [`TextArea`](../kit/widgets/textarea.md) · [`Combo`](../kit/widgets/combo.md) · [`Spinner`](../kit/widgets/spinner.md) · [`Table`](../kit/widgets/table.md).

## Categorias

| Categoria | Que incluye |
|---|---|
| [Tablas](tables.md) | Modelo de tabla con una lista de objetos y renderizadores que resaltan columnas. |
| [Listas y combos](lists-and-combos.md) | Listas con casillas, elementos de combo con etiqueta y valor, grupos de opciones y filas alternas. |
| [Validacion de formularios](forms.md) | Marcar con borde rojo o con un globo los campos con error. |
| [Texto](text.md) | Placeholder para areas de texto, deshacer/rehacer y campos con opciones marcables. |
| [Ventanas y navegacion](windows-and-navigation.md) | Barra de navegacion con etiquetas y mover ventanas sin decoracion. |
| [Bordes](borders.md) | Bordes redondeados y punteados. |
| [Arrastrar y soltar](drag-and-drop.md) | Recibir archivos o texto soltados sobre un componente. |

## Cual uso para...

| Quiero... | Clase |
|---|---|
| llenar una tabla con una lista de objetos | [`BaseTableModel`](tables.md#basetablemodel) |
| una lista con casillas | [`CheckableListModel`](lists-and-combos.md#checkablelistmodel) |
| un combo que muestra un texto y guarda otro valor (enum, id) | [`ComboItem`](lists-and-combos.md#comboitem) |
| leer cual radio / casilla de un grupo esta marcado, como una clave de mi dominio | [`CheckGroup`](lists-and-combos.md#checkgroup) |
| filas alternas en una lista | [`LabelListCell`](lists-and-combos.md#labellistcell) |
| resaltar columnas de una tabla | [`HighlightColumnRenderer`](tables.md#highlightcolumnrenderer), [`FlexibleColumnRenderer`](tables.md#flexiblecolumnrenderer) |
| marcar campos con error al validar un formulario | [`ErrorProvider`](forms.md#errorprovider), [`FloatingErrorProvider`](forms.md#floatingerrorprovider) |
| recibir archivos arrastrados | [`DragAndDrop`](drag-and-drop.md#draganddrop) |
| Ctrl+Z / Ctrl+Y en un campo de texto | [`TextUndoRedoManager`](text.md#textundoredomanager) |
| texto de ayuda dentro de un `JTextArea` | [`TextPlaceholder`](text.md#textplaceholder) |
| un campo de texto con opciones marcables (extensiones, etiquetas) | [`PopupMenuExtension`](text.md#popupmenuextension) |
| barra de navegacion hecha con etiquetas | [`InteractiveNavbar`](windows-and-navigation.md#interactivenavbar) |
| mover una ventana sin decoracion | [`WindowDragListener`](windows-and-navigation.md#windowdraglistener) |
| bordes redondeados o punteados | [`RoundedBorder`](borders.md#roundedborder), [`DashedBorder`](borders.md#dashedborder) |
| elementos especiales en un combo (separador, "boton") | [`ComboBoxRenderer`](lists-and-combos.md#comboboxrenderer) |

## Interno

`CheckBoxCellRenderer` (el dibujo de las casillas de `CheckableListModel`) es de uso interno de la libreria (visibilidad de paquete): no se usa directamente.
