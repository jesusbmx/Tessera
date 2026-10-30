package com.jesustectronic.tessera.preview;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.Action;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObject;
import org.openide.util.ContextAwareAction;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;

/**
 * "Preview con Tessera": clic derecho en un archivo .java (en el editor, en Projects o en Files). Se habilita solo con
 * un archivo java seleccionado.
 */
public final class PreviewAction extends AbstractAction implements ContextAwareAction {

  private final FileObject file;

  /** La instancia que crea el layer (sin contexto: deshabilitada); NetBeans pide la de cada menu con createContextAwareInstance. */
  public PreviewAction() {
    this(null);
  }

  private PreviewAction(FileObject file) {
    super(NbBundle.getMessage(PreviewAction.class, "CTL_PreviewAction"));
    this.file = file;
    setEnabled(file != null && "java".equalsIgnoreCase(file.getExt()));
  }

  @Override
  public Action createContextAwareInstance(Lookup context) {
    final DataObject data = context.lookup(DataObject.class);
    return new PreviewAction(data == null ? null : data.getPrimaryFile());
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    if (file != null) {
      PreviewSession.start(file);
    }
  }
}
