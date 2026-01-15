package integrity;

import core.SDASLogger;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.*;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;

public class IntegrityMonitor {

    private final Path folder;        
    private final SDASLogger logger;  

    // Estado previo en memoria (baseline de la ejecución actual)
    private List<FileInfo> previousState = new ArrayList<>();

    public IntegrityMonitor(Path folder, SDASLogger logger) {
        this.folder = folder;
        this.logger = logger;
    }

    
    public void scan() {
        try {
            
            if (!Files.exists(folder)) {
                logger.log("INTEGRIDAD", "Carpeta de vigilancia no existe: " + folder);
                return;
            }

            List<FileInfo> currentState = getCurrentState();

            if (previousState.isEmpty()) {
               
                previousState = currentState;
                logger.log("INTEGRIDAD", "Baseline de integridad inicializada en " + folder);
                return;
            }

            compareStates(previousState, currentState);
            previousState = currentState;

        } catch (Exception e) {
            logger.log("INTEGRIDAD", "ERROR en scan(): " + e.getMessage());
        }
    }

   
    private List<FileInfo> getCurrentState() throws Exception {
        List<FileInfo> fileList = new ArrayList<>();
        scanFolder(folder, fileList);
        return fileList;
    }

  
    private void scanFolder(Path dir, List<FileInfo> fileList) throws Exception {
        if (!Files.isDirectory(dir)) return;

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir)) {
            for (Path child : stream) {
                if (Files.isRegularFile(child)) {
                    String hash = calculateHash(child);
                    fileList.add(new FileInfo(child, hash));
                } else if (Files.isDirectory(child)) {
                    scanFolder(child, fileList);
                }
            }
        }
    }

    // SHA-256 por streaming
    private String calculateHash(Path file) throws Exception {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");

        try (InputStream is = Files.newInputStream(file)) {
            byte[] buffer = new byte[8192];
            int bytesRead;

            while ((bytesRead = is.read(buffer)) != -1) {
                digest.update(buffer, 0, bytesRead);
            }
        }

        byte[] hashBytes = digest.digest();
        StringBuilder hashString = new StringBuilder();

        for (byte b : hashBytes) {
            hashString.append(String.format("%02x", b));
        }

        return hashString.toString();
    }

    // Comparación de estados (eliminados, modificados, nuevos)
    private void compareStates(List<FileInfo> oldState, List<FileInfo> newState) {

        // Eliminados o modificados
        for (FileInfo oldFile : oldState) {
            FileInfo newFile = findFile(oldFile.getFilePath(), newState);

            if (newFile == null) {
                logger.log("INTEGRIDAD",
                        "El archivo " + oldFile.getFilePath() + " ha sido eliminado.");
            } else if (!oldFile.getHash().equals(newFile.getHash())) {
                logger.log("INTEGRIDAD",
                        "El archivo " + oldFile.getFilePath() + " ha sido modificado.");
            }
        }

        // Nuevos
        for (FileInfo newFile : newState) {
            if (findFile(newFile.getFilePath(), oldState) == null) {
                logger.log("INTEGRIDAD",
                        "El archivo " + newFile.getFilePath() + " ha sido creado.");
            }
        }
    }

    // Busca archivo por ruta
    private FileInfo findFile(Path filePath, List<FileInfo> fileList) {
        for (FileInfo file : fileList) {
            if (file.getFilePath().equals(filePath)) {
                return file;
            }
        }
        return null;
    }
}
