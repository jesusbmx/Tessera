# Bordes (`tessera.components`)

Bordes redondeados y punteados.

[← Componentes sueltos](README.md)

## `RoundedBorder`

Borde redondeado que ademas **rellena** con el fondo actual del componente. El componente debe ser **no opaco** (`setOpaque(false)`), si no el
fondo rectangular de Swing asoma en las esquinas.

| Constructor / metodo | Resultado |
|---|---|
| `new RoundedBorder()` | radio 20, gris claro |
| `new RoundedBorder(radio)` | gris claro |
| `new RoundedBorder(radio, color)` | con ese color de trazo |
| `RoundedBorder.createCardBorder(colorMargen, radio)` | borde de **tarjeta de lista**: margen exterior relleno de `colorMargen` (separa tarjetas) + relleno redondeado + trazo fino |

```java
panel.setOpaque(false);
panel.setBorder(new RoundedBorder(12, new Color(200, 200, 200)));
```

## `DashedBorder`

Borde **punteado** con esquinas redondeadas; da la pista de "clic para elegir" en campos de solo lectura que abren un selector de archivo o carpeta.

```java
txtPath.setEditable(false);
txtPath.setBorder(new DashedBorder(8, Color.GRAY));                      // radio y color
txtPath.setBorder(new DashedBorder(8, Color.GRAY, 4f, 3f));              // ademas largo del trazo y del hueco
```
