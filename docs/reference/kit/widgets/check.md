# Check

**Casilla de verificacion** (`JCheckBox`).

| | |
|---|---|
| **Paquete** | `tessera.kit` |
| **Clase** | `Check extends JCheckBox implements Clickable<Check>` |
| **Se crea con** | `Check.of(txt)` |
| **Importar** | `import tessera.kit.Check;` |

![Check: estados](../../../img/widget-check.png)

## Cuando usarlo

- Una opcion que se activa o desactiva y se guarda despues.
- Varias opciones independientes.

**Cuando no:** Para opciones excluyentes usa `Radio`; para un si/no inmediato, `Switch`.

## Referencia

`Check.of(txt)`: casilla de verificacion (`JCheckBox`). Hereda `Clickable`.

## Errores comunes

- `onClick` solo atiende clics del usuario, no `setSelected(true)` por codigo: para eso usa un `ItemListener` registrado **despues** de armar la pantalla.

## Ver tambien

- [`Radio`](radio.md) · [`Switch`](switch.md) · [`Button`](button.md)
- [Referencia del kit](../README.md): ideas base y metodos comunes (`Styled`, `Clickable`, `Box`).
