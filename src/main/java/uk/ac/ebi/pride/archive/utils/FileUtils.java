package uk.ac.ebi.pride.archive.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileUtils {
    public static Path newEmptyPath() {
        return Paths.get("");
    }

    public static void writeToFile(final File file, final String content) throws IOException {
        try (final OutputStream outputStream = new FileOutputStream(file)) {
            outputStream.write(content.getBytes());
        }
    }
}