# Button

Boton con estilos y eventos encadenables. `Button.primary` marca la accion que se espera del usuario.

| | |
|---|---|
| **Paquete** | `tessera.kit` |
| **Clase** | `Button extends JButton implements Clickable<Button>` |
| **Se crea con** | `Button.of(txt)` · `Button.primary(txt)` |
| **Importar** | `import tessera.kit.Button;` |

![Button: normal, principal, con icono y deshabilitado](../../../img/widget-button.png)

## Cuando usarlo

- Cualquier accion: guardar, cancelar, buscar.
- La accion principal de una pantalla (`primary`).

**Cuando no:** Para un si/no que se aplica al instante usa `Switch`; para un modo de vista, `Toggle`.

## Referencia

`Button.of(txt)`: boton normal. `Button.primary(txt)` o `boton.primary()`: boton **principal** (color `Kit.PRIMARY`, texto blanco), la accion
que se espera del usuario. Hereda `Clickable`.

```java
Button.primary("Guardar").onClick(e -> save())
Button.of("Buscar").icon("app/img/search.svg", 16).iconGap(8)
```

## Errores comunes

- Dentro de un `Col` un boton no se estira: usa `.fill(boton)`.
- Convencion: un solo `primary` por pantalla, el **ultimo** de la derecha.

## Ver tambien

- [`Footer`](../containers/footer.md) · [`Check`](check.md) · [`Switch`](switch.md) · [`Toggle`](toggle.md)
- [Referencia del kit](../README.md): ideas base y metodos comunes (`Styled`, `Clickable`, `Box`).
