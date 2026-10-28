# Tabs

**Pestanas**: el usuario elige cual contenido ver. Dentro de un `Row` o un `Col` ocupa el espacio que sobra.

| | |
|---|---|
| **Paquete** | `tessera.kit` |
| **Clase** | `Tabs extends JTabbedPane implements Styled<Tabs>` |
| **Se crea con** | `Tabs.create()` |
| **Importar** | `import tessera.kit.Tabs;` |

![Tabs y Split](../../../img/layout-tabs-split.png)

## Cuando usarlo

- Una pantalla de ajustes con varias secciones.
- Contenidos largos que el usuario quiere recorrer por separado.

**Cuando no:** Para pasos que el programa controla usa `Pages`.

## Referencia

Pestanas (`JTabbedPane`). Ocupa el espacio que sobra.

```java
Tabs.create().tab("General", general).tab("Avanzado", iconAdvanced, advanced)
```

Metodos: `.tab(titulo, contenido)` y `.tab(titulo, icono, contenido)`.

## Errores comunes

- Cada pestana pide su propio contenedor: pon un `Col` con `padding` en cada una.

## Ver tambien

- [`Pages`](pages.md) · [`Split`](split.md) · [`Card`](card.md)
- [Referencia del kit](../README.md): ideas base y metodos comunes (`Styled`, `Clickable`, `Box`).
- [Guia visual de disposiciones](../../../guides/screens.md#guia-visual-de-disposiciones): cada contenedor con imagenes y pantallas completas.
