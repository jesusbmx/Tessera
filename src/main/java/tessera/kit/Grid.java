package tessera.kit;

import java.awt.Component;
import java.util.TreeMap;

/**
 * Cuadricula: varias celdas por fila, alineadas en columnas (cada columna es tan ancha como su celda mas
 * ancha). {@link Form} sirve para un par etiqueta / campo por fila; Grid para varios pares por fila.
 * Una celda oculta no ocupa espacio y una fila sin celdas visibles desaparece con su espacio, pero las
 * demas celdas se quedan en su columna.
 *
 * <pre>
 * Grid.gap(6)
 *     .columnGap(1, 30)                                         // mas aire entre el 2.o y el 3.er par
 *     .row(lblDpi, cbDpi.width(170), lblWidth, Row.gap(12).children(spinW, lblMm))
 *     .row(lblType, cbType.width(170), lblGap, spinGap)
 * </pre>
 */
public class Grid extends Box<Grid> {

  /** Espacio (px) entre columnas, salvo las que tienen el suyo (ver {@link #columnGap(int, int)}). */
  private int columnGap = 6;

  /** Espacio (px) despues de la columna indicada. */
  private final TreeMap<Integer, Integer> gaps = new TreeMap<>();

  /** Filas agregadas hasta ahora. */
  private int rows;

  /** Columnas usadas hasta ahora (la de la celda mas a la derecha). */
  private int columnCount;

  protected Grid(int gap) {
    super(gap);
    relayout();
  }

  /** Cuadricula con ese espacio (px) entre filas y 6 px entre columnas. */
  public static Grid gap(int gap) {
    return new Grid(gap);
  }

  /** Espacio (px) entre columnas (6 por defecto). */
  public Grid columnGap(int px) {
    this.columnGap = px;
    relayout();
    return this;
  }

  /** Espacio (px) solo entre la columna {@code after} (desde 0) y la siguiente. */
  public Grid columnGap(int after, int px) {
    gaps.put(after, px);
    relayout();
    return this;
  }

  /** Una fila: cada componente va en la columna que le toca por su posicion (un null deja la celda vacia). */
  public Grid row(Component... cells) {
    for (int column = 0; column < cells.length; column++) {
      if (cells[column] != null) {
        put(cells[column], Col.constraints(cells[column], "cell " + column + " " + rows + ", aligny center"));
      }
    }
    rows++;
    if (cells.length > columnCount) {
      columnCount = cells.length;
      relayout();
    }
    return this;
  }

  @Override
  protected String migLayout() {
    return migInsets() + ", gap " + Kit.px(columnGap) + "px " + Kit.px(gap()) + "px, hidemode 3, novisualpadding";
  }

  /** Un espacio explicito despues de cada columna (si no, MigLayout repite el ultimo que se le dio). */
  @Override
  protected String columns() {
    if (gaps.isEmpty()) {
      return "";
    }
    final StringBuilder constraints = new StringBuilder();
    for (int column = 0; column < columnCount; column++) {
      constraints.append("[]");
      if (column < columnCount - 1) {
        constraints.append(Kit.px(gaps.containsKey(column) ? gaps.get(column) : columnGap)).append("px");
      }
    }
    return constraints.toString();
  }

  /** Con children(...) cada componente ocupa una fila propia (en la primera columna). */
  @Override
  protected void place(Component child) {
    row(child);
  }
}
