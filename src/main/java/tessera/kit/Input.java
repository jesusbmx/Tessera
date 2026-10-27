package tessera.kit;

import java.awt.event.ActionListener;
import javax.swing.JTextField;

/**
 * Campo de texto de una linea. Con {@link #password()} y {@link #area} salen los otros tipos de entrada.
 *
 * <pre>
 * Input.text().placeholder("Nombre del cliente")
 * Input.text().columns(12).onChange(this::validate)
 * Input.password().placeholder("Clave")
 * </pre>
 */
public class Input extends JTextField implements Styled<Input> {

  protected Input() {
  }

  /** Campo de texto normal. */
  public static Input text() {
    return new Input();
  }

  /** Campo de clave: el contenido no se ve. */
  public static Password password() {
    return new Password();
  }

  /** Area de texto de varias lineas (va dentro de un {@link Scroll}). */
  public static TextArea area(int rows) {
    return new TextArea(rows);
  }

  /** Texto gris que se ve mientras el campo esta vacio. */
  public Input placeholder(String text) {
    Texts.placeholder(this, text);
    return this;
  }

  /** Ancho preferido en caracteres. */
  public Input columns(int columns) {
    setColumns(columns);
    return this;
  }

  public Input editable(boolean editable) {
    setEditable(editable);
    return this;
  }

  /** Que hacer cada vez que cambia el contenido. */
  public Input onChange(Runnable action) {
    Texts.onChange(this, action);
    return this;
  }

  /** Que hacer al presionar Enter en el campo. */
  public Input onEnter(ActionListener listener) {
    addActionListener(listener);
    return this;
  }
}
