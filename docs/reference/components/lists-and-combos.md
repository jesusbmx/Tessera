# Listas y combos (`tessera.components`)

Listas con casillas, elementos de combo con etiqueta y valor, grupos de opciones y filas alternas.

[← Componentes sueltos](README.md)

## `CheckableListModel`

Modelo de lista (`DefaultListModel<T>`) **con casilla por elemento**.

```java
final CheckableListModel<String> model = new CheckableListModel<>();
model.addElement("Etiqueta de producto");
model.addElement("Etiqueta de envio");

final JList<String> list = new JList<>();
model.configureList(list);                                   // renderer con casillas + clic que alterna

final List<String> elegidos = model.getCheckedItems();
```

| Metodo | Que hace |
|---|---|
| `configureList(JList<T>)` | deja la lista lista: casillas, seleccion simple, un clic alterna la casilla y asigna este modelo |
| `setChecked(indice, boolean)` / `isChecked(indice)` | marcar / leer un elemento |
| `checkAll(boolean)` | marcar o desmarcar todos |
| `clearChecks()` | desmarcar todo |
| `getCheckedItems()` | los elementos marcados (el orden **no esta garantizado**) |

Detalles: el estado se guarda por **elemento** (segun `equals`/`hashCode`): dos elementos iguales comparten casilla. `configureList` **quita los
`MouseListener` que la lista ya tuviera**, incluidos los del propio look and feel: es el precio de que un clic alterne la casilla; ponle a la lista un
`ListSelectionListener` si necesitas reaccionar a la seleccion.

## `ComboItem`

Un elemento para un combo que separa **lo que ve el usuario** (la etiqueta) del **valor real** (un enum, un id). El combo muestra `toString()` (la
etiqueta) y tu recuperas el valor ya tipado.

```java
enum Mode { DIRECT, TRANSFER }

final Combo<ComboItem<Mode>> cbMode = Combo.empty();
final ComboItem.Binder<Mode> mode = new ComboItem.Binder<>(
    new ComboItem<>("Termico directo", Mode.DIRECT),
    new ComboItem<>("Transferencia", Mode.TRANSFER));
mode.setup(cbMode);                                          // llena el combo
mode.selectValue(Mode.TRANSFER);                             // elige por valor
final Mode elegido = mode.getSelectedValue(Mode.DIRECT);     // lee el valor (o el de respaldo)
```

| Metodo | Que hace |
|---|---|
| `new ComboItem<>(etiqueta, valor)` / `getValue()` | el elemento |
| `ComboItem.getSelectedValue(combo)` / `(combo, porDefecto)` | el valor seleccionado (o `null` / el de respaldo si no hay seleccion) |
| `ComboItem.selectValue(combo, valor)` | elige el primer item con ese valor; si no esta, no cambia nada |
| `ComboItem.setupModel(combo, lista)` | le pone el modelo al combo |
| `ComboItem.itemsOf(items...)` | una lista mutable de items |
| `ComboItem.valuesOf(items...)` | la lista de sus valores |
| `ComboItem.fromEnum(valores, e -> etiqueta)` | un arreglo de items a partir de un enum |
| `ComboItem.Binder<T>` | une la lista de items a un combo: `setup(combo)`, `selectValue(v)`, `getSelectedValue()`, `getSelectedValue(porDefecto)`. Lanza `IllegalStateException` si se usa antes de `setup` |

## `CheckGroup`

Une **claves de tu dominio** con los botones que las representan (`Radio`, `Check`, `Toggle`), para leer y fijar la seleccion sin `if` en cadena.

```java
final CheckGroup<Mode> modes = new CheckGroup<Mode>()
    .add(Mode.DIRECT, rbtnDirect)
    .add(Mode.TRANSFER, rbtnTransfer);

modes.select(Mode.TRANSFER);                                 // marca ese y desmarca el resto
final Mode actual = modes.selectedKeyOr(Mode.DIRECT);        // la clave marcada, o la de respaldo
```

| Metodo | Que hace |
|---|---|
| `add(clave, boton)` / `addAll(Map)` | agrega elementos (no admite `null`) |
| `select(clave)` | marca esa clave y desmarca las demas; si no existe, no marca ninguna |
| `selectedKey()` | un `Optional<K>` con la clave marcada |
| `selectedKeyOr(porDefecto)` | la clave marcada o el valor de respaldo |
| `clearSelection()` | desmarca todo |
| `size()` / `isEmpty()` / `asMap()` | tamano y una vista inmutable |

`CheckGroup` **no** agrupa los botones para que sean excluyentes: para eso usa `Radio.group(...)` o `Kit.group(...)`.

## `LabelListCell`

Renderizador de filas de lista con **colores alternos** (`Kit.ROW_ODD`, `Kit.ROW_EVEN`) y fila seleccionada en `Kit.ROW_SELECTED`, con 8 px de margen.

```java
list.setCellRenderer(new LabelListCell());                   // en una lista
UIManager.put("List.cellRenderer", new LabelListCell());     // o en todas las listas de la app (antes de crearlas)
```

## `ComboBoxRenderer`

Renderizador de combos: pinta el `toString()` del elemento, o **el propio componente si el elemento ya es un `Component`**. Trae dos
elementos listos para meter en un combo:

| Clase | Que es |
|---|---|
| `ComboBoxRenderer.Separator` | una linea divisoria entre elementos |
| `ComboBoxRenderer.Button(int id, String texto)` / `(int id, Icon icono)` | un elemento con aspecto de enlace (color de acento) y un `id` para distinguirlo ("Actualizar lista...") |

```java
final JComboBox<Object> cb = new JComboBox<>(new Object[] {
    "Impresora A", "Impresora B", new ComboBoxRenderer.Separator(), new ComboBoxRenderer.Button(1, "Actualizar...")});
cb.setRenderer(new ComboBoxRenderer());
cb.addActionListener(e -> {
  if (cb.getSelectedItem() instanceof ComboBoxRenderer.Button) {
    final int id = ((ComboBoxRenderer.Button) cb.getSelectedItem()).id;
    // atender la accion `id` y volver a elegir un valor real
  }
});
```
