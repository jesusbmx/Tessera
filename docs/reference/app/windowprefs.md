# WindowPrefs

Recuerda el **tamano de las ventanas**: cada `JFrame` y `JDialog` se abre como el usuario la dejo la ultima vez (y maximizada, si la dejo asi).

| | |
|---|---|
| **Paquete** | `tessera.app` |
| **Clase** | `WindowPrefs` (clase final), `WindowPrefs.Store` y `WindowPrefs.Saved` |
| **Se crea con** | `WindowPrefs.install(paquetes...)` (una sola vez) |
| **Importar** | `import tessera.app.WindowPrefs;` |

Recuerda el tamano de las ventanas: cada `JFrame` y `JDialog` se abre **como el usuario la dejo la ultima vez**, y maximizada si la dejo asi. Se
activa **una sola vez** al iniciar; no hay que tocar cada ventana.

## Ejemplo

```java
WindowPrefs.install("com.miapp.");                  // guarda en las preferencias del usuario del sistema
```

Con eso, toda ventana de un paquete que empiece con `com.miapp.` queda cubierta.

## Referencia

| Metodo | Que hace |
|---|---|
| `WindowPrefs.install(String... paquetes)` | activa el recuerdo usando las preferencias del usuario (`java.util.prefs.Preferences`) como almacen |
| `WindowPrefs.install(Store almacen, String... paquetes)` | igual, pero guardando donde quiera la app. **El almacen va primero** |
| `WindowPrefs.remember(Window ventana, String clave)` | aplica y guarda el tamano de una ventana que la deteccion automatica no cubre. Se llama **antes de mostrarla** |

## Que ventanas cubre

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

## Como funciona

- La clave es el **nombre de la clase** de la ventana: todas las instancias de la misma clase comparten su tamano.
- Justo antes de mostrarse, la ventana recibe el tamano guardado. Se mantiene **centrada donde estaba**, se recorta a la zona usable de la pantalla
  (sin la barra de tareas) y **no baja de su tamano minimo** si lo tiene.
- El tamano se guarda **500 ms despues del ultimo cambio** (arrastrar un borde genera muchos eventos).
- Si la ventana esta **maximizada**, se guarda ese estado y se conserva el tamano de antes de maximizar (el que cuenta al restaurarla).
- Una ventana **minimizada** no se guarda.

## Almacen propio (`WindowPrefs.Store`)

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

## Detalles a tener en cuenta

- Llama a `install` **una sola vez**, al iniciar, antes de crear las ventanas.
- Si cambias el nombre de la clase de una ventana (o su paquete), pierde lo guardado: la clave es ese nombre. Con el almacen por defecto, las claves
  de mas de 80 caracteres se recortan por el principio.
- `WindowPrefs` guarda solo el **tamano y el estado maximizado**, no la posicion.
