package tessera.theme;

import com.formdev.flatlaf.util.UIScale;
import java.awt.Font;
import javax.swing.UIManager;

/**
 * Medidas y fuente de la interfaz, ya escaladas con el zoom (igual en 100%).
 *
 * <pre>
 * Metrics.px(12)                  // 12 px a 100%, 18 a 150%
 * Metrics.font(16, true)          // la fuente del tema, en negrita y a 16
 * </pre>
 */
public final class Metrics {

  private Metrics() {
  }

  /** Escala un tamano en pixeles con el zoom de la interfaz (igual en 100%). */
  public static int px(int value) {
    return UIScale.scale(value);
  }

  /** Fuente de la interfaz (la que fijo el tema) con ese tamano y estilo. */
  public static Font font(int size, boolean bold) {
    Font base = UIManager.getFont("defaultFont");
    if (base == null) {
      base = UIManager.getFont("Label.font");
    }
    if (base == null) {
      base = new Font(Font.SANS_SERIF, Font.PLAIN, 12);
    }
    return base.deriveFont(bold ? Font.BOLD : Font.PLAIN, UIScale.scale((float) size));
  }
}
