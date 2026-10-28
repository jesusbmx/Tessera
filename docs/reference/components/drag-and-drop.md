# Arrastrar y soltar (`tessera.components`)

Recibir archivos o texto soltados sobre un componente.

[← Componentes sueltos](README.md)

## `DragAndDrop`

Receptor de **archivos (o texto) soltados** sobre un componente: sobrescribe el metodo que te interese.

```java
new DropTarget(panelDrop, new DragAndDrop() {
  @Override
  public void dropFile(File file) {                          // se llama una vez por archivo
    txtPath.setText(file.getAbsolutePath());
  }
});
```

| Metodo a sobrescribir | Se llama cuando |
|---|---|
| `dropFile(File)` | se suelta un archivo (una vez por cada uno) |
| `dropFiles(List<File>)` | se sueltan archivos; por defecto llama a `dropFile` con cada uno |
| `dropString(String)` | se suelta texto |

Si lo soltado no es ni archivo ni texto, o algo falla durante el procesamiento, **se ignora en silencio** (no hay excepcion ni aviso).
