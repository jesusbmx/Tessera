package tessera.kit;

import com.formdev.flatlaf.FlatClientProperties;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.JTextComponent;

/** Piezas que comparten Input, Password y TextArea. */
final class Texts {

  private Texts() {
  }

  static void placeholder(JTextComponent field, String text) {
    field.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, text);
  }

  /** Avisa cada vez que cambia el contenido, escrito por el usuario o por codigo. */
  static void onChange(JTextComponent field, Runnable action) {
    field.getDocument().addDocumentListener(new DocumentListener() {
      @Override
      public void insertUpdate(DocumentEvent e) {
        action.run();
      }

      @Override
      public void removeUpdate(DocumentEvent e) {
        action.run();
      }

      @Override
      public void changedUpdate(DocumentEvent e) {
        action.run();
      }
    });
  }
}
