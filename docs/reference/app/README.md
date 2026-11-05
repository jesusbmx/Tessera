# Integracion con el escritorio (`tessera.app`)

Dos piezas para que una app de escritorio se sienta nativa: un **icono en la bandeja del sistema** (y reapertura desde el Dock de macOS) y que **cada ventana se abra con el
tamano que el usuario le dejo**.

| Clase | Para que |
|---|---|
| [`TrayDock`](traydock.md) | icono de la bandeja del sistema con menu, y reapertura desde el Dock de macOS |
| [`WindowPrefs`](windowprefs.md) | cada `JFrame` y `JDialog` se abre con el tamano que el usuario le dejo |
| [`Preview` y `Previewer`](preview.md) | ver una pantalla en su propia ventana mientras se arma, con `@Preview` y un `main` |

Receta completa con las dos: [App que vive en la bandeja](../../guides/recipes.md#6-app-que-vive-en-la-bandeja).
