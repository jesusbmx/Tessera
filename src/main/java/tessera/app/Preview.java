package tessera.app;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marca una pantalla que {@link Previewer} sabe mostrar sola, sin arrancar la app: una clase (se crea con su
 * constructor sin parametros) o un metodo estatico sin parametros que devuelve un componente.
 *
 * <pre>
 * &#64;Preview("Herramientas")
 * public class ToolsPanel extends JPanel { ... }
 *
 * &#64;Preview                                          // un metodo estatico: arma la pantalla con datos de muestra
 * static JComponent withData() { ... }
 *
 * public static void main(String[] args) {              // Run File en el IDE
 *   Previewer.show(ToolsPanel.class);
 * }
 * </pre>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface Preview {

  /** Nombre de la pestana (por defecto, el de la clase o el del metodo). */
  String value() default "";

  /** Ancho inicial de la ventana (px logicos); 0 = el que pida el componente. */
  int width() default 0;

  /** Alto inicial de la ventana (px logicos); 0 = el que pida el componente. */
  int height() default 0;
}
