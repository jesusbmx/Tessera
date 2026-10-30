package tessera.kit;

import java.awt.Cursor;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import tessera.theme.Icons;
import tessera.theme.Variant;

/**
 * Etiqueta de texto con estilos listos: {@link #title} (titulos), {@link #field} (nombre de un campo),
 * {@link #muted} (texto secundario) y {@link #text} (texto normal).
 *
 * <pre>
 * Label.title("Gestion de clientes")
 * Label.text("Administra tus clientes").color(Kit.MUTED)
 * Label.field("Nombre:").tip("El nombre del cliente")
 * Label.text("Ayuda").icon("app/img/help_blue.svg", 16).onClick(this::openHelp)
 * Label.success("Guardado")        // tambien danger, warning, info, secondary, primary, light, dark
 * Label.text("Quedan 2 dias").variant(Variant.WARNING)
 * </pre>
 */
public class Label extends JLabel implements Styled<Label> {

  /** Margen (px logicos) alrededor del texto; se recuerda para poder cambiar solo el vertical o el horizontal. */
  private final Insets padding = new Insets(0, 0, 0, 0);

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

  /** Texto del color de una variante (primary, success, danger...). */
  public static Label of(Variant variant, String text) {
    return new Label(text).variant(variant);
  }

  public static Label primary(String text) {
    return of(Variant.PRIMARY, text);
  }

  public static Label secondary(String text) {
    return of(Variant.SECONDARY, text);
  }

  public static Label success(String text) {
    return of(Variant.SUCCESS, text);
  }

  public static Label danger(String text) {
    return of(Variant.DANGER, text);
  }

  public static Label warning(String text) {
    return of(Variant.WARNING, text);
  }

  public static Label info(String text) {
    return of(Variant.INFO, text);
  }

  /** Texto casi blanco (para fondos oscuros). */
  public static Label light(String text) {
    return of(Variant.LIGHT, text);
  }

  /** Texto casi negro. */
  public static Label dark(String text) {
    return of(Variant.DARK, text);
  }

  /** El texto toma el color de la variante. */
  public Label variant(Variant variant) {
    setForeground(variant.color());
    return this;
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
    this.padding.top = top;
    this.padding.left = left;
    this.padding.bottom = bottom;
    this.padding.right = right;
    setBorder(new EmptyBorder(Kit.px(top), Kit.px(left), Kit.px(bottom), Kit.px(right)));
    return this;
  }

  /** Copia del margen (px) alrededor del texto: arriba, izquierda, abajo y derecha. Cambiarla no afecta a la etiqueta. */
  public Insets padding() {
    return new Insets(padding.top, padding.left, padding.bottom, padding.right);
  }

  /** Margen (px) arriba y abajo del texto; izquierda y derecha quedan como estaban. */
  public Label paddingVertical(int vertical) {
    return padding(vertical, padding.left, vertical, padding.right);
  }

  /** Margen (px) a la izquierda y a la derecha del texto; arriba y abajo quedan como estaban. */
  public Label paddingHorizontal(int horizontal) {
    return padding(padding.top, horizontal, padding.bottom, horizontal);
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
