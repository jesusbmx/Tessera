#!/usr/bin/env bash
# Descarga las dependencias de Tessera a lib/ (Maven Central). Ver la tabla de dependencias en README.md.
set -euo pipefail
cd "$(dirname "$0")/.."
mkdir -p lib

M=https://repo1.maven.org/maven2
get() { [ -f "lib/$(basename "$1")" ] || { echo "descargando $(basename "$1")"; curl -fsSL -o "lib/$(basename "$1")" "$M/$1"; }; }

get com/formdev/flatlaf/3.7/flatlaf-3.7.jar
get com/formdev/flatlaf-extras/3.7/flatlaf-extras-3.7.jar
get com/github/weisj/jsvg/2.0.0/jsvg-2.0.0.jar
get com/miglayout/miglayout-core/4.2/miglayout-core-4.2.jar
get com/miglayout/miglayout-swing/4.2/miglayout-swing-4.2.jar
echo "listo: lib/"
