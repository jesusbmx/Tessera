# Table

**Tabla** (`JTable`). Se pone siempre dentro de un `Scroll`.

| | |
|---|---|
| **Paquete** | `tessera.kit` |
| **Clase** | `Table extends JTable implements Styled<Table>` |
| **Se crea con** | `Table.of(modelo)` |
| **Importar** | `import tessera.kit.Table;` |

![Table dentro de un Scroll](../../../img/widget-table.png)

## Cuando usarlo

- Mostrar una lista de datos con columnas.

**Cuando no:** Para pocas celdas fijas de texto usa `Grid`.

## Referencia

Tabla (`JTable`). **Siempre dentro de un [`Scroll`](../containers/scroll.md)**, si no el encabezado no se ve.

| Metodo | Que hace |
|---|---|
| `Table.of(TableModel)` | tabla con ese modelo (ver [`BaseTableModel`](../../components/tables.md#basetablemodel)) |
| `.rowHeight(px)` | alto de cada fila |
| `.singleSelection()` | una sola fila seleccionada a la vez |
| `.onSelect(Runnable)` | cuando cambia la fila seleccionada (una sola vez por cambio) |
| `.columnWidth(columna, px)` | ancho preferido de una columna, por posicion en la vista |

```java
Table table = Table.of(model).rowHeight(28).singleSelection().onSelect(this::onRowSelected);
Scroll.of(table)
```

## Errores comunes

- Sin `Scroll.of(table)` no se ve el encabezado.
- Si la tabla se ordena o filtra, usa `table.convertRowIndexToModel(fila)` antes de leer el modelo.

## Ver tambien

- [`Scroll`](../containers/scroll.md) · [`BaseTableModel`](../../components/tables.md#basetablemodel) · [`Col`](../containers/col.md)
- [Referencia del kit](../README.md): ideas base y metodos comunes (`Styled`, `Clickable`, `Box`).
