package mg.sprint.framework.core;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

public class RouteScanner {
    public static List<Class<?>> scan(String basePackage ,  ClassLoader classLoader ) throws IOException, ClassNotFoundException {
        List<Class<?>> classes = new ArrayList<>() ;
        String path = basePackage.replace('.', '/');
        Enumeration<URL> resources = classLoader.getResources(path);

        while (resources.hasMoreElements()) {
            URL resource = resources.nextElement();
            File directory = new File(resource.getFile());
            if (directory.exists()) {
                for (String file : directory.list()) {
                    if (file.endsWith(".class")) {
                        String className = basePackage + "." + file.substring(0, file.length() - 6);
                        Class<?> clazz = Class.forName(className);
                        classes.add(clazz);
                    }
                }
            }
        }

        return classes;
    }
}
