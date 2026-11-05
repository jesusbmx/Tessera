# Referencia

Una pagina por componente: **imagen, cuando usarlo, todos sus metodos y los errores comunes**. Organizada por paquete. Si no sabes cual usar, empieza por la tabla "Que quiero hacer" de abajo.

## Que quiero hacer

| Quiero... | Mira |
|---|---|
| acomodar widgets en fila, columna, formulario o cuadricula (y ver **todo lo que hace cada uno**) | [`Row`](kit/containers/row.md) · [`Col`](kit/containers/col.md) · [`Form`](kit/containers/form.md) · [`Grid`](kit/containers/grid.md) · [todos los contenedores](kit/containers/README.md) |
| una ventana con cabecera, centro y pie | [`Page`](kit/containers/page.md) · [`Header`](kit/containers/header.md) · [`Footer`](kit/containers/footer.md) |
| agrupar contenido en un bloque con titulo | [`Card`](kit/containers/card.md) |
| varias vistas en el mismo lugar | [`Tabs`](kit/containers/tabs.md) · [`Pages`](kit/containers/pages.md) · [`Stack`](kit/containers/stack.md) · [`Split`](kit/containers/split.md) |
| textos, botones y opciones | [`Label`](kit/widgets/label.md) · [`Button`](kit/widgets/button.md) · [`Check`](kit/widgets/check.md) · [`Radio`](kit/widgets/radio.md) · [`Switch`](kit/widgets/switch.md) |
| campos de entrada | [`Input`](kit/widgets/input.md) · [`Password`](kit/widgets/password.md) · [`TextArea`](kit/widgets/textarea.md) · [`Combo`](kit/widgets/combo.md) · [`Spinner`](kit/widgets/spinner.md) |
| un boton que abre un menu: elegir una opcion, marcar varias o correr una accion | [`ChoiceButton`](kit/widgets/choicebutton.md) · [`MultiChoiceButton`](kit/widgets/multichoicebutton.md) · [`MenuButton`](kit/widgets/menubutton.md) · [`Menu`](kit/widgets/menu.md) (clic derecho) |
| una tabla de datos | [`Table`](kit/widgets/table.md) + [`BaseTableModel`](components/tables.md#basetablemodel) |
| cambiar colores, fuente y esquinas | [`Theme`](theme/theme.md) |
| avisar al usuario | [`Notification`](notify/notification.md) · [`Toast`](notify/toast.md) |
| icono en la bandeja o recordar el tamano de la ventana | [`TrayDock`](app/traydock.md) · [`WindowPrefs`](app/windowprefs.md) |
| validar un formulario | [Validacion de formularios](components/forms.md) |

## `tessera.kit`: contenedores y widgets

Vista general, metodos comunes (`Styled`, `Clickable`, `Box`) y utilidades: [Referencia del kit](kit/README.md).

### Contenedores

La tabla de **cual uso para cada cosa** y lo que comparten todos (`gap`, `padding`, tamano fijo, ocultar) esta en [Contenedores](kit/containers/README.md) y [`Box`](kit/containers/box.md). Cada pagina tiene una imagen por cada cosa que se puede hacer.

| Clase | Para que |
|---|---|
| [`Row`](kit/containers/row.md) | Pone sus hijos **en una fila**, de izquierda a derecha, cada uno con su ancho natural. |
| [`Col`](kit/containers/col.md) | Apila sus hijos **en una columna**, de arriba hacia abajo. |
| [`Stack`](kit/containers/stack.md) | Apila varios componentes **en el mismo lugar**; quien arma la pantalla decide cual se ve. |
| [`Grid`](kit/containers/grid.md) | Cuadricula: varias celdas por fila, **alineadas en columnas** (cada columna mide lo que su celda mas ancha). |
| [`Form`](kit/containers/form.md) | Formulario de **dos columnas**: la etiqueta a la izquierda y el campo a la derecha, que ocupa el ancho que sobra. |
| [`Card`](kit/containers/card.md) | Bloque con **fondo suave y borde redondeado**: titulo (con icono opcional), texto de ayuda y debajo sus hijos en columna. |
| [`Header`](kit/containers/header.md) | **Banner oscuro** con un titulo y, opcionalmente, una descripcion: la cabecera de una ventana o de cada paso de un asistente. |
| [`Footer`](kit/containers/footer.md) | Barra de botones de un dialogo: **abajo a la derecha**, con el espacio de siempre alrededor. |
| [`Page`](kit/containers/page.md) | Estructura de una ventana o panel: **arriba, centro y abajo** (y a los lados si hace falta). |
| [`Pages`](kit/containers/pages.md) | Varias paginas **en el mismo lugar**, de las que se ve una a la vez: los pasos de un asistente, las vistas de la ventana principal. |
| [`Scroll`](kit/containers/scroll.md) | Contenedor con **barras de desplazamiento**. |
| [`Tabs`](kit/containers/tabs.md) | **Pestanas**: el usuario elige cual contenido ver. |
| [`Split`](kit/containers/split.md) | Dos zonas separadas por una **barra que se arrastra**. |
| [`Separator`](kit/containers/separator.md) | **Linea fina** que separa secciones. |

### Widgets

| Clase | Para que |
|---|---|
| [`Label`](kit/widgets/label.md) | Etiqueta de texto con **estilos listos**: titulo, nombre de campo, texto secundario y texto normal. |
| [`Hint`](kit/widgets/hint.md) | Texto de ayuda: **pequeno, gris, y se parte en lineas** al ancho que reciba. |
| [`Button`](kit/widgets/button.md) | Boton con estilos y eventos encadenables. |
| [`Check`](kit/widgets/check.md) | **Casilla de verificacion** (`JCheckBox`). |
| [`Radio`](kit/widgets/radio.md) | **Opcion** de un grupo de opciones excluyentes (`JRadioButton`). |
| [`Toggle`](kit/widgets/toggle.md) | Boton que **queda presionado**: un modo de vista o un filtro. |
| [`Switch`](kit/widgets/switch.md) | **Interruptor** estilo iOS: un si/no que se aplica al instante. |
| [`Input`](kit/widgets/input.md) | **Campo de texto** de una linea. |
| [`Password`](kit/widgets/password.md) | **Campo de clave**: el contenido no se ve. |
| [`TextArea`](kit/widgets/textarea.md) | **Area de texto** de varias lineas. |
| [`Combo`](kit/widgets/combo.md) | **Lista desplegable** (`JComboBox<T>`). |
| [`ChoiceButton`](kit/widgets/choicebutton.md) | **Boton que abre un menu para elegir una opcion**: el boton muestra la elegida. |
| [`MultiChoiceButton`](kit/widgets/multichoicebutton.md) | **Boton que abre un menu de casillas** para marcar varias opciones. |
| [`MenuButton`](kit/widgets/menubutton.md) | **Boton que abre un menu de acciones**: el clic corre algo, no queda nada elegido. |
| [`Menu`](kit/widgets/menu.md) | **Menu emergente de acciones** (y su `MenuItem`), con o sin boton: el clic derecho de una lista. |
| [`Spinner`](kit/widgets/spinner.md) | **Selector numerico** con flechas (`JSpinner`). |
| [`Table`](kit/widgets/table.md) | **Tabla** (`JTable`). |

## `tessera.theme`

| Clase | Para que |
|---|---|
| [`Theme`](theme/theme.md) | instala el look and feel, la fuente, el acento, el fondo y las esquinas |
| [`Icons`](theme/icons.md) | iconos SVG y el interruptor estilo iOS |

## `tessera.notify`

Comparacion y cuando usar cada uno: [Avisos](notify/README.md).

| Clase | Para que |
|---|---|
| [`Notification`](notify/notification.md) | tarjetas apiladas en la esquina de la pantalla, con id, progreso y acciones |
| [`Toast`](notify/toast.md) | mensaje breve centrado sobre una ventana, en cola |

## `tessera.app`

| Clase | Para que |
|---|---|
| [`TrayDock`](app/traydock.md) | icono de la bandeja del sistema con menu, y reapertura desde el Dock de macOS |
| [`WindowPrefs`](app/windowprefs.md) | cada ventana se abre con el tamano que el usuario le dejo |
| [`Preview` y `Previewer`](app/preview.md) | ver una pantalla en su propia ventana mientras se arma (`@Preview`) |

## `tessera.components`

Piezas sueltas por categoria: [Componentes sueltos](components/README.md).

| Categoria | Que incluye |
|---|---|
| [Tablas](components/tables.md) | Modelo de tabla con una lista de objetos y renderizadores que resaltan columnas. |
| [Listas y combos](components/lists-and-combos.md) | Listas con casillas, elementos de combo con etiqueta y valor, grupos de opciones y filas alternas. |
| [Validacion de formularios](components/forms.md) | Marcar con borde rojo o con un globo los campos con error. |
| [Texto](components/text.md) | Placeholder para areas de texto, deshacer/rehacer y campos con opciones marcables. |
| [Ventanas y navegacion](components/windows-and-navigation.md) | Barra de navegacion con etiquetas y mover ventanas sin decoracion. |
| [Bordes](components/borders.md) | Bordes redondeados y punteados. |
| [Arrastrar y soltar](components/drag-and-drop.md) | Recibir archivos o texto soltados sobre un componente. |
