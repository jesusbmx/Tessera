# Form

Formulario de **dos columnas**: la etiqueta a la izquierda y el campo a la derecha, que ocupa el ancho que sobra.

| | |
|---|---|
| **Paquete** | `tessera.kit` |
| **Clase** | `Form extends Box<Form>`: un `JPanel` transparente con MigLayout |
| **Se crea con** | `Form.create()` · `Form.gap(n)` |
| **Importar** | `import tessera.kit.Form;` |

## Cuando usarlo

- Cualquier formulario de etiqueta + campo.
- Un campo que lleva otro contenedor (un `Row` con un campo y un boton).

**Cuando no:** Con mas de dos columnas o varios pares por fila usa `Grid`.

## Lo que se puede hacer

Cada seccion muestra una cosa que se puede pedir, con la imagen de como queda y el codigo que la produce (el titulo de cada tarjeta de las imagenes es ese mismo codigo). En las imagenes, una caja de color (`zone(...)`) es solo una pieza dibujada y el borde punteado marca los limites de un contenedor: ver [como leer las imagenes](README.md#como-leer-las-imagenes).

### Filas: `row`, `rowTop`, `wide`, `field` y `space`

![Form: row, rowTop y wide](../../../img/layout-form.png)

```java
Form.create()
    .row("Nombre", Input.text().placeholder("Nombre completo"))
    .row("Carpeta", Row.gap(8).fill(Input.text().placeholder("Carpeta de salida")).children(Button.of("...")))
    .row("Tipo", Combo.of("Gap", "Continuo"))
    .row("Copias", Spinner.integer(1, 1, 99, 1).width(80))
    .rowTop("Notas", Scroll.of(Input.area(3).wrap()))
    .wide(Check.of("Imprimir al guardar"), 6)
    .wide(Hint.of("Lo que ocupa las dos columnas, como esta ayuda, se pone con wide(...)."))
```

| Fila | Para que |
|---|---|
| `.row("Etiqueta", campo)` | la fila normal; el campo puede ser **otro contenedor** (`Row` con un campo y un boton) |
| `.rowTop("Etiqueta", campo)` | etiqueta alineada **arriba**, junto a un campo alto (area de texto) |
| `.wide(c)` / `.wide(c, espacioArriba)` | una fila que ocupa **las dos columnas** (casilla, ayuda, titulo); el numero suma aire encima |
| `.field(c)` | la **ultima** fila, solo en la columna de campos (un enlace bajo un area de texto) |
| `.space(px)` | un hueco fijo para separar grupos de filas |

Un campo con **ancho fijo** (`Spinner...width(80)`) no se estira; el resto, si. Y **cuidado**: un boton o una etiqueta usados como campo se quedan con su ancho natural.

### Espaciado de las etiquetas

`labelsRight()` las pega a su campo; `labelGap(px)` cambia el espacio entre etiqueta y campo y `Form.gap(n)` el espacio entre filas:

![Form: labelsRight y labelGap](../../../img/layout-form-labels.png)

```java
Form.create().labelsRight()
    .row("Nombre", Input.text().placeholder("Nombre"))
    .row("Direccion", Input.text().placeholder("Direccion"))
    .row("Ciudad", Input.text().placeholder("Ciudad"))
```

```java
Form.gap(4).labelGap(30)
    .row("Usuario", Input.text().placeholder("Usuario"))
    .row("Correo", Input.text().placeholder("Correo"))
```

### Ocultar una fila

En un `Form`, una fila son **dos componentes**: para ocultar la fila hay que ocultar **la etiqueta y el campo** (si solo ocultas el campo, la etiqueta se queda). Por eso, cuando
una fila va a mostrarse y ocultarse, conviene guardar la etiqueta en una variable como `lblPass`.

El resto de lo que hace cualquier contenedor (margen, tamano fijo, ocultar) esta en [`Box`](box.md).

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

## Ejemplos completos donde se usa

Pantallas completas y recetas que lo usan, para ver `Form` dentro de una aplicacion real:

- [Dialogo clasico](../../../guides/screens.md#dialogo-clasico)
- [Lista y detalle](../../../guides/screens.md#lista-y-detalle)
- [Menu lateral](../../../guides/screens.md#menu-lateral)
- [Asistente de pasos](../../../guides/screens.md#asistente-de-pasos)
- [Dos columnas de bloques](../../../guides/screens.md#dos-columnas-de-bloques)
- [Formulario centrado (inicio de sesion)](../../../guides/screens.md#formulario-centrado-inicio-de-sesion)
- [Ajustes con pestanas](../../../guides/screens.md#ajustes-con-pestanas)
- [1. Dialogo de inicio de sesion](../../../guides/recipes.md#1-dialogo-de-inicio-de-sesion)
- [2. Lista y detalle](../../../guides/recipes.md#2-lista-y-detalle)
- [3. Asistente de pasos](../../../guides/recipes.md#3-asistente-de-pasos)

## Errores comunes

- Al ocultar una fila queda la etiqueta: una fila son **dos** componentes; oculta los dos.
- Un boton o una etiqueta usados como campo se quedan con su ancho natural.
- Un campo con `.width(px)` fijo no se estira.

## Ver tambien

- [`Grid`](grid.md) · [`Card`](card.md) · [`Row`](row.md) · [`Label`](../widgets/label.md)
- [Referencia del kit](../README.md): ideas base y metodos comunes (`Styled`, `Clickable`, `Box`).
- [Contenedores](README.md): la tabla "que uso para cada cosa" y todos los contenedores.
- [Lo comun a los contenedores (`Box`)](box.md): `gap`, `padding`, `children`, tamano fijo y ocultar.
- [Como armar una pantalla](../../../guides/screens.md#pantallas-completas): pantallas completas que los combinan.
