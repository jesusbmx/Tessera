package tessera.components;

import java.awt.BorderLayout;
import java.awt.Color;
import javax.swing.JLabel;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.JTextComponent;

/**
 * Muestra un texto de ayuda en gris dentro de un JTextArea (o cualquier
 * JTextComponent) mientras esta vacio. Desaparece en cuanto el usuario escribe.
 *
 * FlatLaf ya soporta "JTextField.placeholderText" para JTextField, pero no para
 * JTextArea; esta clase cubre ese caso sin cambiar el tipo del componente.
 */
public final class TextPlaceholder {

    private static final Color COLOR = new Color(160, 160, 160);

    private TextPlaceholder() {
    }

    /** Etiqueta transparente al mouse, para que el clic llegue al area de texto. */
    private static final class HintLabel extends JLabel {
        HintLabel(String text) {
            super(text);
        }

        @Override
        public boolean contains(int x, int y) {
            return false;
        }
    }

    public static void install(final JTextComponent component, String text) {
        final JLabel hint = new HintLabel(text);
        hint.setForeground(COLOR);
        hint.setFont(component.getFont());
        hint.setBorder(new EmptyBorder(component.getInsets()));
        hint.setVerticalAlignment(JLabel.TOP);

        component.setLayout(new BorderLayout());
        component.add(hint, BorderLayout.CENTER);

        // si luego cambian la fuente del componente, la ayuda sigue igual de grande
        component.addPropertyChangeListener("font", (e) -> hint.setFont(component.getFont()));

        hint.setVisible(component.getDocument().getLength() == 0);
        final DocumentListener listener = new DocumentListener() {
            private void update() {
                hint.setVisible(component.getDocument().getLength() == 0);
            }

            @Override
            public void insertUpdate(DocumentEvent e) {
                update();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                update();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                update();
            }
        };
        component.getDocument().addDocumentListener(listener);
        // setText() cambia el Document en algunos casos: re-enganchamos el listener
        component.addPropertyChangeListener("document", (e) -> {
            if (e.getOldValue() instanceof javax.swing.text.Document) {
                ((javax.swing.text.Document) e.getOldValue()).removeDocumentListener(listener);
            }
            component.getDocument().addDocumentListener(listener);
            hint.setVisible(component.getDocument().getLength() == 0);
        });
    }
}
