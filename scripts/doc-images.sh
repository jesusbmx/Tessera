#!/usr/bin/env bash
# Genera las imagenes de docs/img/ (capturas de los ejemplos de las guias, incluida la guia visual de docs/guides/screens.md).
# Uso: ./scripts/doc-images.sh
# Necesita build/classes: corre antes ./build.sh
set -euo pipefail
cd "$(dirname "$0")/.."

case "$(uname -s)" in MINGW*|MSYS*|CYGWIN*) SEP=';' ;; *) SEP=':' ;; esac
CP=$(ls lib/*.jar | tr '\n' "$SEP")

rm -rf build/demo && mkdir -p build/demo docs/img
javac --release 8 -encoding UTF-8 -Xlint:-options -cp "build/classes$SEP$CP" -d build/demo $(find src/demo/java -name '*.java')

for name in screen-example containers styled theme-default theme-custom arc-0 arc-12 arc-24; do
  java -cp "build/demo${SEP}build/classes$SEP$CP" tessera.demo.DocImages "$name" "docs/img/$name.png"
  echo "docs/img/$name.png"
done

# guia visual de disposiciones (docs/guides/screens.md): layout-*.png y app-*.png, todas en una sola ejecucion
java -cp "build/demo${SEP}build/classes$SEP$CP" tessera.demo.LayoutImages docs/img

# imagenes de los widgets (docs/reference/kit): widget-*.png y container-*.png
java -cp "build/demo${SEP}build/classes$SEP$CP" tessera.demo.WidgetImages docs/img

# ejemplos de las paginas de widgets y de la guia docs/guides/widgets.md: example-*.png y widgets-*.png
java -cp "build/demo${SEP}build/classes$SEP$CP" tessera.demo.WidgetExamples docs/img

# imagenes de la guia de menus (docs/guides/menus.md): menus-*.png, con el menu abierto encima del boton
java -cp "build/demo${SEP}build/classes$SEP$CP" tessera.demo.MenuImages docs/img

# imagenes de la guia de Row, Col y Stack (docs/guides/layout.md): box-*.png
java -cp "build/demo${SEP}build/classes$SEP$CP" tessera.demo.BoxImages docs/img

# imagenes de los avisos (docs/reference/notify): notify-*.png. Muestran las ventanas un instante: necesita un escritorio (no sirve sin pantalla)
java -cp "build/demo${SEP}build/classes$SEP$CP" tessera.demo.NotifyImages docs/img
