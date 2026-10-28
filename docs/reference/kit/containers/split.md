# Split

Dos zonas separadas por una **barra que se arrastra**. Dentro de un `Row` o un `Col` ocupa el espacio que sobra.

| | |
|---|---|
| **Paquete** | `tessera.kit` |
| **Clase** | `Split extends JSplitPane implements Styled<Split>` |
| **Se crea con** | `Split.horizontal(izq, der)` · `Split.vertical(arriba, abajo)` |
| **Importar** | `import tessera.kit.Split;` |

![Tabs y Split](../../../img/layout-tabs-split.png)

![Split en una pantalla de lista y detalle](../../../img/app-master-detail.png)

## Cuando usarlo

- Lista a la izquierda y detalle a la derecha.
- Editor arriba y consola abajo.

## Referencia

Dos zonas separadas por una **barra que se arrastra** (`JSplitPane`). Ocupa el espacio que sobra.

| Metodo | Que hace |
|---|---|
| `Split.horizontal(izq, der)` | una zona a la izquierda y otra a la derecha |
| `Split.vertical(arriba, abajo)` | una zona arriba y otra abajo |
| `.divider(px)` | posicion inicial de la barra |
| `.weight(0..1)` | cuanto del espacio extra se queda la primera zona al cambiar el tamano |

```java
Split.horizontal(list, details).divider(260)
Split.vertical(editor, console).weight(0.7)
```

## Errores comunes

- `divider(px)` es la posicion **inicial** de la barra; `weight(0..1)` decide quien se queda con el espacio extra al cambiar el tamano.

## Ver tambien

- [`Tabs`](tabs.md) · [`Page`](page.md) · [`Scroll`](scroll.md)
- [Referencia del kit](../README.md): ideas base y metodos comunes (`Styled`, `Clickable`, `Box`).
- [Guia visual de disposiciones](../../../guides/screens.md#guia-visual-de-disposiciones): cada contenedor con imagenes y pantallas completas.
