# Contenedores

Los contenedores **acomodan** a otros componentes: deciden quien va al lado de quien, quien se estira y cuanto aire hay entre ellos. Casi todos son `JPanel` con
MigLayout adentro (no hace falta saber MigLayout) y se arman encadenando metodos. Cada pagina tiene **una imagen por cada cosa que se puede hacer** con ese
contenedor, con el codigo que la produce.

![Contenedores del kit](../../../img/containers.png)

## Que uso para cada cosa

| Quiero... | Uso | Mas |
|---|---|---|
| varios widgets **uno al lado del otro** | `Row.gap(n).children(a, b, c)` | [`Row`](row.md) |
| un campo que **se estire** junto a su boton | `Row.gap(10).fill(campo).children(boton)` | [`fill`](row.md#ocupar-el-ancho-que-sobra-fill) |
| botones o texto **pegados a la derecha** | `Row.gap(8).glue().children(botones)` | [`glue`](row.md#empujar-con-glue) |
| dos bloques del **mismo ancho**, lado a lado | `Row.gap(10).fillEqual(a, b)` | [`fillEqual`](row.md#repartir-el-ancho-fill-frente-a-fillequal) |
| varios widgets **uno debajo del otro** | `Col.gap(n).children(...)` | [`Col`](col.md) |
| un boton **a todo el ancho** de una columna | `Col.gap(6).fill(boton)` | [`start` y `fill`](col.md#cambiar-la-regla-start-y-fill) |
| algo **pegado abajo** en una columna | `Col.gap(8).children(...).glue().children(boton)` | [`glue`](col.md#empujar-hacia-abajo-glue) |
| que un widget **ocupe el alto que sobra** (tabla, area de texto) | `Col...fillBoth(Scroll.of(widget))` | [`fillBoth`](col.md#ocupar-el-alto-que-sobra-fillboth) |
| **etiqueta + campo** alineados | `Form.create().row("Etiqueta", campo)` | [`Form`](form.md) |
| **dos pares** etiqueta/campo en la misma fila, o una tabla de celdas | `Grid.gap(8).row(lbl1, campo1, lbl2, campo2)` | [`Grid`](grid.md) |
| un **bloque con titulo** y borde redondeado | `Card.create("Titulo")` / `Card.section("Titulo")` | [`Card`](card.md) |
| una **cabecera** oscura con titulo | `Header.create("Titulo", "descripcion")` | [`Header`](header.md) |
| botones **abajo a la derecha** | `Footer.of(botones...)` | [`Footer`](footer.md) |
| **cabecera + centro + pie** en una ventana | `Page.create().top(..).center(..).bottom(..)` | [`Page`](page.md) |
| un **menu lateral** | `Page.create().left(menu).center(contenido)` | [`Page`](page.md) |
| un bloque **centrado** en la ventana | `Col.gap(0).glue().children(Row.gap(0).glue().children(bloque.width(320)).glue()).glue()` | [centrar](page.md#centrar-un-bloque-en-la-ventana) |
| **dos zonas** que el usuario redimensiona | `Split.horizontal(a, b)` / `Split.vertical(a, b)` | [`Split`](split.md) |
| **pestanas** | `Tabs.create().tab("A", a).tab("B", b)` | [`Tabs`](tabs.md) |
| **pasos** (uno visible a la vez, lo decide el programa) | `Pages.create().page("a", a).page("b", b)` + `pages.show("a")` | [`Pages`](pages.md) |
| **alternativas** de las que se ve una (un campo u otro, un boton u otro) | `Stack.fill(a, b, c)` y `setVisible(..)` | [`Stack`](stack.md#una-alternativa-a-la-vez) |
| un widget **centrado, a la izquierda o a la derecha** de su bloque | `Stack.center(c)` / `Stack.start(c)` / `Stack.end(c)` | [`Stack`](stack.md#alineacion) |
| contenido con **scroll** (siempre, con una tabla) | `Scroll.of(c)` | [`Scroll`](scroll.md) |
| una **linea** que separa grupos | `Separator.horizontal()` / `Separator.vertical().height(24)` | [`Separator`](separator.md) |
| **margen** alrededor del contenido | `padding(12)` · `paddingVertical(..)` · `paddingHorizontal(..)` | [`Box`](box.md#margen-interior-padding) |
| **tamano fijo** de un componente | `.width(px)` · `.height(px)` | [`Box`](box.md#tamano-fijo-width-y-height) |
| **ocultar** algo sin dejar hueco | `widget.setVisible(false)` | [`Box`](box.md#ocultar-sin-dejar-hueco) |

## Los contenedores

### Acomodar hijos

| Contenedor | Para que |
|---|---|
| [`Row`](row.md) | Pone sus hijos **en una fila**, de izquierda a derecha, cada uno con su ancho natural. |
| [`Col`](col.md) | Apila sus hijos **en una columna**, de arriba hacia abajo. |
| [`Grid`](grid.md) | Cuadricula: varias celdas por fila, **alineadas en columnas**. |
| [`Form`](form.md) | Formulario de **dos columnas**: la etiqueta a la izquierda y el campo a la derecha. |
| [`Card`](card.md) | Bloque con **fondo suave y borde redondeado**, con titulo, icono y ayuda opcionales. |

### Estructura de una ventana

| Contenedor | Para que |
|---|---|
| [`Page`](page.md) | **Arriba, centro y abajo** (y a los lados si hace falta). |
| [`Header`](header.md) | **Banner oscuro** con titulo y descripcion: la cabecera de una ventana o de un paso. |
| [`Footer`](footer.md) | Barra de botones **abajo a la derecha**. |

### Varias vistas en el mismo lugar

| Contenedor | Quien decide cual se ve |
|---|---|
| [`Tabs`](tabs.md) | el **usuario**, con pestanas |
| [`Pages`](pages.md) | el **programa**, para pasos de un asistente o vistas de la ventana principal |
| [`Stack`](stack.md) | el **programa**, para piezas pequenas dentro de una fila o un bloque |
| [`Split`](split.md) | las dos se ven; el usuario reparte el espacio con una barra |

### Utilidades

| Contenedor | Para que |
|---|---|
| [`Scroll`](scroll.md) | **Barras de desplazamiento** (siempre alrededor de una `Table`). |
| [`Separator`](separator.md) | **Linea fina** que separa grupos. |

### Lo que comparten

[`Box`](box.md) es la base de `Row`, `Col`, `Stack`, `Grid`, `Form`, `Card` y `Header`: `gap`, `padding`, `paddingVertical`, `paddingHorizontal`, `children`, tamano
fijo y ocultar sin dejar hueco.

## Como leer las imagenes

Las imagenes de cada pagina estan generadas con el codigo que se muestra al lado (`src/demo/java/tessera/demo/`), asi que lo que ves es lo que obtienes. Dos
convenciones para ilustrar:

- La linea **punteada** dibuja el borde de un contenedor para que se vea hasta donde llega. No forma parte de la libreria.
- `zone("texto")` es solo un `Label` con fondo de color para **dibujar una zona**; en tu app ahi iria tu contenido:

```java
final Label label = Label.text(text).center().background(color);   // (mas un borde fino, solo para ilustrar)
```

## Ejemplos que los combinan

- [Como armar una pantalla](../../../guides/screens.md#pantallas-completas): 10 pantallas completas (listado con tabla, lista y detalle, menu lateral, asistente, tablero...).
  Cada una dice que contenedores y widgets usa, con enlace a su pagina.
- [Combinar Row, Col y Stack](../../../guides/layout.md): formularios, barras, tableros y busquedas armados con las tres piezas.
- [Recetas](../../../guides/recipes.md): ejemplos completos con su logica.

## Ver tambien

- [Referencia del kit](../README.md): los widgets y las ideas base.
- [Que widget uso](../../../guides/widgets.md): para elegir el widget correcto dentro de cada contenedor.
