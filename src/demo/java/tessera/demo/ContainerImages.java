package tessera.demo;

import static tessera.demo.BoxImages.AMBER;
import static tessera.demo.BoxImages.BLUE;
import static tessera.demo.BoxImages.GRAY;
import static tessera.demo.BoxImages.GREEN;
import static tessera.demo.BoxImages.ROSE;
import static tessera.demo.BoxImages.captioned;
import static tessera.demo.BoxImages.card;
import static tessera.demo.BoxImages.page;
import static tessera.demo.BoxImages.save;
import static tessera.demo.BoxImages.zone;

import javax.swing.JComponent;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import tessera.kit.Button;
import tessera.kit.Check;
import tessera.kit.Col;
import tessera.kit.Input;
import tessera.kit.Label;
import tessera.kit.Page;
import tessera.kit.Pages;
import tessera.kit.Radio;
import tessera.kit.Row;
import tessera.kit.Scroll;
import tessera.kit.Separator;
import tessera.kit.Split;
import tessera.kit.Tabs;
import tessera.theme.Icons;
import tessera.theme.Theme;

/**
 * Genera las imagenes de las paginas de Tabs, Split, Pages, Scroll, Separator y Page (docs/img/container-*.png): lo que se
 * puede hacer con cada una. Los ayudantes de dibujo (zone, captioned...) son los de {@link BoxImages}; el titulo de cada
 * tarjeta es el codigo de la imagen. scripts/doc-images.sh la lanza.
 *
 * <pre>
 * java ... tessera.demo.ContainerImages [carpeta]        (por defecto docs/img)
 * </pre>
 */
public final class ContainerImages {

  private ContainerImages() {
  }

  public static void main(String[] args) throws Exception {
    final String dir = args.length > 0 ? args[0] : "docs/img";
    Theme.builder().accent("#1976D2").panelBackground("#F7F6F3").install();

    SwingUtilities.invokeAndWait(() -> {
      save(dir, "container-tabs", tabs());
      save(dir, "container-split", split());
      save(dir, "container-pages", pages());
      save(dir, "container-scroll-modes", scroll());
      save(dir, "container-separator-uses", separator());
      save(dir, "container-page-variants", pageVariants());
    });
    System.exit(0);
  }

  // ------------------------------------------------------------------------ //
  //   T A B S
  // ------------------------------------------------------------------------ //

  private static Col tabContent(String... options) {
    final Col col = Col.gap(6).padding(10);
    for (String option : options) {
      col.children(Check.of(option));
    }
    return col;
  }

  private static JComponent tabs() {
    final Tabs left = Tabs.create()
        .tab("General", tabContent("Iniciar con el sistema", "Buscar actualizaciones"))
        .tab("Impresion", tabContent("Vista previa"))
        .tab("Avanzado", tabContent("Guardar registro"));
    left.setTabPlacement(JTabbedPane.LEFT);

    return page(
        card("Tabs.create().tab(titulo, contenido)",
            Tabs.create()
                .tab("General", tabContent("Opcion 1", "Opcion 2"))
                .tab("Impresion", tabContent("Vista previa"))
                .tab("Avanzado", tabContent("Guardar registro"))),
        card("Tabs.create().tab(titulo, icono, contenido)",
            Tabs.create()
                .tab("General", Icons.of("tessera/img/info.svg", 16), tabContent("Opcion 1", "Opcion 2"))
                .tab("Avanzado", Icons.of("tessera/img/warning_amber.svg", 16), tabContent("Guardar registro"))),
        card("tabs.setTabPlacement(JTabbedPane.LEFT)  ->  pestanas a la izquierda", left.height(110)));
  }

  // ------------------------------------------------------------------------ //
  //   S P L I T
  // ------------------------------------------------------------------------ //

  private static JComponent split() {
    return page(
        card("Split.horizontal(lista, detalle).divider(160)",
            Split.horizontal(zone("lista", BLUE, 22, 10), zone("detalle", GRAY, 22, 10)).divider(160)),
        card("Split.vertical(editor, consola).weight(0.7)",
            Split.vertical(zone("editor", BLUE, 22, 10), zone("consola", GRAY, 22, 10)).weight(0.7)),
        card("Split.horizontal(lista, Split.vertical(detalle, consola))  ->  un Split dentro de otro",
            Split.horizontal(
                zone("lista", BLUE, 22, 10),
                Split.vertical(zone("detalle", GRAY, 22, 10), zone("consola", GREEN, 22, 10)))
                .divider(140).height(130)));
  }

  // ------------------------------------------------------------------------ //
  //   P A G E S
  // ------------------------------------------------------------------------ //

  /** Un asistente de tres pasos con la pagina `show` a la vista. */
  private static Pages steps(String show) {
    final Pages pages = Pages.create()
        .page("name", Col.gap(8).padding(8).children(Label.field("Nombre"), Input.text().placeholder("Mi servicio")))
        .page("type", Col.gap(8).padding(8).children(
            Label.field("Tipo"), Radio.of("Manual").selected(true), Radio.of("Automatico"), Check.of("Reintentar")))
        .page("done", Col.gap(8).padding(8).children(Label.field("Listo"), Label.muted("Todo configurado")));
    pages.show(show);
    return pages;
  }

  private static JComponent pages() {
    return page(
        Row.gap(10).top().fillEqual(
            card("pages.show(\"name\")", steps("name")),
            card("pages.show(\"type\")", steps("type")),
            card("pages.show(\"done\")", steps("done"))));
  }

  // ------------------------------------------------------------------------ //
  //   S C R O L L
  // ------------------------------------------------------------------------ //

  private static Col longContent() {
    return Col.gap(4).padding(6).children(
        Check.of("Etiqueta de producto 1"), Check.of("Etiqueta de producto 2"),
        Check.of("Etiqueta de producto 3"), Check.of("Etiqueta de producto 4"),
        Input.text().columns(40).placeholder("Un campo ancho"));
  }

  private static JComponent scroll() {
    return page(
        Row.gap(12).fillEqual(
            captioned("Scroll.of(contenido)", Scroll.of(longContent()).height(150)),
            captioned("Scroll.of(contenido).vertical()", Scroll.of(longContent()).vertical().height(150)),
            captioned("Scroll.of(contenido).noBorder()", Scroll.of(longContent()).noBorder().height(150))));
  }

  // ------------------------------------------------------------------------ //
  //   S E P A R A T O R
  // ------------------------------------------------------------------------ //

  private static JComponent separator() {
    return page(
        card("Separator.horizontal()  ->  entre dos grupos de una columna",
            Col.gap(8).children(
                Label.field("Impresion"), Check.of("Vista previa"),
                Separator.horizontal(),
                Label.field("Archivo"), Check.of("Guardar copia"))),
        card("Separator.vertical().height(24)  ->  entre grupos de botones de una fila",
            Row.gap(8).children(
                Button.of("Nuevo"), Button.of("Abrir"),
                Separator.vertical().height(24),
                Button.of("Guardar"), Button.of("Imprimir"))));
  }

  // ------------------------------------------------------------------------ //
  //   P A G E
  // ------------------------------------------------------------------------ //

  private static JComponent pageVariants() {
    return page(
        Row.gap(12).fillEqual(
            captioned("center + bottom",
                Page.create().center(zone("center", GRAY, 14, 8)).bottom(zone("bottom", ROSE, 8, 8)).height(150)),
            captioned("left + center",
                Page.create().left(zone("left", GREEN, 14, 14)).center(zone("center", GRAY, 14, 8)).height(150)),
            captioned("top + center + right",
                Page.create().top(zone("top", BLUE, 8, 8)).center(zone("center", GRAY, 14, 8))
                    .right(zone("right", AMBER, 14, 10)).height(150))));
  }
}
