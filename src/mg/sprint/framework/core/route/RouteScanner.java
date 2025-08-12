package mg.sprint.framework.core.route;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class RouteScanner {

    public static List<Class<?>> scan(String basePackage, ClassLoader classLoader) throws ClassNotFoundException {
        String path = basePackage.replace('.', '/');
        URL resource = classLoader.getResource(path);
        if (resource == null) {
            throw new ClassNotFoundException("Package " + basePackage + " introuvable");
        }

        File directory = new File(resource.getFile());
        List<Class<?>> classes = new ArrayList<>();
        findClasses(directory, basePackage, classes, classLoader);
        return classes;
    }

    private static void findClasses(File directory, String packageName, List<Class<?>> classes, ClassLoader classLoader) throws ClassNotFoundException {
        if (!directory.exists()) {
            return;
        }
        for (File file : directory.listFiles()) {
            if (file.isDirectory()) {
                // appel récursif dans les sous-dossiers
                findClasses(file, packageName + "." + file.getName(), classes, classLoader);
            } else if (file.getName().endsWith(".class")) {
                String className = packageName + '.' + file.getName().substring(0, file.getName().length() - 6);
                classes.add(classLoader.loadClass(className));
            }
        }
    }
}
