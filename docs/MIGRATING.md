# Pasar una app Swing a Tessera

Dos casos: una app que ya trae su propia copia del kit y de los componentes (se reemplaza por la libreria) y una app
con formularios del disenador de NetBeans (`.form`, `GroupLayout`, `initComponents()`).

## 1. Poner la libreria en el proyecto

1. Copiar `dist/tessera-<version>.jar` a la carpeta de librerias de la app (por ejemplo `vendor/tessera/`).
2. Agregarla al classpath del proyecto (en NetBeans: `file.reference.tessera-<version>.jar=vendor/tessera/tessera-<version>.jar`
   en `nbproject/project.properties` y en `javac.classpath`; en Maven, la dependencia).
3. Que el empaquetado, instalador u ofuscador de la app la incluya donde ya incluye FlatLaf y MigLayout
   (buscar `flatlaf` en `build.xml`, scripts del instalador, `.pro` de ProGuard).
4. Dependencias de Tessera que la app debe tener tambien: FlatLaf, FlatLaf Extras, JSVG, MigLayout (ver README).

## 2. Instalar el tema

Una sola llamada, antes de crear ventanas. La app conserva su propia clase de tema solo para lo que es suyo
(colores de marca, pestanas, barra de titulo) y delega lo comun:

```java
Theme.builder().accent("#1976D2").primary("#3B82F6").panelBackground("#F7F6F3")
     .surface("#FFFFFF").cardLine("#E4E1D9").install();
UIManager.put("TabbedPane.selectedBackground", ...);          // lo propio de la app, despues
WindowPrefs.install(storeDeLaApp, "com.miapp.");              // tamano de ventanas (ver abajo)
```

La fuente Inter y los SVG del interruptor ya viajan dentro de la libreria: se borran de los recursos de la app.

## 3. Reemplazar la copia local por la libreria

| Antes (copia en la app) | Ahora |
|---|---|
| `<app>.ui.kit.*` | `tessera.kit.*` (borrar la carpeta `ui/kit`) |
| `Notification`, `Toast` | `tessera.notify.*` |
| `BaseTableModel`, `CheckBoxCellRenderer`, `CheckGroup`, `CheckableListModel`, `ComboBoxRenderer`, `ComboItem`, `DashedBorder`, `DragAndDrop`, `ErrorProvider`, `FlexibleColumnRenderer`, `HighlightColumnRenderer`, `InteractiveNavbar`, `LabelListCell`, `PopupMenuExtension`, `RoundedBorder`, `TextPlaceholder`, `TextUndoRedoManager`, `WindowDragListener` | `tessera.components.*` (borrar la copia local) |
| `ErrorProvider` con globo flotante (variante) | `tessera.components.FloatingErrorProvider` |
| `NavbarMenu` | `tessera.components.InteractiveNavbar` |
| `UI.setupSwitch(..)`, `UI.svgIcon(..)` | `Icons.setupSwitch(..)`, `Icons.svg(..)` (la app puede dejar un metodo que delegue) |
| `WindowPrefs` propio | `tessera.app.WindowPrefs` con un `Store` que lee y escribe donde la app ya guarda sus preferencias |
| icono de bandeja (`SystemTray`/`TrayIcon` a mano) | `tessera.app.TrayDock` |
| `Desktop.addAppEventListener(AppReopenedListener)` por reflexion | `TrayDock.onAppReopened(Runnable)` |

Se mantiene en la app lo que depende de la app: su `Lang`, sus eventos, sus servicios, los editores de codigo, los dialogos de negocio.

Cuidado con los colores: los defaults de `Kit` son los de la libreria; si la app tenia otros (tarjetas blancas, otro azul)
se fijan en `Theme.builder()` (`surface`, `cardLine`, `primary`) y no en cada pantalla.

## 4. De formulario NetBeans a kit

1. **Origen de la verdad**: el `initComponents()` generado y el `.form` (texto, fuente, colores, bordes, claves de recursos, gaps).
2. Se borran el `.form`, `initComponents()` y todo lo generado (`GroupLayout`, `// <editor-fold ... GEN-BEGIN`, `// Variables declaration`, `// NOI18N`).
3. La clase queda con la **logica arriba** y, al final, dos secciones: `// UI: componentes` (los widgets declarados con
   texto, icono, tooltip y eventos del kit) y `// UI: construccion` (`buildUI()` con Card/Row/Col/Form/...).
4. Mismo orden y aspecto que antes; los huecos y tamanos fijos que solo existian por como se dibujo en el disenador no se reproducen.
5. `TitledBorder` pasa a `Card.create(titulo)` o `Card.section(titulo)`.
6. No cambiar firmas publicas ni logica de negocio. `ItemListener`/`ChangeListener` que reaccionaban a `setSelected` por codigo
   se conservan (no se cambian a `onClick`) y se registran despues de armar la pantalla.
7. Imports explicitos (`Label`, `Button`, `List`, `TextArea` chocan con `java.awt`).

La referencia de toda la API esta en [AGENTS.md](../AGENTS.md).

## 5. Verificar

Compilar el proyecto completo y abrir las pantallas. Para comparar contra el original conviene renderizar cada panel
a PNG antes y despues (`panel.paint(imagen.getGraphics())` tras darle tamano) y revisar las diferencias.
