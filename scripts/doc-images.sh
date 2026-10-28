#!/usr/bin/env bash
# Genera las imagenes de docs/img/ (capturas de los ejemplos de las guias, incluida la guia visual de docs/SCREENS.md).
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

# guia visual de disposiciones (docs/SCREENS.md): layout-*.png y app-*.png, todas en una sola ejecucion
java -cp "build/demo${SEP}build/classes$SEP$CP" tessera.demo.LayoutImages docs/img
