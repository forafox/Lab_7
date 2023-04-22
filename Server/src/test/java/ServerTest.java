import org.junit.jupiter.api.Test;

import java.io.IOException;

/**
 * @author Karabanov Andrey
 * @version 1.0
 * @date 19.04.2023 0:45
 */
class ServerTest {
    @Test
    public void test_loadResource() throws IOException {
        String content = CommonTestUtility.getFileAsString("data/credentials.txt");
        System.out.println(content);
    }

}
