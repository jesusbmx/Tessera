package tessera.notify;

import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.formdev.flatlaf.util.UIScale;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.awt.Window;
import java.awt.geom.RoundRectangle2D;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Consumer;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import tessera.theme.Theme;

/**
 * Mensaje breve y transitorio sobre una ventana o componente.
 *
 * Mismo estilo que {@link Notification}: tarjeta siempre negra con texto
 * blanco. El tipo de mensaje ({@link Style}) solo cambia el ícono de la
 * izquierda y su color. A diferencia de Notification, se muestran de uno en
 * uno (en cola) y centrados sobre su dueño.
 *
 * <pre>
 * Toast.makeToast(frame, "Guardado", Toast.Style.SUCCESS).show();
 *
 * // Con botón de acción, igual que Snackbar.setAction de Android
 * Toast.makeToast(frame, "Archivo eliminado")
 *         .setAction("Deshacer", toast -&gt; restaurar())
 *         .show();
 * </pre>
 */
public class Toast {

  public enum Style {
    NORMAL, SUCCESS, ERROR, WARNING
  };

  public static final int LENGTH_SHORT = 3000;
  public static final int LENGTH_LONG = 6000;
  /** No se cierra solo; queda visible hasta que el usuario le haga clic. */
  public static final int LENGTH_INDEFINITE = -1;

  private static final float MAX_OPACITY = 0.95f;
  private static final float OPACITY_INCREMENT = 0.07f;
  private static final int FADE_REFRESH_RATE = 20;
  /** Tope de espera al fundido de salida antes de pasar al siguiente toast de la cola. */
  private static final int FADE_OUT_TIMEOUT = 1000;

  /** Radio máximo de las esquinas; con una sola línea de texto la tarjeta queda casi en píldora. */
  private static final int MAX_RADIUS = 18;
  /** Ancho máximo del texto: si lo pasa, se parte en varias líneas. */
  private static final int MAX_TEXT_WIDTH = 420;
  private static final int ICON_SIZE = 22;

  /** El tipo NORMAL no resalta nada: su ícono va en blanco suave. */
  private static final Color NORMAL_ACCENT = new Color(230, 230, 230);

  private final JDialog mToast;
  private Component mOwner;
  private String mText;
  private int mDuration;
  private Style mStyle = Style.NORMAL;
  private Color mBackgroundColor = Notification.CARD_BG;
  private Color mForegroundColor = Color.WHITE;
  private String mActionText;
  private Consumer<Toast> mAction;
  private volatile boolean mClosed = false;

  public Toast(Component owner) {
    mToast = new JDialog(ow(owner));
    mOwner = owner;
  }

  static Window ow(Component c) {
    if (c == null) {
      return null;
    } else if (c instanceof JFrame) {
      return (JFrame) c;
    } else if (c instanceof JDialog) {
      return (JDialog) c;
    } else {
      return SwingUtilities.getWindowAncestor(c);
    }
  }

  /** Medida en píxeles escalada según la pantalla (FlatLaf). */
  private static int px(int value) {
    return UIScale.scale(value);
  }

  private static Font dialogFont(int style, int size) {
    return new Font(Theme.fontFamily(), style, Math.round(UIScale.scale((float) size)));
  }

  /** Construye la tarjeta: ícono a la izquierda, texto al centro y acción (si hay) a la derecha. */
  private void createGUI() {
    mToast.setAlwaysOnTop(true);
    mToast.setUndecorated(true);
    mToast.setFocusableWindowState(false);
    mToast.setModalityType(JDialog.ModalityType.MODELESS);
    // Sin esto, un diálogo modal abierto en la app (p.ej. IpScannerDialog)
    // bloquea los clics del botón de acción aunque el toast se vea encima.
    mToast.setModalExclusionType(Dialog.ModalExclusionType.APPLICATION_EXCLUDE);

    final JPanel content = new JPanel(new BorderLayout(px(12), 0));
    content.setBackground(mBackgroundColor);
    content.setBorder(BorderFactory.createEmptyBorder(
            px(12), px(16), px(12), hasAction() ? px(10) : px(18)));
    mToast.setContentPane(content);

    content.add(new JLabel(iconFor(mStyle)), BorderLayout.WEST);
    content.add(createMessage(), BorderLayout.CENTER);

    if (hasAction()) {
      // Contenedor que centra el botón sin estirarlo a lo alto de la tarjeta
      final JPanel actionPane = new JPanel(new GridBagLayout());
      actionPane.setOpaque(false);
      actionPane.add(createActionButton());
      content.add(actionPane, BorderLayout.EAST);
    }

    mToast.pack();

    // Esquinas redondeadas. Requiere translucidez por píxel; sin ella (típico
    // en Linux sin compositor) queda rectangular en vez de fallar.
    if (Notification.SHAPE_SUPPORTED) {
      final int radius = Math.min(mToast.getHeight(), px(MAX_RADIUS * 2));
      mToast.setShape(new RoundRectangle2D.Double(0, 0,
              mToast.getWidth(), mToast.getHeight(), radius, radius));
    }
  }

  /** Ícono del tipo de mensaje: el mismo dibujo, teñido con el color de acento del estilo. */
  private static Icon iconFor(Style style) {
    final String name;
    final Color accent;

    switch (style) {
      case SUCCESS:
        name = "success_green";
        accent = Notification.SUCCESS_ACCENT;
        break;
      case ERROR:
        name = "error_red";
        accent = Notification.ERROR_ACCENT;
        break;
      case WARNING:
        name = "warning_amber";
        accent = Notification.WARNING_ACCENT;
        break;
      default:
        name = "info";
        accent = NORMAL_ACCENT;
    }

    final FlatSVGIcon icon = new FlatSVGIcon("tessera/img/" + name + ".svg", ICON_SIZE, ICON_SIZE);
    icon.setColorFilter(new FlatSVGIcon.ColorFilter(color -> accent));
    return icon;
  }

  /** El texto; si es muy largo se parte en varias líneas en vez de ensanchar la tarjeta. */
  private JLabel createMessage() {
    final JLabel label = new JLabel(mText);
    label.setForeground(mForegroundColor);
    label.setFont(dialogFont(Font.PLAIN, 14));

    final boolean isHtml = mText != null && mText.startsWith("<html>");
    final int maxWidth = px(MAX_TEXT_WIDTH);

    if (!isHtml && label.getPreferredSize().width > maxWidth) {
      label.setText("<html><body style='width:" + maxWidth + "px'>" + escapeHtml(mText) + "</body></html>");
    }
    return label;
  }

  private static String escapeHtml(String text) {
    return text.replace("&", "&amp;")
            .replace("<", "&lt;")
            .replace(">", "&gt;")
            .replace("\n", "<br>");
  }

  /** Botón de acción con forma de píldora, translúcido sobre el fondo negro. */
  private JButton createActionButton() {
    final JButton button = new JButton(mActionText);
    button.setFocusable(false);
    button.setFont(dialogFont(Font.BOLD, 13));
    button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    button.putClientProperty(FlatClientProperties.BUTTON_TYPE, FlatClientProperties.BUTTON_TYPE_ROUND_RECT);
    button.putClientProperty(FlatClientProperties.STYLE,
            "borderWidth:0; focusWidth:0; margin:5,14,5,14; foreground:#FFFFFF"
            + "; background:#FFFFFF3C; hoverBackground:#FFFFFF5A; pressedBackground:#FFFFFF6E");
    button.addActionListener(e -> {
      // Como en Android: al usar la acción el toast se cierra y luego se ejecuta
      close();
      mAction.accept(this);
    });
    return button;
  }

  /** Hay botón de acción solo si se definieron el texto y la acción (ver {@link #setAction}). */
  private boolean hasAction() {
    return mActionText != null && mAction != null;
  }

  /** Cierra el toast de inmediato (p.ej. al hacer clic), sin esperar el fundido. */
  private void close() {
    if (mClosed) return;
    mClosed = true;
    mToast.setVisible(false);
    mToast.dispose();
  }

  /** Espera (sin ocupar el EDT) hasta que el toast se cierre o pase el tiempo. Devuelve si ya se cerró. */
  private boolean awaitClosed(long timeoutMs) throws InterruptedException {
    final long end = System.currentTimeMillis() + timeoutMs;
    while (!mClosed && System.currentTimeMillis() < end) {
      Thread.sleep(25);
    }
    return mClosed;
  }

  private void fadeIn() {
    if (Notification.OPACITY_SUPPORTED) {
      mToast.setOpacity(0f);
    }

    mToast.setLocationRelativeTo(mOwner);
    mToast.setVisible(true);

    if (Notification.OPACITY_SUPPORTED) {
      fade(0f, MAX_OPACITY, null);
    }
  }

  private void fadeOut() {
    if (mClosed) return;

    if (Notification.OPACITY_SUPPORTED) {
      fade(MAX_OPACITY, 0f, this::close);
    } else {
      close();
    }
  }

  /** Cambia la opacidad de {@code from} a {@code to} y al terminar ejecuta {@code onDone}. */
  private void fade(float from, float to, Runnable onDone) {
    final float step = from < to ? OPACITY_INCREMENT : -OPACITY_INCREMENT;
    final float[] opacity = {from};

    final Timer timer = new Timer(FADE_REFRESH_RATE, null);
    timer.addActionListener(e -> {
      if (mClosed) {
        timer.stop();
        return;
      }

      opacity[0] += step;
      final boolean done = step > 0 ? opacity[0] >= to : opacity[0] <= to;
      // El último paso queda exactamente en el destino (no se pasa del tope)
      mToast.setOpacity(done ? to : opacity[0]);

      if (done) {
        timer.stop();
        if (onDone != null) {
          onDone.run();
        }
      }
    });
    timer.start();
  }

  public void show() {
    Service.getService().queue.add(this);
  }

  public void setText(String text) {
    mText = text;
  }

  public void setDuration(int duration) {
    mDuration = duration;
  }

  /** Tipo de mensaje: define el ícono de la izquierda. */
  public void setStyle(Style style) {
    mStyle = style;
  }

  /** Cambia el fondo de la tarjeta (por defecto negro, igual que {@link Notification}). */
  public void setBackground(Color backgroundColor) {
    mBackgroundColor = backgroundColor;
  }

  public void setForeground(Color foregroundColor) {
    mForegroundColor = foregroundColor;
  }

  /**
   * Botón de acción, igual que {@code Snackbar.setAction} de Android: el texto
   * del botón y la acción a ejecutar, que recibe este mismo toast. Al hacer
   * clic el toast se cierra y luego se ejecuta la acción (en el EDT).
   * Con {@code null} en cualquiera de los dos se quita el botón.
   *
   * @return este toast, para encadenar: {@code makeToast(...).setAction(...).show()}
   */
  public Toast setAction(String text, Consumer<Toast> action) {
    mActionText = text;
    mAction = action;
    return this;
  }

  /**
   * Cierra el toast (como {@code Snackbar.dismiss} de Android), desde cualquier
   * hilo. Si todavía está en la cola, ya no se muestra.
   */
  public void dismiss() {
    if (SwingUtilities.isEventDispatchThread()) {
      close();
    } else {
      SwingUtilities.invokeLater(this::close);
    }
  }

  public static Toast makeToast(Component owner, String text) {
    return makeToast(owner, text, LENGTH_SHORT);
  }

  public static Toast makeToast(Component owner, String text, Style style) {
    return makeToast(owner, text, LENGTH_SHORT, style);
  }

  public static Toast makeToast(Component owner, String text, int duration) {
    return makeToast(owner, text, duration, Style.NORMAL);
  }

  public static Toast makeToast(Component owner, String text, int duration, Style style) {
    Toast toast = new Toast(owner);
    toast.mText = text;
    toast.mDuration = duration;
    toast.mStyle = style;
    return toast;
  }

  /** Igual que {@link #makeToast(Component, String, int, Style)} con botón de acción (ver {@link #setAction}). */
  public static Toast makeToast(Component owner, String text, int duration, Style style,
          String actionText, Consumer<Toast> action) {
    return makeToast(owner, text, duration, style).setAction(actionText, action);
  }

  private static class Service {
    private static Service _instance;
    private LinkedBlockingQueue<Toast> queue = new LinkedBlockingQueue<Toast>();
    private Dispatchers[] mDispatchers = new Dispatchers[1];

    private Service() {
    }

    public void start() {
      stop();
      for (int i = 0; i < mDispatchers.length; i++) {
        mDispatchers[i] = new Dispatchers(queue);
        mDispatchers[i].start();
      }
    }

    public void stop() {
      for (int i = 0; i < mDispatchers.length; i++) {
        if (mDispatchers[i] != null) {
          mDispatchers[i].interrupt();
          mDispatchers[i] = null;
        }
      }
    }

    public static Service getService() {
      if (_instance == null) {
        _instance = new Service();
        _instance.start();
      }
      return _instance;
    }
  }

  private static class Dispatchers extends Thread {
    private final BlockingQueue<Toast> queue;
    private volatile boolean quit = false;

    private Dispatchers(BlockingQueue<Toast> queue) {
      this.queue = queue;
      setName("Toast.Dispatchers");
      setPriority(Thread.MIN_PRIORITY);
      setDaemon(true);
    }

    @Override
    public void interrupt() {
      quit = true;
      super.interrupt();
    }

    @Override
    public void run() {
      while (true) {
        Toast t;
        try {
          t = queue.take();
        } catch (InterruptedException ex) {
          if (quit == true) {
            return;
          }
          continue;
        }

        // Lo cerraron (dismiss) mientras esperaba en la cola: ya no se muestra
        if (t.mClosed) {
          continue;
        }

        try {
          // Los componentes Swing se crean y se muestran en el EDT; este hilo
          // solo lleva la cuenta de cuánto tiempo debe verse.
          SwingUtilities.invokeAndWait(() -> {
            t.createGUI();
            t.fadeIn();
          });
          if (t.mDuration != LENGTH_INDEFINITE) {
            // Si el usuario lo cierra antes (botón de acción) no hay que esperar el resto
            if (!t.awaitClosed(t.mDuration)) {
              SwingUtilities.invokeLater(t::fadeOut);
              // Espera a que termine el fundido: así el siguiente no se encima con este
              t.awaitClosed(FADE_OUT_TIMEOUT);
            }
          }
        } catch (Exception ignore) {
        }
      }
    }
  }

  public static void main(String... args) {
    final JFrame frame = new JFrame();
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setSize(new Dimension(520, 300));
    frame.setLayout(new java.awt.FlowLayout());

    for (Style style : Style.values()) {
      final JButton button = new JButton(style.name());
      button.addActionListener(e -> Toast.makeToast(frame,
              "Annotations were successfully saved.", LENGTH_SHORT, style).show());
      frame.add(button);
    }

    final JButton action = new JButton("WITH ACTION");
    action.addActionListener(e -> Toast.makeToast(frame, "Settings were updated.", LENGTH_LONG, Style.SUCCESS)
            .setAction("Open Settings", toast -> System.out.println("open settings"))
            .show());
    frame.add(action);

    frame.setLocationRelativeTo(null);
    frame.setVisible(true);
  }
}
