package integrity;

import java.nio.file.Path;

public class FileInfo {

    private final Path filePath;
    private final String hash;

    public FileInfo(Path filePath, String hash) {
        this.filePath = filePath;
        this.hash = hash;
    }

    public Path getFilePath() {
        return filePath;
    }

    public String getHash() {
        return hash;
    }
}
