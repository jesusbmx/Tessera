package tessera.kit;

import java.awt.Component;
import javax.swing.Icon;
import javax.swing.JTabbedPane;

/**
 * Pestanas. Dentro de un Row o un Col ocupa el espacio que sobra (ancho y alto).
 *
 * <pre>
 * Tabs.create().tab("General", general).tab("Avanzado", advanced)
 * </pre>
 */
public class Tabs extends JTabbedPane implements Styled<Tabs> {

  protected Tabs() {
    Kit.fill(this, true, true);
  }

  public static Tabs create() {
    return new Tabs();
  }

  public Tabs tab(String title, Component content) {
    addTab(title, content);
    return this;
  }

  public Tabs tab(String title, Icon icon, Component content) {
    addTab(title, icon, content);
    return this;
  }
}
