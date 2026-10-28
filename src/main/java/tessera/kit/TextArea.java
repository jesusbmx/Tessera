package tessera.kit;

import javax.swing.JTextArea;
import tessera.components.TextPlaceholder;

/** Area de texto de varias lineas (se crea con {@link Input#area}); va dentro de un {@link Scroll}. */
public class TextArea extends JTextArea implements Styled<TextArea> {

  protected TextArea(int rows) {
    super(rows, 0);
  }

  /** Texto gris que se ve mientras el area esta vacia (FlatLaf no lo pinta en un JTextArea: se usa {@link TextPlaceholder}). */
  public TextArea placeholder(String text) {
    TextPlaceholder.install(this, text);
    return this;
  }

  /** Ancho preferido en caracteres. */
  public TextArea columns(int columns) {
    setColumns(columns);
    return this;
  }

  public TextArea editable(boolean editable) {
    setEditable(editable);
    return this;
  }

  /** Las lineas largas se parten en vez de salirse del area. */
  public TextArea wrap() {
    setLineWrap(true);
    setWrapStyleWord(true);
    return this;
  }

  public TextArea onChange(Runnable action) {
    Texts.onChange(this, action);
    return this;
  }
}
