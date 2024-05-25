package web.utils;

import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ResourcesOps {
    public static String dirUnsafe(String prefix) {
        try {
            URL resourceUrl = ResourcesOps.class.getClassLoader().getResource(prefix);
            if (resourceUrl == null) {
                throw new IllegalArgumentException("Resource not found: " + prefix);
            }
            Path resourcePath = Paths.get(resourceUrl.toURI());
            return resourcePath.toString();
        } catch (Exception ex) {
            throw new RuntimeException("Error while getting resource directory: " + prefix, ex);
        }
    }
}
