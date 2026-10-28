# Tablas (`tessera.components`)

Modelo de tabla con una lista de objetos y renderizadores que resaltan columnas.

[← Componentes sueltos](README.md)

## `BaseTableModel`

Modelo de tabla **por columnas, con una lista de objetos T** (cada fila es un `T`). Es abstracto: solo se escribe `getValueAt`.

```java
class PrinterModel extends BaseTableModel<Printer> {
  PrinterModel() {
    super("Impresora", "Puerto", "Estado");                  // nombres de las columnas
  }

  @Override
  public Object getValueAt(int row, int col) {
    final Printer p = getRow(row);
    switch (col) {
      case 0: return p.name;
      case 1: return p.port;
      default: return p.status;
    }
  }
}

final PrinterModel model = new PrinterModel();
model.setItems(printers);                                    // llena y avisa a la tabla
final Table table = Table.of(model).rowHeight(28);
Scroll.of(table)
```

| Metodo | Que hace |
|---|---|
| `getRow(i)` / `getRows()` | la fila `i` / una **copia** de todas las filas |
| `addRow(item)` / `addRow(indice, item)` | agrega al final / en esa posicion |
| `setRow(indice, item)` | reemplaza esa fila |
| `removeRow(indice)` / `removeRow(item)` | quita una fila (devuelve el item; con `item` devuelve `null` si no estaba) |
| `removeAllRows(Collection<T>)` | quita varios items |
| `clearRows()` | vacia la tabla |
| `setItems(Collection<T>)` / `setItems(T...)` | **reemplaza** todo el contenido |
| `containsRow(item)` | si el item esta en la tabla |
| `getColumns()` / `addColumn(nombre)` / `setColumnIdentifiers(...)` / `clearColumns()` | manejo de columnas (`String...` o `List<String>`) |
| `rowsToArray(Class<T>)` | las filas como arreglo `T[]` |
| `iterator()` | se puede recorrer con `for (T item : model)` (sobre una copia) |
| `BaseTableModel.adjustColumnPreferredWidths(JTable)` | **estatico**: ajusta el ancho de cada columna al de su contenido mas ancho (mas 10 px) |

Cada cambio avisa a la tabla (`fireTable...`): no hay que llamar nada mas. El tipo de cada columna (`getColumnClass`) se deduce del valor de la primera
fila, asi que las columnas numericas se ordenan como numeros.

**Sin crear una clase**, hay dos modelos listos:

| Clase | Cada fila es... | Ejemplo |
|---|---|---|
| `BaseTableModel.TArray` | un `Object[]` | `model.addRow(new Object[] {"Zebra", "USB001"})` |
| `BaseTableModel.TList<T>` | una `List<T>` | `model.addRow(Arrays.asList("Zebra", "USB001"))` |

```java
final BaseTableModel.TArray model = new BaseTableModel.TArray("Impresora", "Puerto");
model.addRow(new Object[] {"Zebra ZD420", "192.168.0.10"});
final Object port = model.getValueAt(0, "Puerto");          // tambien se lee y escribe por nombre de columna
```

## `HighlightColumnRenderer`

Pinta de **amarillo suave** las columnas que le indiques **por nombre** (el del encabezado). La fila seleccionada usa los colores normales de
seleccion y el resto queda en blanco.

```java
final HighlightColumnRenderer renderer = new HighlightColumnRenderer("Estado", "Total");
renderer.applyCellRenderer(table);                           // lo pone en todas las columnas
```

| Metodo | Que hace |
|---|---|
| `new HighlightColumnRenderer(String...)` / `(List<String>)` / `()` | las columnas a resaltar |
| `addHighlightedColumn(nombre)` / `removeHighlightedColumn(nombre)` | cambia la lista |
| `setHighlightedColumns(List<String>)` | reemplaza la lista |
| `applyCellRenderer(JTable)` | lo asigna como renderer de **todas** las columnas |

> El color de resaltado es fijo. Para colores propios, usa `FlexibleColumnRenderer`.

## `FlexibleColumnRenderer`

Como el anterior, pero con **fondo, texto y borde propios por columna**.

```java
final FlexibleColumnRenderer renderer = new FlexibleColumnRenderer("Estado");
renderer.setColumnBackground("Estado", new Color(220, 252, 231));
renderer.setColumnForeground("Estado", new Color(22, 101, 52));
renderer.applyToTable(table);
```

| Metodo | Que hace |
|---|---|
| `new FlexibleColumnRenderer(String... columnas)` | las columnas a resaltar |
| `addHighlightedColumn(c)` / `removeHighlightedColumn(c)` | cambia la lista |
| `setColumnBackground(c, color)` / `setColumnForeground(c, color)` / `setColumnBorder(c, borde)` | estilo de **una** columna |
| `setDefaultBackground(color)` / `setDefaultForeground(color)` / `setDefaultBorder(borde)` | estilo de las columnas resaltadas que no tengan el suyo (amarillo suave y texto negro por defecto) |
| `applyToTable(JTable)` | lo asigna a todas las columnas |
