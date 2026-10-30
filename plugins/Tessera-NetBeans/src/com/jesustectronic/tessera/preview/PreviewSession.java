package com.jesustectronic.tessera.preview;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.openide.filesystems.FileObject;
import org.openide.util.RequestProcessor;
import org.openide.windows.IOProvider;
import org.openide.windows.InputOutput;

/**
 * La vista previa en ventana propia: arma el comando con {@link PreviewTarget} y la lanza con {@link PreviewProcess}.
 * Una sola sesion por clase: volver a pedirla la reinicia.
 */
final class PreviewSession {

  private static final Map<String, PreviewProcess> RUNNING = new HashMap<>();

  private PreviewSession() {
  }

  static void start(FileObject file) {
    RequestProcessor.getDefault().post(() -> run(file));
  }

  private static void run(FileObject file) {
    final InputOutput io = IOProvider.getDefault().getIO("Tessera Preview", false);
    try {
      final PreviewTarget target = PreviewTarget.resolve(file);

      final List<String> command = new ArrayList<>();
      command.add(target.java.getAbsolutePath());
      if (target.theme != null) {
        command.add("-Dtessera.preview.theme=" + target.theme);
      }
      command.add("-cp");
      command.add(target.classpath);
      command.add(PreviewTarget.PREVIEWER);
      command.add(target.className);

      final String shortName = target.className.substring(target.className.lastIndexOf('.') + 1);
      final PreviewProcess process = new PreviewProcess(command, target.projectDir, target.watchRoots,
          line -> io.getOut().println("[" + shortName + "] " + line),
          () -> {
            synchronized (RUNNING) {
              RUNNING.remove(target.projectKey);
            }
          });

      final PreviewProcess previous;
      synchronized (RUNNING) {
        previous = RUNNING.remove(target.projectKey);
        RUNNING.put(target.projectKey, process);
      }
      if (previous != null) {
        previous.stop();
      }
      io.getOut().println("[" + shortName + "] Preview: " + target.className
          + (target.watchRoots.isEmpty() ? "" : "  (se relanza solo al compilar; cierra la ventana para terminar)"));
      process.start();
    } catch (PreviewTarget.Unavailable e) {
      fail(io, e.getMessage());
    } catch (Exception e) {
      fail(io, "No se pudo lanzar la vista previa: " + e);
    }
  }

  private static void fail(InputOutput io, String message) {
    io.getErr().println("[Preview] " + message);
    io.select();
  }
}
