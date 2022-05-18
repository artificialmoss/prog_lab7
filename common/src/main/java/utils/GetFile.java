package utils;

import utils.exceptions.NoFileException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Interface for checking whether a file with the specified path exists and is readable or writable
 */
public interface GetFile {
    /**
     * Checks whether a file is readable
     * @param filename The filepath
     * @throws NoFileException Thrown when the filepath is null, doesn't exist, is a directory, or is not readable
     */
    default void getReadableFile(String filename) throws NoFileException {
        try {
            Path path = Paths.get(filename);
            if (!Files.exists(path) | Files.isDirectory(path) | !Files.isReadable(path)) {
                throw new NoFileException("This file doesn't exist or can't be accessed.");
            }
        } catch (NullPointerException e) {
            throw new NoFileException();
        }
    }

    /**
     * Gets a writable file with the specified filepath
     * @param filename The filepath
     * @param defaultName The default filepath in case the first filepath doesn't result in a writable file
     * @return The resulting file
     * @throws NoFileException Thrown when the file with the specified path can't be written into
     */
    default File getWritableFile(String filename, String defaultName) throws NoFileException {
        Path path;
        if (filename != null) {
            path = Paths.get(filename);
        } else path = Paths.get(defaultName);
        if (filename == null || (Files.exists(path) && (Files.isDirectory(path) | !Files.isWritable(path)))) {
            Path defaultPath = Paths.get(defaultName);
            if (Files.exists(defaultPath) && (Files.isDirectory(defaultPath) | !Files.isWritable(defaultPath))) {
                throw new NoFileException("Can't create a writable file with this name.");
            } else {
                File file = new File(defaultName);
                try {
                    file.getAbsoluteFile().getParentFile().mkdirs();
                    file.createNewFile();
                    return file;
                } catch (IOException e) {
                    System.out.println("get1");
                    throw new NoFileException("Can't create a writable file with this name.");
                }
            }
        } else {
            File file = new File(filename);
            try {
                file.getAbsoluteFile().getParentFile().mkdirs();
                file.createNewFile();
                return file;
            } catch (IOException e) {
                throw new NoFileException("Can't create a writable file with this name.");
            }
        }
    }
}
