package tessera.components;

import java.awt.Color;
import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.text.JTextComponent;
import tessera.notify.Toast;

/**
 * Marca con borde rojo y tooltip los controles de un formulario que tienen un error, y los
 * restaura con {@link #clear()}. Variante con globo flotante: {@link FloatingErrorProvider}.
 */
public class ErrorProvider {

  private static final Border BORDER_ERROR = 
          javax.swing.BorderFactory.createLineBorder(Color.RED, 1, true);
  
  private static final Pattern EMAIL_PATTERN
          = Pattern.compile("^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
          + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$");

  private final Map<JComponent, BorderPart> components
          = new HashMap<JComponent, BorderPart>();

  /**
   * Obtiene el numero de errores asociados a este componente.
   *
   * @return int
   */
  public int size() {
    return components.size();
  }
  
  /**
   * Borra todos los valores de configuración asociados a este componente.
   */
  public void clear() {
    for (Map.Entry<JComponent, BorderPart> entry : components.entrySet()) {
      final JComponent component = entry.getKey();
      final BorderPart part = entry.getValue();
      component.setBorder(part.border);
      component.setToolTipText(part.oldToolTipText);
    }
    components.clear();
  }

  /**
   * Devuelve la cadena de descripción del error actual para el control
   * especificado.
   *
   * @param component
   * @return string
   */
  public String getError(JComponent component) {
    final BorderPart part = components.get(component);
    if (part == null) return null;
    return part.error;
  }

  /**
   * Establece la cadena de descripción del error para el control especificado.
   *
   * @param component
   * @param error
   */
  public void setError(JComponent component, String error) {
    if (component == null) return;

    final BorderPart marked = components.get(component);
    if (marked != null) {
      // Ya estaba marcado: solo cambia el mensaje (no se vuelve a envolver el borde)
      marked.error = error;
      component.setToolTipText(error);
    } else {
      final BorderPart part = new BorderPart(component.getBorder(), component.getToolTipText());
      part.error = error;
      components.put(component, part);
      component.setBorder(BorderFactory.createCompoundBorder(BORDER_ERROR, part.border));
      component.setToolTipText(error);
    }

    component.requestFocus();
    log(component);
  }
  
  /**
   * Valida que el texto del componente tenga forma de correo (le quita los espacios de los extremos).
   *
   * @param txt a validar
   * @param error a mostrar si no lo es
   *
   * @return {@code true} si NO es un correo valido (y marca el error); {@code false} si esta bien
   */
  public boolean notIsEmail(JTextComponent txt, String error) {
    txt.setText(txt.getText().trim());

    if (!EMAIL_PATTERN.matcher(txt.getText()).matches()) {
      setError(txt, error);
      return TRUE;
    }

    return FALSE;
  }
  
  static void log(JComponent c) {
    Toast t = Toast.makeToast(c, c.getToolTipText(), 3000, Toast.Style.ERROR);
    //t.parent().setLocation(c.getLocationOnScreen());
    t.show();
  }

  public static class BorderPart {
    Border border;
    String oldToolTipText;
    /** Mensaje del error actual (ver {@link ErrorProvider#getError}). */
    String error;
    public BorderPart(Border border, String oldToolTipText) {
      this.border = border;
      this.oldToolTipText = oldToolTipText;
    }
  }
}
