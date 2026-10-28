# Validacion de formularios (`tessera.components`)

Marcar con borde rojo o con un globo los campos con error.

[← Componentes sueltos](README.md)

## `ErrorProvider`

Marca con **borde rojo y tooltip** los campos con error y los **restaura** con `clear()`.

```java
private final ErrorProvider errors = new ErrorProvider();

private boolean validate() {
  errors.clear();                                            // quita los errores de la vez anterior
  if (txtName.getText().trim().isEmpty()) {
    errors.setError(txtName, "El nombre es obligatorio");
    return false;
  }
  if (errors.notIsEmail(txtEmail, "Correo no valido")) {     // true cuando NO es un correo
    return false;
  }
  return true;
}
```

| Metodo | Que hace |
|---|---|
| `setError(componente, mensaje)` | borde rojo + tooltip con el mensaje + **da el foco** al campo + muestra un [`Toast`](../notify/toast.md) de error de 3 s |
| `clear()` | restaura bordes y tooltips originales de todos los campos marcados |
| `size()` | cuantos campos estan marcados |
| `notIsEmail(JTextComponent, mensaje)` | recorta el texto y, si **no** tiene forma de correo, marca el error y devuelve `true` (ojo con el nombre: `true` = hay error) |
| `getError(componente)` | el mensaje del error de ese campo, o `null` si no esta marcado |

Como `setError` **mueve el foco**, marca un solo error por pasada (el primero) y sal con `return`, como en el ejemplo. Si llamas a `setError` otra
vez sobre el mismo campo, solo cambia el mensaje (el borde no se acumula) y `clear()` lo deja como estaba.

## `FloatingErrorProvider`

La misma idea con un **globo flotante debajo del campo** en vez de borde rojo y toast.

```java
private final FloatingErrorProvider errors = new FloatingErrorProvider();
// igual que ErrorProvider: errors.clear(); errors.setError(txtName, "..."); errors.notIsEmail(txtEmail, "...");
```

| Metodo | Que hace |
|---|---|
| `setError(componente, mensaje)` | muestra el globo bajo el campo y le da el foco. **No hace nada si el componente esta oculto** (`isVisible()` es `false`) |
| `removeError(componente)` | cierra el globo de ese campo |
| `clear()` | cierra todos |
| `getError(componente)` | el mensaje mostrado, o `null` |
| `size()` | cuantos globos hay |
| `notIsEmail(JTextComponent, mensaje)` | igual que en `ErrorProvider` (`true` = no es correo) |

El globo mide unos 250 px de ancho y se parte en lineas. El mensaje se inserta como HTML: **no incluyas texto del usuario sin escapar**.
