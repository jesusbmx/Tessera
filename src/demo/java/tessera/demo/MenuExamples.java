package tessera.demo;

import java.awt.Color;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Arrays;
import java.util.Set;
import javax.swing.DefaultListModel;
import javax.swing.Icon;
import javax.swing.JList;
import javax.swing.SwingUtilities;
import tessera.kit.Check;
import tessera.kit.ChoiceButton;
import tessera.kit.Kit;
import tessera.kit.Menu;
import tessera.kit.MenuButton;
import tessera.kit.MenuItem;
import tessera.kit.MultiChoiceButton;

/**
 * Los fragmentos de docs/guides/menus.md que no salen de MenuImages, tal cual los muestra la guia. No se ejecuta:
 * existe para que compile junto con la libreria (si cambia una firma, el demo deja de compilar y la guia se corrige).
 */
final class MenuExamples {
  static class Service { String name; boolean enabled; }
  static class Model { boolean removeResidues; boolean sameFolder; int printerType; }
  static final int DRIVER = 0, NETWORK = 1, USB = 2;

  final Model model = new Model();
  final DefaultListModel<Service> listModel = new DefaultListModel<>();
  final JList<Service> listView = new JList<>(listModel);
  Icon proIcon;
  boolean licensed;

  void setPrinterForAll() { } void export() { } void create() { } void importFromWeb() { } void importFromFile() { }
  void toggleSelected() { } void moveUp() { } void moveDown() { } void edit() { } void delete() { }
  void save(Set<String> values) { }

  final ChoiceButton<Integer> method = ChoiceButton.<Integer>of().option(DRIVER, "a").option(NETWORK, "b").option(USB, "c");
  final MultiChoiceButton<String> unzip = MultiChoiceButton.<String>of("x").option("residues", "r").option("sameFolder", "s");

  void section1() {
    model.printerType = method.getValue(-1);
    method.setValue(model.printerType);
    method.optionEnabled(USB, false, "No disponible en este equipo");
    method.optionEnabled(USB, true);
  }

  void section2() {
    model.removeResidues = unzip.isChecked("residues");
    Set<String> marked = unzip.getValues();
    unzip.setChecked("sameFolder", model.sameFolder);
    unzip.setValues(Arrays.asList("residues", "sameFolder"));
    unzip.onChange(values -> save(values));
  }

  // seccion 3
  private final MenuButton more = MenuButton.of("Mas opciones")
      .item("Establecer impresora para todos", this::setPrinterForAll)
      .item("Exportar servicios", this::export);

  private final MenuButton add = MenuButton.of("Agregar")
      .color(Color.WHITE)
      .item("Crear servicio", this::create)
      .separator()
      .item("Importar de la web", this::importFromWeb)
      .item("Importar de un archivo", this::importFromFile);

  void section3() {
    Kit.makePrimary(add);
    MenuButton iconOnly = MenuButton.of("").icon("app/img/more.svg", 24);
    MenuButton none = MenuButton.of("x").noArrow();
  }

  private final MenuItem miImport = MenuItem.of("Importar de un archivo", this::importFromFile)
      .icon("app/img/pro.svg", 16).iconRight().padding(6, 10);
  private final MenuButton add2 = MenuButton.of("Agregar").item("Crear servicio", this::create).item(miImport);

  void section3b() {
    miImport.setIcon(licensed ? null : proIcon);
    miImport.setEnabled(false);
  }

  // seccion 4
  private final MenuItem miTitle = MenuItem.title("").fontSize(15);
  private final MenuItem miToggle = MenuItem.of("Deshabilitar", this::toggleSelected);
  private final MenuItem miUp = MenuItem.of("Subir", this::moveUp);
  private final MenuItem miDown = MenuItem.of("Bajar", this::moveDown);

  private final Menu rowMenu = Menu.create()
      .item(miTitle)
      .item("Editar", this::edit)
      .item(miToggle)
      .separator()
      .item(miUp)
      .item(miDown)
      .item("Eliminar", this::delete);

  void section4() {
    listView.addMouseListener(new MouseAdapter() {
      @Override
      public void mousePressed(MouseEvent evt) {
        if (!SwingUtilities.isRightMouseButton(evt)) return;
        final int index = listView.locationToIndex(evt.getPoint());
        miTitle.setText(listModel.getElementAt(index).name);
        miToggle.setText(listModel.getElementAt(index).enabled ? "Deshabilitar" : "Habilitar");
        miUp.setEnabled(index > 0);
        miDown.setEnabled(index < listModel.getSize() - 1);
        rowMenu.show(listView, evt.getX(), evt.getY());
      }
    });
    listView.setComponentPopupMenu(rowMenu);
    rowMenu.showBelow(listView);
  }
}
