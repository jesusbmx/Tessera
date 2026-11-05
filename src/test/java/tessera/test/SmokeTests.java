package tessera.test;

import javax.swing.SwingUtilities;

/**
 * Pruebas de humo de Tessera: arman y dibujan el kit, los cuatro temas y los bordes sin abrir ninguna ventana, y
 * revisan los colores que importan. No usan JUnit (la libreria no tiene dependencias de pruebas).
 *
 * <pre>
 * ./build.sh --test        o        ./build.ps1 -Test
 * </pre>
 *
 * Termina con codigo 1 si algo falla. Se corre sin pantalla ({@code java.awt.headless=true}). No cubre
 * {@code Notification}, {@code Toast}, {@code TrayDock} ni {@code Previewer}: necesitan una ventana de verdad.
 */
public final class SmokeTests {

  private SmokeTests() {
  }

  public static void main(String[] args) throws Exception {
    final Checks check = new Checks();
    // Cada grupo corre en el hilo de Swing; si uno se cae, se anota y siguen los demas.
    // Lo primero: antes de que cualquier prueba instale un tema, Kit debe traer los colores de fabrica.
    run(check, "Valores de fabrica", () -> ThemeTests.factory(check));
    run(check, "RoundedBorder, Card y Scroll", () -> BorderAndCardTests.run(check));
    run(check, "Temas", () -> ThemeTests.run(check));
    run(check, "Kit", () -> KitTests.run(check));

    System.out.println();
    System.out.println(check.passed() + " bien, " + check.failed() + " con falla");
    System.exit(check.failed() == 0 ? 0 : 1);
  }

  private static void run(Checks check, String name, Runnable group) throws Exception {
    SwingUtilities.invokeAndWait(() -> {
      try {
        group.run();
      } catch (Throwable t) {
        check.crashed(name, t);
      }
    });
  }
}
