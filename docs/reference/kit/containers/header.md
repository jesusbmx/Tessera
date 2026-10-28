# Header

**Banner oscuro** con un titulo y, opcionalmente, una descripcion: la cabecera de una ventana o de cada paso de un asistente.

| | |
|---|---|
| **Paquete** | `tessera.kit` |
| **Clase** | `Header extends Box<Header>`: un `JPanel` con fondo `Kit.DARK` |
| **Se crea con** | `Header.create(titulo)` · `Header.create(titulo, descripcion)` |
| **Importar** | `import tessera.kit.Header;` |

![Header: variantes](../../../img/layout-header.png)

## Cuando usarlo

- La parte de arriba de una ventana o dialogo (`Page.top(...)`).
- La cabecera de cada paso de un asistente.

**Cuando no:** Para un bloque con titulo dentro de la pantalla usa `Card`.

## Referencia

Banner **oscuro** con titulo y descripcion opcional: la cabecera de cada paso de un asistente o dialogo. Colores de `Kit.DARK` y
`Kit.ON_DARK`.

| Metodo | Que hace |
|---|---|
| `Header.create(titulo)` / `Header.create(titulo, descripcion)` | banner |
| `.subtitle(String)` | descripcion bajo el titulo (si ya existe, cambia su texto) |
| `.corner(Component)` | un componente (un enlace de ayuda) a la derecha del titulo |
| `.title()` | la `Label` del titulo, para cambiarla despues |

## Errores comunes

- `.corner(componente)` coloca el componente junto al **titulo**, no junto a la descripcion.
- Para cambiar el texto despues de armado: `header.title().setText(..)` y `header.subtitle(..)`.

## Ver tambien

- [`Page`](page.md) · [`Footer`](footer.md) · [`Card`](card.md)
- [Referencia del kit](../README.md): ideas base y metodos comunes (`Styled`, `Clickable`, `Box`).
- [Guia visual de disposiciones](../../../guides/screens.md#guia-visual-de-disposiciones): cada contenedor con imagenes y pantallas completas.
