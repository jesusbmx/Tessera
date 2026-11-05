package tessera.test;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.image.BufferedImage;
import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import tessera.components.FlexibleColumnRenderer;
import tessera.components.HighlightColumnRenderer;
import tessera.components.LabelListCell;
import tessera.kit.Button;
import tessera.kit.Buttons;
import tessera.kit.Card;
import tessera.kit.Check;
import tessera.kit.Col;
import tessera.kit.Combo;
import tessera.kit.Footer;
import tessera.kit.Form;
import tessera.kit.Grid;
import tessera.kit.Header;
import tessera.kit.Hint;
import tessera.kit.Input;
import tessera.kit.Label;
import tessera.kit.Menu;
import tessera.kit.MenuButton;
import tessera.kit.MultiChoiceButton;
import tessera.kit.ChoiceButton;
import tessera.kit.Page;
import tessera.kit.Pages;
import tessera.kit.Radio;
import tessera.kit.Row;
import tessera.kit.Scroll;
import tessera.kit.Separator;
import tessera.kit.Spinner;
import tessera.kit.Split;
import tessera.kit.Stack;
import tessera.kit.Switch;
import tessera.kit.Table;
import tessera.kit.Tabs;
import tessera.kit.TextArea;
import tessera.kit.Toggle;
import tessera.theme.Theme;
import tessera.theme.Variant;

/** Que el kit se arme, se acomode y se dibuje en cada tema, con los colores del tema en su lugar. */
final class KitTests {

  private KitTests() {
  }

  static void run(Checks t) {
    factories(t);
    metricsLayoutButtons(t);
    buttonStyles(t);
    variants(t);
    renderers(t);
    screens(t);
    Theme.light().install();
  }

  /** Crear cada contenedor y widget no debe fallar. */
  private static void factories(Checks t) {
    t.group("Kit: se crean sin fallar");
    Theme.light().install();
    t.noThrow("Row, Col, Stack, Grid, Form", () -> {
      Row.gap(6).padding(4).children(Label.text("a"), Label.text("b")).glue();
      Col.gap(6).fill(Label.text("a")).fillBoth(Label.text("b"));
      Stack.fill(Label.text("a"), Label.text("b"));
      Stack.center(Label.text("a"));
      Grid.gap(6).row(Label.text("a"), Label.text("b")).row(Label.text("c"), Label.text("d"));
      Form.create().row("Nombre", Input.text()).rowTop("Notas", Scroll.of(Input.area(3)));
    });
    t.noThrow("Card, Header, Footer, Page, Pages, Tabs, Split, Separator", () -> {
      Card.create("Titulo").subtitle("ayuda").children(Label.text("a"));
      Card.section("Seccion");
      Card.create();
      Header.create("Titulo", "Subtitulo");
      Footer.of(Button.of("Cancelar"), Button.primary("Guardar"));
      Page.create().top(Label.text("a")).center(Label.text("b")).bottom(Label.text("c"));
      Pages.create().page("uno", Label.text("1")).page("dos", Label.text("2")).show("dos");
      Tabs.create().tab("Uno", Label.text("1")).tab("Dos", Label.text("2"));
      Split.horizontal(Label.text("a"), Label.text("b")).divider(6).weight(0.5);
      Split.vertical(Label.text("a"), Label.text("b"));
      Separator.horizontal();
      Separator.vertical();
    });
    t.noThrow("Label, Hint y Button en todas las variantes", () -> {
      for (Variant v : Variant.values()) {
        Label.of(v, "texto");
        Button.of(v, "boton");
        Button.outline(v, "boton");
      }
      Label.title("t");
      Label.field("f");
      Label.muted("m");
      Hint.of("ayuda");
      Hint.ofHtml("<b>ayuda</b>");
      Button.secondary("a");
      Button.success("a");
      Button.danger("a");
      Button.warning("a");
      Button.info("a");
      Button.light("a");
      Button.dark("a");
    });
    t.noThrow("Check, Radio, Toggle, Switch, Input, Password, TextArea, Combo, Spinner", () -> {
      Check.of("c");
      Radio.group(Radio.of("a"), Radio.of("b"));
      Toggle.group(Toggle.of("a"), Toggle.of("b"));
      Switch.of("s");
      Input.text().placeholder("x").columns(10);
      Input.password();
      final TextArea area = Input.area(3).columns(20).wrap().editable(true);
      area.placeholder("aqui");
      Combo.of("a", "b", "c");
      Combo.empty();
      Spinner.integer(1, 0, 10, 1);
      Spinner.decimal(1.5, 0, 10, 0.5);
    });
    t.noThrow("ChoiceButton, MultiChoiceButton, MenuButton, Menu", () -> {
      ChoiceButton.<String>of().option("a", "Uno").option("b", "Dos", "ayuda").setValue("a");
      MultiChoiceButton.<String>of("Varios").option("a", "Uno").option("b", "Dos").setChecked("a", true);
      MenuButton.of("Mas").item("Hacer algo", () -> { }).separator();
      Menu.create().item(tessera.kit.MenuItem.of("Algo", () -> { }));
    });
    t.noThrow("Table y Scroll", () -> {
      Table.of(new DefaultTableModel(new Object[][] {{"a", 1}}, new Object[] {"Nombre", "Cant"})).rowHeight(24);
      Scroll.of(Input.area(3)).noBorder().vertical().step(8);
      Scroll.of(Input.area(3)).rounded().height(80);
    });
  }

  /** Lo que antes vivia en Kit: medidas, fuente, lo que un componente pide a su contenedor, y agrupar botones. */
  private static void metricsLayoutButtons(Checks t) {
    t.group("Metrics, Layout y Buttons.group");
    Theme.light().install();
    t.eq("Metrics.px escala como FlatLaf", com.formdev.flatlaf.util.UIScale.scale(10), tessera.theme.Metrics.px(10));
    t.eq("Metrics.px(0)", 0, tessera.theme.Metrics.px(0));
    final java.awt.Font bold = tessera.theme.Metrics.font(16, true);
    final java.awt.Font plain = tessera.theme.Metrics.font(12, false);
    t.ok("Metrics.font en negrita", bold.isBold());
    t.ok("Metrics.font normal", !plain.isBold());
    t.eq("Metrics.font respeta el tamano (escalado)", (float) com.formdev.flatlaf.util.UIScale.scale(16f), bold.getSize2D());

    final javax.swing.JTextField field = new javax.swing.JTextField();
    t.ok("Layout.fill devuelve el mismo componente", tessera.kit.Layout.fill(field, true, false) == field);
    t.eq("Layout.fill horizontal", "x", field.getClientProperty("kit.fill"));
    tessera.kit.Layout.fill(field, true, true);
    t.eq("Layout.fill en los dos ejes", "xy", field.getClientProperty("kit.fill"));
    tessera.kit.Layout.fill(field, false, false);
    t.eq("Layout.fill sin ejes", "", field.getClientProperty("kit.fill"));
    t.eq("width() guarda el ancho pedido", 120, Check.of("c").width(120).getClientProperty("kit.width"));

    final javax.swing.JRadioButton a = new javax.swing.JRadioButton("a");
    final javax.swing.JRadioButton b = new javax.swing.JRadioButton("b");
    Buttons.group(a, b);
    a.setSelected(true);
    b.setSelected(true);
    t.ok("Buttons.group: elegir uno deselecciona el otro", b.isSelected() && !a.isSelected());
  }

  /** El estilo de un boton segun su variante: el de Button y el de Buttons (para botones de Swing). */
  @SuppressWarnings("unchecked")
  private static void buttonStyles(Checks t) {
    t.group("Botones: estilo por variante");
    Theme.light().install();
    final String key = com.formdev.flatlaf.FlatClientProperties.STYLE;

    java.util.Map<String, Object> style = (java.util.Map<String, Object>) Button.danger("x").getClientProperty(key);
    t.color("Button.danger: fondo de la variante", Variant.DANGER.color(), (Color) style.get("background"));
    t.color("Button.danger: texto que contrasta", Variant.DANGER.onColor(), (Color) style.get("foreground"));
    t.color("Button.danger: hover", Variant.DANGER.hover(), (Color) style.get("hoverBackground"));

    style = (java.util.Map<String, Object>) Button.primary("x").getClientProperty(key);
    t.color("Button.primary: fondo Theme.palette().primary", Theme.palette().primary, (Color) style.get("background"));

    style = (java.util.Map<String, Object>) Button.outline(Variant.SUCCESS, "x").getClientProperty(key);
    t.eq("outline: fondo transparente", 0, ((Color) style.get("background")).getAlpha());
    t.color("outline: texto del color de la variante", Variant.SUCCESS.color(), (Color) style.get("foreground"));
    t.color("outline: relleno al pasar el mouse", Variant.SUCCESS.color(), (Color) style.get("hoverBackground"));

    final javax.swing.JButton swing = new javax.swing.JButton("x");
    Buttons.variant(swing, Variant.WARNING);
    style = (java.util.Map<String, Object>) swing.getClientProperty(key);
    t.color("Buttons.variant sobre un JButton", Variant.WARNING.color(), (Color) style.get("background"));
    Buttons.primary(swing);
    style = (java.util.Map<String, Object>) swing.getClientProperty(key);
    t.color("Buttons.primary sobre un JButton", Theme.palette().primary, (Color) style.get("background"));
    Buttons.outline(swing, Variant.INFO);
    style = (java.util.Map<String, Object>) swing.getClientProperty(key);
    t.eq("Buttons.outline sobre un JButton", 0, ((Color) style.get("background")).getAlpha());
  }

  /** onColor, hover y el regreso a los colores de fabrica. */
  private static void variants(Checks t) {
    t.group("Variant");
    Theme.light().install();
    t.color("texto sobre PRIMARY: blanco", Color.WHITE, Variant.PRIMARY.onColor());
    t.color("texto sobre WARNING (amarillo): oscuro", "#212529", Variant.WARNING.onColor());
    t.color("texto sobre INFO (celeste): oscuro", "#212529", Variant.INFO.onColor());
    t.color("texto sobre DANGER: blanco", Color.WHITE, Variant.DANGER.onColor());
    t.ok("hover es mas oscuro que el color", lum(Variant.SUCCESS.hover()) < lum(Variant.SUCCESS.color()));
    t.ok("pressed es mas oscuro que hover", lum(Variant.SUCCESS.pressed()) < lum(Variant.SUCCESS.hover()));
    Variant.SUCCESS.setColor(Color.decode("#123456"));
    t.color("setColor cambia la variante", "#123456", Variant.SUCCESS.color());
    Variant.SUCCESS.setColor(null);
    t.color("setColor(null) vuelve al de fabrica", "#198754", Variant.SUCCESS.color());
  }

  private static double lum(Color c) {
    return 0.2126 * c.getRed() + 0.7152 * c.getGreen() + 0.0722 * c.getBlue();
  }

  /** Las celdas normales de una tabla usan los colores de la tabla (claros u oscuros), no blanco y negro fijos. */
  private static void renderers(Checks t) {
    t.group("Renderizadores de tabla");
    for (boolean dark : new boolean[] {false, true}) {
      if (dark) {
        Theme.dark().install();
      } else {
        Theme.light().install();
      }
      final String tema = dark ? "dark" : "light";
      final JTable table = new JTable(new DefaultTableModel(new Object[][] {{"a", "b"}}, new Object[] {"Uno", "Dos"}));

      final FlexibleColumnRenderer flexible = new FlexibleColumnRenderer();
      java.awt.Component c = flexible.getTableCellRendererComponent(table, "a", false, false, 0, 0);
      t.color(tema + ": FlexibleColumnRenderer, fondo de la tabla", table.getBackground(), c.getBackground());
      t.color(tema + ": FlexibleColumnRenderer, texto de la tabla", table.getForeground(), c.getForeground());

      final HighlightColumnRenderer highlight = new HighlightColumnRenderer();
      c = highlight.getTableCellRendererComponent(table, "a", false, false, 0, 0);
      t.color(tema + ": HighlightColumnRenderer, fondo de la tabla", table.getBackground(), c.getBackground());
      t.color(tema + ": HighlightColumnRenderer, texto de la tabla", table.getForeground(), c.getForeground());

      c = flexible.getTableCellRendererComponent(table, "a", true, false, 0, 0);
      t.color(tema + ": seleccionada usa la seleccion de la tabla", table.getSelectionBackground(), c.getBackground());
    }
  }

  /** Una pantalla entera en cada tema: se dibuja, no esta en blanco y cada pieza trae el color del tema. */
  private static void screens(Checks t) {
    for (int theme = 0; theme < 4; theme++) {
      final String name;
      switch (theme) {
        case 1:
          Theme.dark().install();
          name = "dark";
          break;
        case 2:
          Theme.ubuntu().install();
          name = "ubuntu";
          break;
        case 3:
          Theme.ubuntuDark().install();
          name = "ubuntuDark";
          break;
        default:
          Theme.light().install();
          name = "light";
      }
      t.group("Pantalla de ejemplo: " + name);

      final DefaultListModel<String> model = new DefaultListModel<>();
      model.addElement("Primer servicio");
      model.addElement("Segundo servicio");
      model.addElement("Tercer servicio");
      final JList<String> list = new JList<>(model);
      list.setCellRenderer(new LabelListCell());
      list.setSelectedIndex(1);

      final Header header = Header.create("Titulo", "Subtitulo");
      final Card card = Card.create("Conexion").subtitle("Como se conecta").padding(10, 12, 12, 12).gap(8)
          .children(
              Row.gap(8).children(Button.primary("Guardar"), Button.of("Cancelar"), Button.danger("Borrar")),
              Row.gap(12).children(Check.of("Casilla"), Radio.of("Opcion"), Switch.of("Interruptor")),
              Input.text().placeholder("Escribe aqui"),
              Scroll.of(Input.area(3)).rounded().height(70));
      final JPanel body = new JPanel(new BorderLayout(8, 8));
      body.setBorder(javax.swing.BorderFactory.createEmptyBorder(8, 8, 8, 8));
      body.add(card, BorderLayout.NORTH);
      body.add(Row.gap(8).fillBoth(Scroll.of(list), Scroll.of(Table.of(new DefaultTableModel(
          new Object[][] {{"A-1", 10}, {"A-2", 24}}, new Object[] {"Codigo", "Cant"})))), BorderLayout.CENTER);
      final JPanel root = new JPanel(new BorderLayout());
      root.add(header, BorderLayout.NORTH);
      root.add(body, BorderLayout.CENTER);

      final BufferedImage image = Shots.paint(root, 560, 470);
      t.ok("se dibuja con detalle (no esta en blanco)", Shots.distinctColors(image) > 30);
      t.color("banner: fondo Theme.palette().banner", Theme.palette().banner, Shots.at(image, root, header, header.getWidth() - 6, header.getHeight() / 2));
      t.color("fondo de la pantalla: el del tema", Theme.panelBackground(), Shots.at(image, root, body, 2, 2));
      t.color("tarjeta: relleno Theme.palette().surface", Theme.palette().surface, Shots.at(image, root, card, card.getWidth() - 6, card.getHeight() / 2));

      final java.awt.Rectangle selected = list.getCellBounds(1, 1);
      t.color("lista: fila seleccionada Theme.palette().rowSelected", Theme.palette().rowSelected,
          Shots.at(image, root, list, selected.x + 3, selected.y + selected.height / 2));
      final java.awt.Rectangle odd = list.getCellBounds(0, 0);
      t.color("lista: fila 1 Theme.palette().rowOdd", Theme.palette().rowOdd,
          Shots.at(image, root, list, odd.x + 3, odd.y + odd.height / 2));
    }
    Theme.light().install();
  }
}
