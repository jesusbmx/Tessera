# Compila el plugin "Tessera Preview" contra NetBeans 15 y arma dist/tessera-preview.nbm.
# Uso: ./build.ps1 [-NetBeans "C:\Program Files\NetBeans-15\netbeans"] [-Install]
# -Install copia el modulo a la carpeta de usuario de NetBeans (cierra NetBeans antes: bloquea el jar mientras corre).
param(
  [string]$NetBeans = 'C:\Program Files\NetBeans-15\netbeans',
  [switch]$Install,
  [string]$UserDir = "$env:APPDATA\NetBeans\15"
)
$ErrorActionPreference = 'Stop'
Set-Location $PSScriptRoot

$code = 'com.jesustectronic.tessera.preview'
$base = $code.Replace('.', '-')
$version = '1.0.0'

# javac suele estar en el PATH sin jar: se prefiere JAVA_HOME si existe.
$bin = if ($env:JAVA_HOME -and (Test-Path "$env:JAVA_HOME/bin/jar.exe")) { "$env:JAVA_HOME/bin/" } else { '' }

# Los modulos de NetBeans contra los que se compila (y que el plugin declara como dependencias).
$deps = @(
  @{ file = 'platform/lib/org-openide-util.jar';             dep = 'org.openide.util > 9.25' },
  @{ file = 'platform/lib/org-openide-util-lookup.jar';      dep = 'org.openide.util.lookup > 8.51' },
  @{ file = 'platform/lib/org-openide-util-ui.jar';          dep = 'org.openide.util.ui > 9.25' },
  @{ file = 'platform/core/org-openide-filesystems.jar';     dep = 'org.openide.filesystems > 9.29' },
  @{ file = 'platform/modules/org-openide-loaders.jar';      dep = 'org.openide.loaders > 7.87' },
  @{ file = 'platform/modules/org-openide-io.jar';           dep = 'org.openide.io > 1.66' },
  @{ file = 'platform/modules/org-openide-windows.jar';      dep = 'org.openide.windows > 6.94' },
  @{ file = 'platform/modules/org-openide-nodes.jar';        dep = 'org.openide.nodes > 7.62' },
  @{ file = 'platform/modules/org-openide-awt.jar';          dep = 'org.openide.awt > 7.85' },
  @{ file = 'platform/modules/org-netbeans-core-multiview.jar'; dep = 'org.netbeans.core.multiview/1 > 1.40' },
  @{ file = 'ide/modules/org-netbeans-modules-projectapi.jar'; dep = 'org.netbeans.modules.projectapi/1 > 1.87' },
  @{ file = 'ide/modules/org-netbeans-api-java-classpath.jar'; dep = 'org.netbeans.api.java.classpath/1 > 1.71' },
  @{ file = 'java/modules/org-netbeans-modules-java-platform.jar'; dep = 'org.netbeans.modules.java.platform/1 > 1.59' }
)
$cp = ($deps | ForEach-Object { Join-Path $NetBeans $_.file }) -join ';'
$dependencies = ($deps | Where-Object { $_.dep -notmatch 'nodes' } | ForEach-Object { $_.dep }) -join ', '

Remove-Item build, dist -Recurse -Force -ErrorAction SilentlyContinue
New-Item -ItemType Directory build/classes, dist | Out-Null

$sources = Get-ChildItem src -Recurse -Filter *.java | ForEach-Object { '"' + ($_.FullName -replace '\\', '/') + '"' }
$sources | Set-Content build/sources.txt -Encoding ASCII
& "${bin}javac" --release 8 -encoding UTF-8 -Xlint:-options -proc:none -cp $cp -d build/classes '@build/sources.txt'
if ($LASTEXITCODE) { exit $LASTEXITCODE }
Get-ChildItem src -Recurse | Where-Object { -not $_.PSIsContainer -and $_.Extension -ne '.java' } | ForEach-Object {
  $target = Join-Path build/classes ($_.FullName.Substring((Resolve-Path src).Path.Length + 1))
  New-Item -ItemType Directory (Split-Path $target) -Force | Out-Null
  Copy-Item $_.FullName $target
}

# MANIFEST: las lineas de mas de 72 bytes se parten con una continuacion (espacio al inicio).
function Add-Header($name, $value) {
  $line = "${name}: $value"
  $out = @()
  while ($line.Length -gt 70) { $out += $line.Substring(0, 70); $line = ' ' + $line.Substring(70) }
  $out + $line
}
$manifest = @('Manifest-Version: 1.0')
$manifest += Add-Header 'OpenIDE-Module' $code
$manifest += Add-Header 'OpenIDE-Module-Name' 'Tessera Preview'
$manifest += Add-Header 'OpenIDE-Module-Specification-Version' $version
$manifest += Add-Header 'OpenIDE-Module-Implementation-Version' $version
$manifest += Add-Header 'OpenIDE-Module-Localizing-Bundle' 'com/jesustectronic/tessera/preview/Bundle.properties'
$manifest += Add-Header 'OpenIDE-Module-Layer' 'com/jesustectronic/tessera/preview/layer.xml'
$manifest += Add-Header 'OpenIDE-Module-Module-Dependencies' $dependencies
$manifest += Add-Header 'OpenIDE-Module-Public-Packages' '-'
$manifest += Add-Header 'OpenIDE-Module-Java-Dependencies' 'Java > 1.8'
$manifest += Add-Header 'OpenIDE-Module-Requires' 'org.openide.modules.ModuleFormat1'
($manifest -join "`r`n") + "`r`n" | Set-Content build/manifest.mf -Encoding ASCII -NoNewline

& "${bin}jar" cfm "build/$base.jar" build/manifest.mf -C build/classes .
if ($LASTEXITCODE) { exit $LASTEXITCODE }

# NBM: un zip con Info/info.xml, el modulo y su estado inicial (habilitado)
$info = @"
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE module PUBLIC "-//NetBeans//DTD Autoupdate Module Info 2.5//EN" "http://www.netbeans.org/dtds/autoupdate-info-2_5.dtd">
<module codenamebase="$code" distribution="" downloadsize="0" homepage="" license="uso-interno" moduleauthor="" needsrestart="false" releasedate="$(Get-Date -Format 'yyyy/MM/dd')">
    <manifest AutoUpdate-Show-In-Client="true" OpenIDE-Module="$code" OpenIDE-Module-Name="Tessera Preview" OpenIDE-Module-Specification-Version="$version"/>
    <license name="uso-interno">Uso interno.</license>
</module>
"@
$status = @"
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE module PUBLIC "-//NetBeans//DTD Module Status 1.0//EN" "http://www.netbeans.org/dtds/module-status-1_0.dtd">
<module name="$code">
    <param name="autoload">false</param>
    <param name="eager">false</param>
    <param name="enabled">true</param>
    <param name="jar">modules/$base.jar</param>
    <param name="reloadable">false</param>
</module>
"@
Add-Type -AssemblyName System.IO.Compression, System.IO.Compression.FileSystem
$nbm = Join-Path (Resolve-Path dist).Path 'tessera-preview.nbm'
$zip = [System.IO.Compression.ZipFile]::Open($nbm, 'Create')
function Add-Text($name, $text) {
  $e = $zip.CreateEntry($name)
  $w = New-Object System.IO.StreamWriter($e.Open(), (New-Object System.Text.UTF8Encoding($false)))
  $w.Write($text); $w.Dispose()
}
Add-Text 'Info/info.xml' $info
Add-Text "netbeans/config/Modules/$base.xml" $status
[System.IO.Compression.ZipFileExtensions]::CreateEntryFromFile($zip, (Resolve-Path "build/$base.jar").Path, "netbeans/modules/$base.jar") | Out-Null
$zip.Dispose()
"dist/tessera-preview.nbm"

if ($Install) {
  New-Item -ItemType Directory "$UserDir/modules", "$UserDir/config/Modules" -Force | Out-Null
  Copy-Item "build/$base.jar" "$UserDir/modules/" -Force
  Set-Content "$UserDir/config/Modules/$base.xml" $status -Encoding UTF8
  "instalado en $UserDir (reinicia NetBeans)"
}
