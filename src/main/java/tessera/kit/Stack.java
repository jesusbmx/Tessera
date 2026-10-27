package tessera.kit;

import java.awt.Component;

/**
 * Varios componentes en el mismo lugar, uno encima de otro: sirve para las alternativas de las que solo se
 * muestra una (un combo, un campo de texto o un combo USB segun la opcion elegida; quien arma la pantalla
 * decide cual esta visible). Los ocultos no ocupan espacio.
 *
 * <pre>
 * Row.gap(10).children(
 *     Stack.fill(cbPrinters, txtIpPrinter, cbUsbPrinters),            // ocupa el ancho que sobra
 *     Stack.start(chckHardwareAcceleration, btnIpScanner, btnUsbRefresh))  // ancho natural
 * </pre>
 */
public class Stack extends Box<Stack> {

  /** Como se acomodan los hijos dentro del espacio del Stack. */
  private final String cell;

  protected Stack(String cell) {
    super(0);
    this.cell = cell;
    relayout();
  }

  /** Los hijos ocupan todo el ancho, y el Stack ocupa el ancho que sobra de su contenedor. */
  public static Stack fill(Component... children) {
    final Stack stack = new Stack("cell 0 0, growx, aligny center");
    Kit.fill(stack, true, false);
    return stack.children(children);
  }

  /** Los hijos conservan su ancho natural y se alinean a la izquierda. */
  public static Stack start(Component... children) {
    return new Stack("cell 0 0, left, aligny center").children(children);
  }

  /** Los hijos conservan su ancho natural y se alinean a la derecha. */
  public static Stack end(Component... children) {
    return new Stack("cell 0 0, right, aligny center").children(children);
  }

  /** Los hijos conservan su tamano natural y se centran. */
  public static Stack center(Component... children) {
    return new Stack("cell 0 0, center, aligny center").children(children);
  }

  @Override
  protected String migLayout() {
    return migInsets() + ", hidemode 3, novisualpadding";
  }

  @Override
  protected String columns() {
    return "[grow]";
  }

  @Override
  protected void place(Component child) {
    put(child, cell);
  }
}
