package com.jesustectronic.tessera.preview;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URI;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
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
 * La pestana "Tessera Preview" del editor de un archivo .java (junto a Source y History): la pantalla viva. Corre en un
 * proceso aparte ({@link LiveSession}, con el classpath del proyecto) y la pestana pinta lo que ese proceso le manda y le
 * devuelve el tamano del panel, el mouse y el teclado: estirar el panel (o elegir un ancho) reacomoda la pantalla como en la
 * app. Se reinicia sola cuando el IDE compila cambios (al guardar). Para ver la pantalla en su propia ventana, "Abrir en
 * ventana" ({@link PreviewSession}).
 */
public final class PreviewElement implements MultiViewElement {

  private static final RequestProcessor RP = new RequestProcessor("Tessera Preview", 2);
  /** Cuanto tiempo vive el proceso de una pestana que ya no se ve (cada uno es una JVM). */
  private static final long IDLE_MS = 30_000;
  /** Espera (ms) tras el ultimo cambio de tamano del panel antes de avisar al proceso. */
  private static final int RESIZE_MS = 120;
  private static final int[] PRESET_WIDTHS = {0, 360, 768, 1200, 1600};

  private static final String CARD_LIVE = "live";
  private static final String CARD_TEXT = "text";

  private final Lookup lookup;
  private final FileObject file;

  private final RequestProcessor.Task startTask = RP.create(this::ensureSession);
  private final RequestProcessor.Task restartTask = RP.create(this::restartSession);
  private final RequestProcessor.Task idleTask = RP.create(this::stopSession);

  private JPanel panel;
  private JToolBar toolbar;
  private JLabel status;
  private JComboBox<String> sizes;
  private CardLayout cards;
  private JPanel content;
  private JScrollPane scroll;
  private LivePanel live;
  private JTextArea text;
  private Timer resizeTimer;
  private MultiViewElementCallback callback;

  private volatile boolean showing;
  /** El tamano que pide la pantalla (ancho segun el combo, alto del panel); lo pone el hilo de Swing. */
  private volatile Dimension wanted = new Dimension(1000, 700);

  /** La sesion actual: se cambia con "this" tomado, pero se lee sin candado (el hilo de Swing la usa en cada evento de mouse). */
  private volatile LiveSession session;
  private int generation;
  private long sessionSignature;
  private ClassWatcher watcher;
  private PreviewTarget target;

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
    showing = false;
    idleTask.cancel();
    stopSession();
  }

  @Override
  public void componentShowing() {
    showing = true;
    idleTask.cancel();
    startTask.schedule(0);
  }

  @Override
  public void componentHidden() {
    showing = false;
    stopWatching();
    idleTask.schedule((int) IDLE_MS);
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
    live = new LivePanel();
    live.setSink(new LivePanel.Sink() {
      @Override
      public void mouse(int id, int x, int y, int button, int modifiers, int clicks, boolean popup) {
        final LiveSession current = currentSession();
        if (current != null) {
          current.mouse(id, x, y, button, modifiers, clicks, popup);
        }
      }

      @Override
      public void key(int id, int code, char ch, int modifiers) {
        final LiveSession current = currentSession();
        if (current != null) {
          current.key(id, code, ch, modifiers);
        }
      }
    });
    scroll = new JScrollPane(live);
    scroll.getVerticalScrollBar().setUnitIncrement(24);

    text = new JTextArea();
    text.setEditable(false);
    text.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
    text.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

    cards = new CardLayout();
    content = new JPanel(cards);
    content.add(scroll, CARD_LIVE);
    content.add(new JScrollPane(text), CARD_TEXT);

    panel = new JPanel(new BorderLayout());
    panel.add(content, BorderLayout.CENTER);

    // el tamano del panel se le avisa a la pantalla (un poco despues del ultimo cambio, no en cada pixel)
    resizeTimer = new Timer(RESIZE_MS, e -> sendSize());
    resizeTimer.setRepeats(false);
    scroll.getViewport().addComponentListener(new ComponentAdapter() {
      @Override
      public void componentResized(ComponentEvent e) {
        resizeTimer.restart();
      }
    });

    toolbar = new JToolBar();
    toolbar.setFloatable(false);
    toolbar.setRollover(true);
    final JButton reload = new JButton(NbBundle.getMessage(PreviewElement.class, "CTL_PreviewReload"));
    reload.setToolTipText(NbBundle.getMessage(PreviewElement.class, "CTL_PreviewReload.tip"));
    reload.addActionListener(e -> restartTask.schedule(0));
    final JButton open = new JButton(NbBundle.getMessage(PreviewElement.class, "CTL_PreviewOpen"));
    open.setToolTipText(NbBundle.getMessage(PreviewElement.class, "CTL_PreviewOpen.tip"));
    open.addActionListener(e -> {
      if (file != null) {
        PreviewSession.start(file);
      }
    });
    sizes = new JComboBox<>();
    for (int i = 0; i < PRESET_WIDTHS.length; i++) {
      sizes.addItem(NbBundle.getMessage(PreviewElement.class, "CTL_PreviewSize." + i));
    }
    sizes.setToolTipText(NbBundle.getMessage(PreviewElement.class, "CTL_PreviewSize.tip"));
    sizes.setMaximumSize(sizes.getPreferredSize());
    sizes.addActionListener(e -> {
      live.setFit(sizes.getSelectedIndex() == 0);
      sendSize();
    });
    status = new JLabel(" ");
    status.setForeground(Color.GRAY);
    status.setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 0));
    toolbar.add(reload);
    toolbar.add(open);
    toolbar.addSeparator();
    toolbar.add(sizes);
    toolbar.add(status);
  }

  /** Lo que se pide a la pantalla: el ancho del combo (o el del panel) y el alto del panel. */
  private void sendSize() {
    final Dimension extent = scroll.getViewport().getExtentSize();
    if (extent.width <= 0 || extent.height <= 0) {
      return;
    }
    final int preset = PRESET_WIDTHS[Math.max(0, sizes.getSelectedIndex())];
    wanted = new Dimension(preset == 0 ? extent.width : preset, extent.height);
    final LiveSession current = currentSession();
    if (current != null) {
      current.resize(wanted.width, wanted.height);
    }
  }

  private void showFrame(BufferedImage image) {
    cards.show(content, CARD_LIVE);
    live.setImage(image);
    status.setText(image.getWidth() + " x " + image.getHeight());
  }

  private void showMessage(String message) {
    text.setText(message);
    text.setCaretPosition(0);
    cards.show(content, CARD_TEXT);
    status.setText(" ");
  }

  // ---------------------------------------------------------------------- //
  //   L A   P A N T A L L A   V I V A
  // ---------------------------------------------------------------------- //

  private LiveSession currentSession() {
    return session;
  }

  private synchronized void stopWatching() {
    if (watcher != null) {
      watcher.stop();
      watcher = null;
    }
  }

  private synchronized void stopSession() {
    stopWatching();
    generation++;                       // los avisos del proceso que se va ya no cuentan
    if (session != null) {
      session.stop();
      session = null;
    }
  }

  /** Al mostrar la pestana: arranca la pantalla si no esta viva o si se recompilo mientras no se veia. */
  private synchronized void ensureSession() {
    build();
    if (session != null && session.isAlive() && target != null
        && ClassWatcher.signature(target.watchRoots) == sessionSignature) {
      watch();
      return;
    }
    startSession();
  }

  private synchronized void restartSession() {
    stopSession();
    startSession();
  }

  private synchronized void startSession() {
    if (session != null) {
      session.stop();
      session = null;
    }
    if (file == null) {
      showMessageLater(NbBundle.getMessage(PreviewElement.class, "MSG_PreviewNoFile"));
      return;
    }
    try {
      target = PreviewTarget.resolve(file);
    } catch (PreviewTarget.Unavailable e) {
      showMessageLater(e.getMessage());
      return;
    }
    sessionSignature = ClassWatcher.signature(target.watchRoots);
    final int mine = ++generation;
    try {
      session = new LiveSession(target, pluginJar(), new LiveSession.Listener() {
        @Override
        public void frame(BufferedImage image) {
          if (isCurrent(mine)) {
            SwingUtilities.invokeLater(() -> showFrame(image));
          }
        }

        @Override
        public void error(String message) {
          if (isCurrent(mine)) {
            showMessageLater(message);
          }
        }

        @Override
        public void ended(String log) {
          if (isCurrent(mine)) {
            showMessageLater(log.trim().isEmpty() ? NbBundle.getMessage(PreviewElement.class, "MSG_PreviewFailed") : log);
          }
        }
      });
      final Dimension size = wanted;
      session.resize(size.width, size.height);
      SwingUtilities.invokeLater(() -> status.setText(NbBundle.getMessage(PreviewElement.class, "MSG_PreviewStarting")));
      watch();
    } catch (Exception e) {
      showMessageLater(NbBundle.getMessage(PreviewElement.class, "MSG_PreviewError", e.toString()));
    }
  }

  private synchronized boolean isCurrent(int mine) {
    return mine == generation;
  }

  /** Vigila los .class del proyecto mientras la pestana se ve: al compilar, la pantalla se reinicia. */
  private synchronized void watch() {
    if (showing && watcher == null && target != null && !target.watchRoots.isEmpty()) {
      watcher = new ClassWatcher(target.watchRoots, () -> restartTask.schedule(0));
      watcher.start();
    }
  }

  private void showMessageLater(String message) {
    SwingUtilities.invokeLater(() -> showMessage(message));
  }

  /** El jar del plugin: ahi vive {@link LiveRenderer}, que se agrega al classpath del proyecto. */
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
