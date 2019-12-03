package client.integration;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 *  Utility class for making operations to files for the client.
 */
public class FileManager {
    private static final String ROOT_PATH = System.getProperty("user.dir") + "/src/localfiles";
    private static Path localfiles = Paths.get(ROOT_PATH);

    /**
     *  Reads the content of a given file name.
     * @param filename (STRING) the file name in /localfiles.
     * @return (BYTE[]) the content of the file
     * @throws IOException if it isn't possible to read or file dies'bt exist.
     */
    public static byte[] readFile(String filename) throws IOException{
        Path filePath = localfiles.resolve(Paths.get(filename));
        if (!Files.exists(filePath)) {
            throw new FileNotFoundException("Could not find " + filePath);
        }
        return Files.readAllBytes(filePath);
    }
}
