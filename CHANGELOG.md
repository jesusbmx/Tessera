# Cambios

Los cambios importantes de Tessera. Formato basado en [Keep a Changelog](https://keepachangelog.com/es-ES/1.1.0/); las versiones siguen
[Versionado Semantico](https://semver.org/lang/es/): `MAYOR.MENOR.PARCHE` (un cambio de API que rompe lo existente sube el numero mayor).

## Sin publicar

### Corregido

- `TextArea.placeholder(...)` (y `Input.area(n).placeholder(...)`) **ahora se ve**: FlatLaf 3.7 no pinta el placeholder en un `JTextArea`, asi que `TextArea`
  usa `TextPlaceholder`. `TextPlaceholder.install(...)` ademas se puede llamar mas de una vez sobre el mismo componente (cambia el texto en vez de duplicarlo).
- `new HighlightColumnRenderer(String...)` ya no guarda una lista de tamano fijo: `addHighlightedColumn` y `removeHighlightedColumn` funcionan.
- `ErrorProvider.getError(...)` devuelve el **mensaje del error** (antes devolvia el tooltip que el campo tenia antes). Llamar a `setError` varias veces sobre el mismo
  campo ya no apila bordes: solo cambia el mensaje, y `clear()` restaura el borde original.
- `build.sh` usa el `jar` de `JAVA_HOME` cuando el PATH solo trae `java` y `javac` (caso comun en Windows), y avisa con un mensaje claro si no hay JDK.

### Documentacion

- Guias nuevas: [Primeros pasos](docs/GETTING-STARTED.md), [referencia del kit](docs/KIT.md), [avisos](docs/NOTIFICATIONS.md),
  [bandeja y ventanas](docs/APP.md), [componentes](docs/COMPONENTS.md), [recetas](docs/RECIPES.md) y [preguntas frecuentes](docs/FAQ.md).
- Javadoc corregido: `WindowPrefs.install(store, paquetes...)` (el orden de los parametros estaba al reves en el ejemplo), `DragAndDrop` (`dropString`, no
  `dropText`), `Toast.LENGTH_INDEFINITE` (un clic sobre el toast no lo cierra) y `notIsEmail` (devuelve `true` cuando el texto **no** es un correo).

## 1.0.0

Primera version.

### Agregado

- **`tessera.kit`**: contenedores `Row`, `Col`, `Stack`, `Grid`, `Form`, `Card`, `Header`, `Footer`, `Page`, `Pages`, `Scroll`, `Tabs`, `Split`,
  `Separator`; widgets encadenables `Label`, `Hint`, `Button`, `Check`, `Radio`, `Toggle`, `Switch`, `Input`, `Password`, `TextArea`, `Combo`, `Spinner`,
  `Table`; paleta y utilidades en `Kit`.
- **`tessera.theme`**: `Theme` (FlatLaf, fuente Inter empaquetada, acento, fondo y esquinas redondeadas) e `Icons` (SVG e interruptor estilo iOS).
- **`tessera.notify`**: `Notification` (tarjetas apiladas con id, progreso y acciones) y `Toast` (mensaje breve con cola).
- **`tessera.app`**: `TrayDock` (bandeja del sistema y reapertura desde el Dock de macOS) y `WindowPrefs` (tamano de las ventanas).
- **`tessera.components`**: modelos y renderizadores de tabla y lista, bordes, validacion de formularios, arrastrar y soltar, deshacer/rehacer y
  navegacion.
- Scripts de compilacion sin Maven ni Gradle (`build.sh`, `build.ps1`) y `pom.xml`.
- Showcase ejecutable (`./build.sh --demo`).

### Limitaciones conocidas

- El radio de las esquinas de `Card` es fijo (12) y no sigue a `Theme.arc`.
- `Theme.accent` no pinta radios, casillas ni barras de progreso (ver [CUSTOMIZING.md](docs/CUSTOMIZING.md#nota-sobre-el-acento)).
- La licencia de Tessera todavia no esta definida.
