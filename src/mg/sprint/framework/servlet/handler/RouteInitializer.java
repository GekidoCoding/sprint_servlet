package mg.sprint.framework.servlet.handler;

import mg.sprint.framework.annotation.auth.AuthController;
import mg.sprint.framework.annotation.auth.AuthMethod;
import mg.sprint.framework.annotation.auth.ClassLevel;
import mg.sprint.framework.annotation.controller.BaseUrl;
import mg.sprint.framework.annotation.controller.Controller;
import mg.sprint.framework.annotation.http.Get;
import mg.sprint.framework.annotation.http.Post;
import mg.sprint.framework.annotation.method.Url;
import mg.sprint.framework.core.object.Mapping;
import mg.sprint.framework.core.object.VerbAction;
import mg.sprint.framework.core.route.RouteRegistry;
import mg.sprint.framework.core.route.RouteScanner;

import javax.servlet.ServletException;
import java.lang.reflect.Method;
import java.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RouteInitializer {
    private static final Logger logger = LoggerFactory.getLogger(RouteInitializer.class);
    private Set<Integer> validLevels = new HashSet<>();

    public Map<String, Mapping> initializeRoutes(String basePackage, String modelPackage, ClassLoader classLoader)
            throws Exception {
        logger.info("Initializing routes for basePackage={}, modelPackage={}", basePackage, modelPackage);

        if (modelPackage != null && !modelPackage.isEmpty()) {
            List<Class<?>> modelClasses = RouteScanner.scan(modelPackage, classLoader);
            for (Class<?> modelClass : modelClasses) {
                if (modelClass.isAnnotationPresent(ClassLevel.class)) {
                    int level = modelClass.getAnnotation(ClassLevel.class).value();
                    validLevels.add(level);
                    logger.debug("Found model class {} with level {}", modelClass.getName(), level);
                }
            }
        }

        List<Class<?>> classes = RouteScanner.scan(basePackage, classLoader);
        if (classes.isEmpty()) {
            logger.error("No classes found in package {}", basePackage);
            throw new ServletException("Aucune classe trouvée dans le package " + basePackage);
        }

        Map<String, Mapping> routes = new HashMap<>();
        RouteValidator routeValidator = new RouteValidator();

        for (Class<?> cls : classes) {
            if (cls.isAnnotationPresent(Controller.class)) {
                processControllerClass(cls, routes, routeValidator);
            } else if (cls.isAnnotationPresent(AuthController.class)) {
                logger.error("Class {} uses @AuthController without @Controller", cls.getName());
                throw new ServletException(
                    "La classe " + cls.getName() + " utilise @AuthController sans @Controller"
                );
            }
        }

        logAllRoutes(routes);
        return routes;
    }

    private void processControllerClass(Class<?> cls, Map<String, Mapping> routes,
                                       RouteValidator routeValidator) throws ServletException {
        logger.debug("Processing controller class: {}", cls.getName());

        int authControllerLevel = -1;
        if (cls.isAnnotationPresent(AuthController.class)) {
            authControllerLevel = cls.getAnnotation(AuthController.class).level();
            if (!validLevels.contains(authControllerLevel)) {
                logger.error("Invalid auth level {} in class {}", authControllerLevel, cls.getName());
                throw new ServletException(
                    "Niveau d'autorisation " + authControllerLevel + " de @AuthController dans la classe " +
                    cls.getName() + " n'est défini dans aucune classe modèle"
                );
            }
        }

        String index_path = cls.isAnnotationPresent(BaseUrl.class) 
            ? cls.getAnnotation(BaseUrl.class).path() 
            : "";
        logger.debug("Base URL for class {}: {}", cls.getName(), index_path);

        for (Method method : cls.getDeclaredMethods()) {
            if (method.isAnnotationPresent(Url.class)) {
                String url = index_path + method.getAnnotation(Url.class).path();
                String verb = determineHttpVerb(method);

                if (method.isAnnotationPresent(AuthMethod.class)) {
                    int level = method.getAnnotation(AuthMethod.class).level();
                    if (!validLevels.contains(level)) {
                        logger.error("Invalid auth level {} in method {}.{}", 
                            level, cls.getName(), method.getName());
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
                logger.info("Registered route: url={}, verb={}, controller={}, method={}, authLevel={}", 
                    url, verb, cls.getName(), method.getName(), authControllerLevel);
            }
        }
    }

    private String determineHttpVerb(Method method) {
        String verb = "GET";
        if (method.isAnnotationPresent(Post.class)) {
            verb = "POST";
        } else if (method.isAnnotationPresent(Get.class)) {
            verb = "GET";
        }
        logger.trace("Determined HTTP verb for method {}: {}", method.getName(), verb);
        return verb;
    }

    public void logAllRoutes(Map<String, Mapping> routes) {
        logger.info("=== Registered Routes ===");
        for (Map.Entry<String, Mapping> entry : routes.entrySet()) {
            String path = entry.getKey();
            Mapping mapping = entry.getValue();
            for (VerbAction va : mapping.getVerbActions()) {
                logger.info("Route: path={}, verb={}, controller={}, method={}, authLevel={}", 
                    path, va.getVerb(), mapping.getControllerClass().getName(), 
                    va.getMethodName(), mapping.getControllerAuthLevel());
            }
        }
        logger.info("=== End of Registered Routes ===");
    }
}