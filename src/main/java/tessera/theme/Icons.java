package tessera.theme;

import com.formdev.flatlaf.extras.FlatSVGIcon;
import java.awt.Color;
import java.awt.Cursor;
import java.net.URL;
import javax.swing.AbstractButton;
import javax.swing.Icon;
import javax.swing.ImageIcon;

/** Iconos (SVG vectorial, nitido con cualquier escala de pantalla, o imagen) y el interruptor estilo iOS. */
public final class Icons {

  private static final String SWITCH = "tessera/img/switch_";

  private Icons() {
  }

  /** Icono de un recurso del classpath: SVG (vectorial, a ese tamano) o imagen (PNG, tal cual). {@code null} si una imagen no existe. */
  public static Icon of(String resource, int size) {
    if (resource.endsWith(".svg")) {
      return new FlatSVGIcon(resource, size, size);
    }
    URL url = Icons.class.getResource(resource.startsWith("/") ? resource : "/" + resource);
    return url == null ? null : new ImageIcon(url);
  }

  /** Icono SVG cuadrado de un solo color (ej. blanco sobre un boton principal). */
  public static Icon of(String svg, int size, Color color) {
    return svg(svg, size, size, color);
  }

  /** Icono SVG de un recurso del classpath ("app/img/print.svg"). */
  public static FlatSVGIcon svg(String path, int width, int height) {
    return new FlatSVGIcon(path, width, height);
  }

  /** Icono SVG recoloreado por completo a un color fijo (ej. blanco sobre un boton principal). */
  public static FlatSVGIcon svg(String path, int width, int height, Color color) {
    return new FlatSVGIcon(path, width, height).setColorFilter(new FlatSVGIcon.ColorFilter(c -> color));
  }

  /**
   * Da forma de interruptor a un JCheckBox o JToggleButton. Los estados apagado, presionado y deshabilitado
   * llevan borde visible para que no se pierdan sobre fondos claros. Con el kit: {@code Toggle}.
   */
  public static void setupSwitch(AbstractButton button) {
    button.setIcon(new FlatSVGIcon(SWITCH + "off.svg", 50, 27));
    button.setSelectedIcon(new FlatSVGIcon(SWITCH + "on.svg", 50, 27));
    button.setPressedIcon(new FlatSVGIcon(SWITCH + "pressed.svg", 50, 27));
    button.setRolloverIcon(new FlatSVGIcon(SWITCH + "hover.svg", 50, 27));
    button.setRolloverSelectedIcon(new FlatSVGIcon(SWITCH + "on_hover.svg", 50, 27));
    button.setDisabledIcon(new FlatSVGIcon(SWITCH + "disabled.svg", 50, 27));
    button.setDisabledSelectedIcon(new FlatSVGIcon(SWITCH + "on_disabled.svg", 50, 27));
    button.setRolloverEnabled(true);

    // Que solo se vea el SVG, sin el rectangulo ni el foco de checkbox de FlatLaf
    button.setBorderPainted(false);
    button.setContentAreaFilled(false);
    button.setFocusPainted(false);
    button.setCursor(new Cursor(Cursor.HAND_CURSOR));
  }
}
