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
    return part.oldToolTipText;
  }

  /**
   * Establece la cadena de descripción del error para el control especificado.
   *
   * @param component
   * @param error
   */
  public void setError(JComponent component, String error) {
    if (component == null) return;
    final Border oldBorder = component.getBorder();
    final String oldToolTipText = component.getToolTipText();
    
    final Border newBorder = BorderFactory.createCompoundBorder(
            BORDER_ERROR, 
            oldBorder);
    
    component.setBorder(newBorder);
    component.setToolTipText(error);
    
    components.putIfAbsent(component, new BorderPart(oldBorder, oldToolTipText));
    
    component.requestFocus();
    log(component);
  }
  
  /**
   * Valida si el componente es un email.
   *
   * @param txt a validad
   * @param error a mostrar
   *
   * @return @true si el componente es un email
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
    public BorderPart(Border border, String oldToolTipText) {
      this.border = border;
      this.oldToolTipText = oldToolTipText;
    }
  }
}
