package core;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class SDASLogger {
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("HH:mm:ss");
    private final Path logFile;

    public SDASLogger(Path logFile) {
        this.logFile = logFile;
        init();
    }

    private void init() {
        try {
            Path parent = logFile.getParent();
            if (parent != null) Files.createDirectories(parent);
            if (!Files.exists(logFile)) Files.createFile(logFile);
        } catch (IOException e) {
            throw new RuntimeException("No se pudo inicializar el log: " + logFile, e);
        }
    }

    public void log(String module, String msg) {
        String line = String.format("[%s] [%s] %s",
                LocalDateTime.now().format(FMT),
                module,
                msg
        );
        append(line);
    }

    public void log(Alert alert) {
        String line = String.format("[%s] [%s] %s",
                alert.getTimestamp().format(FMT),
                alert.getModule(),
                alert.getMessage()
        );
        append(line);
    }

    private synchronized void append(String line) {
        try (BufferedWriter bw = Files.newBufferedWriter(logFile, StandardOpenOption.APPEND)) {
            bw.write(line);
            bw.newLine();
        } catch (IOException e) {
            throw new RuntimeException("Error escribiendo log_sdas.txt", e);
        }
    }
}
