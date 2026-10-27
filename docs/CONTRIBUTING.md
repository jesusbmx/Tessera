# Como esta hecha Tessera y como modificarla

Para quien va a **cambiar la libreria** (agregar un widget, abrir un valor que hoy es fijo, corregir un bug). Si solo quieres otro aspecto
en una app, mira [CUSTOMIZING.md](CUSTOMIZING.md): casi siempre basta con `Theme`.

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
4. Agregarlo al `Showcase` (`src/demo/java/tessera/demo/Showcase.java`) y a las tablas de [AGENTS.md](../AGENTS.md) y del [README](../README.md).

## Abrir un valor fijo a las apps

Ejemplo: que el radio de `Card` siga al tema.

1. En `Kit`, agregar un campo `public static int CARD_ARC = 12;` (mismo estilo que `PRIMARY`, `SURFACE`...).
2. En `Theme.Builder`, un metodo `cardArc(int)` que lo guarde y, en `install()`, asignarlo a `Kit.CARD_ARC`.
3. En `Card.CardBorder`, leer `Kit.CARD_ARC` en lugar de la constante `ARC`.
4. Documentarlo en [CUSTOMIZING.md](CUSTOMIZING.md) (quitar la fila de "Lo que no se puede cambiar" y agregarla a "Resumen").

Con el valor por defecto igual al de antes, las apps que no lo llaman no cambian.

## Reglas

- **Java 8**: sin `var`, sin `List.of`, sin records. Se compila con `javac --release 8`.
- **Dependencias**: solo FlatLaf, FlatLaf Extras, JSVG y MigLayout. Nada especifico de una app (nombres, rutas, claves de idioma).
- **Compatibilidad**: no cambiar la firma de lo que ya existe; las apps lo usan. Agregar metodos nuevos esta bien, renombrar o quitar no.
- **Estilo**: codigo y comentarios en espanol, 2 espacios de indentacion, el estilo del archivo que se toca.
- **Sin datos sensibles** (nombres de empresas, correos, URLs, claves) en codigo, docs ni ejemplos: el proyecto puede ser open source.
- **Docs**: si cambia algo visible para quien usa la libreria, actualizar el Javadoc de la clase, [AGENTS.md](../AGENTS.md) y, si aplica,
  [CUSTOMIZING.md](CUSTOMIZING.md).

## Verificar

No hay tests automaticos. Se comprueba compilando y mirando el Showcase:

```
./build.sh --demo        (Windows: ./build.ps1 -Demo)
```

Las imagenes de las guias (`docs/img/`) las genera `src/demo/java/tessera/demo/DocImages.java`. Despues de cambiar el aspecto de algo
que aparezca en ellas: `./build.sh && ./scripts/doc-images.sh`, y revisar los PNG antes de subirlos. `ScreenExample.java` es el ejemplo de
[SCREENS.md](SCREENS.md) compilado: si cambia ese ejemplo, se cambian los dos.

`build.sh` compila con `javac --release 8`, copia los recursos y arma `dist/tessera-<version>.jar`. Las dependencias salen de `lib/`
(`scripts/fetch-libs.sh` las descarga de Maven Central).

## Publicar un cambio en las apps

Las apps (Text2Barcode, Codaly Sync, PDFLabelCut) llevan una **copia del jar** en su carpeta `vendor/tessera/`, no una dependencia viva.
Despues de cambiar Tessera:

1. Subir la version en `pom.xml` si cambia la API.
2. `./build.sh` y tomar `dist/tessera-<version>.jar`.
3. Copiarlo a `vendor/tessera/` de cada app y, si cambio el nombre, actualizar el classpath del proyecto (en NetBeans:
   `nbproject/project.properties`, ver [MIGRATING.md](MIGRATING.md)).
4. Compilar cada app y abrir sus pantallas.
