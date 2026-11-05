package tessera.test;

import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatIntelliJLaf;
import java.awt.Color;
import javax.swing.UIManager;
import tessera.theme.Palette;
import tessera.theme.Theme;
import tessera.theme.Variant;

/**
 * Los cuatro temas listos. Los colores esperados estan escritos aqui a proposito: son parte de lo que la libreria
 * promete, y esta prueba avisa si un cambio interno (por ejemplo, mover la paleta de lugar) los altera.
 */
final class ThemeTests {

  private ThemeTests() {
  }

  /** Un tema: nombre, cuantos colores esperamos y si es oscuro. */
  private static final class Expected {
    final String name;
    final boolean dark;
    final String accent;
    final String primary;
    final String panel;
    final String surface;
    final String cardLine;
    final String muted;
    final String line;
    final String rowOdd;
    final String rowEven;
    final String rowSelected;
    final String banner;
    final String onBanner;

    Expected(String name, boolean dark, String... c) {
      this.name = name;
      this.dark = dark;
      this.accent = c[0];
      this.primary = c[1];
      this.panel = c[2];
      this.surface = c[3];
      this.cardLine = c[4];
      this.muted = c[5];
      this.line = c[6];
      this.rowOdd = c[7];
      this.rowEven = c[8];
      this.rowSelected = c[9];
      this.banner = c[10];
      this.onBanner = c[11];
    }
  }

  private static final Expected LIGHT = new Expected("light()", false,
      "#1976D2", "#3B82F6", "#FFFFFF", "#F8FAFC", "#E2E8F0", "#666666", "#E5E5E5",
      "#FAFAFA", "#FFFFFF", "#BBDEFB", "#232F34", "#CCCCCC");
  private static final Expected DARK = new Expected("dark()", true,
      "#4C9AFF", "#3B82F6", "#1E2227", "#272C33", "#3A414A", "#9AA4AF", "#3A414A",
      "#242930", "#1E2227", "#2B4A73", "#14181C", "#D0D5DA");
  private static final Expected UBUNTU = new Expected("ubuntu()", false,
      "#E95420", "#E95420", "#F6F5F4", "#FFFFFF", "#DEDDDA", "#77767B", "#DEDDDA",
      "#FAF9F8", "#FFFFFF", "#F9D3C6", "#2C001E", "#F2E4EC");
  private static final Expected UBUNTU_DARK = new Expected("ubuntuDark()", true,
      "#E95420", "#E95420", "#2D2D2D", "#383838", "#4A4A4A", "#ADADAD", "#4A4A4A",
      "#323232", "#2D2D2D", "#6B3A27", "#2C001E", "#F2E4EC");

  /** Antes de instalar ningun tema: la paleta es Palette.LIGHT, los colores de siempre. */
  static void factory(Checks t) {
    t.group("Valores de fabrica de Theme.palette() (los de Palette.LIGHT)");
    t.color("Theme.palette().primary", "#3B82F6", Theme.palette().primary);
    t.color("Theme.palette().surface", "#F8FAFC", Theme.palette().surface);
    t.color("Theme.palette().cardLine", "#E2E8F0", Theme.palette().cardLine);
    t.color("Theme.palette().muted", "#666666", Theme.palette().muted);
    t.color("Theme.palette().line", "#E5E5E5", Theme.palette().line);
    t.color("Theme.palette().rowOdd", "#FAFAFA", Theme.palette().rowOdd);
    t.color("Theme.palette().rowEven", "#FFFFFF", Theme.palette().rowEven);
    t.color("Theme.palette().rowSelected", "#BBDEFB", Theme.palette().rowSelected);
    t.color("Theme.palette().banner", "#232F34", Theme.palette().banner);
    t.color("Theme.palette().onBanner", "#CCCCCC", Theme.palette().onBanner);
    t.color("Theme.palette().primary es el de Palette.LIGHT", Palette.LIGHT.primary, Theme.palette().primary);
  }

  static void run(Checks t) {
    install(LIGHT).install();
    palette(t, LIGHT);
    install(DARK).install();
    palette(t, DARK);
    install(UBUNTU).install();
    palette(t, UBUNTU);
    install(UBUNTU_DARK).install();
    palette(t, UBUNTU_DARK);

    variants(t);
    switching(t);
    accent(t);
    plainBuilder(t);
    palettes(t);
    Theme.light().install();
  }

  private static Theme.Builder install(Expected e) {
    switch (e.name) {
      case "dark()":
        return Theme.dark();
      case "ubuntu()":
        return Theme.ubuntu();
      case "ubuntuDark()":
        return Theme.ubuntuDark();
      default:
        return Theme.light();
    }
  }

  private static void palette(Checks t, Expected e) {
    t.group("Tema " + e.name);
    t.eq("isDark()", e.dark, Theme.isDark());
    t.eq("look and feel", e.dark ? FlatDarkLaf.class : FlatIntelliJLaf.class, UIManager.getLookAndFeel().getClass());
    t.color("Theme.accent()", e.accent, Theme.accent());
    t.color("Theme.panelBackground()", e.panel, Theme.panelBackground());
    t.color("UIManager Panel.background", e.panel, UIManager.getColor("Panel.background"));
    t.color("Theme.palette().primary", e.primary, Theme.palette().primary);
    t.color("Theme.palette().surface", e.surface, Theme.palette().surface);
    t.color("Theme.palette().cardLine", e.cardLine, Theme.palette().cardLine);
    t.color("Theme.palette().muted", e.muted, Theme.palette().muted);
    t.color("Theme.palette().line", e.line, Theme.palette().line);
    t.color("Theme.palette().rowOdd", e.rowOdd, Theme.palette().rowOdd);
    t.color("Theme.palette().rowEven", e.rowEven, Theme.palette().rowEven);
    t.color("Theme.palette().rowSelected", e.rowSelected, Theme.palette().rowSelected);
    t.color("Theme.palette().banner (banner)", e.banner, Theme.palette().banner);
    t.color("Theme.palette().onBanner (texto del banner)", e.onBanner, Theme.palette().onBanner);
  }

  /** Cada tema trae los colores de las variantes que le tocan, y el resto vuelve a los de fabrica. */
  private static void variants(Checks t) {
    t.group("Variantes por tema");
    Theme.light().install();
    t.color("light: DARK de fabrica", "#212529", Variant.DARK.color());
    t.color("light: SUCCESS de fabrica", "#198754", Variant.SUCCESS.color());
    t.color("light: PRIMARY sigue a Theme.palette().primary", Theme.palette().primary, Variant.PRIMARY.color());

    Theme.dark().install();
    t.color("dark: DARK visible sobre fondo oscuro", "#3A414A", Variant.DARK.color());
    t.color("dark: SUCCESS de fabrica", "#198754", Variant.SUCCESS.color());

    Theme.ubuntu().install();
    t.color("ubuntu: SUCCESS", "#0E8420", Variant.SUCCESS.color());
    t.color("ubuntu: DANGER", "#C7162B", Variant.DANGER.color());
    t.color("ubuntu: WARNING", "#F99B11", Variant.WARNING.color());
    t.color("ubuntu: SECONDARY", "#77767B", Variant.SECONDARY.color());
    t.color("ubuntu: DARK de fabrica (el de dark() no se arrastra)", "#212529", Variant.DARK.color());

    Theme.ubuntuDark().install();
    t.color("ubuntuDark: DARK", "#4A4A4A", Variant.DARK.color());
    t.color("ubuntuDark: SUCCESS", "#26A269", Variant.SUCCESS.color());
    t.color("ubuntuDark: DANGER", "#E0334A", Variant.DANGER.color());

    Theme.light().install();
    t.color("al volver a light: SUCCESS de fabrica", "#198754", Variant.SUCCESS.color());
    t.color("al volver a light: DANGER de fabrica", "#DC3545", Variant.DANGER.color());
    t.color("al volver a light: SECONDARY de fabrica", "#6C757D", Variant.SECONDARY.color());
  }

  /** Pasar de un tema a otro no deja colores del anterior. */
  private static void switching(Checks t) {
    t.group("Cambiar de tema");
    Theme.light().install();
    final Color[] before = {Theme.palette().primary, Theme.palette().surface, Theme.palette().cardLine, Theme.palette().muted, Theme.palette().line, Theme.palette().rowOdd, Theme.palette().rowEven,
        Theme.palette().rowSelected, Theme.palette().banner, Theme.palette().onBanner};
    Theme.ubuntuDark().install();
    Theme.dark().install();
    Theme.ubuntu().install();
    Theme.light().install();
    final Color[] after = {Theme.palette().primary, Theme.palette().surface, Theme.palette().cardLine, Theme.palette().muted, Theme.palette().line, Theme.palette().rowOdd, Theme.palette().rowEven,
        Theme.palette().rowSelected, Theme.palette().banner, Theme.palette().onBanner};
    boolean same = true;
    for (int i = 0; i < before.length; i++) {
      same &= before[i].equals(after[i]);
    }
    t.ok("light -> ubuntuDark -> dark -> ubuntu -> light deja la paleta como estaba", same);
    t.eq("y vuelve al look and feel claro", FlatIntelliJLaf.class, UIManager.getLookAndFeel().getClass());
    t.eq("y isDark() es falso", false, Theme.isDark());
  }

  /** tintControls(): barras, seleccion, casillas y radios con el acento; y se quita al pasar a un tema sin el. */
  private static void accent(Checks t) {
    t.group("Acento en los controles");
    Theme.ubuntu().install();
    t.color("ubuntu: barra de progreso con el acento", "#E95420", UIManager.getColor("ProgressBar.foreground"));
    t.color("ubuntu: seleccion de texto con el acento", "#E95420", UIManager.getColor("TextField.selectionBackground"));

    Theme.ubuntuDark().install();
    t.color("ubuntuDark: barra de progreso con el acento", "#E95420", UIManager.getColor("ProgressBar.foreground"));
    t.color("ubuntuDark: casilla marcada con el acento", "#E95420", UIManager.getColor("CheckBox.icon.selectedBackground"));

    Theme.light().install();
    t.ok("light despues de ubuntuDark: la barra ya no es naranja",
        !"#E95420".equals(Checks.hex(UIManager.getColor("ProgressBar.foreground"))));
    t.ok("light despues de ubuntuDark: la seleccion ya no es naranja",
        !"#E95420".equals(Checks.hex(UIManager.getColor("TextField.selectionBackground"))));
  }

  /** Una Palette es solo datos: se puede leer, no se modifica y se ajusta al instalarla. */
  private static void palettes(Checks t) {
    t.group("Palette");
    t.eq("Palette.DARK es oscura", true, Palette.DARK.dark);
    t.eq("Palette.UBUNTU es clara", false, Palette.UBUNTU.dark);
    t.color("Palette.UBUNTU: acento naranja", "#E95420", Palette.UBUNTU.accent);
    t.color("Palette.UBUNTU_DARK: variante DARK", "#4A4A4A", Palette.UBUNTU_DARK.variants().get(Variant.DARK));
    t.ok("Palette.LIGHT no cambia ninguna variante", Palette.LIGHT.variants().isEmpty());
    boolean readOnly = false;
    try {
      Palette.DARK.variants().put(Variant.SUCCESS, Color.RED);
    } catch (UnsupportedOperationException e) {
      readOnly = true;
    }
    t.ok("las variantes de una Palette son de solo lectura", readOnly);

    // Un tema entero y un ajuste encima: lo que se pide despues manda, y el resto sigue siendo del tema.
    Theme.builder().palette(Palette.UBUNTU).accent("#00897B").install();
    t.color("ajuste: el acento pedido", "#00897B", Theme.accent());
    t.color("ajuste: Theme.palette().surface sigue siendo el del tema", "#FFFFFF", Theme.palette().surface);
    t.color("ajuste: Theme.palette().banner sigue siendo el del tema", "#2C001E", Theme.palette().banner);
    t.color("ajuste: las variantes del tema siguen", "#0E8420", Variant.SUCCESS.color());
    t.color("ajuste: la barra de progreso toma el acento pedido", "#00897B", UIManager.getColor("ProgressBar.foreground"));

    Theme.builder().palette(Palette.DARK).palette(Palette.LIGHT).install();
    t.eq("un segundo palette() reemplaza al primero (look and feel)", FlatIntelliJLaf.class,
        UIManager.getLookAndFeel().getClass());
    t.color("un segundo palette() reemplaza al primero (Theme.palette().surface)", "#F8FAFC", Theme.palette().surface);
    t.color("y no arrastra las variantes del primero", "#212529", Variant.DARK.color());
  }

  /** Theme.builder() sigue como antes: no toca la paleta si no se le pide. */
  private static void plainBuilder(Checks t) {
    t.group("Theme.builder() (sin tema listo)");
    Theme.light().install();
    Theme.builder().accent("#00897B").install();
    t.color("el acento pedido se aplica", "#00897B", Theme.accent());
    t.color("no toca Theme.palette().muted", "#666666", Theme.palette().muted);
    t.color("no toca Theme.palette().surface", "#F8FAFC", Theme.palette().surface);
    t.color("no toca Theme.palette().banner", "#232F34", Theme.palette().banner);
    t.eq("look and feel claro", FlatIntelliJLaf.class, UIManager.getLookAndFeel().getClass());

    Theme.builder().primary("#7B1FA2").surface("#FFFDE7").install();
    t.color("primary pedido", "#7B1FA2", Theme.palette().primary);
    t.color("surface pedido", "#FFFDE7", Theme.palette().surface);
  }
}
