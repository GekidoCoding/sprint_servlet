package mg.sprint.framework.core.route;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RouteScanner {
    private static final Logger logger = LoggerFactory.getLogger(RouteScanner.class);

    public static List<Class<?>> scan(String basePackage, ClassLoader classLoader) throws ClassNotFoundException {
        logger.info("Scanning package: {}", basePackage);
        String path = basePackage.replace('.', '/');
        URL resource = classLoader.getResource(path);
        if (resource == null) {
            logger.error("Package {} not found", basePackage);
            throw new ClassNotFoundException("Package " + basePackage + " introuvable");
        }

        File directory = new File(resource.getFile());
        List<Class<?>> classes = new ArrayList<>();
        findClasses(directory, basePackage, classes, classLoader);
        logger.info("Found {} classes in package {}", classes.size(), basePackage);
        return classes;
    }

    private static void findClasses(File directory, String packageName, List<Class<?>> classes, ClassLoader classLoader) throws ClassNotFoundException {
        if (!directory.exists()) {
            logger.warn("Directory {} does not exist", directory.getPath());
            return;
        }
        for (File file : directory.listFiles()) {
            if (file.isDirectory()) {
                logger.debug("Scanning subdirectory: {}.{}", packageName, file.getName());
                findClasses(file, packageName + "." + file.getName(), classes, classLoader);
            } else if (file.getName().endsWith(".class")) {
                String className = packageName + '.' + file.getName().substring(0, file.getName().length() - 6);
                Class<?> clazz = classLoader.loadClass(className);
                classes.add(clazz);
                logger.trace("Found class: {}", className);
            }
        }
    }
}