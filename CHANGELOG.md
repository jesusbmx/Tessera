# Cambios

Los cambios importantes de Tessera. Formato basado en [Keep a Changelog](https://keepachangelog.com/es-ES/1.1.0/); las versiones siguen
[Versionado Semantico](https://semver.org/lang/es/): `MAYOR.MENOR.PARCHE` (un cambio de API que rompe lo existente sube el numero mayor).

## Sin publicar

### Agregado

- **Variantes de color como Bootstrap** (`tessera.theme.Variant`: `PRIMARY ACCENT SECONDARY SUCCESS DANGER WARNING INFO INVERSE`): `Button.success("Aprobar")`, `Button.danger(..)`, `Button.outline(Variant.DANGER, "Eliminar")` (solo borde, se rellena al pasar el mouse), `Label.warning(..)`, `.variant(v)` y metodos de instancia (`.success()`, `.danger()`...) en `Button`, `Toggle` (color al estar presionado), `Label` y `Hint`. El texto sobre el color se elige solo (oscuro en amarillo y celeste). `PRIMARY`, `ACCENT` e `INVERSE` siguen al tema; el resto se cambia con `Theme.builder().success("#2E7D32").danger(..).warning(..).info(..).secondary(..).inverse(..)` o `.variant(Variant, color)`. `Kit.makeVariant` y `Kit.makeOutline` para un boton ya creado.
- **`Theme.builder()` acepta `Color` ademas de texto** en `primary`, `surface`, `cardLine` y `panelBackground` (como ya hacia `accent`): `Theme.builder().primary(MI_AZUL).panelBackground(PANEL_BACKGROUND)` reutiliza las constantes `Color` de la app en vez de repetir el hexadecimal. La version con `String` sigue igual.
- **`@Preview` y `Previewer`** (`tessera.app`): ver una pantalla en su propia ventana mientras se arma, sin arrancar la app. `@Preview` en la clase (o en metodos estaticos con datos de muestra) y un `main` de una linea; F5 la recrea. Ver [preview.md](docs/reference/app/preview.md).
- **Botones que abren un menu** (`tessera.kit`), uno por cada cosa que se quiere hacer, con una base comun (`PopupButton`: flecha, color, abrir debajo del boton):
  - [`ChoiceButton<T>`](docs/reference/kit/widgets/choicebutton.md): elige **una** opcion y el boton la muestra. Cada opcion lleva valor, texto y ayuda;
    `getValue()`, `setValue(v)`, `is(v)` y `onChange(...)` (tambien al elegir por codigo) reemplazan al `JPopupMenu` + un `JRadioButtonMenuItem` por opcion + `Kit.group`.
  - [`MultiChoiceButton<T>`](docs/reference/kit/widgets/multichoicebutton.md): marca **varias** opciones con casillas; `getValues()`, `isChecked(v)`, `setChecked(v, b)`.
  - [`MenuButton`](docs/reference/kit/widgets/menubutton.md): **acciones**; el clic en una opcion corre algo y no queda nada elegido. `onOpen` para menus que se llenan al momento.
  - [`Menu` y `MenuItem`](docs/reference/kit/widgets/menu.md): el menu y sus opciones **sin boton**, por ejemplo el clic derecho de una lista (`menu.show(lista, x, y)`).
  - Guia nueva [Que widget uso](docs/guides/widgets.md): cual elegir segun lo que se le pide al usuario (texto, activar, elegir una o varias, escribir, ejecutar, datos), con 4 imagenes
    de comparacion y la tabla de **que widget avisa de un cambio hecho por codigo**. Cada una de las 13 paginas de widgets ahora empieza con "en una frase" y "que reemplaza
    en Swing puro", y trae un ejemplo con imagen cuyo codigo es el de `WidgetExamples.java` (compilado).
  - Guia nueva [Botones que abren un menu](docs/guides/menus.md): cual usar, con 7 imagenes de pantallas con el menu abierto y ejemplos compilados
    (`MenuImages.java` y `MenuExamples.java`); cada pagina de referencia dice que reemplaza de Swing puro y enlaza a su receta.

### Cambiado

- **`Kit.icon(...)` ahora es `Icons.of(...)`** (`tessera.theme.Icons`): los iconos viven todos en `Icons` (`of`, `svg`, `setupSwitch`). Es un cambio de API: en el codigo que usaba
  `Kit.icon("ruta.svg", 16)` o `Kit.icon("ruta.svg", 16, color)`, reemplazar por `Icons.of(...)` con los mismos argumentos y agregar `import tessera.theme.Icons;`.

### Corregido

- `TextArea.placeholder(...)` (y `Input.area(n).placeholder(...)`) **ahora se ve**: FlatLaf 3.7 no pinta el placeholder en un `JTextArea`, asi que `TextArea`
  usa `TextPlaceholder`. `TextPlaceholder.install(...)` ademas se puede llamar mas de una vez sobre el mismo componente (cambia el texto en vez de duplicarlo).
- `new HighlightColumnRenderer(String...)` ya no guarda una lista de tamano fijo: `addHighlightedColumn` y `removeHighlightedColumn` funcionan.
- `ErrorProvider.getError(...)` devuelve el **mensaje del error** (antes devolvia el tooltip que el campo tenia antes). Llamar a `setError` varias veces sobre el mismo
  campo ya no apila bordes: solo cambia el mensaje, y `clear()` restaura el borde original.
- `build.sh` usa el `jar` de `JAVA_HOME` cuando el PATH solo trae `java` y `javac` (caso comun en Windows), y avisa con un mensaje claro si no hay JDK.

### Documentacion

- **Documentacion reorganizada** en `docs/guides/` (guias) y `docs/reference/` (referencia): ahora hay **una pagina por componente** (14 contenedores y 13 widgets de `tessera.kit`,
  `Theme`, `Icons`, `Notification`, `Toast`, `TrayDock`, `WindowPrefs` y una pagina por categoria de `tessera.components`), todas con la misma plantilla: imagen, cuando usarlo,
  referencia de metodos, errores comunes y ver tambien. Indices en [docs/README.md](docs/README.md) y [docs/reference/README.md](docs/reference/README.md). Los archivos de `docs/`
  se movieron (`docs/SCREENS.md` -> `docs/guides/screens.md`, `docs/KIT.md` -> `docs/reference/kit/`, etc.).
- 20 imagenes nuevas de widgets y avisos (`widget-*.png`, `container-*.png`, `notify-*.png`), generadas con `WidgetImages.java` y `NotifyImages.java` (`scripts/doc-images.sh`).
- Corregido lo que decia la documentacion de `Hint`: `.width(px)` es el ancho que el `Hint` **pide**, no un limite; el texto se parte al ancho real que reciba.
- [screens.md](docs/guides/screens.md) ahora incluye una **guia visual de disposiciones**: `Row`, `Col`, `Form`, `Grid`, `Card`, `Header`, `Footer`, `Stack`, `Page`, `Tabs`, `Split`, `Pages`,
  espaciado y ocultar componentes, mas 9 pantallas completas (listado con tabla, lista y detalle, menu lateral, asistente, tablero, dos columnas, inicio de sesion centrado, editor y consola,
  ajustes con pestanas). Las 26 imagenes se generan con `LayoutImages.java` (`scripts/doc-images.sh`).
- Guias nuevas: [Primeros pasos](docs/guides/getting-started.md), [referencia del kit](docs/reference/kit/README.md), [avisos](docs/reference/notify/README.md),
  [bandeja y ventanas](docs/reference/app/README.md), [componentes](docs/reference/components/README.md), [recetas](docs/guides/recipes.md) y [preguntas frecuentes](docs/guides/faq.md).
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
- `Theme.accent` no pinta radios, casillas ni barras de progreso (ver [customizing.md](docs/guides/customizing.md#nota-sobre-el-acento)).
- La licencia de Tessera todavia no esta definida.
