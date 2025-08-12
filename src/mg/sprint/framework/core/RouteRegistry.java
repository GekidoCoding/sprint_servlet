package mg.sprint.framework.core;

import java.util.HashMap;
import java.util.Map;

public class RouteRegistry {
    private static final Map<String, Mapping> routes = new HashMap<>();

    public static void register(String path, Mapping mapping) {
        routes.put(path, mapping);
    }

    public static Mapping get(String path) {
        return routes.get(path);
    }
} 