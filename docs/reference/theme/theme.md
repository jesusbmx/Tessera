# Theme

Instala el **look and feel** (FlatLaf), la fuente **Inter** empaquetada, el color de acento, el fondo de los paneles y la paleta del kit. Se usa **una sola vez**,
antes de crear cualquier ventana.

| | |
|---|---|
| **Paquete** | `tessera.theme` |
| **Clase** | `Theme` (clase final) y `Theme.Builder` |
| **Se crea con** | `Theme.builder()` ... `.install()` |
| **Importar** | `import tessera.theme.Theme;` |

![Tema por defecto](../../img/theme-default.png)

## Cuando usarlo

- Al iniciar la app, **antes** de crear ventanas: le da a toda la aplicacion su color de marca, su fondo y sus esquinas.
- Para cambiar la fuente de toda la interfaz.

**Cuando no:** para cambiar **un solo widget** usa sus metodos encadenables (`bold()`, `color(...)`, `width(...)`); para algo especifico de FlatLaf, `UIManager.put(...)`
**despues** de `install()`.

## Referencia

```java
Theme.builder()
    .accent("#1976D2")           // color de acento
    .primary("#1976D2")          // botones principales
    .panelBackground("#F7F6F3")  // fondo de las pantallas
    .surface("#FFFFFF")          // relleno de Card
    .cardLine("#E4E1D9")         // borde de Card
    .arc(12)                     // esquinas de botones y campos
    .install();
```

### `Theme.Builder`

| Metodo | Que hace | Por defecto |
|---|---|---|
| `font(String familia)` | usa esa fuente **instalada en el sistema** en vez de la Inter empaquetada | Inter |
| `accent(Color)` / `accent(String hex)` | color de acento global (se lee despues con `Theme.accent()`) | `#1976D2` |
| `primary(String hex)` | color de los botones principales (`Button.primary`) | el de `Kit.PRIMARY` |
| `surface(String hex)` | relleno de las tarjetas (`Card`) | el de `Kit.SURFACE` |
| `cardLine(String hex)` | borde de las tarjetas (`Card`) | el de `Kit.CARD_LINE` |
| `panelBackground(String hex)` | fondo de paneles, scrolls y viewports (las listas y tablas siguen blancas) | blanco |
| `arc(int)` | radio (px) de las esquinas de botones, campos y barras | 12 |
| `tooltipDismissMs(int)` | cuanto dura un tooltip en pantalla | 8000 |
| `install()` | aplica el tema | |

Los metodos devuelven el `Builder`, asi que se encadenan; solo `install()` cierra la cadena.

### Lectura desde tu codigo

| Metodo | Devuelve |
|---|---|
| `Theme.accent()` | el color de acento (foco, seleccion, enlaces) |
| `Theme.panelBackground()` | el fondo estandar de los paneles |
| `Theme.fontFamily()` | la familia de la fuente de la interfaz (para fuentes creadas a mano) |
| `Theme.DEFAULT_FONT_FAMILY` | `"Inter"` |

### Que hace `install()`

Instala FlatLaf (`FlatIntelliJLaf`, claro), registra la fuente y escribe varias claves de `UIManager` (acento, fondos, esquinas, barras de desplazamiento). Si algo
falla, queda el look and feel del sistema y se anota en el log. La lista completa de claves, con una tabla, esta en [Personalizar: el tema](../../guides/customizing.md#1-el-tema-theme).

![Un tema propio](../../img/theme-custom.png)

## Errores comunes

- **Cambiar el tema con ventanas ya abiertas** no las actualiza: instalalo una sola vez, antes de crear ventanas.
- **`accent` no pinta radios, casillas ni barras de progreso**: FlatLaf ya calculo esos colores. Hay un truco, explicado en [la nota sobre el acento](../../guides/customizing.md#nota-sobre-el-acento).
- **Asignar `Kit.PRIMARY` (u otro color) despues de crear widgets** no los cambia: cada widget toma el color al crearse; usa `Theme.builder().primary(...)`.
- **No hay modo oscuro**: `install()` instala el tema claro.

## Ver tambien

- [Personalizar: colores, esquinas, fuente](../../guides/customizing.md): donde se toca cada cosa.
- [Referencia del kit](../kit/README.md#utilidades-kit): la paleta `Kit.*` y `Kit.px`.
- [`Icons`](icons.md): iconos SVG.
