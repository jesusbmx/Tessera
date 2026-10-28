# Col

Apila sus hijos **en una columna**, de arriba hacia abajo. Cada uno ocupa todo el ancho, salvo los botones y las etiquetas.

| | |
|---|---|
| **Paquete** | `tessera.kit` |
| **Clase** | `Col extends Box<Col>`: un `JPanel` transparente con MigLayout |
| **Se crea con** | `Col.gap(n)` · `Col.create()` |
| **Importar** | `import tessera.kit.Col;` |

![Col: ancho natural o completo](../../../img/layout-col-rules.png)

![Col: glue y fillBoth](../../../img/layout-col-fill.png)

## Cuando usarlo

- Apilar widgets de arriba abajo.
- El contenido de una ventana: `Col.gap(10).padding(12)`.
- Pegar algo al fondo (`glue`) o dar el alto sobrante a un hijo (`fillBoth`).

**Cuando no:** Para etiqueta + campo alineados usa `Form`; para una tabla de celdas, `Grid`.

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

## Errores comunes

- Un boton o una etiqueta no se estira: es lo esperado; usa `.fill(boton)`.
- La tabla no muestra el encabezado o no ocupa el alto: falta `Scroll.of(table)` y/o `fillBoth`.

## Ver tambien

- [`Row`](row.md) · [`Form`](form.md) · [`Card`](card.md) · [`Scroll`](scroll.md)
- [Referencia del kit](../README.md): ideas base y metodos comunes (`Styled`, `Clickable`, `Box`).
- [Guia visual de disposiciones](../../../guides/screens.md#guia-visual-de-disposiciones): cada contenedor con imagenes y pantallas completas.
