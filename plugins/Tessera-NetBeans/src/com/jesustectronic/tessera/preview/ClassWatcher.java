package com.jesustectronic.tessera.preview;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.List;

/**
 * Vigila los .class de unas carpetas (las del proyecto, que el IDE recompila al guardar) y avisa cuando cambian. Espera a
 * que dejen de cambiar un momento antes de avisar, porque el IDE compila varias clases seguidas. No depende de NetBeans.
 */
final class ClassWatcher {

  /** Tiempo (ms) sin nuevos cambios antes de avisar. */
  private static final long QUIET_MS = 700;
  private static final long POLL_MS = 400;

  private final List<File> roots;
  private final Runnable onChange;
  private volatile boolean stopped;

  ClassWatcher(List<File> roots, Runnable onChange) {
    this.roots = roots;
    this.onChange = onChange;
  }

  /** Empieza a vigilar en un hilo propio. */
  void start() {
    final Thread thread = new Thread(this::watch, "Tessera Preview watcher");
    thread.setDaemon(true);
    thread.start();
  }

  void stop() {
    stopped = true;
  }

  /** Cambia cuando se agrega, quita o modifica un .class de las carpetas vigiladas. */
  static long signature(List<File> roots) {
    final long[] result = new long[2];             // fecha mas reciente, cantidad
    for (File root : roots) {
      if (!root.isDirectory()) {
        continue;
      }
      try {
        Files.walkFileTree(root.toPath(), new SimpleFileVisitor<Path>() {
          @Override
          public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
            if (file.toString().endsWith(".class")) {
              result[0] = Math.max(result[0], attrs.lastModifiedTime().toMillis());
              result[1]++;
            }
            return FileVisitResult.CONTINUE;
          }
        });
      } catch (IOException ignored) {
        // la carpeta cambio mientras se leia: se vuelve a leer en la siguiente vuelta
      }
    }
    return result[0] * 31 + result[1];
  }

  private void watch() {
    long baseline = signature(roots);
    long pendingSince = 0;
    while (!stopped) {
      try {
        Thread.sleep(POLL_MS);
      } catch (InterruptedException e) {
        return;
      }
      final long now = signature(roots);
      if (now != baseline) {
        baseline = now;
        pendingSince = System.currentTimeMillis();           // sigue cambiando: se espera a que pare
      } else if (pendingSince != 0 && System.currentTimeMillis() - pendingSince >= QUIET_MS) {
        pendingSince = 0;
        if (!stopped) {
          onChange.run();
        }
      }
    }
  }
}
