# Card

Bloque con **fondo suave y borde redondeado**: titulo (con icono opcional), texto de ayuda y debajo sus hijos en columna.

| | |
|---|---|
| **Paquete** | `tessera.kit` |
| **Clase** | `Card extends Box<Card>`: un `JPanel` transparente con MigLayout y borde propio |
| **Se crea con** | `Card.create(titulo)` · `Card.section(titulo)` · `Card.create()` · `Card.create(componente)` |
| **Importar** | `import tessera.kit.Card;` |

![Card: variantes](../../../img/layout-card.png)

## Cuando usarlo

- Agrupar campos relacionados dentro de una pantalla (`Card.section`).
- Un bloque con titulo grande, icono y ayuda (`Card.create`).
- Un bloque con un titulo propio, como un interruptor.

**Cuando no:** Para la cabecera de la ventana usa `Header`.

## Referencia

Bloque con **fondo suave y borde redondeado**: titulo (con icono opcional), texto de ayuda y debajo los hijos en columna (con las reglas
de `Col`). Relleno y borde salen de `Kit.SURFACE` y `Kit.CARD_LINE` (se cambian con `Theme`). El radio de las esquinas es fijo (12): ver
[customizing.md](../../../guides/customizing.md).

| Fabrica | Resultado |
|---|---|
| `Card.create("Titulo")` | titulo en negrita, un poco mas grande que el texto |
| `Card.section("Titulo")` | compacto: titulo en negrita del tamano del texto. Reemplaza a los `TitledBorder` |
| `Card.create()` | sin titulo |
| `Card.create(JComponent)` | titulo propio (un titulo con un interruptor al lado, por ejemplo) |

| Metodo | Que hace |
|---|---|
| `.subtitle(String)` | texto de ayuda bajo el titulo (se parte en lineas solo; ver [`Hint`](../widgets/hint.md)) |
| `.icon(String recurso)` / `.icon(Icon)` | icono junto al titulo (solo si el titulo es de texto); el recurso se carga como SVG de 24 px |
| `.gap(n)` | espacio entre hijos (6 por defecto) |
| `.title()` | el componente del titulo, para cambiarlo despues |
| `.padding(...)` | margen interior (por defecto 10, 12, 12, 12) |

```java
Card.create("Conexion")
    .icon("app/img/print.svg")
    .subtitle("Como se conecta la impresora")
    .children(
        Row.gap(14).children(rbtnPrinter, rbtnIp, rbtnUsb),
        Row.gap(10).fill(txtIp).children(btnScan))
```

## Errores comunes

- El radio de las esquinas es fijo (12) y no sigue a `Theme.arc`: ver [Personalizar](../../../guides/customizing.md#5-lo-que-no-se-puede-cambiar-desde-la-app).
- `.icon(..)` solo funciona si el titulo es de texto; con `Card.create(componente)` se ignora.

## Ver tambien

- [`Header`](header.md) · [`Form`](form.md) · [`Col`](col.md)
- [Referencia del kit](../README.md): ideas base y metodos comunes (`Styled`, `Clickable`, `Box`).
- [Guia visual de disposiciones](../../../guides/screens.md#guia-visual-de-disposiciones): cada contenedor con imagenes y pantallas completas.
