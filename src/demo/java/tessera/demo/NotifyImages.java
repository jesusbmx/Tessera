package tessera.demo;

import java.awt.Graphics2D;
import java.awt.Window;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.imageio.ImageIO;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import tessera.notify.Notification;
import tessera.notify.Toast;
import tessera.theme.Theme;

/**
 * Genera las imagenes de avisos de docs/img/ (notify-*.png). Los avisos son ventanas propias: se muestran un instante en
 * pantalla (hace falta un escritorio, no funciona sin pantalla) y se pinta su ventana en un PNG. scripts/doc-images.sh la lanza.
 *
 * <pre>
 * java ... tessera.demo.NotifyImages [carpeta]        (por defecto docs/img)
 * </pre>
 */
public final class NotifyImages {

  private NotifyImages() {
  }

  public static void main(String[] args) throws Exception {
    final String dir = args.length > 0 ? args[0] : "docs/img";
    SwingUtilities.invokeAndWait(() -> Theme.builder().accent("#1976D2").panelBackground("#F7F6F3").install());

    final JFrame owner = new JFrame("Mi App");
    SwingUtilities.invokeAndWait(() -> {
      owner.setSize(500, 300);
      owner.setLocationRelativeTo(null);
      owner.setVisible(true);
    });

    capture(dir, "notify-notification", () -> new Notification.Builder("img.print")
        .setTitle("Impresion")
        .setMessage("La etiqueta se envio correctamente.")
        .setStyle(Notification.Style.SUCCESS)
        .setDuration(Notification.LENGTH_INDEFINITE)
        .setOnAction("Ver", n -> { })
        .show());

    capture(dir, "notify-notification-progress", () -> new Notification.Builder("img.download")
        .setTitle("Descargando")
        .setMessage("reporte.pdf")
        .setStyle(Notification.Style.NORMAL)
        .setDuration(Notification.LENGTH_INDEFINITE)
        .setProgress(45, 100)
        .show());

    capture(dir, "notify-notification-error", () -> new Notification.Builder("img.error")
        .setTitle("No se pudo imprimir")
        .setMessage("La impresora no responde. Revisa el cable o la conexion de red.")
        .setStyle(Notification.Style.ERROR)
        .setDuration(Notification.LENGTH_INDEFINITE)
        .show());

    capture(dir, "notify-toast", () -> Toast.makeToast(owner, "Archivo eliminado", Toast.LENGTH_INDEFINITE, Toast.Style.SUCCESS,
        "Deshacer", t -> { }).show());

    System.exit(0);
  }

  /** Muestra un aviso, espera a que termine su animacion y guarda la ventana nueva como PNG. */
  private static void capture(String dir, String name, Runnable show) throws Exception {
    final List<Window> before = new ArrayList<>(Arrays.asList(Window.getWindows()));
    SwingUtilities.invokeAndWait(show);
    Thread.sleep(1800);

    SwingUtilities.invokeAndWait(() -> {
      Window found = null;
      for (Window w : Window.getWindows()) {
        if (!before.contains(w) && w.isShowing() && w instanceof JDialog && ((JDialog) w).isUndecorated()) {
          found = w;
        }
      }
      if (found == null) {
        System.out.println("no se encontro la ventana de " + name);
        return;
      }
      final BufferedImage image = new BufferedImage(found.getWidth(), found.getHeight(), BufferedImage.TYPE_INT_RGB);
      final Graphics2D g = image.createGraphics();
      found.paint(g);
      g.dispose();
      try {
        final File file = new File(dir, name + ".png").getAbsoluteFile();
        file.getParentFile().mkdirs();
        ImageIO.write(image, "png", file);
        System.out.println(dir + "/" + name + ".png (" + found.getWidth() + "x" + found.getHeight() + ")");
      } catch (Exception e) {
        throw new IllegalStateException(e);
      }
      found.setVisible(false);
      found.dispose();
    });
    Thread.sleep(300);
  }
}
