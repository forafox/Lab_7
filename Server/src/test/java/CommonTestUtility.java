import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

/**
 * @author Karabanov Andrey
 * @version 1.0
 * @date 19.04.2023 0:44
 */
public class CommonTestUtility {

    public static String getFileAsString(String fileName) throws IOException {
        ClassLoader classLoader = ClassLoader.getSystemClassLoader();
        File file = new File(classLoader.getResource(fileName).getFile());
        String content = new String(Files.readAllBytes(file.toPath()));
        return content;
    }
}