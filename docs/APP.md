# Integracion con el escritorio: `TrayDock` y `WindowPrefs` (`tessera.app`)

Dos piezas para que una app de escritorio se sienta nativa: un **icono en la bandeja del sistema** (y reapertura desde el Dock de macOS) y
que **cada ventana se abra con el tamano que el usuario le dejo**.

## `TrayDock`

Icono de la bandeja del sistema (Windows, Linux) con menu, y reapertura de la app desde el Dock (macOS). Es lo que permite que una app siga
corriendo con la ventana cerrada o minimizada (un servicio, un sincronizador) y se pueda volver a abrir.

### Ejemplo

```java
final Image icon = Toolkit.getDefaultToolkit().getImage(getClass().getResource("/com/miapp/img/tray.png"));

final TrayDock dock = TrayDock.create(icon, "Mi App")
    .onOpen(this::showWindow)                                       // clic (doble clic en Windows) sobre el icono
    .menu(() -> Arrays.asList(
        TrayDock.Item.title("Mi App v1.0"),                         // linea en negrita
        TrayDock.Item.separator(),
        TrayDock.Item.of("Abrir", this::showWindow),
        TrayDock.Item.of(running ? "Detener" : "Iniciar", this::toggleService),
        TrayDock.Item.separator(),
        TrayDock.Item.of("Salir", this::exit)));

dock.install();                                                      // pone el icono en la bandeja
TrayDock.onAppReopened(this::showWindow);                            // macOS: clic en el icono del Dock
```

### Referencia

| Metodo | Que hace |
|---|---|
| `TrayDock.create(Image imagen, String tooltip)` | prepara el icono (todavia no lo muestra). El sistema ajusta la imagen al tamano de la bandeja |
| `.onOpen(Runnable)` | que hacer al hacer clic sobre el icono (doble clic en Windows) |
| `.menu(Supplier<List<TrayDock.Item>>)` | las lineas del menu. Se **pide de nuevo** en cada `refresh()` |
| `.install()` | pone el icono en la bandeja. No hace nada si el sistema no soporta bandeja o si ya esta instalado (no lanza excepcion) |
| `.refresh()` | vuelve a armar el menu (llamalo cuando cambia el estado de la app) |
| `.remove()` | quita el icono de la bandeja |
| `TrayDock.onAppReopened(Runnable)` | macOS: la app ya abierta se reabre con un clic en su icono del Dock. Devuelve `true` si quedo registrado |

Lineas del menu (`TrayDock.Item`):

| Fabrica | Linea |
|---|---|
| `Item.of(texto, accion)` | normal, con accion |
| `Item.title(texto)` | en **negrita**, sin accion (el nombre de la app arriba del menu) |
| `Item.title(texto, accion)` | en negrita y con accion |
| `Item.separator()` | linea divisoria |

### Menu dinamico

El menu se declara como un `Supplier`, no como una lista fija: asi el texto refleja el estado actual. Cada vez que algo cambia, llama a
`refresh()` y se arma de nuevo:

```java
private void toggleService() {
  running = !running;
  dock.refresh();                       // el menu vuelve a preguntar y ahora dice "Detener" / "Iniciar"
}
```

### Detalles a tener en cuenta

- El menu es un `PopupMenu` de **AWT** (nativo): no admite iconos ni usa el aspecto de FlatLaf. La fuente es la de `Theme.fontFamily()`, tamano 14.
- **Linux**: la bandeja depende del entorno de escritorio; algunos no la soportan o necesitan una extension. `install()` simplemente no hace nada
  si no hay soporte, asi que **no dependas de la bandeja como unica via para abrir la app**.
- **macOS**: el icono aparece en la barra de menus. Para reabrir la ventana desde el Dock usa `onAppReopened`, que necesita **Java 9 o superior**;
  con Java 8 devuelve `false` (la libreria compila para Java 8 y lo registra por reflexion). El `Runnable` corre **fuera del EDT**: si toca la
  interfaz, envuelvelo en `SwingUtilities.invokeLater`.
- **Cerrar sin salir**: para que la app siga viva al cerrar la ventana, `frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE)` y un item "Salir"
  que llame a `dock.remove()` y `System.exit(0)`.

## `WindowPrefs`

Recuerda el tamano de las ventanas: cada `JFrame` y `JDialog` se abre **como el usuario la dejo la ultima vez**, y maximizada si la dejo asi. Se
activa **una sola vez** al iniciar; no hay que tocar cada ventana.

### Ejemplo

```java
WindowPrefs.install("com.miapp.");                  // guarda en las preferencias del usuario del sistema
```

Con eso, toda ventana de un paquete que empiece con `com.miapp.` queda cubierta.

### Referencia

| Metodo | Que hace |
|---|---|
| `WindowPrefs.install(String... paquetes)` | activa el recuerdo usando las preferencias del usuario (`java.util.prefs.Preferences`) como almacen |
| `WindowPrefs.install(Store almacen, String... paquetes)` | igual, pero guardando donde quiera la app. **El almacen va primero** |
| `WindowPrefs.remember(Window ventana, String clave)` | aplica y guarda el tamano de una ventana que la deteccion automatica no cubre. Se llama **antes de mostrarla** |

### Que ventanas cubre

Se recuerda una ventana cuando **todo esto** es cierto:

1. es un `JFrame` o `JDialog` (o subclase),
2. el nombre completo de su clase empieza con alguno de los paquetes que le diste a `install`,
3. se puede **redimensionar** (`isResizable()`),
4. **no** es `undecorated`.

Quedan fuera los `JOptionPane`, los avisos (`Toast`, `Notification`) y las ventanas ajenas a tu app. Un `new JDialog(...)` suelto (sin
subclase propia) tiene la clase `javax.swing.JDialog`, que no empieza con tu paquete: para ese caso usa `remember`:

```java
final JDialog dialog = new JDialog(owner, "Opciones", true);
WindowPrefs.remember(dialog, "opciones");           // la clave identifica la ventana en el almacen
dialog.setVisible(true);
```

### Como funciona

- La clave es el **nombre de la clase** de la ventana: todas las instancias de la misma clase comparten su tamano.
- Justo antes de mostrarse, la ventana recibe el tamano guardado. Se mantiene **centrada donde estaba**, se recorta a la zona usable de la pantalla
  (sin la barra de tareas) y **no baja de su tamano minimo** si lo tiene.
- El tamano se guarda **500 ms despues del ultimo cambio** (arrastrar un borde genera muchos eventos).
- Si la ventana esta **maximizada**, se guarda ese estado y se conserva el tamano de antes de maximizar (el que cuenta al restaurarla).
- Una ventana **minimizada** no se guarda.

### Almacen propio (`WindowPrefs.Store`)

Por defecto el tamano va a `java.util.prefs.Preferences` (registro de Windows, preferencias de macOS, `~/.java` en Linux), en el nodo `tessera/windows`.
Si tu app ya guarda su configuracion en otro lado (un archivo, una base de datos), implementa `Store`:

```java
public interface Store {
  Saved load(String clave);              // lo guardado, o null si no hay nada
  void save(String clave, Saved saved);  // Saved tiene width, height y maximized
}
```

```java
WindowPrefs.install(new WindowPrefs.Store() {
  @Override
  public WindowPrefs.Saved load(String key) {
    final String v = config.get("window." + key);              // "800,600,false"
    if (v == null) return null;
    final String[] p = v.split(",");
    return new WindowPrefs.Saved(Integer.parseInt(p[0]), Integer.parseInt(p[1]), Boolean.parseBoolean(p[2]));
  }

  @Override
  public void save(String key, WindowPrefs.Saved s) {
    config.put("window." + key, s.width + "," + s.height + "," + s.maximized);
  }
}, "com.miapp.");
```

`load` y `save` se llaman desde el hilo de Swing.

### Detalles a tener en cuenta

- Llama a `install` **una sola vez**, al iniciar, antes de crear las ventanas.
- Si cambias el nombre de la clase de una ventana (o su paquete), pierde lo guardado: la clave es ese nombre. Con el almacen por defecto, las claves
  de mas de 80 caracteres se recortan por el principio.
- `WindowPrefs` guarda solo el **tamano y el estado maximizado**, no la posicion.
