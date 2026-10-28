package tessera.kit;

import java.awt.Cursor;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import tessera.theme.Icons;

/**
 * Etiqueta de texto con estilos listos: {@link #title} (titulos), {@link #field} (nombre de un campo),
 * {@link #muted} (texto secundario) y {@link #text} (texto normal).
 *
 * <pre>
 * Label.title("Gestion de clientes")
 * Label.text("Administra tus clientes").color(Kit.MUTED)
 * Label.field("Nombre:").tip("El nombre del cliente")
 * Label.text("Ayuda").icon("app/img/help_blue.svg", 16).onClick(this::openHelp)
 * </pre>
 */
public class Label extends JLabel implements Styled<Label> {

  protected Label(String text) {
    super(text);
  }

  /** Texto normal. */
  public static Label text(String text) {
    return new Label(text);
  }

  /** Titulo: negrita, 18. */
  public static Label title(String text) {
    return new Label(text).bold().fontSize(18);
  }

  /** Nombre de un campo: negrita, 15. */
  public static Label field(String text) {
    return new Label(text).bold().fontSize(15);
  }

  /** Texto secundario: gris. */
  public static Label muted(String text) {
    return new Label(text).color(Kit.MUTED);
  }

  /** Icono desde un recurso del classpath (SVG o imagen) a ese tamano. */
  public Label icon(String resource, int size) {
    setIcon(Icons.of(resource, size));
    return this;
  }

  public Label icon(Icon icon) {
    setIcon(icon);
    return this;
  }

  /** Espacio (px) entre el icono y el texto. */
  public Label iconGap(int gap) {
    setIconTextGap(Kit.px(gap));
    return this;
  }

  /** Un punto mas pequeno que el texto normal (relativo al tema): la linea de ayuda de una pantalla, junto con {@link #muted}. */
  public Label small() {
    putClientProperty("FlatLaf.style", "font: -1");
    return this;
  }

  /** Texto centrado horizontalmente dentro de la etiqueta. */
  public Label center() {
    setHorizontalAlignment(SwingConstants.CENTER);
    return this;
  }

  /** Texto alineado a la derecha dentro de la etiqueta. */
  public Label right() {
    setHorizontalAlignment(SwingConstants.TRAILING);
    return this;
  }

  /** Margen (px) alrededor del texto: arriba, izquierda, abajo, derecha. */
  public Label padding(int top, int left, int bottom, int right) {
    setBorder(new EmptyBorder(Kit.px(top), Kit.px(left), Kit.px(bottom), Kit.px(right)));
    return this;
  }

  /** La etiqueta se comporta como un enlace: cursor de mano y una accion al hacer clic. */
  public Label onClick(Runnable action) {
    setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    addMouseListener(new MouseAdapter() {
      @Override
      public void mousePressed(MouseEvent e) {
        action.run();
      }
    });
    return this;
  }
}
