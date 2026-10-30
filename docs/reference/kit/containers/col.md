# Col

Apila sus hijos **en una columna**, de arriba hacia abajo. Cada uno ocupa todo el ancho, salvo los botones y las etiquetas.

| | |
|---|---|
| **Paquete** | `tessera.kit` |
| **Clase** | `Col extends Box<Col>`: un `JPanel` transparente con MigLayout |
| **Se crea con** | `Col.gap(n)` · `Col.create()` |
| **Importar** | `import tessera.kit.Col;` |

## Cuando usarlo

- Apilar widgets de arriba abajo.
- El contenido de una ventana: `Col.gap(10).padding(12)`.
- Pegar algo al fondo (`glue`) o dar el alto sobrante a un hijo (`fillBoth`).

**Cuando no:** Para etiqueta + campo alineados usa `Form`; para una tabla de celdas, `Grid`.

## Lo que se puede hacer

Cada seccion muestra una cosa que se puede pedir, con la imagen de como queda y el codigo que la produce (el titulo de cada tarjeta de las imagenes es ese mismo codigo). En las imagenes, una caja de color (`zone(...)`) es solo una pieza dibujada y el borde punteado marca los limites de un contenedor: ver [como leer las imagenes](README.md#como-leer-las-imagenes).

Los hijos van **de arriba hacia abajo**. A diferencia de `Row`, cada hijo ocupa **todo el ancho** por defecto, **excepto** los botones y las etiquetas,
que se quedan a la izquierda con su ancho natural.

### La regla del ancho

![Col: la regla del ancho](../../../img/box-col-basic.png)

```java
Col.gap(6).children(
    Label.text("Etiqueta: ancho natural"),
    Button.of("Boton: ancho natural"),
    Check.of("Casilla: ancho natural"),
    Input.text().placeholder("Campo: todo el ancho"),
    Combo.of("Combo: todo el ancho"),
    Scroll.of(Input.area(2)))
```

### Cambiar la regla: `start` y `fill`

![Col: start y fill](../../../img/box-col-startfill.png)

```java
Col.gap(6).children(btnSave, txtField)                                         // por defecto
Col.gap(6).fill(btnSave).children(txtField)                                    // el boton ocupa todo el ancho
Col.gap(6).children(btnSave).start(Input.text().columns(6))                    // el campo conserva su ancho natural
```

### Espacio entre hijos: `gap`

![Col: gap](../../../img/box-col-gap.png)

```java
Col.gap(0).children(a, b, c)
Col.gap(4).children(a, b, c)
Col.gap(12).children(a, b, c)
Col.gap(24).children(a, b, c)
```

### Empujar hacia abajo: `glue`

![Col: glue](../../../img/box-col-glue.png)

```java
Col.gap(8).padding(8).children(lblTitle, txtField, btnSave)                  // sin glue: todo arriba

Col.gap(8).padding(8).children(lblTitle, txtField).glue().children(btnSave)  // el boton, abajo
Col.gap(8).padding(8).glue().children(lblTitle, btnSave)                     // todo abajo
Col.gap(8).padding(8).children(top).glue().children(middle).glue().children(bottom)
```

Igual que en `Row`: lo que se agrega **despues** del `glue()` queda pegado al fondo, y dos `glue()` se reparten el espacio. Necesita que el `Col` mida mas
que sus hijos: en estas imagenes lo da `.height(170)`; en una pantalla real, que el `Col` este en un lugar que se estire.

### Ocupar el alto que sobra: `fillBoth`

![Col: fillBoth](../../../img/box-col-fillboth.png)

```java
Col.gap(8).padding(8).children(txtSearch).fillBoth(scrollArea)                        // campo + area que crece
Col.gap(8).padding(8).fillBoth(scrollA, scrollB)                                      // se reparten el alto
Col.gap(8).padding(8).children(lblNotes).fillBoth(scrollArea)
    .children(Row.gap(6).glue().children(btnSave))                                    // titulo + area + botones
```

`Scroll`, `Tabs` y `Split` ya vienen marcados para ocupar el espacio que sobra: dentro de un `Col` o `Row` no hace falta `fillBoth`.

### Margen interior: `padding`

![Col: padding](../../../img/box-col-padding.png)

```java
Col.gap(6).children(a, b)                          // padding(0)
Col.gap(6).padding(12).children(a, b)
Col.gap(6).padding(4, 24).children(a, b)           // vertical, horizontal
Col.gap(6).paddingVertical(16).children(a, b)
Col.gap(6).paddingHorizontal(32).children(a, b)
Col.gap(6).padding(0, 0, 0, 40).children(a, b)     // arriba, izquierda, abajo, derecha
```

### Ocultar sin dejar hueco

![Col: ocultar](../../../img/box-col-hidden.png)

```java
Col.gap(6).children(txtUser, txtPass, btnEnter)                       // todos visibles
Col.gap(6).children(txtUser, txtPass.visible(false), btnEnter)        // la clave oculta: la columna se acorta
```

### Columnas dentro de filas y filas dentro de columnas

![Col: anidar contenedores](../../../img/box-col-nested.png)

```java
// menu lateral y contenido
Row.gap(12).top()
    .children(Col.gap(4).fill(btnServices, btnFiles, btnHistory, btnSettings))
    .fill(Col.gap(6).children(lblTitle, txtName, cbType))

// dos columnas iguales
Row.gap(12).top().fillEqual(
    Col.gap(6).children(lblFrom, txtFrom, chckSubfolders),
    Col.gap(6).children(lblTo, txtTo, chckOverwrite))

// grupos con su propio espacio
Col.gap(14).children(
    Col.gap(4).children(lblConnection, txtIp),
    Col.gap(4).children(lblPaper, cbPaper))
```

Anidar es la forma normal de armar una pantalla: cada `Row` o `Col` resuelve **una** cosa (una fila de botones, una columna de campos) y se combinan.

### Patrones listos

![Col: patrones comunes](../../../img/box-col-patterns.png)

```java
// inicio de sesion
Card.create("Iniciar sesion").children(
    Col.gap(8)
        .children(txtUser, txtPass, chckRemember)
        .fill(btnEnter))                                   // el boton a todo el ancho

// lista de ajustes: nombre a la izquierda, interruptor a la derecha
Col.gap(10).children(
    Row.gap(8).children(Label.text("Notificaciones")).glue().children(Switch.of("Si").selected(true)),
    Row.gap(8).children(Label.text("Sonido")).glue().children(Switch.of("No")),
    Row.gap(8).children(Label.text("Modo oscuro")).glue().children(Switch.of("No")))
```

## Referencia

Hijos **en una columna**, de arriba hacia abajo. Por defecto cada hijo ocupa **todo el ancho**, **excepto** los botones y las etiquetas
(`AbstractButton` y `JLabel`), que se quedan a la izquierda con su ancho natural.

| Metodo | Que hace |
|---|---|
| `Col.gap(n)` / `Col.create()` | columna con `n` px entre hijos / con 8 px |
| `.start(Component...)` | hijos con su ancho natural, a la izquierda |
| `.fill(Component...)` | hijos que ocupan todo el ancho (incluso botones y etiquetas) |
| `.fillBoth(Component...)` | hijos que ocupan todo el ancho **y** el alto que sobra (una tabla, un `Scroll`) |
| `.glue()` | hueco elastico: lo que se agregue despues queda pegado abajo |

```java
Col.gap(6).children(lblTitle, txtName, cbType)
Col.gap(8).children(Row.gap(10).fill(txtIp).children(btnScan)).fillBoth(scrollTable)
```

`Scroll`, `Tabs` y `Split` ya vienen marcados para ocupar el espacio que sobra: dentro de un `Col` o `Row` no hace falta `fillBoth`.

## Ejemplos completos donde se usa

Aparece en casi todos los ejemplos. Pantallas completas y recetas:

<details>
<summary>Ver los 19 ejemplos</summary>

- [Dialogo clasico](../../../guides/screens.md#dialogo-clasico)
- [Barra de herramientas, tabla y barra de estado](../../../guides/screens.md#barra-de-herramientas-tabla-y-barra-de-estado)
- [Lista y detalle](../../../guides/screens.md#lista-y-detalle)
- [Menu lateral](../../../guides/screens.md#menu-lateral)
- [Asistente de pasos](../../../guides/screens.md#asistente-de-pasos)
- [Tablero con indicadores](../../../guides/screens.md#tablero-con-indicadores)
- [Dos columnas de bloques](../../../guides/screens.md#dos-columnas-de-bloques)
- [Formulario centrado (inicio de sesion)](../../../guides/screens.md#formulario-centrado-inicio-de-sesion)
- [Ajustes con pestanas](../../../guides/screens.md#ajustes-con-pestanas)
- [1. Dialogo de inicio de sesion](../../../guides/recipes.md#1-dialogo-de-inicio-de-sesion)
- [2. Lista y detalle](../../../guides/recipes.md#2-lista-y-detalle)
- [3. Asistente de pasos](../../../guides/recipes.md#3-asistente-de-pasos)
- [4. Pantalla de ajustes](../../../guides/recipes.md#4-pantalla-de-ajustes)
- [5. Tarea larga con progreso](../../../guides/recipes.md#5-tarea-larga-con-progreso)
- [6. App que vive en la bandeja](../../../guides/recipes.md#6-app-que-vive-en-la-bandeja)
- [Formulario con acciones](../../../guides/layout.md#formulario-con-acciones)
- [Barra arriba, contenido y barra abajo](../../../guides/layout.md#barra-arriba-contenido-y-barra-abajo)
- [Tablero](../../../guides/layout.md#tablero)
- [Busqueda, tabla y acciones](../../../guides/layout.md#busqueda-tabla-y-acciones)

</details>

## Errores comunes

- Un boton o una etiqueta no se estira: es lo esperado; usa `.fill(boton)`.
- La tabla no muestra el encabezado o no ocupa el alto: falta `Scroll.of(table)` y/o `fillBoth`.
- Los dos valores de `padding(a, b)` son **vertical, horizontal** (no izquierda, arriba). Para cambiar un solo eje usa `paddingVertical(px)` o `paddingHorizontal(px)`.
- `glue()` no empuja nada: el `Col` mide solo lo que miden sus hijos y no sobra alto. Dale alto (`.height(px)`) o ponlo en un lugar que se estire (por ejemplo, dentro de un `fillBoth`).
- Para centrar un bloque en la ventana hace falta un `glue()` a cada lado **y** un ancho fijo: ver [`Page`](page.md#centrar-un-bloque-en-la-ventana).

## Ver tambien

- [`Row`](row.md) · [`Form`](form.md) · [`Card`](card.md) · [`Scroll`](scroll.md)
- [Referencia del kit](../README.md): ideas base y metodos comunes (`Styled`, `Clickable`, `Box`).
- [Contenedores](README.md): la tabla "que uso para cada cosa" y todos los contenedores.
- [Lo comun a los contenedores (`Box`)](box.md): `gap`, `padding`, `children`, tamano fijo y ocultar.
- [Como armar una pantalla](../../../guides/screens.md#pantallas-completas): pantallas completas que los combinan.
