package calendar.persistence;

import java.io.*;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class FileHandler {

    private static final Logger log = Logger.getLogger(FileHandler.class.getName());
    private final Path dataDir;

    public FileHandler(String dataDirectory) throws IOException {
        this.dataDir = Paths.get(dataDirectory);
        ensureDirectoryExists();
    }

    // Doc file
    public List<String> readLines(String filename) throws IOException {
        Path file = dataDir.resolve(filename);
        if (!Files.exists(file)) {
            return new ArrayList<>();
        }
        List<String> lines = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(file.toFile()), "UTF-8"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String trimmed = line.trim();
                if (!trimmed.isEmpty() && !trimmed.startsWith("#")) {
                    lines.add(trimmed);
                }
            }
        }
        log.info("Doc " + lines.size() + " dong tu " + filename);
        return lines;
    }

    // Ghi thang vao file, retry neu bi Windows lock
    public void writeLines(String filename, List<String> lines) throws IOException {
        Path target = dataDir.resolve(filename);

        // Thu ghi truc tiep voi retry
        IOException lastError = null;
        for (int attempt = 1; attempt <= 3; attempt++) {
            try {
                try (BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(new FileOutputStream(target.toFile()), "UTF-8"))) {
                    writer.write("# CalendarApp data - " + filename);
                    writer.newLine();
                    for (String line : lines) {
                        writer.write(line);
                        writer.newLine();
                    }
                    writer.flush();
                }
                log.info("Ghi " + lines.size() + " dong vao " + filename);
                return; // Thanh cong
            } catch (IOException e) {
                lastError = e;
                log.warning("Lan " + attempt + " ghi that bai: " + e.getMessage() + " - thu lai...");
                try { Thread.sleep(200); } catch (InterruptedException ignored) {}
            }
        }
        throw lastError;
    }

    private void ensureDirectoryExists() throws IOException {
        if (!Files.exists(dataDir)) {
            Files.createDirectories(dataDir);
        }
    }

    public boolean fileExists(String filename) {
        return Files.exists(dataDir.resolve(filename));
    }

    public String getDataDirectory() {
        return dataDir.toString();
    }
}
