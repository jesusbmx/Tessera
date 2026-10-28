package tessera.components;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Point;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.text.JTextComponent;

/**
 * Muestra el error de un control del formulario en un globo flotante debajo de el.
 * Variante con borde rojo y tooltip: {@link ErrorProvider}.
 */
public class FloatingErrorProvider {

  private static final Pattern EMAIL_PATTERN
          = Pattern.compile("^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
          + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$");

  private final Map<JComponent, FloatingErrorPanel> components = new HashMap<>();

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
    for (Map.Entry<JComponent, FloatingErrorPanel> entry : components.entrySet()) {
      final FloatingErrorPanel error = entry.getValue();
      error.close();
    }
    components.clear();
  }
  
  public void removeError(JComponent component) {
    final FloatingErrorPanel error = components.remove(component);
    if (error != null) {
      error.close();
    }
  }

  /**
   * Devuelve la cadena de descripción del error actual para el control
   * especificado.
   *
   * @param component
   * @return string
   */
  public String getError(JComponent component) {
    final FloatingErrorPanel error = components.get(component);
    return error == null ? null : error.message;
  }

  /**
   * Establece la cadena de descripción del error para el control especificado.
   *
   * @param component
   * @param error
   */
  public void setError(JComponent component, String error) {
    if (component == null || !component.isVisible()) return;
    
    FloatingErrorPanel errorPanel = components.get(component);
    if (errorPanel == null) {
      errorPanel = new FloatingErrorPanel(component, error);
      components.put(component, errorPanel);
    } else {
      errorPanel.setMessage(error);
    }
    errorPanel.show();
    
    component.requestFocus();
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
      return true;
    }

    return false;
  }
  
  public static class FloatingErrorPanel {
    String message;
    private final JComponent component;
    private final JPopupMenu errorPopup;
    private final JLabel errorLabel;

    public FloatingErrorPanel(JComponent component, String message) {
      this.component = component;
      this.message = message;
      
      errorLabel = new JLabel();
      errorLabel.setForeground(Color.RED);
      
      // Agregar un borde rojo con margen al errorLabel
      Border margin = new EmptyBorder(5, 10, 5, 10);
      errorLabel.setBorder(margin);
      
      errorPopup = new JPopupMenu();
      errorPopup.setLayout(new BorderLayout());
      errorPopup.add(errorLabel, BorderLayout.CENTER);
    }

    public void setMessage(String message) {
      this.message = message;
    }

    public void show() {
      errorLabel.setText("<html><body style='width: 250px;'>" + message + "</body></html>");

      // Programar la visualización del popup para después de que todas las actualizaciones de la UI se hayan procesado
      SwingUtilities.invokeLater(() -> {
        // Obtener la ubicación del componente en la pantalla
        final Point locationOnScreen = component.getLocationOnScreen();
        // Mostrar el errorPopup justo debajo del componente
        errorPopup.show(component, 0, component.getHeight());
      });
    }

    public void close() {
      errorPopup.setVisible(false);
    }
  }
}
