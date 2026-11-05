package tessera.components;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTargetAdapter;
import java.awt.dnd.DropTargetDropEvent;
import java.io.File;
import java.util.List;

/**
 * Receptor de arrastrar y soltar archivos (o texto) sobre un componente: sobrescribir
 * {@code dropFile}/{@code dropFiles}/{@code dropString} para atender lo soltado.
 */
public class DragAndDrop extends DropTargetAdapter {

  @Override
  public void drop(DropTargetDropEvent evento) {
    try {
      //Se comprueba que el tipo de arrastre ocurrido es de los que acepta este objetivo
      if ( ( evento.getDropAction() == DnDConstants.ACTION_MOVE ) ||
         ( evento.getDropAction() == DnDConstants.ACTION_COPY ) ||
         ( evento.getDropAction() == DnDConstants.ACTION_LINK ) )
      { 
        // Se acepta el soltar
        evento.acceptDrop(evento.getDropAction() /*DnDConstants.ACTION_COPY*/);
       
        //Se obtiene el Transferable que contiene el objeto arrastrado
        //proporciona datos para operaciones de transferencia en swing
        Transferable transferable = evento.getTransferable();
  
        /* Si existe una lista de objetos de archivo */
        if ( transferable.isDataFlavorSupported(DataFlavor.javaFileListFlavor) ) 
        { 
          /* obtiene un List con los archivos arrastrados al componente */
          dropFiles( (List<File>) transferable.getTransferData(DataFlavor.javaFileListFlavor) );
        }
        else if ( transferable.isDataFlavorSupported(DataFlavor.stringFlavor) )
        {
          dropString( (String) transferable.getTransferData( DataFlavor.stringFlavor ) );
        }
      
        //Se informa de que la operacion se ha completado
        evento.dropComplete( true );
      }
           
    } catch (Exception e) {
    }
  }
  
  /** Lista de elementos que se arrastran. */
  public void dropFiles(List<File> list) {
    for (File file : list) {
      dropFile(file);
    }
  }
  
  public void dropFile(File file) {
    
  }
  
  public void dropString(String str) {
    
  }
}
