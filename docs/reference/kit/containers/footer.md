# Footer

Barra de botones de un dialogo: **abajo a la derecha**, con el espacio de siempre alrededor.

| | |
|---|---|
| **Paquete** | `tessera.kit` |
| **Clase** | clase de utilidad (no se instancia): `Footer.of(...)` devuelve un `Row` |
| **Se crea con** | `Footer.of(botones...)` |
| **Importar** | `import tessera.kit.Footer;` |

## Cuando usarlo

- La barra de botones de un dialogo o pantalla (`Page.bottom(...)`).

**Cuando no:** Si necesitas algo **a la izquierda** (una casilla, un boton de ayuda), arma tu propia fila con `glue()` en medio.

## Lo que se puede hacer

Cada seccion muestra una cosa que se puede pedir, con la imagen de como queda y el codigo que la produce (el titulo de cada tarjeta de las imagenes es ese mismo codigo). En las imagenes, una caja de color (`zone(...)`) es solo una pieza dibujada y el borde punteado marca los limites de un contenedor: ver [como leer las imagenes](README.md#como-leer-las-imagenes).

### Botones abajo a la derecha, y algo a la izquierda

`Footer.of(botones...)` pone los botones **abajo a la derecha**, con el margen de siempre. Va en `Page.bottom(...)`.

![Footer: variantes](../../../img/layout-footer.png)

```java
Footer.of(Button.of("Cancelar"), Button.primary("Guardar"))
```

Si necesitas **algo a la izquierda** (una casilla, un boton de ayuda), `Footer` no sirve: arma tu propia fila con `glue()` en medio.

```java
Row.gap(8).padding(12, 14)
    .children(Check.of("No volver a mostrar"))
    .glue()
    .children(Button.of("Cancelar"), Button.primary("Aceptar"))
```

La convencion es que el boton de la **accion principal** (`Button.primary`) sea el **ultimo** de la derecha.

## Referencia

`Footer.of(Component... botones)` devuelve un `Row` con los botones **alineados a la derecha**, 8 px entre ellos y 12/14 px de margen.

```java
Footer.of(btnCancel, btnSave)
```

## Ejemplos completos donde se usa

Pantallas completas y recetas que lo usan, para ver `Footer` dentro de una aplicacion real:

- [Dialogo clasico](../../../guides/screens.md#dialogo-clasico)
- [Asistente de pasos](../../../guides/screens.md#asistente-de-pasos)
- [Dos columnas de bloques](../../../guides/screens.md#dos-columnas-de-bloques)
- [Ajustes con pestanas](../../../guides/screens.md#ajustes-con-pestanas)
- [1. Dialogo de inicio de sesion](../../../guides/recipes.md#1-dialogo-de-inicio-de-sesion)
- [3. Asistente de pasos](../../../guides/recipes.md#3-asistente-de-pasos)
- [4. Pantalla de ajustes](../../../guides/recipes.md#4-pantalla-de-ajustes)

## Errores comunes

- `Footer` no admite elementos a la izquierda: usa `Row.gap(8).padding(12, 14).children(izq).glue().children(botones)`.
- Convencion: el boton de la accion principal (`Button.primary`) es el **ultimo** de la derecha.

## Ver tambien

- [`Row`](row.md) · [`Page`](page.md) · [`Header`](header.md) · [`Button`](../widgets/button.md)
- [Referencia del kit](../README.md): ideas base y metodos comunes (`Styled`, `Clickable`, `Box`).
- [Contenedores](README.md): la tabla "que uso para cada cosa" y todos los contenedores.
- [Lo comun a los contenedores (`Box`)](box.md): `gap`, `padding`, `children`, tamano fijo y ocultar.
- [Como armar una pantalla](../../../guides/screens.md#pantallas-completas): pantallas completas que los combinan.
