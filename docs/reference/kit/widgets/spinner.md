# Spinner

**Selector numerico** con flechas (`JSpinner`).

| | |
|---|---|
| **Paquete** | `tessera.kit` |
| **Clase** | `Spinner extends JSpinner implements Styled<Spinner>` |
| **Se crea con** | `Spinner.integer(valor, min, max, paso)` · `Spinner.decimal(...)` · `Spinner.of(modelo)` |
| **Importar** | `import tessera.kit.Spinner;` |

![Spinner: enteros y decimales](../../../img/widget-spinner.png)

**En una frase:** **numero acotado** con flechas: con minimo, maximo y paso.

**Reemplaza, en Swing puro:** `new JSpinner(new SpinnerNumberModel(...))` + un `ChangeListener` para el cambio.

**Cual elegir entre varios widgets parecidos:** [Que widget uso](../../../guides/widgets.md#escribir-input-password-textarea-o-spinner).

## Cuando usarlo

- Un numero acotado: copias, ancho en mm, reintentos.

**Cuando no:** Para un numero libre o sin limites usa `Input`.

## Ejemplo: copias (entero) y ancho en mm (decimal)

![copias (entero) y ancho en mm (decimal)](../../../img/example-spinner.png)

```java
final Label lblTotal = Label.muted("").small();
final Spinner spnCopies = Spinner.integer(2, 1, 99, 1).width(80);
spnCopies.onChange(() -> lblTotal.setText("Se imprimen " + spnCopies.getValue() + " etiquetas"));
spnCopies.setValue(3);

final Card paper = Card.create("Papel").children(
    Form.gap(6)
        .row("Copias", Col.gap(2).children(spnCopies, lblTotal))
        .row("Ancho (mm)", Spinner.decimal(101.6, 10.0, 300.0, 0.1).width(80)));
```

1. `Spinner.integer(valor, min, max, paso)` y `Spinner.decimal(...)`: el usuario no puede salirse de los limites.
2. En un `Form` o `Grid`, un `Spinner` sin `.width(px)` se estira a todo el ancho: dale `.width(80)`.
3. `getValue()` devuelve un `Integer` o un `Double` segun el modelo; `onChange(...)` corre tambien con `setValue(...)` por codigo.

## Referencia

Selector numerico con flechas (`JSpinner`).

| Metodo | Que hace |
|---|---|
| `Spinner.integer(valor, min, max, paso)` | numeros enteros |
| `Spinner.decimal(valor, min, max, paso)` | numeros con decimales |
| `Spinner.of(SpinnerNumberModel)` | con un modelo propio (otro tipo de numero, limites abiertos con `null`) |
| `.onChange(Runnable)` | cada vez que cambia el valor |

## Errores comunes

- En un `Form` o `Grid`, un `Spinner` sin `.width(px)` se estira a todo el ancho: dale `.width(80)`.
- El valor se lee con `getValue()` (un `Integer` o `Double` segun el modelo).

## Ver tambien

- [`Input`](input.md) · [`Form`](../containers/form.md) · [`Grid`](../containers/grid.md)
- [Referencia del kit](../README.md): ideas base y metodos comunes (`Styled`, `Clickable`, `Box`).
