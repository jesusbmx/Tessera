# Recetas

Pantallas y situaciones completas, listas para copiar y adaptar. Todas siguen la convencion de [screens.md](screens.md) (la logica arriba y la
UI al final, campos justo encima del metodo que los arma) y estan **compiladas contra la libreria**.

En los ejemplos se omiten los `import`. Recuerda que deben ser **explicitos** para las clases del kit (`import tessera.kit.Label;`, no
`java.awt.*`), porque `Label`, `Button`, `List` y `TextArea` chocan con AWT (y `Stack` con `java.util`).

| Receta | Muestra |
|---|---|
| [1. Dialogo de inicio de sesion](#1-dialogo-de-inicio-de-sesion) | [`Form`](../reference/kit/containers/form.md), [`Password`](../reference/kit/widgets/password.md), `onEnter`, validacion con [`ErrorProvider`](../reference/components/forms.md) |
| [2. Lista y detalle](#2-lista-y-detalle) | [`Split`](../reference/kit/containers/split.md), [`Table`](../reference/kit/widgets/table.md) + [`BaseTableModel`](../reference/components/tables.md#basetablemodel), `onSelect` |
| [3. Asistente de pasos](#3-asistente-de-pasos) | [`Pages`](../reference/kit/containers/pages.md), [`Header`](../reference/kit/containers/header.md) que cambia, botones Atras / Siguiente |
| [4. Pantalla de ajustes](#4-pantalla-de-ajustes) | `Card.section`, [`Switch`](../reference/kit/widgets/switch.md), [`Combo`](../reference/kit/widgets/combo.md), [`Spinner`](../reference/kit/widgets/spinner.md), [`Grid`](../reference/kit/containers/grid.md), [`Toast`](../reference/notify/toast.md) |
| [5. Tarea larga con progreso](#5-tarea-larga-con-progreso) | [`Notification`](../reference/notify/notification.md) con barra de progreso y `SwingWorker` |
| [6. App que vive en la bandeja](#6-app-que-vive-en-la-bandeja) | [`TrayDock`](../reference/app/traydock.md), [`WindowPrefs`](../reference/app/windowprefs.md), [`Theme`](../reference/theme/theme.md) |

## 1. Dialogo de inicio de sesion

**Usa:** [`Col`](../reference/kit/containers/col.md) · [`Form`](../reference/kit/containers/form.md) · [`Card`](../reference/kit/containers/card.md) · [`Header`](../reference/kit/containers/header.md) · [`Footer`](../reference/kit/containers/footer.md) · [`Page`](../reference/kit/containers/page.md) · [`Button`](../reference/kit/widgets/button.md) · [`Input`](../reference/kit/widgets/input.md) · [`Password`](../reference/kit/widgets/password.md)

Un dialogo modal con dos campos, Enter para entrar y validacion que marca el campo vacio.

```java
public class LoginDialog extends JDialog {

  private final ErrorProvider errors = new ErrorProvider();
  private boolean accepted;

  public LoginDialog(Window owner) {
    super(owner, "Iniciar sesion", ModalityType.APPLICATION_MODAL);
    buildUI();
    pack();
    setLocationRelativeTo(owner);
  }

  /** true si el usuario entro (para que quien lo abrio sepa que hacer). */
  public boolean isAccepted() {
    return accepted;
  }

  private void login() {
    errors.clear();                                          // quita los errores de la vez anterior
    if (txtUser.getText().trim().isEmpty()) {
      errors.setError(txtUser, "Escribe tu usuario");
      return;                                                // un solo error por pasada: setError mueve el foco
    }
    if (txtPass.getPassword().length == 0) {
      errors.setError(txtPass, "Escribe tu clave");
      return;
    }
    accepted = true;
    dispose();
  }

  // ---------------------------------------------------------------------
  // UI: widgets y construcción de la pantalla
  // Cada bloque lleva sus campos (widgets) justo encima del método que lo arma:
  // campos del bloque -> método del bloque. Empieza en buildUI() y sigue el orden
  // en que se usan. La lógica (eventos, datos, etc.) va fuera de esta zona.
  // ---------------------------------------------------------------------

  private final Button btnCancel = Button.of("Cancelar").onClick(this::dispose);
  private final Button btnLogin = Button.primary("Entrar").onClick(this::login);

  private void buildUI() {
    setContentPane(Page.create()
        .top(Header.create("Bienvenido", "Inicia sesion para continuar"))
        .center(Col.gap(10).padding(12).children(credentialsBlock()))
        .bottom(Footer.of(btnCancel, btnLogin)));
    getRootPane().setDefaultButton(btnLogin);
  }

  private final Input txtUser = Input.text().placeholder("Usuario").columns(22);
  private final Password txtPass = Input.password().placeholder("Clave").columns(22).onEnter(e -> login());

  private Card credentialsBlock() {
    return Card.create().children(
        Form.create()
            .row("Usuario", txtUser)
            .row("Clave", txtPass));
  }
}
```

Uso:

```java
final LoginDialog dialog = new LoginDialog(frame);
dialog.setVisible(true);                                     // bloquea hasta que se cierra
if (dialog.isAccepted()) { /* seguir */ }
```

## 2. Lista y detalle

**Usa:** [`Col`](../reference/kit/containers/col.md) · [`Form`](../reference/kit/containers/form.md) · [`Card`](../reference/kit/containers/card.md) · [`Scroll`](../reference/kit/containers/scroll.md) · [`Split`](../reference/kit/containers/split.md) · [`Label`](../reference/kit/widgets/label.md) · [`Table`](../reference/kit/widgets/table.md)

Una tabla a la izquierda y el detalle de la fila elegida a la derecha, con una barra que se arrastra entre las dos.

```java
public class ClientsPanel extends JPanel {

  private static final class Client {
    final String name;
    final String city;

    Client(String name, String city) {
      this.name = name;
      this.city = city;
    }
  }

  private static final class ClientModel extends BaseTableModel<Client> {
    ClientModel() {
      super("Nombre", "Ciudad");
    }

    @Override
    public Object getValueAt(int row, int col) {
      final Client client = getRow(row);
      return col == 0 ? client.name : client.city;
    }
  }

  private final ClientModel model = new ClientModel();

  public ClientsPanel() {
    buildUI();
    model.setItems(Arrays.asList(new Client("Ana", "Lima"), new Client("Luis", "Quito"), new Client("Marta", "Bogota")));
  }

  private void showSelected() {
    final int row = table.getSelectedRow();
    final Client client = row < 0 ? null : model.getRow(table.convertRowIndexToModel(row));
    lblName.setText(client == null ? "-" : client.name);
    lblCity.setText(client == null ? "-" : client.city);
  }

  // ---------------------------------------------------------------------
  // UI: widgets y construcción de la pantalla
  // Cada bloque lleva sus campos (widgets) justo encima del método que lo arma:
  // campos del bloque -> método del bloque. Empieza en buildUI() y sigue el orden
  // en que se usan. La lógica (eventos, datos, etc.) va fuera de esta zona.
  // ---------------------------------------------------------------------

  private final Table table = Table.of(model).rowHeight(28).singleSelection().onSelect(this::showSelected);

  private void buildUI() {
    setLayout(new BorderLayout());
    add(Split.horizontal(Scroll.of(table), detailBlock()).divider(280), BorderLayout.CENTER);
  }

  private final Label lblName = Label.text("-");
  private final Label lblCity = Label.text("-");

  private Col detailBlock() {
    return Col.gap(10).padding(12).children(
        Card.create("Detalle").children(
            Form.create()
                .row("Nombre", lblName)
                .row("Ciudad", lblCity)));
  }
}
```

Puntos clave: `Scroll.of(table)` (la tabla siempre va en un `Scroll`), `onSelect` (se dispara una vez por cambio de seleccion) y
`convertRowIndexToModel` para cuando la tabla este ordenada o filtrada.

## 3. Asistente de pasos

**Usa:** [`Col`](../reference/kit/containers/col.md) · [`Form`](../reference/kit/containers/form.md) · [`Card`](../reference/kit/containers/card.md) · [`Header`](../reference/kit/containers/header.md) · [`Footer`](../reference/kit/containers/footer.md) · [`Page`](../reference/kit/containers/page.md) · [`Pages`](../reference/kit/containers/pages.md) · [`Hint`](../reference/kit/widgets/hint.md) · [`Button`](../reference/kit/widgets/button.md) · [`Input`](../reference/kit/widgets/input.md) · [`Combo`](../reference/kit/widgets/combo.md)

Varios pasos en el mismo lugar con `Pages`; el `Header` cambia de descripcion y los botones se adaptan al paso.

```java
public class WizardDialog extends JDialog {

  private static final String[] STEPS = {"name", "type", "done"};
  private int step;

  public WizardDialog(Window owner) {
    super(owner, "Nuevo servicio", ModalityType.APPLICATION_MODAL);
    buildUI();
    goTo(0);
    pack();
    setLocationRelativeTo(owner);
  }

  private void goTo(int index) {
    step = index;
    pages.show(STEPS[index]);
    header.subtitle("Paso " + (index + 1) + " de " + STEPS.length);
    btnBack.setEnabled(index > 0);
    btnNext.setText(index == STEPS.length - 1 ? "Terminar" : "Siguiente");
  }

  private void back() {
    goTo(step - 1);
  }

  private void next() {
    if (step == STEPS.length - 1) {
      dispose();
    } else {
      goTo(step + 1);
    }
  }

  // ---------------------------------------------------------------------
  // UI: widgets y construcción de la pantalla
  // Cada bloque lleva sus campos (widgets) justo encima del método que lo arma:
  // campos del bloque -> método del bloque. Empieza en buildUI() y sigue el orden
  // en que se usan. La lógica (eventos, datos, etc.) va fuera de esta zona.
  // ---------------------------------------------------------------------

  private final Header header = Header.create("Nuevo servicio");
  private final Button btnBack = Button.of("Atras").onClick(this::back);
  private final Button btnNext = Button.primary("Siguiente").onClick(this::next);

  private void buildUI() {
    setContentPane(Page.create()
        .top(header)
        .center(pages())
        .bottom(Footer.of(btnBack, btnNext))
        .minWidth(420));
  }

  private final Input txtName = Input.text().placeholder("Nombre del servicio");
  private final Combo<String> cbType = Combo.of("Manual", "Automatico");
  private final Pages pages = Pages.create();

  private Pages pages() {
    return pages
        .page("name", Col.gap(10).padding(12).children(
            Card.create("Nombre").children(Form.create().row("Nombre", txtName))))
        .page("type", Col.gap(10).padding(12).children(
            Card.create("Tipo").children(Form.create().row("Inicio", cbType))))
        .page("done", Col.gap(10).padding(12).children(
            Card.create("Listo").children(Hint.of("Revisa los datos y presiona Terminar."))));
  }
}
```

## 4. Pantalla de ajustes

**Usa:** [`Col`](../reference/kit/containers/col.md) · [`Grid`](../reference/kit/containers/grid.md) · [`Card`](../reference/kit/containers/card.md) · [`Header`](../reference/kit/containers/header.md) · [`Footer`](../reference/kit/containers/footer.md) · [`Page`](../reference/kit/containers/page.md) · [`Scroll`](../reference/kit/containers/scroll.md) · [`Label`](../reference/kit/widgets/label.md) · [`Hint`](../reference/kit/widgets/hint.md) · [`Button`](../reference/kit/widgets/button.md) · [`Check`](../reference/kit/widgets/check.md) · [`Switch`](../reference/kit/widgets/switch.md) · [`Combo`](../reference/kit/widgets/combo.md) · [`Spinner`](../reference/kit/widgets/spinner.md) · [`Toast`](../reference/notify/toast.md)

Varios bloques compactos con `Card.section`, interruptores y un `Toast` al guardar.

```java
public class SettingsPanel extends JPanel {

  public SettingsPanel() {
    buildUI();
  }

  private void save() {
    final int copies = (Integer) spCopies.getValue();
    final boolean autostart = swAutostart.isSelected();
    // ... guardar `copies`, `autostart` y el tema elegido donde la app guarde su configuracion ...
    Toast.makeToast(SwingUtilities.getWindowAncestor(this), "Ajustes guardados", Toast.Style.SUCCESS).show();
  }

  // ---------------------------------------------------------------------
  // UI: widgets y construcción de la pantalla
  // Cada bloque lleva sus campos (widgets) justo encima del método que lo arma:
  // campos del bloque -> método del bloque. Empieza en buildUI() y sigue el orden
  // en que se usan. La lógica (eventos, datos, etc.) va fuera de esta zona.
  // ---------------------------------------------------------------------

  private final Button btnSave = Button.primary("Guardar").onClick(this::save);

  private void buildUI() {
    setLayout(new BorderLayout());
    add(Page.create()
        .top(Header.create("Ajustes", "Preferencias de la aplicacion"))
        .center(Scroll.of(Col.gap(10).padding(12).children(generalBlock(), printingBlock())).noBorder().vertical())
        .bottom(Footer.of(btnSave))
        .build());
  }

  private final Switch swAutostart = Switch.of("Iniciar con el sistema").selected(true);
  private final Check chkUpdates = Check.of("Buscar actualizaciones al iniciar").selected(true);

  private Card generalBlock() {
    return Card.section("General").children(
        swAutostart,
        chkUpdates,
        Hint.of("Los cambios se aplican la proxima vez que se abra la aplicacion."));
  }

  private final Combo<String> cbTheme = Combo.of("Claro", "Oscuro", "Sistema");
  private final Spinner spCopies = Spinner.integer(1, 1, 99, 1);

  private Card printingBlock() {
    return Card.section("Impresion").children(
        Grid.gap(8)
            .row(Label.field("Tema"), cbTheme.width(180))
            .row(Label.field("Copias"), spCopies.width(80)));
  }
}
```

## 5. Tarea larga con progreso

**Usa:** [`Col`](../reference/kit/containers/col.md) · [`Card`](../reference/kit/containers/card.md) · [`Button`](../reference/kit/widgets/button.md) · [`Notification`](../reference/notify/notification.md)

Un trabajo en segundo plano que informa su avance en una `Notification` con barra de progreso (ver [Avisos](../reference/notify/README.md)).

```java
public class ExportPanel extends JPanel {

  public ExportPanel() {
    buildUI();
  }

  private void export() {
    btnExport.setEnabled(false);
    final Notification n = new Notification.Builder("export.report")
        .setTitle("Exportando")
        .setMessage("reporte.csv")
        .setDuration(Notification.LENGTH_INDEFINITE)       // se queda hasta que termine
        .setDismissible(false)                              // sin X: es un proceso en curso
        .setProgress(0, 100)
        .show();

    new SwingWorker<Void, Integer>() {
      @Override
      protected Void doInBackground() throws Exception {
        for (int i = 1; i <= 100; i++) {
          Thread.sleep(40);                                 // el trabajo real va aqui, fuera del EDT
          publish(i);
        }
        return null;
      }

      @Override
      protected void process(List<Integer> chunks) {
        n.setProgress(chunks.get(chunks.size() - 1), 100);
      }

      @Override
      protected void done() {
        n.complete("Listo", "reporte.csv exportado", Notification.Style.SUCCESS, Notification.LENGTH_LONG);
        btnExport.setEnabled(true);
      }
    }.execute();
  }

  // ---------------------------------------------------------------------
  // UI: widgets y construcción de la pantalla
  // Cada bloque lleva sus campos (widgets) justo encima del método que lo arma:
  // campos del bloque -> método del bloque. Empieza en buildUI() y sigue el orden
  // en que se usan. La lógica (eventos, datos, etc.) va fuera de esta zona.
  // ---------------------------------------------------------------------

  private final Button btnExport = Button.primary("Exportar").onClick(this::export);

  private void buildUI() {
    setLayout(new BorderLayout());
    add(Col.gap(10).padding(12).children(Card.create("Reporte").children(btnExport)), BorderLayout.NORTH);
  }
}
```

## 6. App que vive en la bandeja

**Usa:** [`Col`](../reference/kit/containers/col.md) · [`Header`](../reference/kit/containers/header.md) · [`Page`](../reference/kit/containers/page.md) · [`Button`](../reference/kit/widgets/button.md) · [`Notification`](../reference/notify/notification.md) · [`TrayDock`](../reference/app/traydock.md) · [`WindowPrefs`](../reference/app/windowprefs.md) · [`Theme`](../reference/theme/theme.md)

Cerrar la ventana no cierra la app: queda un icono en la bandeja con menu, la ventana recuerda su tamano y se puede reabrir desde el icono (o
desde el Dock en macOS). Ver [Integracion con el escritorio](../reference/app/README.md).

```java
public class TrayApp {

  private final JFrame frame = new JFrame("Mi App");
  private TrayDock dock;

  public static void main(String[] args) {
    SwingUtilities.invokeLater(() -> new TrayApp().start());
  }

  private void start() {
    Theme.builder().accent("#1976D2").primary("#1976D2").install();   // el tema, una vez y antes de crear ventanas
    WindowPrefs.install("com.miapp.");                                 // paquete de la app: recuerda el tamano de sus ventanas
    Notification.setAppName("Mi App");

    buildUI();
    frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);              // cerrar = esconder
    frame.pack();
    frame.setLocationRelativeTo(null);
    frame.setVisible(true);

    dock = TrayDock.create(trayIcon(), "Mi App")
        .onOpen(this::showWindow)
        .menu(() -> Arrays.asList(
            TrayDock.Item.title("Mi App"),
            TrayDock.Item.separator(),
            TrayDock.Item.of("Abrir", this::showWindow),
            TrayDock.Item.of("Salir", this::exit)));
    dock.install();
    TrayDock.onAppReopened(this::showWindow);                          // macOS: clic en el Dock (Java 9+)
  }

  private Image trayIcon() {
    final BufferedImage image = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
    final Graphics2D g = image.createGraphics();
    g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    g.setColor(Theme.accent());
    g.fillOval(1, 1, 14, 14);
    g.dispose();
    return image;                                                      // en una app real: la imagen de tu recurso
  }

  private void showWindow() {
    SwingUtilities.invokeLater(() -> {                                 // onAppReopened corre fuera del EDT
      frame.setVisible(true);
      frame.setState(Frame.NORMAL);
      frame.toFront();
    });
  }

  private void exit() {
    dock.remove();
    System.exit(0);
  }

  // ---------------------------------------------------------------------
  // UI: widgets y construcción de la pantalla
  // Cada bloque lleva sus campos (widgets) justo encima del método que lo arma:
  // campos del bloque -> método del bloque. Empieza en buildUI() y sigue el orden
  // en que se usan. La lógica (eventos, datos, etc.) va fuera de esta zona.
  // ---------------------------------------------------------------------

  private final Button btnNotify = Button.of("Avisar").onClick(() ->
      new Notification.Builder("tray.demo").setTitle("Mi App").setMessage("Sigo corriendo en la bandeja").show());

  private void buildUI() {
    frame.setContentPane(Page.create()
        .top(Header.create("Mi App", "Cierra la ventana: la app sigue en la bandeja"))
        .center(Col.gap(10).padding(12).children(btnNotify))
        .build());
  }
}
```
