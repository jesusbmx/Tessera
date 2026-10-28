# Combo

**Lista desplegable** (`JComboBox<T>`).

| | |
|---|---|
| **Paquete** | `tessera.kit` |
| **Clase** | `Combo<T> extends JComboBox<T> implements Styled<Combo<T>>` |
| **Se crea con** | `Combo.of(items...)` · `Combo.empty()` |
| **Importar** | `import tessera.kit.Combo;` |

![Combo: normal, editable y deshabilitado](../../../img/widget-combo.png)

## Cuando usarlo

- Elegir una opcion de varias (5 o mas), o cuando el espacio es poco.

**Cuando no:** Para 2 a 5 opciones visibles a la vez usa `Radio`.

## Referencia

Lista desplegable (`JComboBox<T>`).

| Metodo | Que hace |
|---|---|
| `Combo.of(items...)` | con esos elementos |
| `Combo.empty()` | vacio, se llena despues (con un `ComboItem.Binder`, por ejemplo) |
| `.onChange(Runnable)` | cada vez que se elige un elemento (tambien si se elige por codigo) |
| `.maxRows(n)` | cuantos elementos se ven a la vez al desplegar |
| `.editable(boolean)` | el usuario puede escribir un valor que no esta en la lista |

Para mostrar una etiqueta y guardar otro valor (un enum, un id), usa [`ComboItem`](../../components/lists-and-combos.md#comboitem).

## Errores comunes

- `onChange` tambien se dispara al elegir por codigo y al llenar el combo: registralo **despues** de llenarlo y elegir su valor inicial.
- Para mostrar un texto y guardar otro valor (un enum, un id) usa `ComboItem`.

## Ver tambien

- [`Radio`](radio.md) · [`ComboItem`](../../components/lists-and-combos.md#comboitem) · [`Input`](input.md)
- [Referencia del kit](../README.md): ideas base y metodos comunes (`Styled`, `Clickable`, `Box`).
