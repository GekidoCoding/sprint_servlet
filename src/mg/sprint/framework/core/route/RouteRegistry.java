package mg.sprint.framework.core.route;

import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mg.sprint.framework.core.object.Mapping;

public class RouteRegistry {
    private static final Logger logger = LoggerFactory.getLogger(RouteRegistry.class);
    private static final Map<String, Mapping> routes = new HashMap<>();

    public static void register(String path, Mapping mapping) {
        routes.put(path, mapping);
        logger.info("Registered route: path={}, controller={}, authLevel={}", 
            path, mapping.getControllerClass().getName(), mapping.getControllerAuthLevel());
    }

    public static Mapping get(String path) {
        Mapping mapping = routes.get(path);
        logger.debug("Retrieved route for path: {}, found={}", path, mapping != null);
        return mapping;
    }
}