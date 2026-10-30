package com.jesustectronic.tessera.preview;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import org.netbeans.api.java.classpath.ClassPath;
import org.netbeans.api.java.platform.JavaPlatform;
import org.netbeans.api.project.FileOwnerQuery;
import org.netbeans.api.project.Project;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;

/**
 * Lo que el IDE sabe de un archivo .java para mostrarlo: su clase, el classpath de ejecucion del proyecto, las carpetas
 * de clases compiladas (para vigilarlas), el tema y el JDK. Si algo falta, {@link #resolve} lanza un mensaje claro.
 *
 * Configuracion opcional del proyecto, en nbproject/tessera-preview.properties:
 * <pre>
 * theme=paquete.UI#setLookAndFeel      (metodo estatico sin parametros que instala el tema de la app)
 * </pre>
 */
final class PreviewTarget {

  static final String PREVIEWER = "tessera.app.Previewer";

  /** Un problema que el usuario puede arreglar (falta el jar de Tessera, el archivo no es de un proyecto...). */
  static final class Unavailable extends Exception {
    Unavailable(String message) {
      super(message);
    }
  }

  final String projectKey;
  final String className;
  final File projectDir;
  final String classpath;
  /** Carpetas con las clases del propio proyecto (no los jars). */
  final List<File> watchRoots;
  /** {@code null} si el proyecto no configura tema. */
  final String theme;
  final File java;

  private PreviewTarget(String projectKey, String className, File projectDir, String classpath, List<File> watchRoots,
      String theme, File java) {
    this.projectKey = projectKey;
    this.className = className;
    this.projectDir = projectDir;
    this.classpath = classpath;
    this.watchRoots = watchRoots;
    this.theme = theme;
    this.java = java;
  }

  static PreviewTarget resolve(FileObject file) throws Unavailable {
    final Project project = FileOwnerQuery.getOwner(file);
    final ClassPath source = ClassPath.getClassPath(file, ClassPath.SOURCE);
    final ClassPath exec = ClassPath.getClassPath(file, ClassPath.EXECUTE);
    final String className = source == null ? null : source.getResourceName(file, '.', false);
    if (project == null || exec == null || className == null) {
      throw new Unavailable("El archivo no pertenece a un proyecto Java con codigo fuente: " + file.getPath());
    }
    if (exec.findResource(PREVIEWER.replace('.', '/') + ".class") == null) {
      throw new Unavailable("El classpath del proyecto no trae " + PREVIEWER + ": actualiza el jar de Tessera del proyecto.");
    }

    final File projectDir = FileUtil.toFile(project.getProjectDirectory());
    final StringBuilder classpath = new StringBuilder();
    final List<File> watchRoots = new ArrayList<>();
    for (ClassPath.Entry entry : exec.entries()) {
      final File f = FileUtil.archiveOrDirForURL(entry.getURL());
      if (f == null) {
        continue;
      }
      if (classpath.length() > 0) {
        classpath.append(File.pathSeparator);
      }
      classpath.append(f.getAbsolutePath());
      if (f.isDirectory() && projectDir != null && f.getAbsolutePath().startsWith(projectDir.getAbsolutePath())) {
        watchRoots.add(f);
      }
    }
    return new PreviewTarget(project.getProjectDirectory().getPath() + ":" + className, className, projectDir,
        classpath.toString(), watchRoots, readTheme(projectDir), javaExecutable());
  }

  /** El java del JDK que usa el IDE por defecto para los proyectos (el de la plataforma por defecto). */
  private static File javaExecutable() {
    final JavaPlatform platform = JavaPlatform.getDefault();
    final FileObject tool = platform == null ? null : platform.findTool("java");
    final File file = tool == null ? null : FileUtil.toFile(tool);
    return file != null ? file : new File(System.getProperty("java.home"), "bin" + File.separator + "java");
  }

  private static String readTheme(File projectDir) {
    if (projectDir == null) {
      return null;
    }
    final File config = new File(projectDir, "nbproject" + File.separator + "tessera-preview.properties");
    if (!config.isFile()) {
      return null;
    }
    final Properties props = new Properties();
    try (InputStream in = new FileInputStream(config)) {
      props.load(in);
    } catch (IOException e) {
      return null;
    }
    final String theme = props.getProperty("theme");
    return theme == null || theme.trim().isEmpty() ? null : theme.trim();
  }
}
