# Preguntas frecuentes y solucion de problemas

Cada entrada va de **sintoma** a **causa** y **solucion**. Si no encuentras el tuyo, revisa tambien la tabla de
[errores comunes del kit](../reference/kit/README.md#errores-comunes).

## Indice

- [Instalacion y arranque](#instalacion-y-arranque)
- [Aspecto](#aspecto)
- [Layout y widgets](#layout-y-widgets)
- [Avisos, bandeja y ventanas](#avisos-bandeja-y-ventanas)
- [Preguntas generales](#preguntas-generales)

## Instalacion y arranque

### `NoClassDefFoundError` o `ClassNotFoundException` de `com.formdev...`, `net.miginfocom...` o `com.github.weisj...`

Falta una dependencia en el classpath. La app necesita **seis jars**: `tessera-<version>.jar` mas FlatLaf, FlatLaf Extras, JSVG, MigLayout core y
MigLayout swing. Ver [getting-started.md](getting-started.md#2-ponerlo-en-tu-proyecto).

### Los iconos SVG no aparecen o lanzan excepcion

JSVG es el motor de los SVG y FlatLaf Extras lo pide: comprueba que `jsvg-2.0.0.jar` esta en el classpath. La ruta del icono (`"app/img/x.svg"`)
es del classpath **de tu app**: el SVG tiene que estar empaquetado en tu jar o carpeta de recursos.

### `./build.sh` dice `Falta 'jar'` o `jar: command not found`

Tu PATH no tiene el JDK completo: `javac` esta pero `jar` no. Pasa en Windows cuando el PATH apunta a la carpeta `Common Files\Oracle\Java\javapath`,
que solo expone `java` y `javac`. `build.sh` y `build.ps1` ya buscan el `jar` en `JAVA_HOME` por su cuenta; si aun asi falla, instala un **JDK**
(no solo el JRE) y define `JAVA_HOME` apuntando a su carpeta, o agrega su `bin` al PATH.

### `./build.sh` falla al descargar las dependencias

`scripts/fetch-libs.sh` usa `curl` y Maven Central. Sin red, o sin `curl`/`bash`, descarga a mano los cinco jars a `lib/` (los nombres exactos estan
en el script) y vuelve a ejecutar.

### Compila con Java 17 pero mi proyecto es Java 8

Tessera se compila con `javac --release 8`, asi que su bytecode corre en Java 8. Lo que debes cuidar es compilar **tu** proyecto con un JDK que
produzca bytecode compatible con el Java en que vayas a ejecutarlo.

## Aspecto

### Todo el texto sale al doble de grande

Es el problema de las fuentes **OTF** con el Java 8 de Oracle, que las escala mal. Tessera empaqueta Inter como **TTF** justamente por eso. Si
agregas una fuente propia, usa TTF. Ver [customizing.md](customizing.md#4-la-fuente).

### Cambie el acento (`accent`) y los radios, casillas y barras de progreso siguen azules

Limitacion conocida: `Theme` escribe el acento despues de que FlatLaf ya calculo esos colores. Hay un truco para aplicarlo; esta explicado en
[customizing.md](customizing.md#nota-sobre-el-acento).

### Cambie el radio con `arc(...)` pero las tarjetas siguen con esquinas de 12

El radio de `Card` es fijo (12) y no sigue a `Theme.arc`. Ver [customizing.md](customizing.md#5-lo-que-no-se-puede-cambiar-desde-la-app).

### Asigne `Kit.PRIMARY` (u otro color) y los botones no cambian

Cada widget toma el color **al crearse**. Asignalo antes de armar ninguna pantalla; lo mas seguro es hacerlo en `Theme.builder().primary(...)`.

### Quiero modo oscuro

Tessera no trae modo oscuro: `Theme.install()` instala el look and feel **claro** (`FlatIntelliJLaf`) y la paleta del kit esta pensada para fondos
claros. Se puede partir de FlatLaf directamente, pero los colores del kit (`Kit.SURFACE`, `Kit.DARK`...) habria que ajustarlos a mano.

### Mis iconos PNG se ven pequenos o borrosos en pantallas de alta resolucion

`Icons.of` carga los PNG **tal cual**, sin escalarlos. Los **SVG** se dibujan vectorialmente a cualquier zoom: usa SVG siempre que puedas.

### Quiero cambiar el tema con la app abierta

No esta soportado: el tema se instala una vez, antes de crear ventanas. Las ventanas ya creadas no se actualizan.

## Layout y widgets

### `.width(200)` no hace nada

Solo lo entienden los contenedores del kit (`Row`, `Col`, `Form`, `Card`, `Stack`, `Grid`). Dentro de un `JPanel` suelto, o de un `Page`, se ignora.

### Un boton dentro de un `Col` no se estira

Es lo esperado: botones y etiquetas se quedan con su ancho natural dentro de un `Col`. Usa `col.fill(boton)` para que ocupe todo el ancho.

### La tabla no muestra el encabezado

Las tablas de Swing necesitan un `JScrollPane`: envuelvela con `Scroll.of(table)`.

### `Label`, `Button`, `List`, `TextArea` o `Stack` dicen "reference is ambiguous" o toman la clase equivocada

Tessera y Java tienen clases con el mismo nombre: `Label`, `Button`, `List`, `TextArea` (con `java.awt`) y `Stack` (con `java.util`). Con
importaciones de comodin (`import java.awt.*;`) el compilador no sabe cual quieres. Usa **imports explicitos** (`import tessera.kit.Button;`) y, si
necesitas la otra, su nombre completo (`java.util.Stack`).

### `onClick` no se dispara al llamar a `setSelected(true)` por codigo

`onClick` es un `ActionListener`, y Swing solo lo dispara con acciones del usuario. Para reaccionar a cambios hechos por codigo usa un `ItemListener`
y registralo **despues** de armar la pantalla.

### `Combo.onChange` se dispara mientras lleno el combo

`Combo.onChange` tambien se dispara cuando se elige un elemento por codigo. Llena el combo y elige su valor inicial **antes** de registrar el `onChange`.

### Error `illegal forward reference` en los campos de mi pantalla

Java inicializa los campos en el orden en que aparecen: si un campo usa otro en su inicializador, el usado debe estar **antes**. Ver
[screens.md](screens.md#reglas).

### Un texto con `<` o `&` sale raro en un `Hint` o en `Card.subtitle`

El texto se mete dentro de `<html>`, asi que esos caracteres se interpretan como HTML. Escapalos (`&lt;`, `&amp;`), sobre todo si el texto viene del
usuario. Los textos de `Toast` y `Notification`, en cambio, se escapan solos (salvo un `Toast` que empiece con `<html>`).

### Despues de `CheckableListModel.configureList(lista)` mi `MouseListener` dejo de funcionar

`configureList` **quita los `MouseListener` que la lista ya tuviera** para que un clic alterne la casilla. Agrega los tuyos despues, o escucha la
seleccion con un `ListSelectionListener`.

## Avisos, bandeja y ventanas

### Un `Toast` con `LENGTH_INDEFINITE` no se cierra al hacerle clic

Es asi: un toast indefinido solo se cierra con `toast.dismiss()` o con su boton de accion. Guarda la referencia y cierralo desde tu codigo.

### Las notificaciones se ven cuadradas y sin animacion en Linux

Las esquinas redondeadas y el fundido necesitan translucidez de ventana, que en Linux depende del gestor de ventanas (compositor). Sin ella, la tarjeta
sale rectangular y sin fundido en vez de fallar. Es esperado.

### El icono de la bandeja no aparece (Linux)

La bandeja depende del entorno de escritorio y algunos no la soportan. `TrayDock.install()` no hace nada en ese caso (no lanza excepcion). No hagas
que la bandeja sea la unica forma de abrir tu app.

### `TrayDock.onAppReopened` devuelve `false`

Necesita **macOS y Java 9 o superior** (usa una API que no existe en Java 8). En otros sistemas, o con Java 8, devuelve `false`: es normal.

### `WindowPrefs` no recuerda el tamano de una ventana

Revisa que la ventana cumpla **todo** esto: su clase empieza con uno de los paquetes que pasaste a `install`, es redimensionable y no es
`undecorated`. Un `new JDialog(...)` suelto tiene la clase `javax.swing.JDialog`: para ese caso usa `WindowPrefs.remember(dialog, "clave")` antes de
mostrarlo. Ver [WindowPrefs](../reference/app/windowprefs.md#que-ventanas-cubre).

### Puedo mostrar un aviso desde un hilo secundario?

Si: `Notification` y `Toast` se pueden mostrar desde **cualquier hilo**. Lo demas de Swing (ventanas, widgets) debe tocarse en el hilo de Swing (EDT):
`SwingUtilities.invokeLater(...)`.

## Preguntas generales

### Tessera esta en Maven Central?

No. Se compila del codigo fuente (`./build.sh` o `mvn package`) y se usa el jar resultante, o se instala en el repositorio Maven local con
`mvn install`. Ver [getting-started.md](getting-started.md).

### Puedo mezclar widgets de Tessera con Swing normal?

Si. Los widgets del kit **son** los de Swing (`Button extends JButton`), y los contenedores aceptan cualquier `Component`. Ver
[Referencia del kit](../reference/kit/README.md#mezclar-con-swing-puro).

### Funciona con el disenador de formularios de NetBeans?

Tessera reemplaza al disenador: las pantallas se arman a mano con el kit. Para pasar una app con formularios `.form` / `GroupLayout`, ver
[migrating.md](migrating.md).

### Trae textos en varios idiomas?

No: la libreria no trae un sistema de idiomas. Los textos de tus pantallas los pones tu (con el sistema de idiomas de tu app).

### Tiene pruebas automaticas?

No. Se verifica compilando y mirando el Showcase (`./build.sh --demo`). Ver [contributing.md](contributing.md#verificar).

### Puedo usar los iconos o la fuente que trae?

La fuente **Inter** (SIL Open Font License 1.1) y los iconos de avisos (derivados de Material Symbols, Apache 2.0) viajan dentro del jar. Los iconos
internos son para la propia libreria, no para tus pantallas: los de tu app van en los recursos de tu app. Detalle en la seccion de licencias del
[README](../../README.md#licencias-de-terceros).
