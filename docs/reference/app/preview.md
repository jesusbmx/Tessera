# Preview y Previewer

**Ver una pantalla en su propia ventana** mientras la armas, sin arrancar la app: ejecutas un `main` de una linea. `@Preview` es **opcional**.

| | |
|---|---|
| **Paquete** | `tessera.app` |
| **Clases** | `@Preview` (anotacion) · `Previewer` (la ventana) |
| **Importar** | `import tessera.app.Preview;` y `import tessera.app.Previewer;` |

## Como se usa

```java
public class ToolsPanel extends JPanel {

  /** Vista previa: Run File (Shift+F6) en el IDE. */
  public static void main(String[] args) {
    Previewer.show(UI::setLookAndFeel, ToolsPanel.class);      // el tema de la app, y la pantalla
  }
  ...
}
```

1. `Previewer` crea la clase con su constructor sin parametros (puede ser `private`). No hace falta ninguna anotacion: basta que la clase sea un componente. El `main` solo existe para que el IDE tenga algo que ejecutar.
2. El primer argumento de `show` es lo que la app hace para instalar su tema (`UI::setLookAndFeel`, `Theme.builder()...install()`). `null` deja el aspecto de Swing.
3. La ventana trae **Recargar (F5)**, que vuelve a crear la pantalla. No recarga el codigo: despues de cambiarlo, vuelve a ejecutar el archivo.

## Cuando si usar `@Preview`

En la clase (`@Preview("Nombre", width = 900, height = 600)`) para darle nombre o tamano a la ventana. Y en un metodo estatico para una pantalla con datos de muestra. Un metodo estatico sin parametros que devuelve un componente tambien vale. Sirve para pantallas que necesitan datos o varios estados:

```java
@Preview("Con datos")
static JComponent withData() {
  final MyPanel panel = new MyPanel();
  panel.setModel(sampleModel());
  return panel;
}

@Preview("Vacia")
static JComponent empty() {
  return new MyPanel();
}
```

Si hay varias vistas (la clase y metodos), salen en pestanas.

| Elemento | Que hace |
|---|---|
| `@Preview` / `@Preview("nombre")` | marca la clase o el metodo; el nombre es el de la pestana |
| `@Preview(width = 900, height = 600)` | tamano inicial de la ventana en px logicos (por defecto, el que pida la pantalla) |
| `Previewer.show(Class...)` | abre la ventana con el tema por defecto de Tessera |
| `Previewer.show(Runnable tema, Class...)` | igual, con el tema de la app |
| `Previewer.save(tema, File png, Class)` | guarda la primera vista como PNG, sin abrir la ventana (necesita escritorio) |
| `java tessera.app.Previewer paquete.Clase` | desde la linea de comandos |

## Errores comunes

- Un metodo con `@Preview` debe ser **estatico, sin parametros y devolver un componente**; si no, `Previewer` lo dice con un error claro.
- La vista **no llama** a lo que la app hace al mostrar la pantalla (cargar ajustes, buscar impresoras): solo se ve la UI. Si necesitas ese estado, un metodo `@Preview` que lo prepare.
- Si la pantalla lanza una excepcion al crearse, la ventana muestra el error en lugar de la pantalla.

## Ver tambien

- [`Theme`](../theme/theme.md) · [`WindowPrefs`](windowprefs.md)
