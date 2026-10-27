package tessera.kit;

import java.awt.Color;
import java.awt.Font;
import javax.swing.JComponent;

/**
 * Metodos encadenables comunes a todos los widgets y contenedores del kit: {@code Label.text("Hola").bold().color(...)}.
 * Cada widget (Label, Button, Input, Row...) extiende su clase de Swing y los hereda de aqui.
 *
 * @param <T> el propio widget, para que la cadena siga devolviendo el tipo concreto
 */
public interface Styled<T> {

  @SuppressWarnings("unchecked")
  default T self() {
    return (T) this;
  }

  /** Texto emergente al dejar el mouse encima. */
  default T tip(String text) {
    ((JComponent) this).setToolTipText(text);
    return self();
  }

  /** La misma fuente en negrita. */
  default T bold() {
    final JComponent c = (JComponent) this;
    c.setFont(c.getFont().deriveFont(Font.BOLD));
    return self();
  }

  /** La misma fuente con otro tamano (en puntos, escalado con la interfaz). */
  default T fontSize(int size) {
    final JComponent c = (JComponent) this;
    c.setFont(c.getFont().deriveFont((float) Kit.px(size)));
    return self();
  }

  default T font(Font font) {
    ((JComponent) this).setFont(font);
    return self();
  }

  /** Color del texto. */
  default T color(Color color) {
    ((JComponent) this).setForeground(color);
    return self();
  }

  /** Color de fondo (lo hace opaco). */
  default T background(Color color) {
    final JComponent c = (JComponent) this;
    c.setBackground(color);
    c.setOpaque(true);
    return self();
  }

  /** Ancho fijo (px) dentro de un Row, Col, Form, Card o Stack. */
  default T width(int px) {
    ((JComponent) this).putClientProperty(Kit.WIDTH_KEY, px);
    return self();
  }

  /** Alto fijo (px) dentro de un Row, Col, Form, Card o Stack. */
  default T height(int px) {
    ((JComponent) this).putClientProperty(Kit.HEIGHT_KEY, px);
    return self();
  }

  default T enabled(boolean enabled) {
    ((JComponent) this).setEnabled(enabled);
    return self();
  }

  default T visible(boolean visible) {
    ((JComponent) this).setVisible(visible);
    return self();
  }

  /** Nombre del componente (para depurar o para pruebas). */
  default T name(String name) {
    ((JComponent) this).setName(name);
    return self();
  }
}
