package tessera.test;

import java.awt.Color;

/** Comprobaciones minimas (sin JUnit: la libreria no tiene dependencias de pruebas). Cuenta aciertos y fallas. */
final class Checks {

  private int passed;
  private int failed;

  /** Titulo de un grupo de pruebas. */
  void group(String name) {
    System.out.println();
    System.out.println(name);
  }

  void ok(String name, boolean condition) {
    if (condition) {
      passed++;
      System.out.println("  OK     " + name);
    } else {
      failed++;
      System.out.println("  FALLA  " + name);
    }
  }

  void eq(String name, Object expected, Object actual) {
    final boolean same = expected == null ? actual == null : expected.equals(actual);
    if (same) {
      ok(name, true);
    } else {
      ok(name + "   (esperado " + show(expected) + ", fue " + show(actual) + ")", false);
    }
  }

  /** Compara colores sin tomar en cuenta la transparencia. */
  void color(String name, String expectedHex, Color actual) {
    eq(name, expectedHex.toUpperCase(), actual == null ? null : hex(actual));
  }

  void color(String name, Color expected, Color actual) {
    eq(name, hex(expected), actual == null ? null : hex(actual));
  }

  /** La accion no debe lanzar nada. */
  void noThrow(String name, Runnable action) {
    try {
      action.run();
      ok(name, true);
    } catch (Throwable t) {
      ok(name + "   (" + t + ")", false);
    }
  }

  /** Algo que fallo fuera de una comprobacion (por ejemplo, una excepcion al armar un grupo). */
  void crashed(String group, Throwable t) {
    ok(group + " termino con una excepcion: " + t, false);
    t.printStackTrace(System.out);
  }

  int failed() {
    return failed;
  }

  int passed() {
    return passed;
  }

  static String hex(Color color) {
    return String.format("#%06X", color.getRGB() & 0xFFFFFF);
  }

  private static String show(Object value) {
    return value instanceof Color ? hex((Color) value) : String.valueOf(value);
  }
}
