# TextArea

**Area de texto** de varias lineas. Se crea con `Input.area(filas)` y va dentro de un `Scroll`.

| | |
|---|---|
| **Paquete** | `tessera.kit` |
| **Clase** | `TextArea extends JTextArea implements Styled<TextArea>` |
| **Se crea con** | `Input.area(filas)` |
| **Importar** | `import tessera.kit.TextArea;` |

![TextArea: placeholder y wrap](../../../img/widget-textarea.png)

## Cuando usarlo

- Notas, descripciones, texto libre.

**Cuando no:** Para una sola linea usa `Input`.

## Referencia

`Input.area(filas)` devuelve un `TextArea` (`JTextArea`). Va dentro de un [`Scroll`](../containers/scroll.md).

| Metodo | Que hace |
|---|---|
| `.placeholder(txt)` | texto gris mientras el area esta vacia (se oculta al escribir) |
| `.columns(n)` | ancho preferido en caracteres |
| `.editable(boolean)` | permitir o no escribir |
| `.wrap()` | las lineas largas se parten (por palabras) en vez de salirse |
| `.onChange(Runnable)` | cada vez que cambia el contenido |

## Errores comunes

- Sin `Scroll`, no hay barras de desplazamiento: `Scroll.of(area)`.
- Dentro de un `Form`, usa `rowTop(...)` para que la etiqueta quede arriba.

## Ver tambien

- [`Input`](input.md) · [`Scroll`](../containers/scroll.md) · [`Form`](../containers/form.md) · [`TextPlaceholder`](../../components/text.md#textplaceholder)
- [Referencia del kit](../README.md): ideas base y metodos comunes (`Styled`, `Clickable`, `Box`).
