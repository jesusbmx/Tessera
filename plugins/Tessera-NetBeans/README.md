# Tessera Preview (plugin de NetBeans)

Dos formas de ver una pantalla de Tessera sin arrancar la app:

- **Pestana "Tessera Preview"** en el editor de todo archivo `.java`, junto a *Source* y *History*: muestra como queda la pantalla dentro del
  propio editor (una imagen a su tamano natural), y se vuelve a generar sola cuando el IDE compila cambios (guardar, con "Compile on Save").
- **Clic derecho > Preview con Tessera**: la pantalla en su propia ventana (con [`Previewer`](../Tessera/docs/reference/app/preview.md)),
  que se relanza sola al compilar. La ventana se abre donde se dejo la vez anterior.

## Instalar

1. `./build.ps1` genera `dist/tessera-preview.nbm` (compila contra NetBeans 15; con otra ruta: `-NetBeans "C:\...\netbeans"`).
   `./build.ps1 -Install` ademas lo copia a la carpeta de usuario de NetBeans (`%APPDATA%\NetBeans\15`): cierra NetBeans antes y vuelvelo a abrir.
2. O bien: NetBeans > **Tools > Plugins > Downloaded > Add Plugins...** > elige el `.nbm` > Install. Avisara que no esta firmado: es normal. Reinicia si lo pide.

## Usar

- **Pestana**: abre el archivo y elige *Tessera Preview*. Es una imagen de la pantalla a su tamano natural (no se puede hacer clic en ella). La barra
  trae **Recargar** y **Abrir en ventana** (la version interactiva). Si la clase no es un componente, o falla, la pestana muestra el motivo.
- Clic derecho sobre el archivo (en el editor, en Projects o en Files) > **Preview con Tessera**. Debe ser una clase que sea un componente (`JPanel`...) con
  constructor sin parametros, o tener metodos estaticos con `@Preview` (ver Previewer).
- Cierra la ventana para terminar. Volver a pedir el preview de la misma clase reinicia la sesion.
- La salida de la ventana (errores incluidos) va a la pestana **Tessera Preview** de Output.

## Configuracion del proyecto (opcional)

`nbproject/tessera-preview.properties`, para que la pantalla se vea con el tema de la app:

```
theme=text2barcode.ui.UI#setLookAndFeel
```

Es un metodo estatico sin parametros. Sin el archivo se usa el tema por defecto de Tessera.

## Requisitos

- El proyecto tiene Tessera con `tessera.app.Previewer` en su classpath (jar reciente en `vendor/tessera`).
- "Compile on Save" activado (lo esta por defecto): el reinicio se dispara cuando cambian los `.class` del proyecto, no al guardar el `.java`.
- Se ejecuta con el JDK por defecto de NetBeans para proyectos.

## Como funciona

La pestana (`PreviewElement`, registrada en `Editors/text/x-java/MultiView`) lanza `Renderer` en un proceso `java` aparte con el classpath del
proyecto; `Renderer` guarda un PNG con `Previewer.save` y la pestana lo muestra. `ClassWatcher` mira los `.class` del proyecto para volver a generarlo.

`PreviewAction` (menu) > `PreviewSession` (clase, classpath de ejecucion y JDK del IDE) > `PreviewProcess` (proceso `java` aparte con `Previewer` +
vigilante que relanza cuando cambian los `.class`). Un proceso aparte evita mezclar las clases del proyecto con las del IDE.

## Version "viva" (guardada, sin usar)

En `_live/` esta una version anterior de la pestana que mantenia la pantalla viva en un proceso aparte y la mostraba con ancho ajustable, mouse y teclado
(`LiveRenderer`, `LiveSession`, `LivePanel` y su `PreviewElement`). Se dejo de usar por pedido; para volver a ella, copiar esos archivos a
`src/com/jesustectronic/tessera/preview/` (reemplazando `PreviewElement.java` y quitando `Renderer.java`) y agregar a `Bundle.properties` las claves
`CTL_PreviewSize.0`..`.4`, `CTL_PreviewSize.tip` y `MSG_PreviewStarting`.
