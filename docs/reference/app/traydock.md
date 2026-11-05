# TrayDock

Icono de la **bandeja del sistema** (Windows, Linux) con menu, y reapertura desde el **Dock** de macOS.

| | |
|---|---|
| **Paquete** | `tessera.app` |
| **Clase** | `TrayDock` (clase final) y `TrayDock.Item` (lineas del menu) |
| **Se crea con** | `TrayDock.create(imagen, tooltip)` |
| **Importar** | `import tessera.app.TrayDock;` |

Icono de la bandeja del sistema (Windows, Linux) con menu, y reapertura de la app desde el Dock (macOS). Es lo que permite que una app siga
corriendo con la ventana cerrada o minimizada (un servicio, un sincronizador) y se pueda volver a abrir.

## Ejemplo

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

## Referencia

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

## Menu dinamico

El menu se declara como un `Supplier`, no como una lista fija: asi el texto refleja el estado actual. Cada vez que algo cambia, llama a
`refresh()` y se arma de nuevo:

```java
private void toggleService() {
  running = !running;
  dock.refresh();                       // el menu vuelve a preguntar y ahora dice "Detener" / "Iniciar"
}
```

## Detalles a tener en cuenta

- El menu es un `PopupMenu` de **AWT** (nativo): no admite iconos ni usa el aspecto de FlatLaf. La fuente es la de `Theme.fontFamily()`, tamano 14.
- **Linux**: la bandeja depende del entorno de escritorio; algunos no la soportan o necesitan una extension. `install()` simplemente no hace nada
  si no hay soporte, asi que **no dependas de la bandeja como unica via para abrir la app**.
- **macOS**: el icono aparece en la barra de menus. Para reabrir la ventana desde el Dock usa `onAppReopened`, que necesita **Java 9 o superior**;
  con Java 8 devuelve `false` (la libreria compila para Java 8 y lo registra por reflexion). El `Runnable` corre **fuera del EDT**: si toca la
  interfaz, envuelvelo en `SwingUtilities.invokeLater`.
- **Cerrar sin salir**: para que la app siga viva al cerrar la ventana, `frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE)` y un item "Salir"
  que llame a `dock.remove()` y `System.exit(0)`.
