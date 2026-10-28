# Form

Formulario de **dos columnas**: la etiqueta a la izquierda y el campo a la derecha, que ocupa el ancho que sobra.

| | |
|---|---|
| **Paquete** | `tessera.kit` |
| **Clase** | `Form extends Box<Form>`: un `JPanel` transparente con MigLayout |
| **Se crea con** | `Form.create()` · `Form.gap(n)` |
| **Importar** | `import tessera.kit.Form;` |

![Form: row, rowTop y wide](../../../img/layout-form.png)

![Form: labelsRight y labelGap](../../../img/layout-form-labels.png)

## Cuando usarlo

- Cualquier formulario de etiqueta + campo.
- Un campo que lleva otro contenedor (un `Row` con un campo y un boton).

**Cuando no:** Con mas de dos columnas o varios pares por fila usa `Grid`.

## Referencia

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

## Errores comunes

- Al ocultar una fila queda la etiqueta: una fila son **dos** componentes; oculta los dos.
- Un boton o una etiqueta usados como campo se quedan con su ancho natural.
- Un campo con `.width(px)` fijo no se estira.

## Ver tambien

- [`Grid`](grid.md) · [`Card`](card.md) · [`Row`](row.md) · [`Label`](../widgets/label.md)
- [Referencia del kit](../README.md): ideas base y metodos comunes (`Styled`, `Clickable`, `Box`).
- [Guia visual de disposiciones](../../../guides/screens.md#guia-visual-de-disposiciones): cada contenedor con imagenes y pantallas completas.
