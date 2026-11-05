# Combinar Row, Col y Stack

Una pantalla se arma **anidando contenedores**: cada `Row` o `Col` resuelve una sola cosa (una fila de botones, una columna de campos) y se combinan. Aqui hay cuatro
ejemplos que los combinan; cada uno dice **que usa**, con enlace a la pagina donde esa pieza se explica a fondo.

Para ver **todo lo que puede hacer cada pieza** por separado, con una imagen por cada cosa: [`Row`](../reference/kit/containers/row.md) · [`Col`](../reference/kit/containers/col.md) · [`Stack`](../reference/kit/containers/stack.md) y lo
que comparten todos, [`Box`](../reference/kit/containers/box.md). Para el resto de contenedores (`Form`, `Grid`, `Card`, `Page`...): [Contenedores](../reference/kit/containers/README.md).

Las imagenes con borde punteado dibujan los limites del contenedor para que se vea hasta donde llega (el punteado **no** forma parte de la libreria).

## Cual uso

| Quiero... | Uso | A fondo |
|---|---|---|
| poner cosas **una al lado de otra** | `Row.gap(n).children(a, b, c)` | [`Row`](../reference/kit/containers/row.md) |
| poner cosas **una debajo de otra** | `Col.gap(n).children(a, b, c)` | [`Col`](../reference/kit/containers/col.md) |
| que **una** cosa ocupe lo que sobra a lo ancho (un campo junto a su boton) | `Row...fill(campo)` | [`fill`](../reference/kit/containers/row.md#ocupar-el-ancho-que-sobra-fill) |
| que varias cosas midan **lo mismo** de ancho | `Row...fillEqual(a, b, c)` | [`fillEqual`](../reference/kit/containers/row.md#repartir-el-ancho-fill-frente-a-fillequal) |
| que una cosa ocupe lo que sobra a lo **alto** (una tabla, un area de texto) | `Col...fillBoth(c)` · `Row...fillBoth(c)` | [`Col.fillBoth`](../reference/kit/containers/col.md#ocupar-el-alto-que-sobra-fillboth) · [`Row.fillBoth`](../reference/kit/containers/row.md#ocupar-tambien-el-alto-fillboth) |
| empujar algo a la **derecha** (o hacia **abajo**) | `Row...glue()` · `Col...glue()` | [`Row.glue`](../reference/kit/containers/row.md#empujar-con-glue) · [`Col.glue`](../reference/kit/containers/col.md#empujar-hacia-abajo-glue) |
| mostrar **una** de varias alternativas en el mismo lugar | `Stack.fill(a, b, c)` y mostrar solo una | [`Stack`](../reference/kit/containers/stack.md#una-alternativa-a-la-vez) |
| dejar aire alrededor del contenido | `padding(n)` · `paddingVertical(n)` · `paddingHorizontal(n)` | [`padding`](../reference/kit/containers/box.md#margen-interior-padding) |
| fijar el ancho o el alto de un hijo | `.width(px)` · `.height(px)` | [`width` y `height`](../reference/kit/containers/box.md#tamano-fijo-width-y-height) |

Todos los valores estan en **px logicos**: se escalan con el zoom de la pantalla.

## Todo junto

### Formulario con acciones

**Usa:** [`Row`](../reference/kit/containers/row.md) · [`Col`](../reference/kit/containers/col.md) · [`Card`](../reference/kit/containers/card.md)

![Formulario: Row y Col juntos](../img/box-mix-form.png)

```java
Card.create("Nueva impresora").children(
    Col.gap(10)
        .children(
            Row.gap(10).fill(txtName).children(cbBrand),
            Row.gap(10).fill(txtIp).children(btnScan),
            chckTest)
        .glue()
        .children(Row.gap(8).glue().children(btnCancel, btnSave)))
```

### Barra arriba, contenido y barra abajo

**Usa:** [`Col`](../reference/kit/containers/col.md)

![Barra, contenido y barra](../img/box-mix-layout.png)

```java
Col.gap(6).padding(6)
    .fill(topBar)            // alto natural, todo el ancho
    .fillBoth(content)       // todo lo que sobra
    .fill(bottomBar)         // alto natural, todo el ancho
```

Si la ventana tiene mas piezas (menu lateral, encabezado de pasos), mira [`Page`](../reference/kit/containers/page.md): resuelve las cinco zonas de una ventana.

### Tablero

**Usa:** [`Row`](../reference/kit/containers/row.md) · [`Col`](../reference/kit/containers/col.md) · [`Card`](../reference/kit/containers/card.md) · [`Label`](../reference/kit/widgets/label.md)

![Tablero: tarjetas del mismo ancho](../img/box-mix-dashboard.png)

```java
Col.gap(10).children(
    Row.gap(10).fillEqual(stat("Impresoras", "12"), stat("Trabajos hoy", "148"), stat("Errores", "3")),
    Row.gap(10).top().fillEqual(
        Card.section("Ultimos trabajos").children(Col.gap(4).children(a, b, c)),
        Card.section("Estado").children(Col.gap(4).children(d, e, f))))

static Card stat(String title, String value) {
    return Card.section(title).children(Label.text(value).bold().fontSize(26));
}
```

### Busqueda, tabla y acciones

**Usa:** [`Row`](../reference/kit/containers/row.md) · [`Col`](../reference/kit/containers/col.md) · [`Scroll`](../reference/kit/containers/scroll.md)

![Busqueda, tabla y acciones](../img/box-mix-toolbar.png)

```java
Col.gap(8).padding(8)
    .children(Row.gap(8).fill(txtSearch).children(cbFilter, btnNew))       // barra de arriba
    .fillBoth(Scroll.of(table))                                            // la tabla crece
    .children(Row.gap(8).children(lblCount).glue().children(btnEdit, btnDelete))  // barra de abajo
```

## Ver tambien

- [Contenedores](../reference/kit/containers/README.md): la tabla "que uso para cada cosa" y todos los contenedores.
- [Como armar una pantalla](screens.md#pantallas-completas): pantallas completas con `Page`, `Header`, `Footer`, `Tabs`, `Split`...
- [Recetas](recipes.md): ejemplos completos con su logica.
