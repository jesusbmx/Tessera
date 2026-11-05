# Row

Pone sus hijos **en una fila**, de izquierda a derecha, cada uno con su ancho natural.

| | |
|---|---|
| **Paquete** | `tessera.kit` |
| **Clase** | `Row extends Box<Row>`: un `JPanel` transparente con MigLayout |
| **Se crea con** | `Row.gap(n)` · `Row.create()` |
| **Importar** | `import tessera.kit.Row;` |

## Cuando usarlo

- Un campo con su boton al lado (`fill`).
- Botones o texto pegados a la derecha (`glue`).
- Varios widgets uno junto a otro: las opciones de un grupo, una barra de acciones.
- Dos bloques lado a lado del mismo ancho (`fillEqual`).

**Cuando no:** Si necesitas que las columnas queden **alineadas entre filas**, usa `Form` (etiqueta y campo) o `Grid` (varias columnas).

## Lo que se puede hacer

Cada seccion muestra una cosa que se puede pedir, con la imagen de como queda y el codigo que la produce (el titulo de cada tarjeta de las imagenes es ese mismo codigo). En las imagenes, una caja de color (`zone(...)`) es solo una pieza dibujada y el borde punteado marca los limites de un contenedor: ver [como leer las imagenes](README.md#como-leer-las-imagenes).

Los hijos van **de izquierda a derecha**, cada uno con su ancho natural. Todo lo demas se pide con un metodo.

### Espacio entre hijos: `gap`

![Row: gap](../../../img/box-row-gap.png)

```java
Row.gap(0).children(a, b, c)         // pegados
Row.create().children(a, b, c)       // lo mismo que gap(8)
Row.gap(24).children(a, b, c)        // separados
```

`gap` es el espacio **entre** un hijo y el siguiente. El espacio entre el borde y los hijos es el [`padding`](#margen-interior-padding).

### Ocupar el ancho que sobra: `fill`

![Row: fill](../../../img/box-row-fill.png)

```java
Row.gap(10).children(a, b, c)                                            // cada uno con su ancho natural
Row.gap(10).fill(txtPath).children(btnBrowse)                            // campo + boton
Row.gap(10).children(lblFolder).fill(txtFolder).children(btnPick)        // etiqueta + campo + boton
Row.gap(10).fill(txtName, txtLastName)                                   // dos campos, se reparten el ancho
Row.gap(10).fill(txtSearch).children(cbStatus, btnSearch)                // campo + combo + boton
```

Lo que **no** se pasa por `fill` conserva su ancho natural: ese es el truco de un campo con su boton.

### Repartir el ancho: `fill` frente a `fillEqual`

![Row: fill frente a fillEqual](../../../img/box-row-equal.png)

```java
Row.gap(10).fill(zoneA, zoneB)                    // se reparten lo que sobra: cada uno suma su ancho natural
Row.gap(10).fillEqual(zoneA, zoneB)               // miden exactamente lo mismo
Row.gap(10).fillEqual(a, b, c)                    // tres iguales
Row.gap(10).fillEqual(a, b).children(btnOk)       // dos iguales y un boton con su ancho natural
```

Con `fill`, un hijo con mas texto sale mas ancho. Con `fillEqual` todos miden lo mismo aunque sus textos sean distintos: es lo que se usa para dos
columnas lado a lado.

### Ocupar tambien el alto: `fillBoth`

![Row: fillBoth](../../../img/box-row-fillboth.png)

```java
Row.gap(8).padding(8).fill(scrollArea).children(btnOk)                   // solo el ancho
Row.gap(8).padding(8).fillBoth(scrollArea).children(btnOk)               // ancho y alto
Row.gap(8).padding(8).top().fillBoth(scrollArea).children(Col.gap(6).children(btnOpen, btnSave))
Row.gap(8).padding(8).fillBoth(scrollA, scrollB)                         // se reparten el ancho, los dos altos
```

El alto sobrante solo existe cuando el `Row` mide mas que sus hijos (por ejemplo, dentro de un `Col.fillBoth` o con `.height(px)`).

### Empujar con `glue`

![Row: glue](../../../img/box-row-glue.png)

```java
Row.gap(8).glue().children(btnCancel, btnSave)                                    // a la derecha
Row.gap(8).children(lblInfo).glue().children(btnSave)                             // uno a cada lado
Row.gap(8).glue().children(btnPrev, btnNext).glue()                               // centrados
Row.gap(8).children(btnBack).glue().children(lblStep).glue().children(btnNext)    // tres grupos
```

`glue()` es un hueco elastico: lo que se agrega **despues** queda pegado al otro extremo. Dos `glue()` se reparten el espacio por igual.

### Alineacion vertical: `top` y `center`

![Row: alineacion vertical](../../../img/box-row-align.png)

```java
Row.gap(10).children(lblNotes, scrollNotes, btnSave)               // centrados (por defecto)
Row.gap(10).top().children(lblNotes, scrollNotes, btnSave)         // arriba
Row.gap(10).top().center().children(lblNotes, scrollNotes, btnSave) // center() vuelve al centro
```

Solo se nota cuando los hijos miden distinto de alto. Una etiqueta junto a un area de texto casi siempre pide `top()`.

### Margen interior: `padding`

![Row: padding](../../../img/box-row-padding.png)

```java
Row.gap(6).children(a, b)                          // padding(0)
Row.gap(6).padding(12).children(a, b)              // igual por los cuatro lados
Row.gap(6).padding(4, 24).children(a, b)           // vertical, horizontal
Row.gap(6).paddingVertical(16).children(a, b)      // solo arriba y abajo
Row.gap(6).paddingHorizontal(32).children(a, b)    // solo izquierda y derecha
Row.gap(6).padding(0, 0, 0, 40).children(a, b)     // arriba, izquierda, abajo, derecha
```

`paddingVertical` y `paddingHorizontal` cambian **un solo eje** y conservan el otro: `padding(12).paddingHorizontal(24)` deja 12 arriba y abajo y 24 a los lados.
`padding()` sin argumentos devuelve una copia del margen actual (`Insets`).

### Anchos fijos: `width`

![Row: anchos fijos](../../../img/box-row-width.png)

```java
Row.gap(8).children(
    Input.text().placeholder("CP").width(80),
    Input.text().placeholder("Ciudad").width(160),
    Combo.of("Mexico", "Chile").width(110))

Row.gap(8).children(Combo.of("A4", "Carta").width(100))        // ancho fijo + campo que toma el resto
    .fill(Input.text().placeholder("Descripcion"))
    .children(Spinner.integer(1, 1, 99, 1).width(70))
```

`.width(px)` y `.height(px)` piden un tamano fijo y los respetan `Row`, `Col`, `Form`, `Card` y `Stack`.

### Ocultar sin dejar hueco

![Row: ocultar](../../../img/box-row-hidden.png)

```java
Row.gap(8).children(btnOne, btnTwo, btnThree)                           // todos visibles
Row.gap(8).children(btnOne, btnTwo.visible(false), btnThree)            // Dos oculto: no deja hueco
Row.gap(8).fill(txtIp).children(btnScan.visible(false))                 // el campo toma su lugar
```

Un hijo con `setVisible(false)` (o `.visible(false)`) **no ocupa espacio ni agrega su `gap`**: en la segunda fila, `Uno` y `Tres` quedan separados por un solo `gap`.

### Patrones listos

![Row: patrones comunes](../../../img/box-row-patterns.png)

```java
// barra de herramientas
Row.gap(8).fill(txtSearch).children(cbStatus, btnSearch, btnNew)

// barra de estado
Row.gap(8).children(lblReady).glue().children(lblCount, lblVersion)

// paginacion
Row.gap(6).glue().children(btnPrev, btn1, btn2, btn3, btnNext).glue()

// opciones una al lado de otra
Row.gap(14).children(rbtnDriver, rbtnNetwork, rbtnUsb)
```

## Referencia

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

`children(...)` agrega con el ancho natural, salvo que el hijo ya este marcado con `Kit.fill(...)` (ver [Utilidades](../README.md#utilidades-kit)).

## Ejemplos completos donde se usa

Aparece en casi todos los ejemplos. Pantallas completas y recetas:

<details>
<summary>Ver los 12 ejemplos</summary>

- [Dialogo clasico](../../../guides/screens.md#dialogo-clasico)
- [Barra de herramientas, tabla y barra de estado](../../../guides/screens.md#barra-de-herramientas-tabla-y-barra-de-estado)
- [Lista y detalle](../../../guides/screens.md#lista-y-detalle)
- [Menu lateral](../../../guides/screens.md#menu-lateral)
- [Asistente de pasos](../../../guides/screens.md#asistente-de-pasos)
- [Tablero con indicadores](../../../guides/screens.md#tablero-con-indicadores)
- [Dos columnas de bloques](../../../guides/screens.md#dos-columnas-de-bloques)
- [Formulario centrado (inicio de sesion)](../../../guides/screens.md#formulario-centrado-inicio-de-sesion)
- [Editor y consola](../../../guides/screens.md#editor-y-consola)
- [Formulario con acciones](../../../guides/layout.md#formulario-con-acciones)
- [Tablero](../../../guides/layout.md#tablero)
- [Busqueda, tabla y acciones](../../../guides/layout.md#busqueda-tabla-y-acciones)

</details>

## Errores comunes

- Un campo no se estira junto a su boton: falta `.fill(campo)`; con `.children(campo)` queda con su ancho natural.
- Los hijos quedan a la izquierda y quieres uno a la derecha: pon `.glue()` antes del que debe ir a la derecha.
- Dos bloques lado a lado miden distinto: usa `fillEqual(a, b)` en vez de `fill(a, b)`.
- Los dos valores de `padding(a, b)` son **vertical, horizontal** (no izquierda, arriba). Para cambiar un solo eje usa `paddingVertical(px)` o `paddingHorizontal(px)`.

## Ver tambien

- [`Col`](col.md) · [`Form`](form.md) · [`Grid`](grid.md) · [`Stack`](stack.md) · [`Footer`](footer.md)
- [Referencia del kit](../README.md): ideas base y metodos comunes (`Styled`, `Clickable`, `Box`).
- [Contenedores](README.md): la tabla "que uso para cada cosa" y todos los contenedores.
- [Lo comun a los contenedores (`Box`)](box.md): `gap`, `padding`, `children`, tamano fijo y ocultar.
- [Como armar una pantalla](../../../guides/screens.md#pantallas-completas): pantallas completas que los combinan.
