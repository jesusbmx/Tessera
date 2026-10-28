# Hint

Texto de ayuda: **pequeno, gris, y se parte en lineas** al ancho que reciba.

| | |
|---|---|
| **Paquete** | `tessera.kit` |
| **Clase** | `Hint extends JLabel implements Styled<Hint>` (no es un `Label`) |
| **Se crea con** | `Hint.of(txt)` · `Hint.ofHtml(txt)` |
| **Importar** | `import tessera.kit.Hint;` |

![Hint: se parte al ancho disponible](../../../img/widget-hint.png)

## Cuando usarlo

- La linea de ayuda bajo un campo o un bloque.
- Un texto explicativo largo dentro de un `Card` (`Card.subtitle(..)` lo usa).

**Cuando no:** Para un texto corto y normal usa `Label`.

## Referencia

Texto de ayuda: pequeno, gris, y **se parte en lineas** al ancho que le toque. Extiende `JLabel`, **no** `Label` (son tipos distintos).

| Metodo | Que hace |
|---|---|
| `Hint.of(txt)` | ayuda de texto |
| `Hint.ofHtml(txt)` | igual, para un texto que ya viene en `<html>...</html>` (quita esas marcas) |
| `.width(px)` | el ancho que **pide** (325 por defecto): lo respetan los contenedores que dan a cada hijo su tamano preferido (`pack()`, `FlowLayout`); en `Col`, `Card`, `Form` o `Row` se estira al ancho disponible |
| `.html(txt)` | cambia el texto despues |

El texto se parte al ancho **real** que reciba el `Hint`. El texto se mete dentro de `<html>`: un `<` o un `&` sueltos se interpretan como HTML; si viene de un usuario o del sistema, escapalo.

## Errores comunes

- `.width(px)` es el ancho que el `Hint` **pide**, no un limite: dentro de `Col`, `Card`, `Form` o `Row` se estira al ancho disponible y parte el texto ahi.

## Ver tambien

- [`Label`](label.md) · [`Card`](../containers/card.md) · [`Form`](../containers/form.md)
- [Referencia del kit](../README.md): ideas base y metodos comunes (`Styled`, `Clickable`, `Box`).
