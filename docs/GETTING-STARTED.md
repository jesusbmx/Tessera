# Primeros pasos

De cero a una ventana con Tessera en unos 10 minutos: conseguir el jar, ponerlo en tu proyecto, instalar el tema y armar tu primera
pantalla.

## Que necesitas

| Requisito | Detalle |
|---|---|
| **JDK 8 o superior** | la libreria se compila con `--release 8`, asi que funciona en Java 8 y en versiones nuevas. Necesitas un **JDK** (con `javac` y `jar`), no solo el JRE |
| **bash y curl** (opcional) | solo para descargar las dependencias con `scripts/fetch-libs.sh`. En Windows vienen con Git Bash. Si no los tienes, descarga los jars a mano (ver abajo) |
| **Maven** (opcional) | solo si prefieres `mvn package` en vez de los scripts |

Tessera depende de cuatro librerias (FlatLaf, FlatLaf Extras, JSVG y MigLayout); no necesita nada mas.

## 1. Conseguir el jar de Tessera

Tessera **no esta en Maven Central**: se compila del codigo fuente y se usa el jar resultante.

```
git clone https://github.com/jesusbmx/Tessera.git
cd Tessera
./build.sh                # Windows con PowerShell: ./build.ps1
```

`build.sh` hace tres cosas: descarga las dependencias a `lib/` (si faltan), compila con Java 8 y arma `dist/tessera-1.0.0.jar`.

Si el script dice `jar: command not found`, tu PATH no tiene el JDK completo: ver la [FAQ](FAQ.md#buildsh-dice-falta-jar-o-jar-command-not-found).

Con Maven: `mvn package` deja el jar en `target/`, y `mvn install` lo instala en tu repositorio local (`~/.m2`) para usarlo desde otros
proyectos Maven.

> Si tu equipo no tiene `bash`, descarga a mano estos cinco jars de Maven Central a `lib/`:
> `flatlaf-3.7`, `flatlaf-extras-3.7`, `jsvg-2.0.0`, `miglayout-core-4.2` y `miglayout-swing-4.2`
> (las rutas exactas estan en [scripts/fetch-libs.sh](../scripts/fetch-libs.sh)).

Para ver la libreria funcionando antes de usarla: `./build.sh --demo` abre el **Showcase** con todos los widgets
(en Windows: `./build.ps1 -Demo`).

## 2. Ponerlo en tu proyecto

La app necesita en el classpath **seis jars**: el de Tessera y las cinco dependencias.

| Jar | De donde sale |
|---|---|
| `tessera-1.0.0.jar` | `dist/` despues de compilar |
| `flatlaf-3.7.jar`, `flatlaf-extras-3.7.jar` | `lib/` |
| `jsvg-2.0.0.jar` | `lib/` |
| `miglayout-core-4.2.jar`, `miglayout-swing-4.2.jar` | `lib/` |

### Sin gestor de dependencias (javac / java a mano)

Copia los seis jars a una carpeta `lib/` de tu proyecto. Compilar y ejecutar:

```
# Linux / macOS
javac -cp "lib/*" -d out src/Hola.java
java  -cp "out:lib/*" Hola

# Windows (PowerShell o cmd)
javac -cp "lib/*" -d out src\Hola.java
java  -cp "out;lib/*" Hola
```

La diferencia es el separador del classpath: `:` en Linux y macOS, `;` en Windows.

### En un IDE (NetBeans, IntelliJ, Eclipse, VS Code)

Agrega los seis jars como librerias del proyecto (en NetBeans: clic derecho en *Libraries* → *Add JAR/Folder*; en IntelliJ: *Project
Structure* → *Libraries*). La guia de [MIGRATING.md](MIGRATING.md#1-poner-la-libreria-en-el-proyecto) detalla el caso NetBeans.

### Con Maven

Despues de `mvn install` en Tessera, en el `pom.xml` de tu app:

```xml
<dependency>
  <groupId>tessera</groupId>
  <artifactId>tessera</artifactId>
  <version>1.0.0</version>
</dependency>
```

El `pom.xml` de Tessera ya declara FlatLaf, FlatLaf Extras, JSVG y MigLayout, asi que Maven los trae solo.

### Con Gradle

```groovy
dependencies {
    implementation files('libs/tessera-1.0.0.jar')      // el jar que compilaste
    implementation 'com.formdev:flatlaf:3.7'
    implementation 'com.formdev:flatlaf-extras:3.7'
    implementation 'com.github.weisj:jsvg:2.0.0'
    implementation 'com.miglayout:miglayout-swing:4.2'
}
```

(o `mavenLocal()` y `implementation 'tessera:tessera:1.0.0'` si hiciste `mvn install`).

## 3. Tu primera ventana

Crea `Hola.java`:

```java
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import tessera.kit.Button;
import tessera.kit.Card;
import tessera.kit.Col;
import tessera.kit.Footer;
import tessera.kit.Form;
import tessera.kit.Header;
import tessera.kit.Input;
import tessera.kit.Page;
import tessera.theme.Theme;

public class Hola {

  public static void main(String[] args) {
    SwingUtilities.invokeLater(() -> {
      Theme.builder().install();                       // una sola vez, antes de crear ventanas

      final Input txtName = Input.text().placeholder("Tu nombre");

      final JFrame frame = new JFrame("Hola Tessera");
      frame.setContentPane(Page.create()
          .top(Header.create("Hola", "Mi primera pantalla con Tessera"))
          .center(Col.gap(10).padding(12).children(
              Card.create("Datos").children(Form.create().row("Nombre", txtName))))
          .bottom(Footer.of(
              Button.of("Cancelar").onClick(frame::dispose),
              Button.primary("Guardar").onClick(() -> System.out.println("Hola " + txtName.getText())))));

      frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      frame.pack();
      frame.setLocationRelativeTo(null);
      frame.setVisible(true);
    });
  }
}
```

Compila y ejecuta como se mostro arriba. Lo que acabas de usar:

| Pieza | Para que |
|---|---|
| `Theme.builder().install()` | instala FlatLaf, la fuente Inter y los colores. **Antes** de crear cualquier ventana |
| `Page` | ventana con cabecera (`top`), centro (`center`) y pie (`bottom`) |
| `Header` | el banner oscuro con titulo y descripcion |
| `Col` | acomoda hijos en columna; `padding(12)` deja margen alrededor |
| `Card` | bloque con titulo y borde redondeado |
| `Form` | filas de etiqueta + campo alineadas |
| `Footer` | botones del pie, alineados a la derecha |
| `Button.primary` | el boton principal (azul) |

Los botones usan `frame` dentro de su `onClick`: funciona porque `frame` se declara (y es `final`) **antes** de armar el contenido.

## 4. Reglas de oro

1. **Todo en el hilo de Swing (EDT).** Crear ventanas y tocar widgets se hace dentro de `SwingUtilities.invokeLater(...)`, como arriba.
   Las unicas excepciones son `Notification` y `Toast`, que se pueden mostrar desde cualquier hilo.
2. **El tema se instala una sola vez**, antes de crear ventanas. Cambiarlo con ventanas ya abiertas no las actualiza.
3. **Imports explicitos.** `Label`, `Button`, `List` y `TextArea` existen tambien en `java.awt` (y `Stack` en `java.util`): importa
   `tessera.kit.Button`, no `java.awt.*`.
4. **Medidas en px logicos**, sin multiplicar por el zoom: el kit lo hace.
5. **La logica arriba, la UI al final.** Para pantallas reales sigue la convencion de [SCREENS.md](SCREENS.md).

## 5. Siguientes pasos

| Quiero... | Leer |
|---|---|
| ver todos los contenedores y widgets, con todos sus metodos | [KIT.md](KIT.md) |
| cambiar colores, esquinas, fuente | [CUSTOMIZING.md](CUSTOMIZING.md) |
| armar pantallas completas con el patron recomendado | [SCREENS.md](SCREENS.md) |
| ver ejemplos listos para copiar (login, maestro-detalle, asistente...) | [RECIPES.md](RECIPES.md) |
| mostrar avisos y mensajes | [NOTIFICATIONS.md](NOTIFICATIONS.md) |
| icono de bandeja o recordar el tamano de las ventanas | [APP.md](APP.md) |
| tablas, validacion de formularios, arrastrar y soltar | [COMPONENTS.md](COMPONENTS.md) |
| resolver un problema | [FAQ.md](FAQ.md) |
