package mg.sprint.framework.servlet.handler;

import javax.servlet.ServletException;
import java.util.*;

public class RouteValidator {
    private final Map<String, Set<String>> urlVerbMap = new HashMap<>();

    public void validateRoute(String url, String verb) throws ServletException {
        urlVerbMap.putIfAbsent(url, new HashSet<>());
        
        if (!urlVerbMap.get(url).add(verb)) {
            throw new ServletException(
                "Conflit de route détecté : l'URL '" + url + 
                "' est déjà utilisée avec le verbe HTTP '" + verb + "'"
            );
        }
    }
}