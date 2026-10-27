package tessera.kit;

import java.awt.Component;

/**
 * Barra de botones de un dialogo: a la derecha, con el espacio de siempre alrededor.
 *
 * <pre>
 * Footer.of(btnCancel, btnSave)
 * </pre>
 */
public final class Footer {

  private Footer() {
  }

  /** Botones alineados a la derecha, 8 px entre ellos y 12 px de margen. */
  public static Row of(Component... buttons) {
    return Row.gap(8).padding(12, 14).glue().children(buttons);
  }
}
