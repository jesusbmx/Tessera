# Label

Etiqueta de texto con **estilos listos**: titulo, nombre de campo, texto secundario y texto normal. Tambien sirve como enlace.

| | |
|---|---|
| **Paquete** | `tessera.kit` |
| **Clase** | `Label extends JLabel implements Styled<Label>` |
| **Se crea con** | `Label.text(txt)` · `Label.title(txt)` · `Label.field(txt)` · `Label.muted(txt)` |
| **Importar** | `import tessera.kit.Label;` |

![Label: estilos y enlace](../../../img/widget-label.png)

## Cuando usarlo

- Cualquier texto de la pantalla.
- Un enlace (`.onClick(...)`).
- El nombre de un campo en un `Form` (se crea solo con `.row("Nombre", campo)`).

**Cuando no:** Para ayudas largas que deben partirse en lineas usa `Hint`.

## Referencia

Etiqueta de texto (`JLabel`).

| Fabrica | Estilo |
|---|---|
| `Label.text(txt)` | texto normal |
| `Label.title(txt)` | titulo: negrita, 18 |
| `Label.field(txt)` | nombre de un campo: negrita, 15 |
| `Label.muted(txt)` | texto secundario: gris (`Kit.MUTED`) |

| Metodo | Que hace |
|---|---|
| `.icon(recurso, tam)` / `.icon(Icon)` | icono |
| `.iconGap(px)` | espacio entre icono y texto |
| `.small()` | un punto mas pequena que el texto normal |
| `.center()` / `.right()` | alineacion horizontal del texto |
| `.padding(arriba, izq, abajo, der)` | margen alrededor del texto |
| `.onClick(Runnable)` | la etiqueta se comporta como un **enlace**: cursor de mano y una accion al hacer clic |

```java
Label.text("Ayuda").icon("app/img/help_blue.svg", 16).onClick(this::openHelp)
```

## Errores comunes

- `Hint` **no** es un `Label`: son tipos distintos (los dos extienden `JLabel`).
- `.fontSize(n)` ya escala con el zoom: no multipliques tu mismo.

## Ver tambien

- [`Hint`](hint.md) · [`Form`](../containers/form.md) · [`Button`](button.md)
- [Referencia del kit](../README.md): ideas base y metodos comunes (`Styled`, `Clickable`, `Box`).
