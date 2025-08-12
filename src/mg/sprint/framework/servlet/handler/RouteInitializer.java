package mg.sprint.framework.servlet.handler;

import mg.sprint.framework.annotation.auth.AuthController;
import mg.sprint.framework.annotation.auth.AuthMethod;
import mg.sprint.framework.annotation.auth.ClassLevel;
import mg.sprint.framework.annotation.controller.Controller;
import mg.sprint.framework.annotation.http.Get;
import mg.sprint.framework.annotation.http.Post;
import mg.sprint.framework.annotation.method.Url;
import mg.sprint.framework.core.object.Mapping;
import mg.sprint.framework.core.route.RouteRegistry;
import mg.sprint.framework.core.route.RouteScanner;

import javax.servlet.ServletException;
import java.lang.reflect.Method;
import java.util.*;

public class RouteInitializer {
    private Set<Integer> validLevels = new HashSet<>();

    public Map<String, Mapping> initializeRoutes(String basePackage, String modelPackage, ClassLoader classLoader)
            throws Exception {

        // Charger les classes du package des modèles pour collecter les niveaux
        if (modelPackage != null && !modelPackage.isEmpty()) {
            List<Class<?>> modelClasses = RouteScanner.scan(modelPackage, classLoader);
            for (Class<?> modelClass : modelClasses) {
                if (modelClass.isAnnotationPresent(ClassLevel.class)) {
                    int level = modelClass.getAnnotation(ClassLevel.class).value();
                    validLevels.add(level);
                }
            }
        }

        List<Class<?>> classes = RouteScanner.scan(basePackage, classLoader);
        if (classes.isEmpty()) {
            throw new ServletException("Aucune classe trouvée dans le package " + basePackage);
        }

        Map<String, Mapping> routes = new HashMap<>();
        RouteValidator routeValidator = new RouteValidator();

        for (Class<?> cls : classes) {
            if (cls.isAnnotationPresent(Controller.class)) {
                processControllerClass(cls, routes, routeValidator);
            } else if (cls.isAnnotationPresent(AuthController.class)) {
                throw new ServletException(
                    "La classe " + cls.getName() + " utilise @AuthController sans @Controller"
                );
            }
        }

        return routes;
    }

    private void processControllerClass(Class<?> cls, Map<String, Mapping> routes,
                                       RouteValidator routeValidator) throws ServletException {
        // Vérifier si @AuthController est présent et récupérer son niveau
        int authControllerLevel = -1;
        if (cls.isAnnotationPresent(AuthController.class)) {
            authControllerLevel = cls.getAnnotation(AuthController.class).level();
            if (!validLevels.contains(authControllerLevel)) {
                throw new ServletException(
                    "Niveau d'autorisation " + authControllerLevel + " de @AuthController dans la classe " +
                    cls.getName() + " n'est défini dans aucune classe modèle"
                );
            }
        }

        for (Method method : cls.getDeclaredMethods()) {
            if (method.isAnnotationPresent(Url.class)) {
                String url = method.getAnnotation(Url.class).path();
                String verb = determineHttpVerb(method);

                // Valider le niveau de @AuthMethod
                if (method.isAnnotationPresent(AuthMethod.class)) {
                    int level = method.getAnnotation(AuthMethod.class).level();
                    if (!validLevels.contains(level)) {
                        throw new ServletException(
                            "Niveau d'autorisation " + level + " de @AuthMethod dans la méthode " +
                            method.getName() + " n'est défini dans aucune classe modèle"
                        );
                    }
                }

                routeValidator.validateRoute(url, verb);

                Mapping mapping = routes.getOrDefault(url, new Mapping(cls, method, authControllerLevel));
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