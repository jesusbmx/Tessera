package com.jesustectronic.tessera.preview;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URI;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import javax.imageio.ImageIO;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import org.netbeans.core.spi.multiview.CloseOperationState;
import org.netbeans.core.spi.multiview.MultiViewElement;
import org.netbeans.core.spi.multiview.MultiViewElementCallback;
import org.openide.awt.UndoRedo;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObject;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.RequestProcessor;

/**
 * La pestana "Tessera Preview" del editor de un archivo .java (junto a Source y History): muestra como queda la pantalla.
 * La imagen la genera {@link Renderer} en un proceso aparte con el classpath del proyecto (llega como bytes por su salida
 * estandar, sin pasar por el disco) y se vuelve a generar sola
 * cuando el IDE compila cambios (al guardar). La imagen es la pantalla a su tamano natural; para probarla de verdad
 * ("Abrir en ventana") se usa {@link PreviewSession}.
 */
public final class PreviewElement implements MultiViewElement {

  private static final RequestProcessor RP = new RequestProcessor("Tessera Preview render", 1);
  /** Un render que tarda mas que esto se da por colgado. */
  private static final long TIMEOUT_SECONDS = 40;

  private static final String CARD_IMAGE = "image";
  private static final String CARD_TEXT = "text";

  private final Lookup lookup;
  private final FileObject file;
  private final RequestProcessor.Task renderTask = RP.create(this::render);

  private JPanel panel;
  private JToolBar toolbar;
  private JLabel status;
  private CardLayout cards;
  private JPanel content;
  private JLabel image;
  private JTextArea text;
  private MultiViewElementCallback callback;

  private volatile boolean showing;
  private ClassWatcher watcher;

  /** Lo llama NetBeans (el layer registra la clase) con el contexto del archivo. */
  public PreviewElement(Lookup lookup) {
    this.lookup = lookup;
    final DataObject data = lookup.lookup(DataObject.class);
    this.file = data == null ? null : data.getPrimaryFile();
  }

  // ---------------------------------------------------------------------- //
  //   M U L T I V I E W
  // ---------------------------------------------------------------------- //

  @Override
  public JComponent getVisualRepresentation() {
    build();
    return panel;
  }

  @Override
  public JComponent getToolbarRepresentation() {
    build();
    return toolbar;
  }

  @Override
  public Action[] getActions() {
    return callback == null ? new Action[0] : callback.createDefaultActions();
  }

  @Override
  public Lookup getLookup() {
    return lookup;
  }

  @Override
  public void componentOpened() {
  }

  @Override
  public void componentClosed() {
    stopWatching();
  }

  @Override
  public void componentShowing() {
    showing = true;
    renderTask.schedule(0);
  }

  @Override
  public void componentHidden() {
    showing = false;
    stopWatching();
  }

  @Override
  public void componentActivated() {
  }

  @Override
  public void componentDeactivated() {
  }

  @Override
  public UndoRedo getUndoRedo() {
    return UndoRedo.NONE;
  }

  @Override
  public void setMultiViewCallback(MultiViewElementCallback callback) {
    this.callback = callback;
  }

  @Override
  public CloseOperationState canCloseElement() {
    return CloseOperationState.STATE_OK;
  }

  // ---------------------------------------------------------------------- //
  //   I N T E R F A Z
  // ---------------------------------------------------------------------- //

  private void build() {
    if (panel != null) {
      return;
    }
    image = new JLabel();
    final JPanel imageHolder = new JPanel(new GridBagLayout());       // centra la imagen si cabe
    imageHolder.setBackground(UIManager.getColor("Panel.background"));
    imageHolder.add(image);
    text = new JTextArea();
    text.setEditable(false);
    text.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
    text.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

    cards = new CardLayout();
    content = new JPanel(cards);
    content.add(new JScrollPane(imageHolder), CARD_IMAGE);
    content.add(new JScrollPane(text), CARD_TEXT);

    panel = new JPanel(new BorderLayout());
    panel.add(content, BorderLayout.CENTER);

    toolbar = new JToolBar();
    toolbar.setFloatable(false);
    toolbar.setRollover(true);
    final JButton reload = new JButton(NbBundle.getMessage(PreviewElement.class, "CTL_PreviewReload"));
    reload.setToolTipText(NbBundle.getMessage(PreviewElement.class, "CTL_PreviewReload.tip"));
    reload.addActionListener(e -> renderTask.schedule(0));
    final JButton open = new JButton(NbBundle.getMessage(PreviewElement.class, "CTL_PreviewOpen"));
    open.setToolTipText(NbBundle.getMessage(PreviewElement.class, "CTL_PreviewOpen.tip"));
    open.addActionListener(e -> {
      if (file != null) {
        PreviewSession.start(file);
      }
    });
    status = new JLabel(" ");
    status.setForeground(Color.GRAY);
    status.setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 0));
    toolbar.add(reload);
    toolbar.add(open);
    toolbar.add(status);
  }

  private void showImage(BufferedImage png) {
    SwingUtilities.invokeLater(() -> {
      image.setIcon(new ImageIcon(png));
      cards.show(content, CARD_IMAGE);
      status.setText(png.getWidth() + " x " + png.getHeight());
    });
  }

  private void showMessage(String message) {
    SwingUtilities.invokeLater(() -> {
      text.setText(message);
      text.setCaretPosition(0);
      cards.show(content, CARD_TEXT);
      status.setText(" ");
    });
  }

  private void setStatus(String message) {
    SwingUtilities.invokeLater(() -> status.setText(message));
  }

  // ---------------------------------------------------------------------- //
  //   G E N E R A R   L A   I M A G E N
  // ---------------------------------------------------------------------- //

  private synchronized void stopWatching() {
    if (watcher != null) {
      watcher.stop();
      watcher = null;
    }
  }

  private synchronized void watch(PreviewTarget target) {
    if (showing && watcher == null && !target.watchRoots.isEmpty()) {
      watcher = new ClassWatcher(target.watchRoots, () -> renderTask.schedule(0));
      watcher.start();
    }
  }

  private void render() {
    build();
    if (file == null) {
      showMessage(NbBundle.getMessage(PreviewElement.class, "MSG_PreviewNoFile"));
      return;
    }
    final PreviewTarget target;
    try {
      target = PreviewTarget.resolve(file);
    } catch (PreviewTarget.Unavailable e) {
      showMessage(e.getMessage());
      return;
    }
    watch(target);
    setStatus(NbBundle.getMessage(PreviewElement.class, "MSG_PreviewRendering"));

    final List<String> command = new ArrayList<>();
    final Process process;
    try {
      command.add(target.java.getAbsolutePath());
      if (target.theme != null) {
        command.add("-Dtessera.preview.theme=" + target.theme);
      }
      command.add("-cp");
      command.add(target.classpath + File.pathSeparator + pluginJar());
      command.add(Renderer.class.getName());
      command.add(target.className);
      process = new ProcessBuilder(command).directory(target.projectDir).start();
    } catch (Exception e) {
      showMessage(NbBundle.getMessage(PreviewElement.class, "MSG_PreviewError", e.toString()));
      return;
    }

    // la imagen llega como bytes por la salida estandar (sin archivos); lo que la app escriba en stderr se guarda por si falla
    final StringBuilder log = new StringBuilder();
    final Thread logPump = daemon("Tessera Preview render log", () -> {
      try (Reader reader = new InputStreamReader(process.getErrorStream(), Charset.defaultCharset())) {
        final char[] buffer = new char[2048];
        int count;
        while ((count = reader.read(buffer)) > 0) {
          synchronized (log) {
            log.append(buffer, 0, count);
          }
        }
      } catch (IOException ignored) {
        // el proceso termino
      }
    });
    final BufferedImage[] image = new BufferedImage[1];
    final String[] error = new String[1];
    final Thread reader = daemon("Tessera Preview render output", () -> {
      try (DataInputStream in = new DataInputStream(new BufferedInputStream(process.getInputStream(), 1 << 16))) {
        final int type = in.read();
        if (type == 'F') {
          final byte[] png = new byte[in.readInt()];
          in.readFully(png);
          image[0] = ImageIO.read(new ByteArrayInputStream(png));
        } else if (type == 'E') {
          error[0] = in.readUTF();
        }
      } catch (IOException ignored) {
        // el proceso termino sin responder
      }
    });

    try {
      reader.join(TIMEOUT_SECONDS * 1000);
      if (reader.isAlive()) {
        process.destroyForcibly();
        showMessage(NbBundle.getMessage(PreviewElement.class, "MSG_PreviewTimeout", TIMEOUT_SECONDS));
        return;
      }
      process.waitFor(2, TimeUnit.SECONDS);
      logPump.join(1000);
      if (image[0] != null) {
        showImage(image[0]);
      } else if (error[0] != null) {
        showMessage(error[0]);
      } else {
        synchronized (log) {
          showMessage(log.toString().trim().isEmpty() ? NbBundle.getMessage(PreviewElement.class, "MSG_PreviewFailed") : log.toString());
        }
      }
    } catch (InterruptedException e) {
      process.destroyForcibly();
      Thread.currentThread().interrupt();
    }
  }

  private static Thread daemon(String name, Runnable body) {
    final Thread thread = new Thread(body, name);
    thread.setDaemon(true);
    thread.start();
    return thread;
  }

  /** El jar del plugin: ahi vive {@link Renderer}, que se agrega al classpath del proyecto. */
  private static String pluginJar() throws Exception {
    // el cargador de modulos da la ubicacion como jar:file:/ruta/modulo.jar!/
    String location = PreviewElement.class.getProtectionDomain().getCodeSource().getLocation().toString();
    if (location.startsWith("jar:")) {
      location = location.substring("jar:".length());
    }
    final int bang = location.indexOf("!/");
    if (bang >= 0) {
      location = location.substring(0, bang);
    }
    return new File(new URI(location)).getAbsolutePath();
  }
}
