# Stack

Apila varios componentes **en el mismo lugar**; quien arma la pantalla decide cual se ve. Los ocultos no ocupan espacio.

| | |
|---|---|
| **Paquete** | `tessera.kit` |
| **Clase** | `Stack extends Box<Stack>`: un `JPanel` transparente con MigLayout |
| **Se crea con** | `Stack.fill(c...)` · `Stack.start(c...)` · `Stack.end(c...)` · `Stack.center(c...)` |
| **Importar** | `import tessera.kit.Stack;` |

![Stack: fill, start, center y end](../../../img/layout-stack-align.png)

![Stack: tres alternativas](../../../img/layout-stack.png)

![Stack: Iniciar o Detener, y mensaje vacio o lista](../../../img/layout-stack-patterns.png)

## Cuando usarlo

- Un campo que cambia segun una opcion (un combo, un campo de texto u otro combo).
- Un boton que se cambia por otro ("Iniciar" / "Detener").
- Un mensaje de "no hay datos" o la lista.

**Cuando no:** Para los **pasos** de un asistente usa `Pages`; si el usuario debe elegir que ver, `Tabs`.

## Referencia

Varios componentes **en el mismo lugar**, uno encima de otro; sirve para alternativas de las que se muestra una (un combo, un campo o
otro combo segun la opcion elegida). **Quien arma la pantalla decide cual esta visible**; los ocultos no ocupan espacio.

| Fabrica | Los hijos... |
|---|---|
| `Stack.fill(c...)` | ocupan todo el ancho y el `Stack` ocupa el ancho que sobra de su contenedor |
| `Stack.start(c...)` | conservan el ancho natural, a la izquierda |
| `Stack.end(c...)` | conservan el ancho natural, a la derecha |
| `Stack.center(c...)` | conservan su tamano natural, centrados |

```java
Row.gap(10).children(
    Stack.fill(cbPrinters, txtIpPrinter, cbUsbPrinters),       // una de las tres se ve
    Stack.start(btnIpScanner, btnUsbRefresh))                   // el boton que toque
```

## Errores comunes

- Se ven dos alternativas a la vez: el `Stack` no decide por ti; haz `setVisible(false)` en las demas.
- Un `Stack` dentro de un `Row` no se estira: solo `Stack.fill` lo hace.
- Con ningun hijo visible, el `Stack` se encoge a 0x0.

## Ver tambien

- [`Pages`](pages.md) · [`Tabs`](tabs.md) · [`Row`](row.md)
- [Referencia del kit](../README.md): ideas base y metodos comunes (`Styled`, `Clickable`, `Box`).
- [Guia visual de disposiciones](../../../guides/screens.md#guia-visual-de-disposiciones): cada contenedor con imagenes y pantallas completas.
- [Row, Col y Stack: todo lo que se puede hacer](../../../guides/layout.md): recetario con una imagen por cada cosa que se puede pedir.
