# Como esta hecha Tessera y como modificarla

Para quien va a **cambiar la libreria** (agregar un widget, abrir un valor que hoy es fijo, corregir un bug). Si solo quieres otro aspecto
en una app, mira [customizing.md](customizing.md): casi siempre basta con `Theme`.

## Mapa del codigo

```
src/main/java/tessera/
  kit/         contenedores y widgets (lo que mas se usa)
  theme/       Theme (instala FlatLaf, fuente, colores, arcos), Icons (SVG, interruptor)
  notify/      Notification (tarjetas apiladas), Toast (una a la vez)
  app/         TrayDock (bandeja + Dock macOS), WindowPrefs (tamano de ventanas)
  components/  modelos, renderers, bordes y validacion sueltos
src/main/resources/tessera/   fuente Inter (fonts/) e iconos propios (img/)
src/demo/java/                Showcase: ejemplo ejecutable y fuente de las capturas de docs/
```

## Como funciona el kit

**Widgets** (`Label`, `Button`, `Input`, `Check`, `Radio`, `Toggle`, `Combo`, `Spinner`, `Table`...):

- Cada uno **extiende la clase de Swing** (`Button extends JButton`), asi que se puede usar donde Swing pida un `JButton`.
- Constructor `protected` + metodo estatico de fabrica (`Button.of(..)`, `Button.primary(..)`, `Input.text()`).
- Metodos encadenables que devuelven el propio widget. Los comunes vienen de dos interfaces con metodos por defecto:
  - `Styled<T>`: `tip`, `bold`, `fontSize`, `font`, `color`, `background`, `width`, `height`, `enabled`, `visible`, `name`.
  - `Clickable<T>` (extiende `Styled`): `onClick`, `icon`, `iconGap`, `selected`; para `Button`, `Check`, `Radio`, `Toggle`.
  - `T` es el propio tipo concreto (`Button implements Clickable<Button>`); `self()` hace el cast y asi la cadena no pierde el tipo.
- `Hint` extiende `JLabel`, **no** `Label`: son tipos distintos.

**Contenedores** (`Row`, `Col`, `Stack`, `Grid`, `Form`, `Card`...):

- Heredan de `Box<T>`: un `JPanel` transparente con `MigLayout` por dentro. `Box` guarda `gap` y `padding` y los convierte en restricciones
  con `migInsets()` / `relayout()`.
- Cada contenedor implementa `migLayout()` (restricciones del contenedor) y `place(Component)` (restricciones de cada hijo).
- `put(child, constraints)` agrega el hijo y le suma el tamano fijo que pidio con `.width()/.height()`.
- `.fill()`/`.fillBoth()` y `.width()/.height()` se guardan como **propiedades del componente** (`putClientProperty`, claves en `Kit`), no como
  campos: asi funcionan con cualquier `JComponent` aunque no sea del kit.

**Medidas**: todo valor en pixeles que entra por la API es un px "logico"; el kit lo pasa por `Kit.px(n)` (zoom de pantalla de FlatLaf) antes
de usarlo. Nunca poner numeros fijos sin escalar.

## Agregar un widget

1. Crear `kit/MiWidget.java` extendiendo la clase de Swing e implementando `Styled<MiWidget>` (o `Clickable<MiWidget>` si es un boton).
2. Constructor `protected`, fabrica estatica (`MiWidget.of(..)`), metodos encadenables que devuelven `this`.
3. Javadoc corto en espanol con un `<pre>` de uso, como los de `Button` o `Input`.
4. Agregarlo al `Showcase` (`src/demo/java/tessera/demo/Showcase.java`).
5. Documentarlo: una pagina nueva en `docs/reference/kit/widgets/` (o `containers/`) con la [plantilla de abajo](#plantilla-de-una-pagina-de-referencia), su imagen en `WidgetImages.java`, una fila en la tabla de [Referencia del kit](../reference/kit/README.md) y del [indice de la referencia](../reference/README.md), y la lista de [AGENTS.md](../../AGENTS.md).

## Abrir un valor fijo a las apps

Ejemplo: que el radio de `Card` siga al tema.

1. En `Kit`, agregar un campo `public static int CARD_ARC = 12;` (mismo estilo que `PRIMARY`, `SURFACE`...).
2. En `Theme.Builder`, un metodo `cardArc(int)` que lo guarde y, en `install()`, asignarlo a `Kit.CARD_ARC`.
3. En `Card.CardBorder`, leer `Kit.CARD_ARC` en lugar de la constante `ARC`.
4. Documentarlo en [customizing.md](customizing.md) (quitar la fila de "Lo que no se puede cambiar" y agregarla a "Resumen").

Con el valor por defecto igual al de antes, las apps que no lo llaman no cambian.

## Reglas

- **Java 8**: sin `var`, sin `List.of`, sin records. Se compila con `javac --release 8`.
- **Dependencias**: solo FlatLaf, FlatLaf Extras, JSVG y MigLayout. Nada especifico de una app (nombres, rutas, claves de idioma).
- **Compatibilidad**: no cambiar la firma de lo que ya existe; las apps lo usan. Agregar metodos nuevos esta bien, renombrar o quitar no.
- **Estilo**: codigo y comentarios en espanol, 2 espacios de indentacion, el estilo del archivo que se toca.
- **Sin datos sensibles** (nombres de empresas, correos, URLs, claves) en codigo, docs ni ejemplos: el proyecto puede ser open source.
- **Docs**: si cambia algo visible para quien usa la libreria, actualizar el Javadoc de la clase, [AGENTS.md](../../AGENTS.md), la guia del paquete
  (ver la tabla de abajo) y, si aplica, [customizing.md](customizing.md) y el [CHANGELOG](../../CHANGELOG.md).

| Si tocas... | Actualiza |
|---|---|
| un contenedor o widget de `tessera.kit` | su pagina en `docs/reference/kit/containers/` o `widgets/` (ver [la referencia del kit](../reference/kit/README.md)) |
| `Styled`, `Clickable`, `Box` o `Kit` | [Referencia del kit](../reference/kit/README.md) |
| `Notification` o `Toast` | [notification.md](../reference/notify/notification.md) o [toast.md](../reference/notify/toast.md) |
| `TrayDock` o `WindowPrefs` | [traydock.md](../reference/app/traydock.md) o [windowprefs.md](../reference/app/windowprefs.md) |
| una clase de `tessera.components` | la pagina de su categoria en [Componentes sueltos](../reference/components/README.md) |
| `Theme` o `Icons` | [theme.md](../reference/theme/theme.md) o [icons.md](../reference/theme/icons.md), y si cambia lo que se puede personalizar, [customizing.md](customizing.md) |
| la compilacion, dependencias o requisitos | [getting-started.md](getting-started.md) y el [README](../../README.md) |
| un problema conocido que se corrige (o aparece) | [faq.md](faq.md), la lista de "Trampas conocidas" de [AGENTS.md](../../AGENTS.md) y el [CHANGELOG](../../CHANGELOG.md) |

Los ejemplos de [recipes.md](recipes.md) estan compilados contra la libreria: si cambias una firma que usan, vuelve a compilarlos.

## Verificar

No hay tests automaticos. Se comprueba compilando y mirando el Showcase:

```
./build.sh --demo        (Windows: ./build.ps1 -Demo)
```

Las imagenes de la documentacion (`docs/img/`) las generan cinco clases de `src/demo/java/tessera/demo/`: `WidgetExamples.java` (un ejemplo por widget, `example-*.png`, y las imagenes de comparacion de [widgets.md](widgets.md), `widgets-*.png`; su codigo es el que muestran las paginas), `DocImages.java` (capturas de las guias), `LayoutImages.java` (la guia visual de [screens.md](screens.md): `layout-*.png` y `app-*.png`, con el mismo codigo que muestra la guia), `WidgetImages.java` (las paginas de widgets: `widget-*.png` y `container-*.png`) y `MenuImages.java` (la guia [menus.md](menus.md): `menus-*.png`, pantallas con el menu abierto encima del boton; los fragmentos de esa guia que no estan ahi viven en `MenuExamples.java`, que solo existe para que compilen). Despues de cambiar el aspecto de algo
que aparezca en ellas: `./build.sh && ./scripts/doc-images.sh`, y revisar los PNG antes de subirlos. `ScreenExample.java` es el ejemplo de
[screens.md](screens.md) compilado: si cambia ese ejemplo, se cambian los dos.

`build.sh` compila con `javac --release 8`, copia los recursos y arma `dist/tessera-<version>.jar`. Las dependencias salen de `lib/`
(`scripts/fetch-libs.sh` las descarga de Maven Central).

## Publicar un cambio en las apps

Las apps (Text2Barcode, Codaly Sync, PDFLabelCut) llevan una **copia del jar** en su carpeta `vendor/tessera/`, no una dependencia viva.
Despues de cambiar Tessera:

1. Subir la version en `pom.xml` si cambia la API.
2. `./build.sh` y tomar `dist/tessera-<version>.jar`.
3. Copiarlo a `vendor/tessera/` de cada app y, si cambio el nombre, actualizar el classpath del proyecto (en NetBeans:
   `nbproject/project.properties`, ver [migrating.md](migrating.md)).
4. Compilar cada app y abrir sus pantallas.

## Estructura de la documentacion

```
docs/
  README.md             indice general
  guides/               guias (se leen de principio a fin): primeros pasos, pantallas, recetas, personalizar, migrar, FAQ, contribuir
  reference/            referencia (se consulta): una pagina por componente
    kit/                README.md (lo comun) + containers/ + widgets/
    theme/  notify/  app/  components/
  img/                  imagenes (generadas con scripts/doc-images.sh)
```

La separacion es la de siempre: las **guias** explican *como hacer algo* y se leen en orden; la **referencia** describe *que hace cada cosa* y se consulta suelta.

### Plantilla de una pagina de referencia

Toda pagina de `docs/reference/kit/` tiene el mismo orden, para que se encuentre lo mismo en el mismo lugar:

| Seccion | Contenido |
|---|---|
| Titulo y resumen | el nombre de la clase y una frase: que es |
| Tabla de datos | paquete, clase (de que extiende), como se crea, `import` |
| En una frase | que es y **que reemplaza en Swing puro** (un par de lineas) |
| Imagen | como se ve (generada con `WidgetImages`, `LayoutImages` o `DocImages`; el titulo de cada tarjeta es el codigo que la arma) |
| Cuando usarlo | casos de uso y, en **Cuando no**, que usar en su lugar |
| Ejemplo | una pantalla pequena y real, con su imagen (`example-*.png`) y el codigo que la arma, tomado de `WidgetExamples.java` entre las marcas `// ejemplo:nombre` y `// fin` |
| Referencia | todos los metodos en tablas, con ejemplos de codigo |
| Errores comunes | los sintomas que se repiten y su arreglo |
| Ver tambien | los componentes relacionados |

Reglas: los ejemplos de codigo deben compilar; las imagenes se generan, no se dibujan a mano; un enlace a otra pagina va con su ruta relativa y se comprueba que no este roto.
