# Compila Tessera (Java 8) y arma dist/tessera-<version>.jar.  Uso: ./build.ps1 [-Demo]
param([switch]$Demo)
$ErrorActionPreference = 'Stop'
Set-Location $PSScriptRoot

$version = ([xml](Get-Content pom.xml)).project.version
if (-not (Test-Path lib/*.jar)) {
  if (Get-Command bash -ErrorAction SilentlyContinue) { bash scripts/fetch-libs.sh }
  else { throw 'Faltan las dependencias en lib/: ver README.md (seccion Dependencias).' }
}
# javac suele estar en el PATH sin jar: se prefiere JAVA_HOME si existe.
$bin = if ($env:JAVA_HOME -and (Test-Path "$env:JAVA_HOME/bin/jar.exe")) { "$env:JAVA_HOME/bin/" } else { '' }
$cp = (Get-ChildItem lib/*.jar | ForEach-Object { "lib/$($_.Name)" }) -join ';'

Remove-Item build, dist -Recurse -Force -ErrorAction SilentlyContinue
New-Item -ItemType Directory build/classes, dist | Out-Null
Get-ChildItem src/main/java -Recurse -Filter *.java | ForEach-Object { $_.FullName } | Set-Content build/sources.txt -Encoding ASCII
& "${bin}javac" --release 8 -encoding UTF-8 -Xlint:-options -cp $cp -d build/classes '@build/sources.txt'
if ($LASTEXITCODE) { exit $LASTEXITCODE }
Copy-Item src/main/resources/* build/classes -Recurse -Force
& "${bin}jar" cf "dist/tessera-$version.jar" -C build/classes .
"dist/tessera-$version.jar"

if ($Demo) {
  New-Item -ItemType Directory build/demo | Out-Null
  $demo = Get-ChildItem src/demo/java -Recurse -Filter *.java | ForEach-Object { $_.FullName }
  & "${bin}javac" --release 8 -encoding UTF-8 -Xlint:-options -cp "build/classes;$cp" -d build/demo $demo
  & "${bin}java" -cp "build/demo;build/classes;$cp" tessera.demo.Showcase
}
