package com.jesusbmx.tessera.preview;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.netbeans.api.java.classpath.ClassPath;
import org.netbeans.modules.masterfs.providers.AnnotationProvider;
import org.netbeans.modules.masterfs.providers.InterceptionListener;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileStateInvalidException;
import org.openide.filesystems.FileStatusEvent;
import org.openide.util.ImageUtilities;
import org.openide.util.Lookup;
import org.openide.util.RequestProcessor;

/**
 * Pone una insignia (una ventanita azul) sobre el icono de los .java cuya clase hereda de un componente Swing
 * (JPanel, JDialog, JFrame...), en Projects, Files y donde NetBeans pinte el icono del archivo.
 *
 * Se registra como {@code AnnotationProvider} de masterfs (ver META-INF/services). NetBeans usa el primer proveedor que
 * devuelve un icono, no los encadena: por eso este consulta primero a los demas (Git, Mercurial...) y pone su insignia
 * encima del resultado, asi no se pierden las insignias del control de versiones.
 *
 * Detectar la herencia es barato y asincrono: se lee el archivo (con cache por fecha de modificacion) y, si hereda de
 * una clase del propio proyecto, se sigue la cadena por las fuentes. Si el icono aun no se sabe, no se pone nada y, al
 * terminar de revisar, se avisa a NetBeans para que lo repinte.
 */
public final class ComponentBadge extends AnnotationProvider {

  /** Clases base de Swing que convierten a un archivo en "un componente". */
  private static final Set<String> SWING_BASES = new HashSet<>(Arrays.asList(
      "JPanel", "JDialog", "JFrame", "JWindow", "JInternalFrame", "JApplet"));

  private static final int MAX_DEPTH = 6;
  private static final Pattern COMMENTS = Pattern.compile("/\\*.*?\\*/|//[^\\n]*", Pattern.DOTALL);
  private static final Pattern PACKAGE = Pattern.compile("\\bpackage\\s+([\\w.]+)\\s*;");
  private static final Pattern IMPORT = Pattern.compile("\\bimport\\s+(?!static\\b)([\\w.]+)\\s*;");

  private static final RequestProcessor RP = new RequestProcessor("Tessera component badge", 1);
  private static final Image BADGE = createBadge();

  /** Resultado ya calculado de un archivo, valido mientras no cambie su fecha de modificacion. */
  private static final class Entry {
    final long modified;
    final boolean component;

    Entry(long modified, boolean component) {
      this.modified = modified;
      this.component = component;
    }
  }

  private final Map<FileObject, Entry> cache = Collections.synchronizedMap(new WeakHashMap<FileObject, Entry>());
  private final Set<FileObject> pending = Collections.synchronizedSet(new HashSet<FileObject>());

  /**
   * Evita la recursion infinita: otros proveedores (p. ej. ErrorAnnotator) tambien encadenan a todos los demas, y
   * al llamarnos de vuelta se formaba un ciclo A -> B -> A que desbordaba la pila.
   */
  private static final ThreadLocal<Boolean> REENTRANT = new ThreadLocal<Boolean>();

  @Override
  public Image annotateIcon(Image icon, int type, Set<? extends FileObject> files) {
    if (REENTRANT.get() != null) {
      return null;
    }
    REENTRANT.set(Boolean.TRUE);
    try {
      return annotate(icon, type, files);
    } finally {
      REENTRANT.remove();
    }
  }

  private Image annotate(Image icon, int type, Set<? extends FileObject> files) {
    Image result = null;
    for (AnnotationProvider other : Lookup.getDefault().lookupAll(AnnotationProvider.class)) {
      if (other != this) {
        result = other.annotateIcon(icon, type, files);
        if (result != null) {
          break;
        }
      }
    }
    if (result == null) {
      result = icon;
    }
    final FileObject file = singleJavaFile(files);
    if (file != null && isKnownComponent(file) && result.getWidth(null) >= 16) {
      return ImageUtilities.mergeImages(result, BADGE, result.getWidth(null) - BADGE.getWidth(null),
          result.getHeight(null) - BADGE.getHeight(null));
    }
    return result == icon ? null : result;
  }

  /** Solo el nombre lo pintan otros proveedores: devolver null deja que siga el siguiente. */
  @Override
  public String annotateName(String name, Set<? extends FileObject> files) {
    return null;
  }

  @Override
  public String annotateNameHtml(String name, Set<? extends FileObject> files) {
    return null;
  }

  @Override
  public InterceptionListener getInterceptionListener() {
    return null;
  }

  private static FileObject singleJavaFile(Set<? extends FileObject> files) {
    if (files == null || files.size() != 1) {
      return null;
    }
    final FileObject file = files.iterator().next();
    return file != null && file.isData() && "java".equalsIgnoreCase(file.getExt()) ? file : null;
  }

  /** Lo que ya se sabe del archivo; si no se sabe (o esta viejo) programa la revision y responde que no por ahora. */
  private boolean isKnownComponent(FileObject file) {
    final Entry entry = cache.get(file);
    final long modified = file.lastModified().getTime();
    if (entry != null && entry.modified == modified) {
      return entry.component;
    }
    if (pending.add(file)) {
      RP.post(new Runnable() {
        @Override
        public void run() {
          scan(file, modified, entry != null && entry.component);
        }
      });
    }
    // mientras se revisa, se conserva lo anterior para que el icono no parpadee al guardar
    return entry != null && entry.component;
  }

  private void scan(FileObject file, long modified, boolean before) {
    boolean component = false;
    try {
      component = inheritsFromSwing(file, new HashSet<String>(), 0);
    } catch (RuntimeException | IOException e) {
      // un archivo ilegible simplemente no lleva insignia
    } finally {
      cache.put(file, new Entry(modified, component));
      pending.remove(file);
    }
    if (component != before) {
      try {
        fireFileStatusChanged(new FileStatusEvent(file.getFileSystem(), Collections.singleton(file), true, false));
      } catch (FileStateInvalidException e) {
        // el archivo ya no existe
      }
    }
  }

  // ---------------------------------------------------------------------
  // Herencia: del archivo hacia arriba por las fuentes del proyecto
  // ---------------------------------------------------------------------

  private static boolean inheritsFromSwing(FileObject file, Set<String> visited, int depth) throws IOException {
    if (depth > MAX_DEPTH || !visited.add(file.getPath())) {
      return false;
    }
    final String source = COMMENTS.matcher(new String(file.asBytes(), StandardCharsets.UTF_8)).replaceAll(" ");
    final Matcher extendsMatch = Pattern
        .compile("\\bclass\\s+" + Pattern.quote(file.getName()) + "\\b\\s*(?:<[^{]*?>)?\\s*extends\\s+([\\w.]+)")
        .matcher(source);
    if (!extendsMatch.find()) {
      return false;
    }
    final String parent = extendsMatch.group(1);
    final String simple = parent.substring(parent.lastIndexOf('.') + 1);
    if (SWING_BASES.contains(simple)) {
      return true;
    }
    final ClassPath sources = ClassPath.getClassPath(file, ClassPath.SOURCE);
    if (sources == null) {
      return false;
    }
    for (String candidate : candidates(parent, simple, source)) {
      final FileObject parentFile = sources.findResource(candidate.replace('.', '/') + ".java");
      if (parentFile != null && inheritsFromSwing(parentFile, visited, depth + 1)) {
        return true;
      }
    }
    return false;
  }

  /** Nombres completos posibles de la clase padre: tal cual, por import, del mismo paquete o por import con asterisco. */
  private static Set<String> candidates(String parent, String simple, String source) {
    final Set<String> result = new java.util.LinkedHashSet<>();
    if (parent.indexOf('.') >= 0) {
      result.add(parent);
    }
    final Set<String> wildcards = new java.util.LinkedHashSet<>();
    final Matcher imports = IMPORT.matcher(source);
    while (imports.find()) {
      final String name = imports.group(1);
      if (name.endsWith("." + simple)) {
        result.add(name);
      }
    }
    final Matcher wild = Pattern.compile("\\bimport\\s+([\\w.]+)\\.\\*\\s*;").matcher(source);
    while (wild.find()) {
      wildcards.add(wild.group(1) + "." + simple);
    }
    final Matcher pkg = PACKAGE.matcher(source);
    result.add(pkg.find() ? pkg.group(1) + "." + simple : simple);
    result.addAll(wildcards);
    return result;
  }

  // ---------------------------------------------------------------------
  // La insignia: una ventanita azul de 8x8 en la esquina inferior derecha
  // ---------------------------------------------------------------------

  private static Image createBadge() {
    final BufferedImage image = new BufferedImage(8, 8, BufferedImage.TYPE_INT_ARGB);
    final Graphics2D g = image.createGraphics();
    try {
      g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
      g.setColor(new Color(0x1976D2));
      g.fillRoundRect(0, 0, 8, 8, 2, 2);
      g.setColor(Color.WHITE);
      g.fillRect(1, 1, 6, 2);      // barra de titulo
      g.drawRect(1, 4, 5, 2);      // cuerpo de la ventana
    } finally {
      g.dispose();
    }
    return image;
  }
}
