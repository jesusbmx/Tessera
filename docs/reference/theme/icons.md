# Icons

Iconos **SVG** (vectoriales, nitidos con cualquier escala de pantalla) y el **interruptor** estilo iOS.

| | |
|---|---|
| **Paquete** | `tessera.theme` |
| **Clase** | `Icons` (clase final de utilidades estaticas) |
| **Metodos** | `Icons.svg(...)` · `Icons.setupSwitch(boton)` |
| **Importar** | `import tessera.theme.Icons;` |

## Cuando usarlo

- Para un icono SVG con ancho y alto **distintos**, o recoloreado a un color fijo.
- Para dar forma de interruptor a un `JCheckBox` o `JToggleButton` que no es del kit.

**Cuando no:** para un icono cuadrado de un widget usa `Button.icon("ruta.svg", 16)` o `Kit.icon("ruta.svg", 16)`; para un interruptor, el widget [`Switch`](../kit/widgets/switch.md).

## Referencia

| Metodo | Que hace |
|---|---|
| `Icons.svg(path, ancho, alto)` | icono SVG de un recurso del classpath **de tu app** (`"app/img/print.svg"`); devuelve un `FlatSVGIcon` |
| `Icons.svg(path, ancho, alto, Color)` | igual, **recoloreado por completo** a un color fijo (por ejemplo blanco sobre un boton principal) |
| `Icons.setupSwitch(AbstractButton)` | da forma de **interruptor** a un `JCheckBox` o `JToggleButton`: iconos para cada estado, sin borde ni foco, cursor de mano |

```java
final JLabel lbl = new JLabel(Icons.svg("app/img/print.svg", 24, 24));
final Icon white = Icons.svg("app/img/print.svg", 16, 16, Color.WHITE);

final JCheckBox chk = new JCheckBox("Iniciar con el sistema");
Icons.setupSwitch(chk);                    // con el kit, usa Switch.of("...")
```

## Errores comunes

- La ruta del icono es del **classpath de tu app**: el SVG debe estar empaquetado con tu aplicacion. Los iconos propios de la libreria (`tessera/img/*.svg`) son
  internos y no son para tus pantallas.
- Si el icono no aparece, comprueba que `jsvg-2.0.0.jar` esta en el classpath (ver [la FAQ](../../guides/faq.md)).
- Los SVG del interruptor y de los avisos tienen **colores propios** y no siguen al acento del tema.

## Ver tambien

- [`Switch`](../kit/widgets/switch.md) y [`Button`](../kit/widgets/button.md): usan estos iconos.
- [Referencia del kit: utilidades](../kit/README.md#utilidades-kit): `Kit.icon(...)`.
- [`Theme`](theme.md).
