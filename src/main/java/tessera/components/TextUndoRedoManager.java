package tessera.components;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.KeyStroke;
import javax.swing.text.JTextComponent;
import javax.swing.undo.UndoManager;

/**
 * Deshacer y rehacer (Ctrl+Z / Ctrl+Y) para un componente de texto.
 */
public class TextUndoRedoManager {

    final UndoManager undoManager;

    public TextUndoRedoManager(UndoManager undoManager) {
        this.undoManager = undoManager;
    }
    
    public TextUndoRedoManager() {
        this(new UndoManager());
    }

    // Acción para Deshacer.
    final Action undoAction = new AbstractAction("Deshacer") {
        @Override
        public void actionPerformed(ActionEvent e) {
            undo();
        }
    };

    // Acción para Rehacer.
    final Action redoAction = new AbstractAction("Rehacer") {
        @Override
        public void actionPerformed(ActionEvent e) {
            redo();
        }
    };

    public void addUndoableEditListener(JTextComponent textComponent) {
        textComponent.getDocument().addUndoableEditListener(undoManager);
        
        // Asigna las teclas de atajo.
        textComponent.getInputMap(JComponent.WHEN_FOCUSED)
                .put(KeyStroke.getKeyStroke("control Z"), "undoAction");
        textComponent.getActionMap().put("undoAction", undoAction);

        textComponent.getInputMap(JComponent.WHEN_FOCUSED)
                .put(KeyStroke.getKeyStroke("control Y"), "redoAction");
        textComponent.getActionMap().put("redoAction", redoAction);
    }

    // Acción para Deshacer.
    public void undo() {
        if (undoManager.canUndo()) {
            undoManager.undo();
        }
    }

    // Acción para Rehacer.
    public void redo() {
        if (undoManager.canRedo()) {
            undoManager.redo();
        }
    }

    public UndoManager getUndoManager() {
        return undoManager;
    }
    
    public static void setupUndo(JTextComponent textComponent) {
        new TextUndoRedoManager().addUndoableEditListener(textComponent);
    }
}
