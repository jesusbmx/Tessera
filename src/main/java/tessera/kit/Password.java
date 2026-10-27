package tessera.kit;

import java.awt.event.ActionListener;
import javax.swing.JPasswordField;

/** Campo de clave (se crea con {@link Input#password()}). */
public class Password extends JPasswordField implements Styled<Password> {

  protected Password() {
  }

  public Password placeholder(String text) {
    Texts.placeholder(this, text);
    return this;
  }

  public Password columns(int columns) {
    setColumns(columns);
    return this;
  }

  public Password onChange(Runnable action) {
    Texts.onChange(this, action);
    return this;
  }

  public Password onEnter(ActionListener listener) {
    addActionListener(listener);
    return this;
  }
}
