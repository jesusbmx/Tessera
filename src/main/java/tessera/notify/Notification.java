package tessera.notify;

import com.formdev.flatlaf.FlatClientProperties;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dialog;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Window;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import tessera.theme.Theme;

/**
 * Notificación flotante estilo Google Chrome / Windows Action Center: tarjeta
 * oscura, letras blancas, ícono y acción opcional. A diferencia de {@link Toast},
 * puede haber varias visibles al mismo tiempo: se apilan en la esquina inferior
 * derecha de la pantalla y se van acomodando conforme llegan o se cierran.
 *
 * <p>Cada notificación lleva un {@code id} obligatorio (como el id de
 * {@code NotificationManager.notify()} en Android o de {@code displayNotification}
 * en @notifee/react-native): si se muestra otra con el mismo id mientras la
 * anterior sigue visible, la reemplaza en su misma posición en vez de apilar
 * una tarjeta nueva.
 *
 * <p>Los callbacks ({@code setOnClick}, {@code setOnAction}, {@code setOnDismiss}) reciben la propia
 * notificación, igual que {@code Snackbar.setAction} en Android recibe la vista:
 * con ella se puede cerrar ({@link #dismiss()}), leer su {@link #getId() id} o
 * actualizar su progreso. La notificación ya se está cerrando cuando el
 * callback corre, así que {@code dismiss()} ahí no hace nada.
 *
 * <p>Un mensaje largo se parte en líneas (word wrap) y, pasadas unas 8 líneas, muestra
 * scroll vertical en vez de agrandar la tarjeta; el título va en una línea y se recorta con "...".
 * Todas las tarjetas miden el mismo ancho estándar, sea cual sea su contenido.
 *
 * <pre>
 * new Notification.Builder("print.done")
 *         .setTitle("Impresión")
 *         .setMessage("La etiqueta se envió correctamente.")
 *         .setStyle(Notification.Style.SUCCESS)
 *         .setDuration(Notification.LENGTH_LONG)
 *         .setOnAction("Ver", notification -> abrirHistorial())
 *         .setOnClick(notification -> abrirHistorial())
 *         .show();
 * </pre>
 */
public class Notification {

  public enum Style { NORMAL, SUCCESS, ERROR, WARNING }

  /** Igual que Android Snackbar: se cierra sola tras un lapso corto. */
  public static final int LENGTH_SHORT = 4000;
  /** Igual que Android Snackbar: se cierra sola tras un lapso largo. */
  public static final int LENGTH_LONG = 8000;
  /** Igual que Android Snackbar: no se cierra sola, requiere acción del usuario. */
  public static final int LENGTH_INDEFINITE = -1;

  /**
   * setShape()/setOpacity() requieren que el GraphicsDevice soporte translucidez
   * por píxel, lo cual depende de que haya un compositing window manager activo.
   * Windows (DWM) y macOS (Quartz) siempre lo soportan; en Linux depende del
   * gestor de ventanas (muchos WMs livianos sin picom/compton no lo soportan),
   * y sin este chequeo esas llamadas lanzan UnsupportedOperationException y la
   * notificación nunca llega a mostrarse. Con el fallback se ve un diálogo
   * rectangular sin animación de fundido en vez de nada.
   */
  static final boolean SHAPE_SUPPORTED = isTranslucencySupported(GraphicsDevice.WindowTranslucency.PERPIXEL_TRANSPARENT);
  static final boolean OPACITY_SUPPORTED = isTranslucencySupported(GraphicsDevice.WindowTranslucency.TRANSLUCENT);

  private static boolean isTranslucencySupported(GraphicsDevice.WindowTranslucency kind) {
    try {
      final GraphicsDevice device = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
      return device.isWindowTranslucencySupported(kind);
    } catch (Exception e) {
      return false;
    }
  }

  /** Fondo de la tarjeta: siempre el mismo, para todos los estilos. Lo único que cambia de color es el ícono. */
  static final Color CARD_BG = new Color(0, 0, 0);

  // Colores de acento del ícono, tomados de la paleta estándar de MDBootstrap
  // (mdbootstrap.com/docs/standard/content-styles/colors): SUCCESS→success, ERROR→danger, WARNING→warning.
  // Package-private: también los usa Toast, para que ambos se vean igual.
  static final Color SUCCESS_ACCENT = new Color(20, 164, 77);
  static final Color ERROR_ACCENT = new Color(220, 76, 100);
  static final Color WARNING_ACCENT = new Color(228, 161, 27);

  /** Nombre de la app, mostrado en letras pequeñas arriba del título (como en Chrome). */
  private static volatile String sAppName;

  private String mId;
  private String mTitle;
  private String mMessage;
  private int mDuration = LENGTH_SHORT;
  private Style mStyle = Style.NORMAL;
  private Image mIcon;
  private Consumer<Notification> mOnClick;
  private String mActionText;
  private Consumer<Notification> mOnAction;
  private Consumer<Notification> mOnDismiss;
  private boolean mDismissible = true;
  private Integer mProgressMax;
  private int mProgressValue;

  private Notification() {
  }

  /** Nombre de la app a mostrar en letras pequeñas arriba de cada notificación (estilo Chrome). */
  public static void setAppName(String appName) {
    sAppName = appName;
  }

  /**
   * Constructor de notificaciones. El {@code id} es obligatorio y se pasa al
   * crear el Builder: identifica la notificación, y mostrar otra con el mismo
   * id mientras la anterior sigue visible reemplaza a la anterior en vez de
   * mostrar una nueva.
   */
  public static final class Builder {
    private final String mId;
    private String mTitle;
    private String mMessage;
    private int mDuration = LENGTH_SHORT;
    private Style mStyle = Style.NORMAL;
    private Image mIcon;
    private Consumer<Notification> mOnClick;
    private String mActionText;
    private Consumer<Notification> mOnAction;
    private Consumer<Notification> mOnDismiss;
    private boolean mDismissible = true;
    private Integer mProgressMax;
    private int mProgressValue;

    /** @throws IllegalArgumentException si {@code id} es {@code null} o está vacío */
    public Builder(String id) {
      if (id == null || id.isEmpty()) {
        throw new IllegalArgumentException("Notification id is required");
      }
      mId = id;
    }

    public Builder setTitle(String title) {
      mTitle = title;
      return this;
    }

    public Builder setMessage(String message) {
      mMessage = message;
      return this;
    }

    public Builder setStyle(Style style) {
      mStyle = style;
      return this;
    }

    public Builder setDuration(int duration) {
      mDuration = duration;
      return this;
    }

    /** Ícono personalizado (p.ej. el logo de la app). Si no se define se dibuja uno según el estilo. */
    public Builder setIcon(Image icon) {
      mIcon = icon;
      return this;
    }

    /**
     * Acción al hacer clic en cualquier parte del cuerpo de la notificación (no en el botón de acción).
     * Recibe la propia notificación; ya se está cerrando cuando corre.
     */
    public Builder setOnClick(Consumer<Notification> onClick) {
      mOnClick = onClick;
      return this;
    }

    /**
     * Botón de acción opcional, igual que {@code Snackbar.setAction} de Android: el
     * texto del botón y la acción, que recibe la propia notificación. Ej.
     * {@code setOnAction("Ver", notification -> openHistory())}. Al usarla la
     * notificación se cierra y luego corre la acción.
     */
    public Builder setOnAction(String actionText, Consumer<Notification> onAction) {
      mActionText = actionText;
      mOnAction = onAction;
      return this;
    }

    /**
     * Se ejecuta si la notificación se cierra sin que el usuario haga clic ni use la acción (X o timeout).
     * Recibe la propia notificación.
     */
    public Builder setOnDismiss(Consumer<Notification> onDismiss) {
      mOnDismiss = onDismiss;
      return this;
    }

    /**
     * Si es {@code false}, oculta el botón de cerrar (X) para que el usuario no
     * pueda descartar la notificación sin usar la acción — útil para avisos
     * obligatorios (ej. una actualización forzosa). Por defecto es
     * descartable ({@code true}).
     */
    public Builder setDismissible(boolean dismissible) {
      mDismissible = dismissible;
      return this;
    }

    /**
     * Agrega una barra de progreso opcional (ej. para reflejar el avance de una
     * tarea larga). Usa la {@link Notification} devuelta por {@link #show()} para
     * actualizar el valor conforme avanza la tarea, y {@link Notification#complete}
     * para reemplazarla por el mensaje final.
     */
    public Builder setProgress(int value, int max) {
      mProgressValue = value;
      mProgressMax = Math.max(max, 1);
      return this;
    }

    /**
     * Muestra la notificación. Devuelve la {@link Notification} mostrada, que permite
     * actualizar la barra de progreso (si se definió con {@link #setProgress})
     * o cerrarla manualmente; se puede descartar si no se necesita.
     */
    public Notification show() {
      final Notification n = new Notification();
      n.mId = mId;
      n.mTitle = mTitle;
      n.mMessage = mMessage;
      n.mDuration = mDuration;
      n.mStyle = mStyle;
      n.mIcon = mIcon;
      n.mOnClick = mOnClick;
      n.mActionText = mActionText;
      n.mOnAction = mOnAction;
      n.mOnDismiss = mOnDismiss;
      n.mDismissible = mDismissible;
      n.mProgressMax = mProgressMax;
      n.mProgressValue = mProgressValue;
      return Center.getInstance().enqueue(n);
    }
  }

  /** Color de fondo del círculo del ícono. Para NORMAL es el sutil translúcido de siempre (no hay "tipo" que resaltar). */
  private static Color accentColorFor(Style style) {
    switch (style) {
      case SUCCESS: return SUCCESS_ACCENT;
      case ERROR: return ERROR_ACCENT;
      case WARNING: return WARNING_ACCENT;
      default: return new Color(255, 255, 255, 40);
    }
  }

  private String glyph() {
    return glyphFor(mStyle);
  }

  /** Cruz del error y del boton de cerrar. Inter no tiene este simbolo (Java no lo busca en otra fuente): se dibuja con lineas. */
  private static final String GLYPH_CROSS = "✕";

  private static String glyphFor(Style style) {
    switch (style) {
      case SUCCESS: return "✓";
      case ERROR: return GLYPH_CROSS;
      case WARNING: return "!";
      default: return "i";
    }
  }

  /** Cruz de dos lineas centrada en (centerX, centerY), con el color de "g2" y brazos de "half" pixeles. */
  private static void paintCross(Graphics2D g2, int centerX, int centerY, int half, float width) {
    g2.setStroke(new BasicStroke(width, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
    g2.drawLine(centerX - half, centerY - half, centerX + half, centerY + half);
    g2.drawLine(centerX - half, centerY + half, centerX + half, centerY - half);
  }

  // ------------------------------------------------------------------ //
  //     Actuar sobre una notificación ya mostrada
  // ------------------------------------------------------------------ //
  //
  // Es la misma instancia que devuelve show() y que reciben los callbacks
  // (setOnClick, setOnAction). Cada llamada se resuelve por id: si la
  // notificación ya se cerró se ignora; si otra con el mismo id la reemplazó,
  // actúa sobre la nueva.

  /** Id con el que se creó la notificación (ver {@link Builder#Builder(String)}). */
  public String getId() {
    return mId;
  }

  /** Actualiza el valor de la barra de progreso. No hace nada si la notificación no se creó con {@link Builder#setProgress}. */
  public void setProgress(int value, int max) {
    setProgress(value, max, null);
  }

  /**
   * Igual que {@link #setProgress(int, int)} pero reemplaza el texto de la
   * barra (por defecto "value / max") por {@code label}, ej. "12.3 / 45.6 MB".
   */
  public void setProgress(int value, int max, String label) {
    runOnEdt(() -> {
      final Card card = Center.getInstance().find(mId);
      if (card != null) card.updateProgress(value, max, label);
    });
  }

  /**
   * Oculta la barra de progreso y reemplaza título, mensaje y estilo,
   * reiniciando el auto-cierre con la duración indicada (puede ser
   * {@link #LENGTH_INDEFINITE}). Útil para marcar como terminada una
   * notificación de progreso, ej. "Prompt copiado al portapapeles".
   */
  public void complete(String title, String message, Style style, int duration) {
    complete(title, message, style, duration, null, null);
  }

  /**
   * Igual que {@link #complete(String, String, Style, int)} pero además
   * agrega (o reemplaza) el botón de acción, ej. para que el usuario
   * dispare la acción manualmente en vez de que ocurra sola — útil junto
   * con {@link #LENGTH_INDEFINITE} para esperar a que el usuario actúe.
   */
  public void complete(String title, String message, Style style, int duration,
                        String actionText, Consumer<Notification> onAction) {
    runOnEdt(() -> {
      final Card card = Center.getInstance().find(mId);
      if (card != null) card.complete(title, message, style, duration, actionText, onAction);
    });
  }

  /** Cierra la notificación manualmente antes de que expire. */
  public void dismiss() {
    runOnEdt(() -> {
      final Card card = Center.getInstance().find(mId);
      if (card != null) card.close();
    });
  }

  private static void runOnEdt(Runnable task) {
    if (SwingUtilities.isEventDispatchThread()) {
      task.run();
    } else {
      SwingUtilities.invokeLater(task);
    }
  }

  // ------------------------------------------------------------------ //
  //     Card: la ventana individual de la notificación
  // ------------------------------------------------------------------ //

  private static final class Card {

    private static final int WINDOW_RADIUS = 12;
    // Ancho del texto. Con márgenes, ícono y espacio da CARD_WIDTH = 364 px: el mismo ancho que una
    // notificación nativa de Windows 11, para que no se vea más angosta al lado de ellas.
    private static final int TEXT_WRAP_WIDTH = 278;
    // Alto máximo del mensaje, en líneas: si el texto es más largo aparece scroll vertical
    private static final int MESSAGE_MAX_LINES = 8;
    private static final int ICON_SIZE = 36;
    // Padding único para el borde de la tarjeta, el espacio bajo el app name y el espacio
    // arriba del botón (el de abajo lo da el propio borde inferior, al ser la última fila).
    private static final int CARD_PADDING = 16;
    // Espacio entre el ícono y el texto
    private static final int ICON_GAP = 18;
    // Ancho estándar: todas las notificaciones miden lo mismo, sea cual sea su contenido
    private static final int CARD_WIDTH = CARD_PADDING * 2 + ICON_SIZE + ICON_GAP + TEXT_WRAP_WIDTH;
    private static final float FADE_STEP = 0.12f;
    private static final int FADE_REFRESH_RATE = 20;
    private static final int MOVE_REFRESH_RATE = 15;
    private static final int MOVE_STEPS = 10;

    private final Notification mSpec;
    private final JDialog mWindow;
    private Timer mDismissTimer;
    private Timer mMoveTimer;
    private volatile boolean mClosed = false;
    private boolean mActioned = false;
    private JLabel mTitleLabel;
    private JTextArea mMessageArea;
    private JScrollPane mMessageScroll;
    // Pausa el auto-cierre mientras el mouse está sobre la tarjeta (también sobre el texto y su scroll)
    private final MouseAdapter mHover = new MouseAdapter() {
      @Override
      public void mouseEntered(MouseEvent e) {
        if (mDismissTimer != null) {
          mDismissTimer.stop();
        }
      }

      @Override
      public void mouseExited(MouseEvent e) {
        // Pasar a un componente hijo (texto, scroll) también dispara "exited": solo cuenta si salió de la ventana
        if (!mClosed && mDismissTimer != null && !isPointerInside()) {
          mDismissTimer.restart();
        }
      }
    };
    private JProgressBar mProgressBar;
    private JLabel mProgressLabel;
    private IconBadge mIconBadge;
    private JPanel mContent;
    private ActionButton mActionButton;
    private int mNextRow;

    Card(Notification spec) {
      mSpec = spec;
      mWindow = new JDialog((Window) null);
      buildUI();
    }

    private void buildUI() {
      mWindow.setUndecorated(true);
      mWindow.setAlwaysOnTop(true);
      mWindow.setFocusableWindowState(false);
      mWindow.setModalityType(JDialog.ModalityType.MODELESS);
      // Sin esto, un diálogo APPLICATION_MODAL abierto en la app (p.ej. ColumnRulesDialog,
      // ProgressDialog) bloquea los clics de esta ventana aunque se vea encima por el alwaysOnTop.
      mWindow.setModalExclusionType(Dialog.ModalExclusionType.APPLICATION_EXCLUDE);
      mWindow.addComponentListener(new ComponentAdapter() {
        @Override
        public void componentResized(ComponentEvent e) {
          if (!SHAPE_SUPPORTED) return;
          mWindow.setShape(new RoundRectangle2D.Double(0, 0,
                  mWindow.getWidth(), mWindow.getHeight(), WINDOW_RADIUS, WINDOW_RADIUS));
        }
      });

      final JPanel content = new JPanel(new GridBagLayout());
      content.setBackground(CARD_BG);
      content.setBorder(BorderFactory.createEmptyBorder(CARD_PADDING, CARD_PADDING, CARD_PADDING, CARD_PADDING));
      mWindow.setContentPane(content);
      mContent = content;

      final MouseAdapter clickToOpen = new MouseAdapter() {
        @Override
        public void mouseClicked(MouseEvent e) {
          if (mSpec.mOnClick != null) {
            mActioned = true;
            close();
            mSpec.mOnClick.accept(mSpec);
          }
        }
      };
      if (mSpec.mOnClick != null) {
        content.setCursor(new Cursor(Cursor.HAND_CURSOR));
      }
      content.addMouseListener(clickToOpen);

      if (mSpec.mDismissible) {
        final CloseButton closeButton = new CloseButton();
        closeButton.addActionListener(e -> close());
        final GridBagConstraints closeConstraints = new GridBagConstraints();
        closeConstraints.gridx = 2;
        closeConstraints.gridy = 0;
        closeConstraints.anchor = GridBagConstraints.NORTHEAST;
        closeConstraints.insets = new Insets(-6, 4, 0, -6);
        content.add(closeButton, closeConstraints);
      }

      int row = 0;
      if (sAppName != null && !sAppName.isEmpty()) {
        final JLabel appNameLabel = new JLabel(sAppName);
        appNameLabel.setForeground(Color.WHITE);
        appNameLabel.setFont(new Font(Theme.fontFamily(), Font.PLAIN, 11));
        appNameLabel.addMouseListener(clickToOpen);
        final GridBagConstraints appNameConstraints = new GridBagConstraints();
        appNameConstraints.gridx = 0;
        appNameConstraints.gridy = row;
        appNameConstraints.gridwidth = 2;
        appNameConstraints.fill = GridBagConstraints.HORIZONTAL;
        appNameConstraints.anchor = GridBagConstraints.WEST;
        appNameConstraints.insets = new Insets(0, 0, CARD_PADDING, 0);
        content.add(appNameLabel, appNameConstraints);
        row++;
      }

      final int iconRow = row;
      final int titleRow = row++;
      final int messageRow = row++;

      final JLabel iconLabel = buildIcon();
      iconLabel.addMouseListener(clickToOpen);
      final GridBagConstraints iconConstraints = new GridBagConstraints();
      iconConstraints.gridx = 0;
      iconConstraints.gridy = iconRow;
      iconConstraints.gridheight = messageRow - iconRow + 1;
      iconConstraints.anchor = GridBagConstraints.WEST;
      iconConstraints.insets = new Insets(0, 0, 0, ICON_GAP);
      content.add(iconLabel, iconConstraints);

      mTitleLabel = new JLabel(mSpec.mTitle);
      mTitleLabel.setForeground(Color.WHITE);
      mTitleLabel.setFont(new Font(Theme.fontFamily(), Font.BOLD, 14));
      mTitleLabel.addMouseListener(clickToOpen);
      // Mínimo 0: con el ancho fijo de la tarjeta un título largo se recorta con "..." en vez de ensancharla
      mTitleLabel.setMinimumSize(new java.awt.Dimension(0, mTitleLabel.getPreferredSize().height));
      final GridBagConstraints titleConstraints = new GridBagConstraints();
      titleConstraints.gridx = 1;
      titleConstraints.gridy = titleRow;
      titleConstraints.weightx = 1;
      titleConstraints.fill = GridBagConstraints.HORIZONTAL;
      titleConstraints.anchor = GridBagConstraints.WEST;
      titleConstraints.insets = new Insets(0, 0, 4, 0);
      content.add(mTitleLabel, titleConstraints);

      // Word wrap: un JTextArea transparente dentro de un JScrollPane (solo scroll vertical, si hace falta)
      mMessageArea = new JTextArea();
      mMessageArea.setForeground(Color.WHITE);
      mMessageArea.setFont(new Font(Theme.fontFamily(), Font.PLAIN, 13));
      mMessageArea.setOpaque(false);
      mMessageArea.setEditable(false);
      mMessageArea.setFocusable(false);
      mMessageArea.setHighlighter(null);
      mMessageArea.setBorder(null);
      mMessageArea.setLineWrap(true);
      mMessageArea.setWrapStyleWord(true);
      mMessageArea.setCursor(mSpec.mOnClick != null ? new Cursor(Cursor.HAND_CURSOR) : Cursor.getDefaultCursor());
      mMessageArea.addMouseListener(clickToOpen);

      mMessageScroll = new JScrollPane(mMessageArea,
              ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
      mMessageScroll.setBorder(null);
      mMessageScroll.setOpaque(false);
      mMessageScroll.getViewport().setOpaque(false);
      mMessageScroll.getViewport().addMouseListener(clickToOpen);
      mMessageScroll.getVerticalScrollBar().setUnitIncrement(16);
      mMessageScroll.getVerticalScrollBar().setOpaque(false);
      // Barra discreta, translúcida sobre el fondo negro
      mMessageScroll.getVerticalScrollBar().putClientProperty(FlatClientProperties.STYLE,
              "showButtons:false; trackArc:999; thumbArc:999; track:#00000000; thumb:#FFFFFF4D; hoverThumbColor:#FFFFFF80; pressedThumbColor:#FFFFFFA6");
      setMessage(mSpec.mMessage);

      final GridBagConstraints messageConstraints = new GridBagConstraints();
      messageConstraints.gridx = 1;
      messageConstraints.gridy = messageRow;
      messageConstraints.gridwidth = 2;
      messageConstraints.fill = GridBagConstraints.NONE;
      messageConstraints.anchor = GridBagConstraints.WEST;
      messageConstraints.insets = new Insets(3, 0, 0, 0);
      content.add(mMessageScroll, messageConstraints);

      if (mSpec.mProgressMax != null) {
        mProgressBar = new JProgressBar(0, mSpec.mProgressMax);
        mProgressBar.setValue(mSpec.mProgressValue);
        // El string nativo del JProgressBar (setStringPainted) depende del L&F
        // (con FlatLaf no se pinta, o queda invisible sobre esta tarjeta oscura),
        // así que el texto se muestra aparte con mProgressLabel, que sí controlamos.
        mProgressBar.setForeground(Color.WHITE);
        mProgressBar.setBackground(new Color(255, 255, 255, 40));
        mProgressBar.setBorderPainted(false);
        final int progressRow = row++;
        final GridBagConstraints progressConstraints = new GridBagConstraints();
        progressConstraints.gridx = 1;
        progressConstraints.gridy = progressRow;
        progressConstraints.gridwidth = 2;
        progressConstraints.fill = GridBagConstraints.HORIZONTAL;
        progressConstraints.insets = new Insets(8, 0, 0, 0);
        content.add(mProgressBar, progressConstraints);

        mProgressLabel = new JLabel(mSpec.mProgressValue + " / " + mSpec.mProgressMax, JLabel.RIGHT);
        mProgressLabel.setForeground(Color.WHITE);
        mProgressLabel.setFont(new Font(Theme.fontFamily(), Font.PLAIN, 11));
        final int progressLabelRow = row++;
        final GridBagConstraints progressLabelConstraints = new GridBagConstraints();
        progressLabelConstraints.gridx = 1;
        progressLabelConstraints.gridy = progressLabelRow;
        progressLabelConstraints.gridwidth = 2;
        progressLabelConstraints.fill = GridBagConstraints.HORIZONTAL;
        progressLabelConstraints.insets = new Insets(2, 0, 0, 0);
        content.add(mProgressLabel, progressLabelConstraints);
      }

      if (mSpec.mActionText != null) {
        mActionButton = new ActionButton(mSpec.mActionText);
        mActionButton.addActionListener(e -> {
          mActioned = true;
          close();
          if (mSpec.mOnAction != null) {
            mSpec.mOnAction.accept(mSpec);
          }
        });
        final GridBagConstraints actionConstraints = new GridBagConstraints();
        actionConstraints.gridx = 1;
        actionConstraints.gridy = row;
        actionConstraints.gridwidth = 2;
        actionConstraints.anchor = GridBagConstraints.EAST;
        actionConstraints.insets = new Insets(CARD_PADDING, 0, 0, 0);
        content.add(mActionButton, actionConstraints);
        row++;
      }
      mNextRow = row;

      installHover(content);
      fitWindow();
    }

    private JLabel buildIcon() {
      if (mSpec.mIcon != null) {
        final Image scaled = mSpec.mIcon.getScaledInstance(ICON_SIZE, ICON_SIZE, Image.SCALE_SMOOTH);
        return new JLabel(new ImageIcon(scaled));
      }
      mIconBadge = new IconBadge(mSpec.glyph(), accentColorFor(mSpec.mStyle));
      return mIconBadge;
    }

    /**
     * Pone el texto del mensaje y ajusta su alto: lo que ocupa el texto ya partido en líneas, con tope de
     * {@link #MESSAGE_MAX_LINES}; pasado ese tope se muestra scroll vertical en vez de agrandar la tarjeta.
     */
    private void setMessage(String message) {
      // Un JTextArea solo entiende "\n" como salto de línea ("\r" se dibuja como un cuadro en algunas fuentes)
      final String text = message == null ? "" : message.replace("\r\n", "\n").replace('\r', '\n');
      mMessageArea.setText(text);
      mMessageArea.setCaretPosition(0);

      // Alto del texto ya partido al ancho de la tarjeta
      mMessageArea.setSize(TEXT_WRAP_WIDTH, Short.MAX_VALUE);
      final int lineHeight = mMessageArea.getFontMetrics(mMessageArea.getFont()).getHeight();
      final int height = Math.max(lineHeight,
              Math.min(mMessageArea.getPreferredSize().height, MESSAGE_MAX_LINES * lineHeight));
      final java.awt.Dimension size = new java.awt.Dimension(TEXT_WRAP_WIDTH, height);
      mMessageScroll.setPreferredSize(size);
      // Mínimo = preferido: con el ancho fijo, GridBagLayout usa tamaños mínimos y no debe encoger el texto
      mMessageScroll.setMinimumSize(size);
      mMessageScroll.getVerticalScrollBar().setValue(0);
    }

    /**
     * Ajusta la ventana: todas las notificaciones miden el mismo ancho ({@link #CARD_WIDTH}), sea cual sea su
     * contenido. El alto sale del contenido. Un título más largo que el ancho se recorta con "..." (el texto
     * completo queda en el tooltip).
     */
    private void fitWindow() {
      mWindow.pack();
      mWindow.setSize(CARD_WIDTH, mWindow.getHeight());
      mWindow.validate();
      mTitleLabel.setToolTipText(
              mTitleLabel.getPreferredSize().width > mTitleLabel.getWidth() ? mTitleLabel.getText() : null);
    }

    /** Escucha el mouse en este componente y en todos sus hijos (texto, scroll, botones). */
    private void installHover(java.awt.Component component) {
      component.addMouseListener(mHover);
      if (component instanceof java.awt.Container) {
        for (java.awt.Component child : ((java.awt.Container) component).getComponents()) {
          installHover(child);
        }
      }
    }

    private boolean isPointerInside() {
      final java.awt.PointerInfo pointer = java.awt.MouseInfo.getPointerInfo();
      return pointer != null && mWindow.getBounds().contains(pointer.getLocation());
    }

    /** Actualiza la barra de progreso. No hace nada si esta tarjeta no se creó con {@link Notification#setProgress}. */
    void updateProgress(int value, int max, String label) {
      if (mProgressBar == null) {
        return;
      }
      final int safeMax = Math.max(max, 1);
      mProgressBar.setMaximum(safeMax);
      mProgressBar.setValue(Math.min(value, safeMax));
      if (mProgressLabel != null) {
        mProgressLabel.setText(label != null ? label : (value + " / " + max));
      }
    }

    /**
     * Oculta la barra de progreso y reemplaza título/mensaje/estilo, ajustando
     * el tamaño de la ventana y reiniciando el auto-cierre con la nueva duración.
     */
    void complete(String title, String message, Style style, int duration,
                  String actionText, Consumer<Notification> onAction) {
      mTitleLabel.setText(title);
      setMessage(message);
      if (mProgressBar != null) {
        mProgressBar.setVisible(false);
      }
      if (mProgressLabel != null) {
        mProgressLabel.setVisible(false);
      }
      if (mIconBadge != null) {
        mIconBadge.setGlyph(glyphFor(style), accentColorFor(style));
      }

      if (actionText != null) {
        if (mActionButton == null) {
          mActionButton = new ActionButton(actionText);
          final GridBagConstraints actionConstraints = new GridBagConstraints();
          actionConstraints.gridx = 1;
          actionConstraints.gridy = mNextRow;
          actionConstraints.gridwidth = 2;
          actionConstraints.anchor = GridBagConstraints.EAST;
          actionConstraints.insets = new Insets(CARD_PADDING, 0, 0, 0);
          mContent.add(mActionButton, actionConstraints);
          installHover(mActionButton);
        } else {
          mActionButton.setText(actionText.toUpperCase());
          for (java.awt.event.ActionListener l : mActionButton.getActionListeners()) {
            mActionButton.removeActionListener(l);
          }
        }
        final Consumer<Notification> action = onAction;
        mActionButton.addActionListener(e -> {
          mActioned = true;
          close();
          if (action != null) {
            action.accept(mSpec);
          }
        });
      }

      fitWindow();
      Center.getInstance().relayout();

      if (duration == LENGTH_INDEFINITE) {
        if (mDismissTimer != null) {
          mDismissTimer.stop();
        }
      } else {
        startDismissTimer(duration);
      }
    }

    int getWidth() {
      return mWindow.getWidth();
    }

    int getHeight() {
      return mWindow.getHeight();
    }

    Point getLocation() {
      return mWindow.getLocation();
    }

    /**
     * Descarta la tarjeta de inmediato (sin fundido ni {@code onDismiss}), porque
     * otra notificación con el mismo id la está reemplazando.
     */
    void discard() {
      mClosed = true;
      if (mDismissTimer != null) {
        mDismissTimer.stop();
      }
      if (mMoveTimer != null) {
        mMoveTimer.stop();
      }
      mWindow.setVisible(false);
      mWindow.dispose();
    }

    void showAnimated(Point at, boolean fadeIn) {
      mWindow.setLocation(at);

      if (OPACITY_SUPPORTED && fadeIn) {
        mWindow.setOpacity(0f);
        mWindow.setVisible(true);

        final Timer timer = new Timer(FADE_REFRESH_RATE, null);
        timer.addActionListener(new java.awt.event.ActionListener() {
          private float opacity = 0f;

          @Override
          public void actionPerformed(java.awt.event.ActionEvent e) {
            opacity += FADE_STEP;
            mWindow.setOpacity(Math.min(opacity, 1f));
            if (opacity >= 1f) {
              timer.stop();
            }
          }
        });
        timer.start();
      } else {
        // Sin soporte de translucidez (típico en Linux sin compositor) o al
        // reemplazar otra con el mismo id: se muestra de golpe, sin fundido.
        mWindow.setVisible(true);
      }

      if (mSpec.mDuration != LENGTH_INDEFINITE) {
        startDismissTimer(mSpec.mDuration);
      }
    }

    private void startDismissTimer(int delay) {
      if (mDismissTimer != null) {
        mDismissTimer.stop();
      }
      mDismissTimer = new Timer(delay, e -> close());
      mDismissTimer.setRepeats(false);
      mDismissTimer.start();
    }

    void animateTo(Point target) {
      if (mMoveTimer != null) {
        mMoveTimer.stop();
      }
      final Point start = mWindow.getLocation();
      if (start.equals(target)) {
        return;
      }

      mMoveTimer = new Timer(MOVE_REFRESH_RATE, null);
      mMoveTimer.addActionListener(new java.awt.event.ActionListener() {
        private int step = 0;

        @Override
        public void actionPerformed(java.awt.event.ActionEvent e) {
          step++;
          final double t = Math.min(1.0, step / (double) MOVE_STEPS);
          final int x = (int) (start.x + (target.x - start.x) * t);
          final int y = (int) (start.y + (target.y - start.y) * t);
          mWindow.setLocation(x, y);
          if (t >= 1.0) {
            mMoveTimer.stop();
          }
        }
      });
      mMoveTimer.start();
    }

    private void close() {
      if (mClosed) {
        return;
      }
      mClosed = true;
      if (mDismissTimer != null) {
        mDismissTimer.stop();
      }
      if (!mActioned && mSpec.mOnDismiss != null) {
        mSpec.mOnDismiss.accept(mSpec);
      }
      Center.getInstance().dismiss(this);
      fadeOut();
    }

    private void fadeOut() {
      if (!OPACITY_SUPPORTED) {
        mWindow.setVisible(false);
        mWindow.dispose();
        return;
      }

      final Timer timer = new Timer(FADE_REFRESH_RATE, null);
      timer.addActionListener(new java.awt.event.ActionListener() {
        private float opacity = 1f;

        @Override
        public void actionPerformed(java.awt.event.ActionEvent e) {
          opacity -= FADE_STEP;
          mWindow.setOpacity(Math.max(opacity, 0f));
          if (opacity <= 0f) {
            timer.stop();
            mWindow.setVisible(false);
            mWindow.dispose();
          }
        }
      });
      timer.start();
    }
  }

  /** Círculo relleno con el color de acento del estilo y una letra/símbolo blanco al centro. */
  private static final class IconBadge extends JLabel {

    private String mGlyph;
    private Color mColor;

    IconBadge(String glyph, Color color) {
      mGlyph = glyph;
      mColor = color;
      setPreferredSize(new java.awt.Dimension(Card.ICON_SIZE, Card.ICON_SIZE));
      setFont(new Font(Theme.fontFamily(), Font.BOLD, 16));
    }

    /** Cambia el glifo y el color de fondo (ej. al pasar de "en progreso" a "✓" verde al completar). */
    void setGlyph(String glyph, Color color) {
      mGlyph = glyph;
      mColor = color;
      repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
      final Graphics2D g2 = (Graphics2D) g.create();
      g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
      g2.setColor(mColor);
      g2.fillOval(0, 0, getWidth(), getHeight());

      g2.setColor(Color.WHITE);
      if (GLYPH_CROSS.equals(mGlyph)) {
        paintCross(g2, getWidth() / 2, getHeight() / 2, Math.round(getWidth() * 0.17f), 2.2f);
      } else {
        g2.setFont(getFont());
        final java.awt.FontMetrics fm = g2.getFontMetrics();
        final int tx = (getWidth() - fm.stringWidth(mGlyph)) / 2;
        final int ty = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();
        g2.drawString(mGlyph, tx, ty);
      }
      g2.dispose();
    }
  }

  /** Botón de acción con forma de píldora, estilo Chrome/Snackbar. */
  private static final class ActionButton extends JButton {

    ActionButton(String text) {
      super(text.toUpperCase());
      setFocusPainted(false);
      setBorderPainted(false);
      setContentAreaFilled(false);
      setOpaque(false);
      setForeground(Color.WHITE);
      setFont(new Font(Theme.fontFamily(), Font.BOLD, 12));
      setBorder(BorderFactory.createEmptyBorder(6, 14, 6, 14));
      setCursor(new Cursor(Cursor.HAND_CURSOR));
      setRolloverEnabled(true);
    }

    @Override
    protected void paintComponent(Graphics g) {
      final Graphics2D g2 = (Graphics2D) g.create();
      g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
      final int alpha = getModel().isPressed() ? 110 : getModel().isRollover() ? 80 : 45;
      g2.setColor(new Color(255, 255, 255, alpha));
      g2.fillRoundRect(0, 0, getWidth(), getHeight(), getHeight(), getHeight());
      g2.dispose();
      super.paintComponent(g);
    }
  }

  /** Botón circular pequeño con una "×", para cerrar la notificación manualmente. */
  private static final class CloseButton extends JButton {

    CloseButton() {
      super();
      setFocusPainted(false);
      setBorderPainted(false);
      setContentAreaFilled(false);
      setOpaque(false);
      setForeground(new Color(230, 230, 230));
      setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
      setPreferredSize(new java.awt.Dimension(20, 20));
      setCursor(new Cursor(Cursor.HAND_CURSOR));
      setRolloverEnabled(true);
    }

    @Override
    protected void paintComponent(Graphics g) {
      final Graphics2D g2 = (Graphics2D) g.create();
      g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
      if (getModel().isRollover()) {
        final int alpha = getModel().isPressed() ? 90 : 55;
        g2.setColor(new Color(255, 255, 255, alpha));
        g2.fillOval(0, 0, getWidth(), getHeight());
      }
      g2.setColor(getForeground());
      paintCross(g2, getWidth() / 2, getHeight() / 2, 3, 1.5f);
      g2.dispose();
    }
  }

  // ------------------------------------------------------------------ //
  //     Center: cola global que apila las notificaciones visibles
  // ------------------------------------------------------------------ //

  private static final class Center {

    private static final int MARGIN = 16;
    private static final int GAP = 10;
    private static final Center INSTANCE = new Center();

    private final List<Card> mVisible = new ArrayList<>();

    static Center getInstance() {
      return INSTANCE;
    }

    Notification enqueue(Notification spec) {
      Runnable task = () -> {
        final Card card = new Card(spec);
        final int existing = indexOf(spec.mId);
        if (existing >= 0) {
          // Mismo id: la nueva toma el lugar exacto de la anterior en el stack.
          final Card old = mVisible.get(existing);
          mVisible.set(existing, card);
          card.showAnimated(old.getLocation(), false);
          old.discard();
        } else {
          final Rectangle bounds = screenBounds();
          final Point at = new Point(
                  bounds.x + bounds.width - MARGIN - card.getWidth(),
                  bounds.y + bounds.height - MARGIN - card.getHeight());
          mVisible.add(card);
          card.showAnimated(at, true);
        }
        relayout();
      };
      if (SwingUtilities.isEventDispatchThread()) {
        task.run();
      } else {
        // Bloquea brevemente al hilo llamador (en vez del invokeLater "fire and
        // forget" de antes) para que, al devolver la notificación, la Card ya exista
        // y el llamador pueda actualizar el progreso sin esperas.
        try {
          SwingUtilities.invokeAndWait(task);
        } catch (Exception e) {
          // Sin Card no hay nada que actualizar; la notificación simplemente ignora las llamadas.
        }
      }
      return spec;
    }

    /** Tarjeta visible con ese id, o {@code null}. Solo se llama desde el EDT. */
    Card find(String id) {
      final int index = indexOf(id);
      return index >= 0 ? mVisible.get(index) : null;
    }

    private int indexOf(String id) {
      for (int i = 0; i < mVisible.size(); i++) {
        final Card card = mVisible.get(i);
        if (!card.mClosed && card.mSpec.mId.equals(id)) {
          return i;
        }
      }
      return -1;
    }

    void dismiss(Card card) {
      Runnable task = () -> {
        mVisible.remove(card);
        relayout();
      };
      if (SwingUtilities.isEventDispatchThread()) {
        task.run();
      } else {
        SwingUtilities.invokeLater(task);
      }
    }

    private void relayout() {
      final Rectangle bounds = screenBounds();
      int y = bounds.y + bounds.height - MARGIN;
      for (int i = mVisible.size() - 1; i >= 0; i--) {
        final Card card = mVisible.get(i);
        y -= card.getHeight();
        final int x = bounds.x + bounds.width - MARGIN - card.getWidth();
        card.animateTo(new Point(x, y));
        y -= GAP;
      }
    }

    private Rectangle screenBounds() {
      return GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds();
    }
  }

  public static void main(String... args) {
    final JFrame frame = new JFrame();
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setSize(400, 250);

    final JButton fire = new JButton("Mostrar notificaciones");
    fire.addActionListener(e -> {
      new Notification.Builder("demo.success")
              .setTitle("Impresión")
              .setMessage("La etiqueta se envió correctamente a la impresora.")
              .setStyle(Style.SUCCESS)
              .setDuration(Notification.LENGTH_SHORT)
              .show();

      new Notification.Builder("demo.error")
              .setTitle("Error de conexión")
              .setMessage("No se pudo conectar con el servidor de plantillas.")
              .setStyle(Style.ERROR)
              .setDuration(Notification.LENGTH_LONG)
              .setOnAction("Reintentar", notification -> System.out.println("reintentando..."))
              .show();

      new Notification.Builder("demo.update")
              .setTitle("Actualización disponible")
              .setMessage("Hay una nueva versión de la app lista para instalar.")
              .setStyle(Style.NORMAL)
              .setDuration(Notification.LENGTH_INDEFINITE)
              .setOnClick(notification -> System.out.println("abrir ventana de actualización"))
              .show();
    });

    frame.add(fire);
    frame.setVisible(true);
  }
}
