package tessera.kit;

import java.awt.event.ActionListener;
import javax.swing.AbstractButton;
import javax.swing.Icon;
import tessera.theme.Icons;

/**
 * Metodos encadenables de los widgets que son botones (Button, Check, Radio, Toggle): clic, icono, estado.
 *
 * @param <T> el propio widget
 */
public interface Clickable<T> extends Styled<T> {

  /** Que hacer al hacer clic (o al cambiar de estado, en casillas y opciones). */
  default T onClick(ActionListener listener) {
    ((AbstractButton) this).addActionListener(listener);
    return self();
  }

  default T onClick(Runnable action) {
    ((AbstractButton) this).addActionListener(e -> action.run());
    return self();
  }

  /** Icono desde un recurso del classpath (SVG o imagen) a ese tamano. */
  default T icon(String resource, int size) {
    return icon(Icons.of(resource, size));
  }

  default T icon(Icon icon) {
    ((AbstractButton) this).setIcon(icon);
    return self();
  }

  /** Espacio (px) entre el icono y el texto. */
  default T iconGap(int gap) {
    ((AbstractButton) this).setIconTextGap(Kit.px(gap));
    return self();
  }

  default T selected(boolean selected) {
    ((AbstractButton) this).setSelected(selected);
    return self();
  }
}
