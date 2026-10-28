# Input

**Campo de texto** de una linea. Con `Input.password()` y `Input.area(filas)` salen los otros dos tipos de entrada.

| | |
|---|---|
| **Paquete** | `tessera.kit` |
| **Clase** | `Input extends JTextField implements Styled<Input>` |
| **Se crea con** | `Input.text()` |
| **Importar** | `import tessera.kit.Input;` |

![Input: placeholder, estados y ancho](../../../img/widget-input.png)

## Cuando usarlo

- Cualquier dato de texto de una linea: nombre, ruta, direccion.

**Cuando no:** Para claves usa `Password`; para varias lineas, `TextArea`; para elegir de una lista, `Combo`.

## Referencia

Campo de texto de una linea (`JTextField`). Con `Input.password()` y `Input.area(filas)` salen los otros dos tipos.

| Metodo | Que hace |
|---|---|
| `Input.text()` | campo de texto normal |
| `.placeholder(txt)` | texto gris mientras el campo esta vacio |
| `.columns(n)` | ancho preferido en caracteres |
| `.editable(boolean)` | permitir o no escribir |
| `.onChange(Runnable)` | cada vez que cambia el contenido (por el usuario **o por codigo**) |
| `.onEnter(ActionListener)` | al presionar Enter |

## Errores comunes

- `onChange` tambien se dispara cuando el texto cambia **por codigo** (`setText`): registralo despues de dar el valor inicial.

## Ver tambien

- [`Password`](password.md) · [`TextArea`](textarea.md) · [`Form`](../containers/form.md) · [`Combo`](combo.md)
- [Referencia del kit](../README.md): ideas base y metodos comunes (`Styled`, `Clickable`, `Box`).
