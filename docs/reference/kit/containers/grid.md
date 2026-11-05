# Grid

Cuadricula: varias celdas por fila, **alineadas en columnas** (cada columna mide lo que su celda mas ancha).

| | |
|---|---|
| **Paquete** | `tessera.kit` |
| **Clase** | `Grid extends Box<Grid>`: un `JPanel` transparente con MigLayout |
| **Se crea con** | `Grid.gap(n)` |
| **Importar** | `import tessera.kit.Grid;` |

## Cuando usarlo

- Una tabla de celdas (producto, cantidad, precio).
- Dos o mas pares etiqueta/campo en la misma fila.

**Cuando no:** Para un solo par etiqueta/campo por fila usa `Form`; para una lista de datos editable, `Table`.

## Lo que se puede hacer

Cada seccion muestra una cosa que se puede pedir, con la imagen de como queda y el codigo que la produce (el titulo de cada tarjeta de las imagenes es ese mismo codigo). En las imagenes, una caja de color (`zone(...)`) es solo una pieza dibujada y el borde punteado marca los limites de un contenedor: ver [como leer las imagenes](README.md#como-leer-las-imagenes).

### Columnas alineadas y varios pares por fila

Si necesitas **mas de dos columnas**, o varios pares etiqueta/campo por fila, usa `Grid`: cada columna mide lo que su celda mas ancha.

![Grid: columnas alineadas](../../../img/layout-grid.png)

```java
Grid.gap(6)
    .row(Label.field("Producto"), Label.field("Cantidad"), Label.field("Precio"))
    .row(Label.text("Etiqueta"), Label.text("100"), Label.text("12.50"))
    .row(Label.text("Cinta de impresion"), Label.text("4"), Label.text("30.00"))
```

```java
Grid.gap(8).columnGap(1, 40)
    .row(Label.field("DPI"), Combo.of("203", "300").width(120),
        Label.field("Ancho"), Spinner.integer(100, 10, 200, 1).width(80))
    .row(Label.field("Tipo"), Combo.of("Gap", "Continuo").width(120),
        Label.field("Velocidad"), Combo.of("2 ips", "4 ips").width(80))
```

| Metodo | Efecto |
|---|---|
| `Grid.gap(n)` | `n` px entre filas (y 6 entre columnas) |
| `.row(c1, c2, c3...)` | una fila; cada componente va a la columna que le toca por su **posicion** (un `null` deja la celda vacia) |
| `.columnGap(px)` | espacio entre **todas** las columnas |
| `.columnGap(despues, px)` | espacio solo **despues** de esa columna (la primera es la 0): aqui, mas aire entre los dos pares |

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

## Ejemplos completos donde se usa

Pantallas completas y recetas que lo usan, para ver `Grid` dentro de una aplicacion real:

- [Tablero con indicadores](../../../guides/screens.md#tablero-con-indicadores)
- [4. Pantalla de ajustes](../../../guides/recipes.md#4-pantalla-de-ajustes)

## Errores comunes

- Una fila con menos celdas que las demas deja sus columnas vacias: usa `null` para dejar un hueco explicito.
- `children(...)` pone cada componente en **su propia fila**, en la primera columna: para varias columnas usa `row(...)`.

## Ver tambien

- [`Form`](form.md) · [`Row`](row.md) · [`Col`](col.md)
- [Referencia del kit](../README.md): ideas base y metodos comunes (`Styled`, `Clickable`, `Box`).
- [Contenedores](README.md): la tabla "que uso para cada cosa" y todos los contenedores.
- [Lo comun a los contenedores (`Box`)](box.md): `gap`, `padding`, `children`, tamano fijo y ocultar.
- [Como armar una pantalla](../../../guides/screens.md#pantallas-completas): pantallas completas que los combinan.
