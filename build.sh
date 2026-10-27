#!/usr/bin/env bash
# Compila Tessera (Java 8) y arma dist/tessera-<version>.jar. Uso: ./build.sh [--demo]
#   --demo   ademas compila y abre el Showcase
set -euo pipefail
cd "$(dirname "$0")"

VERSION=$(sed -n 's#.*<version>\(.*\)</version>.*#\1#p' pom.xml | head -1)
ls lib/*.jar >/dev/null 2>&1 || scripts/fetch-libs.sh

# javac de Windows (tambien desde Git Bash) separa el classpath con ';'
case "$(uname -s)" in MINGW*|MSYS*|CYGWIN*) SEP=';' ;; *) SEP=':' ;; esac
CP=$(ls lib/*.jar | tr '\n' "$SEP")

rm -rf build dist && mkdir -p build/classes dist
find src/main/java -name '*.java' > build/sources.txt
javac --release 8 -encoding UTF-8 -Xlint:-options -cp "$CP" -d build/classes @build/sources.txt
cp -r src/main/resources/. build/classes/
jar cf "dist/tessera-$VERSION.jar" -C build/classes .
echo "dist/tessera-$VERSION.jar"

if [ "${1:-}" = "--demo" ]; then
  mkdir -p build/demo
  javac --release 8 -encoding UTF-8 -Xlint:-options -cp "build/classes$SEP$CP" -d build/demo $(find src/demo/java -name '*.java')
  java -cp "build/demo${SEP}build/classes$SEP$CP" tessera.demo.Showcase
fi
