# Avisos: `Notification` y `Toast` (`tessera.notify`)

Dos formas de avisar al usuario sin abrir un dialogo que bloquee. Las dos tienen el mismo aspecto (tarjeta **negra** con texto blanco y
un icono de color segun el estilo) y ninguna usa `JOptionPane`.

![Pestana de avisos del Showcase](showcase-2.png)

## Cual usar

| | `Notification` | `Toast` |
|---|---|---|
| Aparece | en la **esquina inferior derecha de la pantalla** | **centrado sobre una ventana** o componente |
| Varios a la vez | **si**, se apilan y se acomodan | **no**, se muestran de uno en uno, en cola |
| Identificador | **si** (`id` obligatorio): mostrar otra con el mismo `id` la **reemplaza** | no |
| Barra de progreso | si | no |
| Boton de accion | si (`setOnAction`) | si (`setAction`) |
| Clic en el cuerpo | si (`setOnClick`) | no hace nada |
| Duracion corta / larga | 4 s / 8 s | 3 s / 6 s |
| Ideal para | cosas que pasan **en segundo plano** (una impresion termino, hay una actualizacion, una descarga avanza) | **confirmaciones inmediatas** de lo que acaba de hacer el usuario ("Copiado", "Guardado") |

Regla practica: si el usuario esta mirando la ventana y acaba de hacer clic, `Toast`; si algo ocurre solo y puede que no este mirando,
`Notification`.

Estilos de las dos: `NORMAL`, `SUCCESS`, `ERROR`, `WARNING`. El estilo solo cambia el **icono** y su color; el fondo siempre es negro.

## `Notification`

### Ejemplo

```java
new Notification.Builder("print.done")                  // el id es obligatorio
    .setTitle("Impresion")
    .setMessage("La etiqueta se envio correctamente.")
    .setStyle(Notification.Style.SUCCESS)
    .setDuration(Notification.LENGTH_LONG)
    .setOnAction("Ver", n -> abrirHistorial())          // boton de la tarjeta
    .setOnClick(n -> abrirHistorial())                  // clic en el cuerpo
    .show();
```

### El `id`

Cada notificacion lleva un `id` (texto, no vacio: si es `null` o vacio el `Builder` lanza `IllegalArgumentException`). Si muestras otra con
el **mismo id mientras la anterior sigue visible**, la nueva **toma su lugar** en vez de apilarse. Sirve para estados que cambian
("Conectando..." → "Conectado") y para no llenar la pantalla de avisos repetidos. Usa ids con significado: `"print.done"`, `"update.ready"`,
`"download.report"`.

### `Notification.Builder`

| Metodo | Que hace |
|---|---|
| `new Notification.Builder(id)` | empieza una notificacion con ese `id` |
| `.setTitle(String)` | titulo, en una linea (si no cabe se recorta con "...") |
| `.setMessage(String)` | mensaje; se parte en lineas y, pasadas unas 8, aparece scroll vertical en vez de agrandar la tarjeta |
| `.setStyle(Notification.Style)` | `NORMAL` (por defecto), `SUCCESS`, `ERROR`, `WARNING` |
| `.setDuration(int)` | milisegundos: `LENGTH_SHORT` (4000, por defecto), `LENGTH_LONG` (8000), `LENGTH_INDEFINITE` (-1: no se cierra sola) o cualquier valor en ms |
| `.setIcon(Image)` | icono propio (el logo de la app) en lugar del dibujado por el estilo |
| `.setOnClick(Consumer<Notification>)` | clic en cualquier parte del cuerpo (no en el boton de accion) |
| `.setOnAction(String texto, Consumer<Notification>)` | boton de accion, estilo `Snackbar` de Android. Al usarlo la tarjeta se cierra y luego corre la accion |
| `.setOnDismiss(Consumer<Notification>)` | se ejecuta si se cierra **sin** clic ni accion (la X o el tiempo) |
| `.setDismissible(boolean)` | con `false` oculta la X: el usuario no puede descartarla sin usar la accion (avisos obligatorios, como una actualizacion forzosa) |
| `.setProgress(int valor, int max)` | agrega una barra de progreso |
| `.show()` | la muestra y devuelve la `Notification` (se puede ignorar) |

Los `Consumer<Notification>` reciben **la propia notificacion**, con la que puedes leer el `id` o actualizarla. Cuando el callback corre,
la notificacion **ya se esta cerrando**, asi que `dismiss()` dentro del callback no hace nada.

### Sobre una notificacion ya mostrada

Son los metodos de la `Notification` que devuelve `show()` (y la que reciben los callbacks). Cada llamada busca la tarjeta por `id`: si ya
se cerro, se ignora; si otra con el mismo `id` la reemplazo, actua sobre la nueva.

| Metodo | Que hace |
|---|---|
| `getId()` | el `id` con el que se creo |
| `setProgress(valor, max)` | actualiza la barra (no hace nada si no se creo con `setProgress`) |
| `setProgress(valor, max, etiqueta)` | igual, con un texto propio en la barra (por defecto `valor / max`), p. ej. `"12.3 / 45.6 MB"` |
| `complete(titulo, mensaje, estilo, duracion)` | oculta la barra y reemplaza titulo, mensaje y estilo; reinicia el cierre automatico con esa duracion |
| `complete(titulo, mensaje, estilo, duracion, textoAccion, accion)` | igual, y ademas agrega o reemplaza el boton de accion |
| `dismiss()` | la cierra antes de que expire |

### Texto de la app

`Notification.setAppName("Mi App")` muestra el nombre de la app en letras pequenas sobre el titulo de **todas** las notificaciones (como
Chrome). Se llama una vez al iniciar.

### Receta: tarea larga con progreso

```java
final Notification n = new Notification.Builder("download.report")
    .setTitle("Descargando")
    .setMessage("reporte.pdf")
    .setDuration(Notification.LENGTH_INDEFINITE)
    .setProgress(0, 100)
    .show();

new Thread(() -> {                                       // trabajo fuera del EDT
  for (int i = 1; i <= 100; i++) {
    hacerUnPaso();
    n.setProgress(i, 100);                               // se puede llamar desde cualquier hilo
  }
  n.complete("Listo", "reporte.pdf descargado", Notification.Style.SUCCESS, Notification.LENGTH_LONG,
      "Abrir", done -> abrirArchivo());
}).start();
```

### Comportamiento y limites

- **Hilos**: `show()` se puede llamar desde cualquier hilo (si no es el EDT, espera brevemente a que la tarjeta exista para que puedas
  actualizar su progreso enseguida). `setProgress`, `complete` y `dismiss` tambien. Los callbacks corren en el EDT.
- **Cierre automatico**: se pausa mientras el mouse esta sobre la tarjeta y se reanuda al salir.
- **Posicion**: esquina inferior derecha del area usable de la pantalla (sin la barra de tareas), con 16 px de margen y 10 px entre
  tarjetas. Las nuevas se apilan hacia arriba.
- **Tamano**: todas miden el mismo ancho (364 px), sea cual sea el contenido.
- **Linux sin compositor**: las esquinas redondeadas y el fundido necesitan translucidez de ventana. Si el gestor de ventanas no la
  soporta, la tarjeta se ve **rectangular y sin animacion** en lugar de fallar.
- **Aspecto fijo**: colores, radio y tamanos estan dentro de la clase. Ver "Lo que no se puede cambiar" en [CUSTOMIZING.md](CUSTOMIZING.md#5-lo-que-no-se-puede-cambiar-desde-la-app).

## `Toast`

Mensaje breve y transitorio sobre una ventana o componente. Se muestran **de uno en uno** (cola interna): el siguiente aparece cuando termina
el anterior.

### Ejemplo

```java
Toast.makeToast(frame, "Guardado", Toast.Style.SUCCESS).show();

// con boton de accion, como Snackbar.setAction de Android
Toast.makeToast(frame, "Archivo eliminado")
    .setAction("Deshacer", toast -> restaurar())
    .show();
```

El primer parametro (`owner`) es la ventana o componente sobre el que se centra: puede ser un `JFrame`, un `JDialog` o cualquier componente
(se usa su ventana).

### Crear

| Metodo estatico | Resultado |
|---|---|
| `Toast.makeToast(owner, texto)` | estilo `NORMAL`, `LENGTH_SHORT` |
| `Toast.makeToast(owner, texto, Style)` | con estilo |
| `Toast.makeToast(owner, texto, int duracion)` | con duracion |
| `Toast.makeToast(owner, texto, int duracion, Style)` | con duracion y estilo |
| `Toast.makeToast(owner, texto, duracion, Style, textoAccion, accion)` | todo, incluida la accion |

Duraciones: `Toast.LENGTH_SHORT` (3000 ms), `Toast.LENGTH_LONG` (6000 ms), `Toast.LENGTH_INDEFINITE` (-1) o cualquier valor en ms.

> **Cuidado con `LENGTH_INDEFINITE`**: el toast no se cierra solo y **un clic sobre el no lo cierra**. Solo se va con `dismiss()` o con su boton de
> accion. Si lo usas, guarda la referencia y cierralo desde tu codigo. A diferencia de los toasts con duracion, la cola **no espera** a que
> este se cierre: el siguiente toast aparece enseguida (encima, en la misma posicion).

### Configurar y controlar

| Metodo | Que hace |
|---|---|
| `.setAction(String texto, Consumer<Toast>)` | boton de accion. Al usarlo el toast se cierra y luego corre la accion (en el EDT). Con `null` en cualquiera de los dos se quita. **Devuelve el toast** (se puede encadenar) |
| `.setText(String)` | cambia el texto |
| `.setDuration(int)` | cambia la duracion |
| `.setStyle(Style)` | cambia el estilo (el icono) |
| `.setBackground(Color)` / `.setForeground(Color)` | colores de la tarjeta (negro y blanco por defecto) |
| `.show()` | lo pone en la cola |
| `.dismiss()` | lo cierra desde cualquier hilo; si seguia en la cola, ya no se muestra |

Salvo `setAction`, los metodos de configuracion **no se encadenan** (devuelven `void`): configura el toast y luego llama a `show()`.

### Comportamiento

- **Texto largo**: se parte en lineas a partir de 420 px. Un texto que empieza con `<html>` se toma como HTML tal cual; los demas se escapan
  (un `<` o `&` salen como texto).
- **Hilos**: `show()` se puede llamar desde cualquier hilo.
- **Dialogos modales**: el toast se ve y atiende clics aunque haya un dialogo modal abierto (no se bloquea).
- **Foco**: no roba el foco de la ventana.
- **Linux sin compositor**: igual que `Notification`, queda rectangular y sin fundido.

## Los avisos y el `ErrorProvider`

`ErrorProvider.setError(...)` (ver [COMPONENTS.md](COMPONENTS.md#errorprovider)) muestra un `Toast` de error de 3 segundos ademas de marcar el
campo con borde rojo.
