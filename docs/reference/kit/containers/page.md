# Page

Estructura de una ventana o panel: **arriba, centro y abajo** (y a los lados si hace falta).

| | |
|---|---|
| **Paquete** | `tessera.kit` |
| **Clase** | `Page extends JPanel implements Styled<Page>`, con `BorderLayout` |
| **Se crea con** | `Page.create()` |
| **Importar** | `import tessera.kit.Page;` |

![Page: las cinco zonas](../../../img/layout-page.png)

## Cuando usarlo

- El contenido de una ventana: `Header` arriba, contenido al centro, `Footer` abajo.
- Un menu lateral (`left`) con el contenido a la derecha.

## Referencia

Estructura de una ventana o panel: **arriba, centro, abajo** (y a los lados si hace falta), con `BorderLayout`.

| Metodo | Que hace |
|---|---|
| `Page.create()` | pagina vacia |
| `.top(c)` / `.bottom(c)` / `.left(c)` / `.right(c)` / `.center(c)` | coloca el componente en esa zona |
| `.minWidth(px)` | ancho minimo que pide la pagina: `pack()` no abre la ventana mas angosta |
| `.build()` | devuelve la pagina |

```java
frame.setContentPane(Page.create()
    .top(Header.create("Titulo"))
    .center(content)
    .bottom(Footer.of(btnCancel, btnOk)));
```

`Page` **no** es un `Box`: no tiene `children`, `gap` ni `padding`. Para margenes pon un `Col` con `padding` en el centro.

## Errores comunes

- `Page` **no** es un `Box`: no tiene `children`, `gap` ni `padding`; para margenes pon un `Col` con `padding` en el centro.
- `.width(px)` no tiene efecto en una zona de `Page`: `left` y `right` miden lo que pida su contenido.

## Ver tambien

- [`Header`](header.md) · [`Footer`](footer.md) · [`Pages`](pages.md) · [`Split`](split.md)
- [Referencia del kit](../README.md): ideas base y metodos comunes (`Styled`, `Clickable`, `Box`).
- [Guia visual de disposiciones](../../../guides/screens.md#guia-visual-de-disposiciones): cada contenedor con imagenes y pantallas completas.
