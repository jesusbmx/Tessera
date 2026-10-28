# Pages

Varias paginas **en el mismo lugar**, de las que se ve una a la vez: los pasos de un asistente, las vistas de la ventana principal.

| | |
|---|---|
| **Paquete** | `tessera.kit` |
| **Clase** | `Pages extends JPanel implements Styled<Pages>`, con `CardLayout` |
| **Se crea con** | `Pages.create()` |
| **Importar** | `import tessera.kit.Pages;` |

![Pages dentro de un asistente de pasos](../../../img/app-wizard.png)

## Cuando usarlo

- Los pasos de un asistente.
- Las vistas de una ventana principal con menu lateral.

**Cuando no:** Para que el usuario elija la vista con pestanas usa `Tabs`; para dos alternativas pequenas, `Stack`.

## Referencia

Varias paginas **en el mismo lugar**, de las que se ve una a la vez (pasos de un asistente, vistas de la ventana principal); usa
`CardLayout`.

```java
Pages pages = Pages.create().page("name", namePanel).page("type", typePanel);
pages.show("type");
```

## Errores comunes

- `pages.show("nombre")` con un nombre que no se agrego no hace nada.
- `Pages` mide lo que la **pagina mas grande**: la ventana no cambia de tamano entre paso y paso.

## Ver tambien

- [`Tabs`](tabs.md) · [`Stack`](stack.md) · [`Page`](page.md)
- [Referencia del kit](../README.md): ideas base y metodos comunes (`Styled`, `Clickable`, `Box`).
- [Guia visual de disposiciones](../../../guides/screens.md#guia-visual-de-disposiciones): cada contenedor con imagenes y pantallas completas.
