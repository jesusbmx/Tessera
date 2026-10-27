package tessera.components;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import javax.swing.AbstractButton;
import javax.swing.JRadioButton;

/**
 * Gestiona una colección de elementos "seleccionables" asociados a una clave.
 * Útil para mapear claves de dominio (K) a componentes de UI (p. ej. JRadioButton, JCheckBox, JComboBox).
 *
 * @param <K> Tipo de la clave (enum, String, Integer, etc.)
 */
public final class CheckGroup<K> {

    private final Map<K, AbstractButton> items = new LinkedHashMap<>();

    /** Agrega un elemento con su clave. */
    public CheckGroup<K> add(K key, AbstractButton selectable) {
        Objects.requireNonNull(key, "key");
        Objects.requireNonNull(selectable, "selectable");
        items.put(key, selectable);
        return this;
    }

    
    /** Agrega todos los elementos del mapa dado. */
    public CheckGroup<K> addAll(Map<? extends K, ? extends AbstractButton> map) {
        Objects.requireNonNull(map, "map");
        map.forEach(this::add);
        return this;
    }

    /** Devuelve la clave actualmente seleccionada (si existe). */
    public Optional<K> selectedKey() {
        return items.entrySet().stream()
                .filter(e -> e.getValue().isSelected())
                .map(Map.Entry::getKey)
                .findFirst();
    }

    /** Devuelve la clave seleccionada o un valor por defecto si no hay selección. */
    public K selectedKeyOr(K fallback) { return selectedKey().orElse(fallback); }

    /**
     * Marca como seleccionada la clave indicada y desmarca el resto.
     * Si la clave no existe, no selecciona nada.
     */
    public CheckGroup<K> select(K key) {
        items.forEach((k, button) -> button.setSelected(Objects.equals(k, key)));
        return this;
    }

    /** Limpia la selección (desmarca todos). */
    public void clearSelection() { items.values().forEach(s -> s.setSelected(false)); }

    /** Cantidad de elementos del grupo. */
    public int size() { return items.size(); }

    /** ¿El grupo está vacío? */
    public boolean isEmpty() { return items.isEmpty(); }

    /** Vista inmutable del mapa interno. */
    public Map<K, AbstractButton> asMap() { return Collections.unmodifiableMap(items); }

    
    // --- Ejemplo mínimo ---
    public static void main(String[] args) {
        JRadioButton a = new JRadioButton("A");
        JRadioButton b = new JRadioButton("B");
        // Uso ilustrativo con combo:
        // JComboBox<Object> cb = new JComboBox<>();
        // new SelectionGroup<MyEnum>()
        //     .combo(cb)
        //         .item(MyEnum.OPT1, "Opción 1")
        //         .item(MyEnum.OPT2, "Opción 2")
        //         .defaultKey(MyEnum.OPT2)
        //     .done();
    }
}
