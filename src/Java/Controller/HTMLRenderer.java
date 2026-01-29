package Java.Controller;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class HTMLRenderer {

    public static String render(String htmlPath) {
        try (InputStream is =
                     HTMLRenderer.class.getResourceAsStream(htmlPath)) {

            if (is == null) {
                throw new RuntimeException("HTML file not found: " + htmlPath);
            }

            return new String(is.readAllBytes(), StandardCharsets.UTF_8);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
