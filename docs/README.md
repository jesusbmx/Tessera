# Documentacion de Tessera

Todo esta en Markdown dentro de este repositorio: no depende de ningun sitio web. Dos partes: las **guias** (para aprender y resolver tareas) y la **referencia** (para consultar cada componente).

## Guias

| Quiero... | Leer |
|---|---|
| instalarla y ver mi primera ventana | [Primeros pasos](guides/getting-started.md) |
| ver como se coloca todo (Row, Col, Form, Page...) con imagenes y pantallas completas | [Como armar una pantalla](guides/screens.md) |
| ejemplos completos listos para copiar | [Recetas](guides/recipes.md) |
| cambiar colores, esquinas redondeadas, fuente | [Personalizar](guides/customizing.md) |
| pasar una app NetBeans / GroupLayout a Tessera | [Migrar una app](guides/migrating.md) |
| resolver un problema o una duda | [Preguntas frecuentes](guides/faq.md) |
| modificar la libreria o publicar el jar | [Contribuir](guides/contributing.md) |

## Referencia

Una pagina por componente, con imagen, cuando usarlo, todos sus metodos y errores comunes. Empieza por el [indice de la referencia](reference/README.md).

| Paquete | Contenido |
|---|---|
| `tessera.kit` | [contenedores y widgets](reference/kit/README.md): `Row`, `Col`, `Stack`, `Grid`, `Form`, `Card`, `Header`, `Footer`, `Page`, `Pages`, `Scroll`, `Tabs`, `Split`, `Separator`, `Label`, `Hint`, `Button`, `Check`, `Radio`, `Toggle`, `Switch`, `Input`, `Password`, `TextArea`, `Combo`, `ChoiceButton`, `MultiChoiceButton`, `MenuButton`, `Menu`, `Spinner`, `Table` |
| `tessera.theme` | [`Theme`](reference/theme/theme.md), [`Icons`](reference/theme/icons.md) |
| `tessera.notify` | [`Notification`](reference/notify/notification.md), [`Toast`](reference/notify/toast.md) |
| `tessera.app` | [`TrayDock`](reference/app/traydock.md), [`WindowPrefs`](reference/app/windowprefs.md) |
| `tessera.components` | [componentes sueltos](reference/components/README.md) |

Cada clase publica tiene ademas Javadoc corto con un ejemplo de uso. Para una lectura rapida de toda la API (pensada para IA y devs nuevos): [AGENTS.md](../AGENTS.md).
