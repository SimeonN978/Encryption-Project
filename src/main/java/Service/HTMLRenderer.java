package Service;

import spark.utils.IOUtils;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class HTMLRenderer {

    public static String render(String htmlPath) {
        return render(htmlPath, new HashMap<>());
    }

    public static String render(String htmlPath, Map<String, Object> model) {
        try (InputStream is = HTMLRenderer.class.getResourceAsStream(htmlPath)) {
            if (is == null) {
                throw new RuntimeException("HTML file not found: " + htmlPath);
            }

            String html = IOUtils.toString(is);

            for (Map.Entry<String, Object> entry : model.entrySet()) {
                String placeholder = "{{" + entry.getKey() + "}}";
                String value = entry.getValue() != null ? entry.getValue().toString() : "";
                html = html.replace(placeholder, value);
            }

            return html;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}