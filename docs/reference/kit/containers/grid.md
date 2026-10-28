# Grid

Cuadricula: varias celdas por fila, **alineadas en columnas** (cada columna mide lo que su celda mas ancha).

| | |
|---|---|
| **Paquete** | `tessera.kit` |
| **Clase** | `Grid extends Box<Grid>`: un `JPanel` transparente con MigLayout |
| **Se crea con** | `Grid.gap(n)` |
| **Importar** | `import tessera.kit.Grid;` |

![Grid: columnas alineadas](../../../img/layout-grid.png)

## Cuando usarlo

- Una tabla de celdas (producto, cantidad, precio).
- Dos o mas pares etiqueta/campo en la misma fila.

**Cuando no:** Para un solo par etiqueta/campo por fila usa `Form`; para una lista de datos editable, `Table`.

## Referencia

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

## Errores comunes

- Una fila con menos celdas que las demas deja sus columnas vacias: usa `null` para dejar un hueco explicito.
- `children(...)` pone cada componente en **su propia fila**, en la primera columna: para varias columnas usa `row(...)`.

## Ver tambien

- [`Form`](form.md) · [`Row`](row.md) · [`Col`](col.md)
- [Referencia del kit](../README.md): ideas base y metodos comunes (`Styled`, `Clickable`, `Box`).
- [Guia visual de disposiciones](../../../guides/screens.md#guia-visual-de-disposiciones): cada contenedor con imagenes y pantallas completas.
