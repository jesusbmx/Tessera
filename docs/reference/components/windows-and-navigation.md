# Ventanas y navegacion (`tessera.components`)

Barra de navegacion con etiquetas y mover ventanas sin decoracion.

[← Componentes sueltos](README.md)

## `InteractiveNavbar`

Barra de navegacion hecha con **etiquetas** (o cualquier componente): cursor de mano, color al pasar el mouse y color para el elemento seleccionado. Se
arma con un `Builder`.

```java
final InteractiveNavbar nav = new InteractiveNavbar.Builder()
    .foreground(Kit.MUTED)
    .selectedForeground(Theme.accent())
    .items(lblHome, lblSettings, lblAbout)
    .build();
nav.setSelectedItem(lblHome);                                // aplica los colores iniciales
```

| Metodo del `Builder` | Que hace |
|---|---|
| `foreground(Color)` | color normal (negro por defecto) |
| `selectedForeground(Color)` | color del seleccionado (azul por defecto) |
| `hoverForeground(Color)` / `hoverSelectedForeground(Color)` | color al pasar el mouse (por defecto, el normal / el del seleccionado, 1.5 veces mas claro) |
| `items(Component...)` | los elementos |
| `build()` | crea la barra |

Instancia: `setSelectedItem(Component)` y `updateComponents()`. La barra **solo cambia colores**: la accion de cada elemento se la das tu
(`Label.onClick(...)`). Llama a `setSelectedItem` despues de `build()` para que los colores iniciales se apliquen.

## `WindowDragListener`

Mueve una **ventana sin decoracion** arrastrando un componente (tu propia barra de titulo).

```java
final JFrame frame = new JFrame();
frame.setUndecorated(true);
new WindowDragListener(frame).setup(titleBar);               // al arrastrar titleBar se mueve la ventana
```
