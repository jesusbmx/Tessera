# Stack

Apila varios componentes **en el mismo lugar**; quien arma la pantalla decide cual se ve. Los ocultos no ocupan espacio.

| | |
|---|---|
| **Paquete** | `tessera.kit` |
| **Clase** | `Stack extends Box<Stack>`: un `JPanel` transparente con MigLayout |
| **Se crea con** | `Stack.fill(c...)` · `Stack.start(c...)` · `Stack.end(c...)` · `Stack.center(c...)` |
| **Importar** | `import tessera.kit.Stack;` |

## Cuando usarlo

- Un campo que cambia segun una opcion (un combo, un campo de texto u otro combo).
- Un boton que se cambia por otro ("Iniciar" / "Detener").
- Un mensaje de "no hay datos" o la lista.

**Cuando no:** Para los **pasos** de un asistente usa `Pages`; si el usuario debe elegir que ver, `Tabs`.

## Lo que se puede hacer

Cada seccion muestra una cosa que se puede pedir, con la imagen de como queda y el codigo que la produce (el titulo de cada tarjeta de las imagenes es ese mismo codigo). En las imagenes, una caja de color (`zone(...)`) es solo una pieza dibujada y el borde punteado marca los limites de un contenedor: ver [como leer las imagenes](README.md#como-leer-las-imagenes).

### Como funciona

Tres reglas lo explican todo:

1. Un `Stack` mide lo que mida su hijo **visible mas grande**; los **ocultos no ocupan espacio**, asi que al alternar no hay saltos.
2. Si no hay ningun hijo visible, el `Stack` se encoge a nada.
3. El **como se acomoda** cada hijo dentro del `Stack` lo decide la fabrica: `fill`, `start`, `center` o `end`.

`Stack` es para **piezas pequenas** dentro de una fila o un bloque; para pantallas enteras o pestanas, mira [`Pages`](pages.md) y [`Tabs`](tabs.md).

### Alineacion

![Stack: fill, start, center y end](../../../img/box-stack-align.png)

```java
Stack.fill(btn)        // ocupa todo el ancho
Stack.start(btn)       // ancho natural, a la izquierda
Stack.center(btn)      // ancho natural, centrado
Stack.end(btn)         // ancho natural, a la derecha
```

### Una alternativa a la vez

![Stack: el campo cambia segun la opcion](../../../img/box-stack-switch.png)

```java
final Combo<String> cbDriver = Combo.of("Zebra ZD420", "TSC TE200");
final Input txtIp = Input.text().placeholder("192.168.0.10");
final Combo<String> cbUsb = Combo.of("USB001 (Zebra)");
final Button btnScan = Button.of("Buscar");
final Button btnRefresh = Button.of("Actualizar");

Row.gap(8).children(
    Stack.fill(cbDriver, txtIp, cbUsb),          // los tres en el mismo lugar
    Stack.start(btnScan, btnRefresh))            // y su boton

// al cambiar la opcion: se muestra una, se ocultan las demas
cbDriver.setVisible(mode == 0);
txtIp.setVisible(mode == 1);   btnScan.setVisible(mode == 1);
cbUsb.setVisible(mode == 2);   btnRefresh.setVisible(mode == 2);
```

El `Stack` **no decide** cual se ve: tu codigo hace `setVisible`. Los ocultos no ocupan espacio, asi que el `Stack` mide lo que mide el visible.

### Cambiar una cosa por otra

![Stack: Iniciar o Detener, clave oculta o visible, mensaje o lista](../../../img/box-stack-swap.png)

```java
// "Iniciar" o "Detener" segun el estado
Row.gap(10).children(lblState).glue().children(Stack.start(btnStart, btnStop))

// clave oculta o a la vista
Row.gap(10).children(Stack.fill(txtPasswordHidden, txtPasswordVisible), chckShow)

// mensaje de "no hay datos" o la lista
Stack.fill(hintEmpty, scrollList)
```

### Cambiar de alternativa en tiempo de ejecucion

Para cambiar de alternativa en tiempo de ejecucion, haz `setVisible(true)` en una y `setVisible(false)` en las demas (por ejemplo, desde el `onClick` de cada `Radio`):

```java
rbtnDriver.onClick(() -> showOnly(cbPrinters));
rbtnIp.onClick(() -> showOnly(txtIp));
rbtnUsb.onClick(() -> showOnly(cbUsb));

private void showOnly(JComponent visible) {
  for (JComponent c : new JComponent[] {cbPrinters, txtIp, cbUsb}) {
    c.setVisible(c == visible);
  }
}
```

Y para el par "Iniciar" / "Detener" (o cualquier boton que cambia por otro):

```java
private void setRunning(boolean running) {
  btnStart.setVisible(!running);
  btnStop.setVisible(running);
}
```

Igual con el mensaje de "no hay datos": `emptyMessage.setVisible(model.getRowCount() == 0)` y `list.setVisible(model.getRowCount() > 0)`.

### Dentro de otros contenedores

![Stack dentro de Row y Col](../../../img/box-stack-layouts.png)

```java
Row.gap(8).children(lblValue, Stack.fill(txtField), Stack.start(btnOk))         // Stack.fill se estira
Row.gap(8).children(lblValue, Stack.start(txtField), Stack.start(btnOk))        // Stack.start, no
Col.gap(8).children(txtName, Stack.end(btnSave))                                // boton a la derecha
Col.gap(0).padding(8).glue().children(Stack.center(lblEmpty)).glue()            // centrado en un area alta
```

`Stack.center` centra **a lo ancho**. Para centrar tambien en vertical dentro de un area alta, rodealo con un `glue()` arriba y otro abajo, como en el ultimo ejemplo.

## Referencia

Varios componentes **en el mismo lugar**, uno encima de otro; sirve para alternativas de las que se muestra una (un combo, un campo o
otro combo segun la opcion elegida). **Quien arma la pantalla decide cual esta visible**; los ocultos no ocupan espacio.

| Fabrica | Los hijos... | Y el `Stack`... |
|---|---|---|
| `Stack.fill(c...)` | se **estiran** a todo el ancho | pide ocupar **lo que sobra** cuando va dentro de un `Row` |
| `Stack.start(c...)` | conservan su ancho natural, a la **izquierda** | ocupa el ancho de su contenedor |
| `Stack.center(c...)` | conservan su tamano natural, **centrados** | ocupa el ancho de su contenedor |
| `Stack.end(c...)` | conservan su ancho natural, a la **derecha** | ocupa el ancho de su contenedor |

En todos, los hijos se **centran en vertical**. Dentro de un `Row`, solo `Stack.fill` se estira; los demas toman el ancho de su hijo visible mas grande.

```java
Row.gap(10).children(
    Stack.fill(cbPrinters, txtIpPrinter, cbUsbPrinters),       // una de las tres se ve
    Stack.start(btnIpScanner, btnUsbRefresh))                   // el boton que toque
```

## Errores comunes

- Se ven dos alternativas a la vez: el `Stack` no decide por ti; haz `setVisible(false)` en las demas.
- Un `Stack` dentro de un `Row` no se estira: solo `Stack.fill` lo hace.
- Con ningun hijo visible, el `Stack` se encoge a 0x0.

## Ver tambien

- [`Pages`](pages.md) · [`Tabs`](tabs.md) · [`Row`](row.md)
- [Referencia del kit](../README.md): ideas base y metodos comunes (`Styled`, `Clickable`, `Box`).
- [Contenedores](README.md): la tabla "que uso para cada cosa" y todos los contenedores.
- [Lo comun a los contenedores (`Box`)](box.md): `gap`, `padding`, `children`, tamano fijo y ocultar.
- [Como armar una pantalla](../../../guides/screens.md#pantallas-completas): pantallas completas que los combinan.
