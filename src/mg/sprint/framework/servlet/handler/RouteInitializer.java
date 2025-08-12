package mg.sprint.framework.servlet.handler;

import mg.sprint.framework.core.object.Mapping;
import mg.sprint.framework.core.route.RouteRegistry;
import mg.sprint.framework.core.route.RouteScanner;
import mg.sprint.framework.annotation.controller.Controller;
import mg.sprint.framework.annotation.http.Get;
import mg.sprint.framework.annotation.http.Post;
import mg.sprint.framework.annotation.method.Url;

import javax.servlet.ServletException;
import java.lang.reflect.Method;
import java.util.*;

public class RouteInitializer {
    
    public Map<String, Mapping> initializeRoutes(String basePackage, ClassLoader classLoader) 
            throws Exception {
        
        List<Class<?>> classes = RouteScanner.scan(basePackage, classLoader);
        
        if (classes.isEmpty()) {
            throw new ServletException("Aucune classe trouvée dans le package " + basePackage);
        }

        Map<String, Mapping> routes = new HashMap<>();
        RouteValidator routeValidator = new RouteValidator();

        for (Class<?> cls : classes) {
            if (cls.isAnnotationPresent(Controller.class)) {
                processControllerClass(cls, routes, routeValidator);
            }
        }

        return routes;
    }

    private void processControllerClass(Class<?> cls, Map<String, Mapping> routes, 
                                     RouteValidator routeValidator) throws ServletException {
        
        for (Method method : cls.getDeclaredMethods()) {
            if (method.isAnnotationPresent(Url.class)) {
                String url = method.getAnnotation(Url.class).path();
                String verb = determineHttpVerb(method);

                routeValidator.validateRoute(url, verb);

                Mapping mapping = routes.getOrDefault(url, new Mapping(cls, method));
                mapping.addVerbAction(verb, method.getName());
                routes.put(url, mapping);
                RouteRegistry.register(url, mapping);
            }
        }
    }

    private String determineHttpVerb(Method method) {
        if (method.isAnnotationPresent(Post.class)) {
            return "POST";
        } else if (method.isAnnotationPresent(Get.class)) {
            return "GET";
        }
        return "GET"; // Par défaut
    }
}