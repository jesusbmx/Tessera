# Texto (`tessera.components`)

Placeholder para areas de texto, deshacer/rehacer y campos con opciones marcables.

[← Componentes sueltos](README.md)

## `TextPlaceholder`

Texto gris de ayuda **dentro de un `JTextArea`** (o cualquier `JTextComponent`) mientras esta vacio; desaparece en cuanto el usuario escribe.

```java
final JTextArea area = new JTextArea(4, 30);                          // un JTextArea de Swing puro
TextPlaceholder.install(area, "Escribe aqui las notas...");

final TextArea notes = Input.area(4).wrap().placeholder("Escribe aqui las notas...");   // con el kit
```

Existe porque FlatLaf no pinta `placeholderText` en un `JTextArea`. **Con el kit no hace falta llamarla**: `Input.area(n).placeholder("...")` la usa por
dentro, y para `Input` y `Password` se usa `.placeholder(...)` (que si lo soporta FlatLaf). Es para un `JTextArea` o `JTextComponent` de Swing puro. Llamarla de
nuevo sobre el mismo componente solo cambia el texto. **Cambia el layout del componente** a `BorderLayout` para alojar la etiqueta.

## `TextUndoRedoManager`

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

## `PopupMenuExtension`

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
