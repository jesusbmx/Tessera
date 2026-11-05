package tessera.components;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import java.util.function.Function;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;

/**
 * Representa un elemento para usar en JComboBox, separando la etiqueta visible (label)
 * del valor real (value). El combo mostrará {@link #toString()} (la etiqueta), pero
 * podrás recuperar el valor fuertemente tipado con los helpers estáticos.
 *
 * @param <K> tipo del valor asociado (Integer, String, Enum, etc.)
 */
public final class ComboItem<K> {

    private final String label;
    private final K value;

    public ComboItem(String label, K value) {
        this.label = label;
        this.value = value;
    }

    /** Lo que verá el usuario en el JComboBox. */
    @Override
    public String toString() { return label; }

    /** Valor real que te interesa en tu lógica. */
    public K getValue() { return value; }

    /* =========================================================
       ===============   Helpers ESTÁTICOS   ===================
       ========================================================= */

    /**
     * Devuelve el valor seleccionado del combo, o {@code defaultVal} si no hay selección.
     */
    public static <K> K getSelectedValue(JComboBox<ComboItem<K>> combo, K defaultVal) {
        @SuppressWarnings("unchecked")
        final ComboItem<K> sel = (ComboItem<K>) combo.getSelectedItem();
        return sel != null ? sel.getValue() : defaultVal;
    }

    /**
     * Devuelve el valor seleccionado del combo, o {@code null} si no hay selección.
     */
    public static <K> K getSelectedValue(JComboBox<ComboItem<K>> combo) {
        return getSelectedValue(combo, null);
    }

    /**
     * Selecciona en el combo el primer ítem cuyo {@code value} sea igual (Objects.equals) al valor indicado.
     * Si no lo encuentra, la selección no cambia.
     */
    public static <K> void selectValue(JComboBox<ComboItem<K>> combo, K value) {
        for (int i = 0; i < combo.getItemCount(); i++) {
            final ComboItem<K> item = combo.getItemAt(i);
            if (Objects.equals(item.getValue(), value)) {
                combo.setSelectedIndex(i);
                return;
            }
        }
    }

    /**
     * Convierte un arreglo de {@code ComboItem<V>} en una lista de sus valores ({@code V}).
     * Útil si quieres recuperar solo los "values" de una definición estática de items.
     */
    @SafeVarargs
    public static <V> java.util.List<V> valuesOf(ComboItem<V>... items) {
        final java.util.List<V> out = new ArrayList<>(items.length);
        for (ComboItem<V> it : items) out.add(it.getValue());
        return out;
    }

    /**
     * Devuelve una lista (mutable) de {@code ComboItem<V>} a partir de varargs.
     * Azúcar sintáctico para crear modelos rápidamente.
     */
    @SafeVarargs
    public static <V> java.util.List<ComboItem<V>> itemsOf(ComboItem<V>... items) {
        return new ArrayList<>(Arrays.asList(items));
    }

    /**
     * Crea un arreglo de {@code ComboItem<E>} a partir de valores de un Enum usando un "labeler".
     * Ejemplo:
     *   ComboItem.fromEnum(MyEnum.values(), e -> "Etiqueta " + e.name());
     */
    public static <E extends Enum<E>> ComboItem<E>[] fromEnum(E[] values, Function<E, String> labeler) {
        @SuppressWarnings("unchecked")
        ComboItem<E>[] arr = new ComboItem[values.length];
        for (int i = 0; i < values.length; i++) {
            arr[i] = new ComboItem<>(labeler.apply(values[i]), values[i]);
        }
        return arr;
    }

    /**
     * Configura el modelo de un {@link JComboBox} a partir de una lista de {@code ComboItem<K>}.
     * Mantiene los tipos en el combo y evita casts innecesarios.
     */
    public static <K> void setupModel(JComboBox<ComboItem<K>> combo, java.util.List<ComboItem<K>> items) {
        @SuppressWarnings("unchecked")
        ComboItem<K>[] array = items.toArray(new ComboItem[0]);
        combo.setModel(new DefaultComboBoxModel<>(array));
    }

    /* =========================================================
       ============   Binder opcional para ComboBox   ==========
       ========================================================= */

    /**
     * Clase de ayuda que "vincula" una lista de {@code ComboItem<T>} a un {@code JComboBox}
     * y expone operaciones cómodas sin tener que pasar el combo en cada llamada.
     *
     * Uso típico:
     * <pre>
     *   var items = ComboItem.itemsOf(
     *       new ComboItem<>("Térmico directo", PrinterMode.DIRECT),
     *       new ComboItem<>("Transferencia",     PrinterMode.TRANSFER)
     *   );
     *
     *   var binder = new ComboItem.Binder<>(items);
     *   binder.setup(miCombo);
     *   binder.selectValue(PrinterMode.DIRECT);
     *   PrinterMode mode = binder.getSelectedValue(PrinterMode.DIRECT);
     * </pre>
     */
    public static final class Binder<T> {

        private final java.util.List<ComboItem<T>> items;
        private JComboBox<ComboItem<T>> combo;

        public Binder(java.util.List<ComboItem<T>> items) {
            this.items = new ArrayList<>(items);
        }
        
        public Binder(ComboItem<T>... items) {
            this(Arrays.asList(items));
        }

        /**
         * Asocia este binder a un {@link JComboBox} y establece su modelo con los items.
         */
        public void setup(JComboBox<ComboItem<T>> comboBox) {
            this.combo = comboBox;
            ComboItem.setupModel(this.combo, this.items);
        }

        /**
         * Selecciona el valor indicado (si existe en el modelo).
         */
        public void selectValue(T value) {
            checkBound();
            ComboItem.selectValue(this.combo, value);
        }

        /**
         * Obtiene el valor seleccionado o {@code null} si no hay selección.
         */
        public T getSelectedValue() {
            checkBound();
            return ComboItem.getSelectedValue(this.combo);
        }

        /**
         * Obtiene el valor seleccionado o {@code defaultVal} si no hay selección.
         */
        public T getSelectedValue(T defaultVal) {
            checkBound();
            return ComboItem.getSelectedValue(this.combo, defaultVal);
        }

        private void checkBound() {
            if (this.combo == null) {
                throw new IllegalStateException("Binder no inicializado. Llama a setup(JComboBox) primero.");
            }
        }
    }
}
