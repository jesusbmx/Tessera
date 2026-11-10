# Cambios

Los cambios importantes de Tessera. Formato basado en [Keep a Changelog](https://keepachangelog.com/es-ES/1.1.0/); las versiones siguen
[Versionado Semantico](https://semver.org/lang/es/): `MAYOR.MENOR.PARCHE` (un cambio de API que rompe lo existente sube el numero mayor).

## Sin publicar

### Agregado

- **`Palette`** (`tessera.theme`): los colores de un tema como datos inmutables (`Palette.LIGHT`, `DARK`, `UBUNTU`, `UBUNTU_DARK`). `Theme.light()`, `dark()`, `ubuntu()` y `ubuntuDark()` son ahora `builder().palette(Palette.X)`, y `Theme.builder().palette(p)` acepta cualquiera y se ajusta despues. `Theme.palette()` devuelve la instalada (la clara hasta que se instale un tema).
- **Pruebas de humo** (`./build.sh --test` o `./build.ps1 -Test`): arman y dibujan el kit en los cuatro temas sin abrir ventanas y revisan colores en puntos concretos (paleta de cada tema, cambio de un tema a otro, `Card`, `RoundedBorder`, renderizadores de tabla, variantes). Sin JUnit: es un `main` en `src/test/java/tessera/test/` y termina con codigo 1 si algo falla. No cubren `Notification`, `Toast`, `TrayDock` ni `Previewer`.
- **Temas listos: `Theme.light()`, `Theme.dark()`, `Theme.ubuntu()` y `Theme.ubuntuDark()`** (Ubuntu = estilo Yaru, naranja `#E95420`): definen toda la paleta del kit, usan `FlatDarkLaf` los oscuros y activan `tintControls()` (radios, casillas, barras y seleccion con el acento). Devuelven un `Builder`, asi que se ajustan (`Theme.dark().accent("#4F9EFF").install()`). Nuevos en el `Builder`: `darkMode(bool)`, `tintControls()`, `muted(..)`, `line(..)`, `rows(impar, par, seleccionada)` y `banner(fondo, texto)`; y `Theme.isDark()`. `Theme.builder()` no cambia. Ver [customizing.md](docs/guides/customizing.md).
- **`RoundedBorder`: el trazo por omision sigue el color de borde del tema** (`Component.borderColor`, antes `lightGray` fijo), asi se ve bien en un tema oscuro. `FlexibleColumnRenderer` y `HighlightColumnRenderer` pintan las celdas normales con los colores de la tabla (antes blanco y negro fijos). **`Card` toma su relleno del tema al crearse** (`Kit.SURFACE`) y el borde lo pinta con el fondo del componente: se cambia con `card.background(color)` (que no la vuelve opaca, para que las esquinas sigan redondeadas) o `setBackground`.
- **`RoundedBorder` configurable**: `RoundedBorder.of(14).line(Color.GRAY).margin(6, 8, 2, 8).gutter(Color.WHITE)`. Cada metodo devuelve un borde nuevo: `radius`, `line` / `noLine`, `fill` (si no, el fondo actual del componente), `margin(t, l, b, r)` / `margin(n)`, `gutter(color)` / `parentGutter()` (el margen toma el fondo del padre al pintar) y `padding(t, l, b, r)` / `padding(n)` (por omision un cuarto del radio). Los constructores y `createCardBorder(color, radio)` siguen igual; este ultimo ahora es un preset de los anteriores.
- **`Scroll.rounded()` y `Scroll.rounded(radio)`**: scroll con esquinas redondeadas en una linea, `Scroll.of(txtLog).rounded()`. Reemplaza a `scroll.setBorder(RoundedBorder.createCardBorder(getBackground(), 20))`; el margen toma el fondo del contenedor al pintar, asi que no hay que pasar un color ni cuidar que coincida. Tambien `RoundedBorder.createCardBorder(radio)` sin color.
- **`paddingVertical(px)` y `paddingHorizontal(px)`** en todos los contenedores del kit (`Box`: `Row`, `Col`, `Stack`, `Grid`, `Form`, `Card`) y en `Label`: cambian solo el margen de un eje (arriba y abajo, o izquierda y derecha) y conservan el otro. `Col.gap(10).padding(12).paddingHorizontal(24)` deja 12 arriba y abajo y 24 a los lados. `padding(...)` sigue igual, y `padding()` sin argumentos devuelve una copia del margen actual como `Insets`.
- **Variantes de color como Bootstrap** (`tessera.theme.Variant`: `PRIMARY SECONDARY SUCCESS DANGER WARNING INFO LIGHT DARK`): `Button.success("Aprobar")`, `Button.danger(..)`, `Button.outline(Variant.DANGER, "Eliminar")` (solo borde, se rellena al pasar el mouse), `Label.warning(..)`, `.variant(v)` en `Button`, `Toggle` (color al estar presionado), `Label` y `Hint`. El texto sobre el color se elige solo (oscuro en amarillo y celeste). `PRIMARY` sigue al tema; el resto se cambia con `Theme.builder().success("#2E7D32").danger(..).warning(..).info(..).secondary(..).light(..).dark(..)` o `.variant(Variant, color)`. `Kit.makeVariant` y `Kit.makeOutline` para un boton ya creado.
- **`Theme.builder()` acepta `Color` ademas de texto** en `primary`, `surface`, `cardLine` y `panelBackground` (como ya hacia `accent`): `Theme.builder().primary(MI_AZUL).panelBackground(PANEL_BACKGROUND)` reutiliza las constantes `Color` de la app en vez de repetir el hexadecimal. La version con `String` sigue igual.
- **`@Preview` y `Previewer`** (`tessera.app`): ver una pantalla en su propia ventana mientras se arma, sin arrancar la app. `@Preview` en la clase (o en metodos estaticos con datos de muestra) y un `main` de una linea; F5 la recrea. Ver [preview.md](docs/reference/app/preview.md).
- **Botones que abren un menu** (`tessera.kit`), uno por cada cosa que se quiere hacer, con una base comun (`PopupButton`: flecha, color, abrir debajo del boton):
  - [`ChoiceButton<T>`](docs/reference/kit/widgets/choicebutton.md): elige **una** opcion y el boton la muestra. Cada opcion lleva valor, texto y ayuda;
    `getValue()`, `setValue(v)`, `is(v)` y `onChange(...)` (tambien al elegir por codigo) reemplazan al `JPopupMenu` + un `JRadioButtonMenuItem` por opcion + `Buttons.group`.
  - [`MultiChoiceButton<T>`](docs/reference/kit/widgets/multichoicebutton.md): marca **varias** opciones con casillas; `getValues()`, `isChecked(v)`, `setChecked(v, b)`.
  - [`MenuButton`](docs/reference/kit/widgets/menubutton.md): **acciones**; el clic en una opcion corre algo y no queda nada elegido. `onOpen` para menus que se llenan al momento.
  - [`Menu` y `MenuItem`](docs/reference/kit/widgets/menu.md): el menu y sus opciones **sin boton**, por ejemplo el clic derecho de una lista (`menu.show(lista, x, y)`).
  - Guia nueva [Que widget uso](docs/guides/widgets.md): cual elegir segun lo que se le pide al usuario (texto, activar, elegir una o varias, escribir, ejecutar, datos), con 4 imagenes
    de comparacion y la tabla de **que widget avisa de un cambio hecho por codigo**. Cada una de las 13 paginas de widgets ahora empieza con "en una frase" y "que reemplaza
    en Swing puro", y trae un ejemplo con imagen cuyo codigo es el de `WidgetExamples.java` (compilado).
  - Guia nueva [Botones que abren un menu](docs/guides/menus.md): cual usar, con 7 imagenes de pantallas con el menu abierto y ejemplos compilados
    (`MenuImages.java` y `MenuExamples.java`); cada pagina de referencia dice que reemplaza de Swing puro y enlaza a su receta.

### Cambiado

- **La clase `Kit` desaparece** (cada cosa va a su lugar). `Metrics.px(n)` y `Metrics.font(size, bold)` (`tessera.theme`); `Buttons.primary/variant/outline(boton, ..)` y `Buttons.group(...)` para botones de Swing ya creados; `Layout.fill(c, horizontal, vertical)` (y el resto de lo que los contenedores leen: ancho y alto fijos, que antes eran claves de `Kit`); y los colores en `Theme.palette()` (`.primary .surface .cardLine .banner .onBanner .muted .line .rowOdd .rowEven .rowSelected`). Es un cambio de API: reemplazar `Kit.px(` por `Metrics.px(`, `Kit.font(` por `Metrics.font(`, `Kit.group(` por `Buttons.group(`, `Kit.makePrimary(` por `Buttons.primary(`, `Kit.fill(` por `Layout.fill(` y `Kit.MUTED` (y los demas colores) por `Theme.palette().muted`, y agregar los imports. **Los colores ya no se asignan a mano** (`Kit.MUTED = ..` no cambia nada): se piden con `Theme.builder().muted(..)`, o eligiendo otro tema. `Variant.PRIMARY` y todos los widgets leen `Theme.palette()`. La descripcion del kit esta ahora en `package-info.java`.
- **`Kit.icon(...)` ahora es `Icons.of(...)`** (`tessera.theme.Icons`): los iconos viven todos en `Icons` (`of`, `svg`, `setupSwitch`). Es un cambio de API: en el codigo que usaba
  `Kit.icon("ruta.svg", 16)` o `Kit.icon("ruta.svg", 16, color)`, reemplazar por `Icons.of(...)` con los mismos argumentos y agregar `import tessera.theme.Icons;`.
- **`Notification` en macOS sale arriba a la derecha**: bajo la barra de menu, y las nuevas empujan a las anteriores hacia abajo, como las notificaciones nativas del sistema. En Windows y Linux no cambia nada (abajo a la derecha, apiladas hacia arriba).

### Corregido

- El plugin de NetBeans **Tessera Preview** ya no se queda en una **recursion infinita** al indexar: `ComponentBadge` (la insignia de los `.java` que heredan de un componente Swing) y `ErrorAnnotator` de NetBeans se llamaban entre si (`StackOverflowError`, y NetBeans se trababa en "Scanning projects"). Ahora lleva una guardia de reentrada.
- `Previewer.save(...)` (la imagen PNG de una pantalla) **ya no sale con el pie cortado**: usaba `doLayout()`, que solo reacomoda el contenedor de afuera, asi que los de adentro conservaban la distribucion de `pack()` a otro alto. Ahora usa `validate()` y repite hasta que el tamano se estabiliza. El plugin de NetBeans *Tessera Preview* tenia el mismo defecto en su pestana y tambien se corrigio.
- `TextArea.placeholder(...)` (y `Input.area(n).placeholder(...)`) **ahora se ve**: FlatLaf 3.7 no pinta el placeholder en un `JTextArea`, asi que `TextArea`
  usa `TextPlaceholder`. `TextPlaceholder.install(...)` ademas se puede llamar mas de una vez sobre el mismo componente (cambia el texto en vez de duplicarlo).
- `new HighlightColumnRenderer(String...)` ya no guarda una lista de tamano fijo: `addHighlightedColumn` y `removeHighlightedColumn` funcionan.
- `ErrorProvider.getError(...)` devuelve el **mensaje del error** (antes devolvia el tooltip que el campo tenia antes). Llamar a `setError` varias veces sobre el mismo
  campo ya no apila bordes: solo cambia el mensaje, y `clear()` restaura el borde original.
- `build.sh` usa el `jar` de `JAVA_HOME` cuando el PATH solo trae `java` y `javac` (caso comun en Windows), y avisa con un mensaje claro si no hay JDK.

### Documentacion

- Pagina nueva [`Palette`](docs/reference/theme/palette.md) (los colores de cada tema, con una imagen de cada uno) y [`Theme`](docs/reference/theme/theme.md) con los cuatro temas listos, `Theme.palette()` y los metodos nuevos del `Builder`. [`RoundedBorder`](docs/reference/components/borders.md) (builder), [`Scroll`](docs/reference/kit/containers/scroll.md) (`rounded()`) y [`Card`](docs/reference/kit/containers/card.md) (`background`) al dia. Guia nueva "Agregar un tema" en [contributing.md](docs/guides/contributing.md). Imagenes nuevas `theme-light.png`, `theme-dark.png`, `theme-ubuntu.png` y `theme-ubuntu-dark.png` (`DocImages.java`, `scripts/doc-images.sh`).
- **Cada contenedor explica a fondo lo que puede hacer, con una imagen por cosa**: las paginas de `docs/reference/kit/containers/` pasaron de una tabla de metodos a una galeria (**Lo que se puede hacer**: un titulo, una imagen y el codigo por cada cosa que se puede pedir). `Row`, `Col` y `Stack` traen 10, 9 y 4 secciones (`gap`, `fill`, `fillEqual`, `fillBoth`, `glue`, alineacion, `padding`, anchos fijos, ocultar, anidar, patrones listos); `Form`, `Grid`, `Card`, `Header`, `Footer` y `Page` reciben lo que antes solo estaba en `screens.md`; `Tabs`, `Split`, `Pages`, `Scroll` y `Separator` ganan imagenes propias.
- Paginas nuevas [Contenedores](docs/reference/kit/containers/README.md) (la tabla **que uso para cada cosa** y todos los contenedores agrupados por proposito) y [`Box`](docs/reference/kit/containers/box.md) (lo que comparten `Row`, `Col`, `Stack`, `Grid`, `Form`, `Card` y `Header`: `gap`, `padding`, tamano fijo y ocultar sin dejar hueco).
- Los ejemplos generales **enlazan a las piezas que usan**: cada pantalla completa de [screens.md](docs/guides/screens.md), cada [receta](docs/guides/recipes.md) y cada ejemplo de [Combinar Row, Col y Stack](docs/guides/layout.md) lleva debajo del titulo una linea **Usa:** con un enlace a la pagina de cada contenedor y widget que emplea. A la inversa, cada contenedor lista los ejemplos completos donde se usa.
- Imagenes nuevas: 27 `box-*.png` (`BoxImages.java`) y 6 `container-*.png` (`ContainerImages.java`), generadas por `scripts/doc-images.sh`. Se eliminaron 10 imagenes que quedaron sin uso (`layout-row-*`, `layout-col-*`, `layout-stack*`, `layout-tabs-split`, `container-scroll`, `container-separator`) y el codigo que las generaba.
- **Documentacion reorganizada** en `docs/guides/` (guias) y `docs/reference/` (referencia): ahora hay **una pagina por componente** (14 contenedores y 13 widgets de `tessera.kit`,
  `Theme`, `Icons`, `Notification`, `Toast`, `TrayDock`, `WindowPrefs` y una pagina por categoria de `tessera.components`), todas con la misma plantilla: imagen, cuando usarlo,
  referencia de metodos, errores comunes y ver tambien. Indices en [docs/README.md](docs/README.md) y [docs/reference/README.md](docs/reference/README.md). Los archivos de `docs/`
  se movieron (`docs/SCREENS.md` -> `docs/guides/screens.md`, `docs/KIT.md` -> `docs/reference/kit/`, etc.).
- 20 imagenes nuevas de widgets y avisos (`widget-*.png`, `container-*.png`, `notify-*.png`), generadas con `WidgetImages.java` y `NotifyImages.java` (`scripts/doc-images.sh`).
- Corregido lo que decia la documentacion de `Hint`: `.width(px)` es el ancho que el `Hint` **pide**, no un limite; el texto se parte al ancho real que reciba.
- [screens.md](docs/guides/screens.md) incluye 10 **pantallas completas** (listado con tabla, lista y detalle, menu lateral, asistente, tablero, dos columnas, inicio de sesion centrado, editor y consola, ajustes con pestanas) generadas con `LayoutImages.java` (`scripts/doc-images.sh`). Su guia visual de cada contenedor se movio a la pagina de cada uno (ver arriba).
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
