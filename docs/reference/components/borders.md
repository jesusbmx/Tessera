# Bordes (`tessera.components`)

Bordes redondeados y punteados.

[← Componentes sueltos](README.md)

## `RoundedBorder`

Borde redondeado que ademas **rellena** con el fondo actual del componente. Se arma con **metodos encadenables**; cada uno devuelve un borde **nuevo**
(el original no cambia), asi que se puede guardar uno como base y derivar variantes.

```java
RoundedBorder.of(14)                                                   // esquinas y trazo del tema
RoundedBorder.of(14).line(Color.GRAY).margin(6, 8, 2, 8).gutter(Color.WHITE)
RoundedBorder.of(10).line(new Color(0x1976D2)).fill(Color.WHITE)       // trazo azul, relleno propio
RoundedBorder.of(10).noLine()                                          // solo el relleno redondeado
```

Partes, de afuera hacia adentro: el **margen** (espacio exterior de esquinas rectas, que con `gutter` se pinta del color de lo que hay detras), la **forma redondeada**
(relleno + trazo) y el **relleno interior** (`padding`, por omision un cuarto del radio).

| Metodo | Que hace | Por defecto |
|---|---|---|
| `RoundedBorder.of(radio)` | punto de partida: esquinas y trazo, sin margen | trazo del tema |
| `.radius(n)` | radio de las esquinas, en px | |
| `.line(Color)` / `.noLine()` | color del trazo, o sin trazo | `Component.borderColor` del tema (claro u oscuro) |
| `.fill(Color)` | relleno de la forma | el fondo actual del componente |
| `.margin(arriba, izq, abajo, der)` / `.margin(n)` | espacio exterior de esquinas rectas | 0 |
| `.gutter(Color)` | color con que se pinta el margen (el fondo de atras) | sin color |
| `.parentGutter()` | el margen toma el fondo del **padre** cada vez que se pinta: no hay que indicar color | |
| `.padding(arriba, izq, abajo, der)` / `.padding(n)` | espacio entre el trazo y el contenido | un cuarto del radio |

El componente debe ser **no opaco** (`setOpaque(false)`) si pinta su propio fondo; si no, el fondo rectangular de Swing asoma en las esquinas. `Scroll.rounded()` ya se ocupa de eso.

Los constructores de siempre siguen valiendo:

| Constructor / metodo | Resultado |
|---|---|
| `new RoundedBorder()` | radio 20, trazo del tema |
| `new RoundedBorder(radio)` | trazo del tema |
| `new RoundedBorder(radio, color)` | con ese color de trazo |
| `RoundedBorder.createCardBorder(colorMargen, radio)` | borde de **tarjeta de lista**: margen `(6, 8, 2, 8)` relleno de `colorMargen` (separa tarjetas) + relleno redondeado + trazo. Equivale a `of(radio).margin(6, 8, 2, 8).gutter(colorMargen)` |
| `RoundedBorder.createCardBorder(radio)` | igual, pero el margen toma el fondo del padre (`parentGutter()`) |

```java
panel.setOpaque(false);
panel.setBorder(RoundedBorder.of(12).line(new Color(200, 200, 200)));
```

Un `Scroll` redondeado en una linea, sin armar el borde a mano: `Scroll.of(txtLog).rounded()` (ver [`Scroll`](../kit/containers/scroll.md)).
## `DashedBorder`

Borde **punteado** con esquinas redondeadas; da la pista de "clic para elegir" en campos de solo lectura que abren un selector de archivo o carpeta.

```java
txtPath.setEditable(false);
txtPath.setBorder(new DashedBorder(8, Color.GRAY));                      // radio y color
txtPath.setBorder(new DashedBorder(8, Color.GRAY, 4f, 3f));              // ademas largo del trazo y del hueco
```
