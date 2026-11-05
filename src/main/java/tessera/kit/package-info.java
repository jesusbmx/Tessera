/**
 * Contenedores y widgets de Tessera.
 *
 * El kit es una capa delgada sobre Swing + FlatLaf + MigLayout: los widgets ({@link tessera.kit.Label},
 * {@link tessera.kit.Button}, {@link tessera.kit.Input}...) son los de Swing con metodos encadenables, y los
 * contenedores ({@link tessera.kit.Row}, {@link tessera.kit.Col}, {@link tessera.kit.Stack}, {@link tessera.kit.Form},
 * {@link tessera.kit.Card}...) arman el layout sin escribir restricciones de MigLayout.
 *
 * Los colores salen del tema ({@code Theme.palette()}), las medidas de {@code Metrics}, y los botones de Swing ya
 * creados se estilan con {@link tessera.kit.Buttons}. Lo que un componente le pide a su contenedor (ocupar el espacio
 * que sobra, un ancho fijo) esta en {@link tessera.kit.Layout}.
 *
 * Ejemplo:
 * <pre>
 * Card.create("Conexion").subtitle("Como se conecta la impresora").padding(10, 12, 12, 12).gap(6)
 *     .children(
 *         Row.gap(14).children(rbtnPrinter, rbtnIp),
 *         Row.gap(10).fill(txtIp).children(btnScan));
 * </pre>
 */
package tessera.kit;
