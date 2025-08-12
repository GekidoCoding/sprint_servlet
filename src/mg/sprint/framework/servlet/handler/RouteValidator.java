package mg.sprint.framework.servlet.handler;

import javax.servlet.ServletException;
import java.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RouteValidator {
    private static final Logger logger = LoggerFactory.getLogger(RouteValidator.class);
    private final Map<String, Set<String>> urlVerbMap = new HashMap<>();

    public void validateRoute(String url, String verb) throws ServletException {
        logger.debug("Validating route: url={}, verb={}", url, verb);
        urlVerbMap.putIfAbsent(url, new HashSet<>());
        
        if (!urlVerbMap.get(url).add(verb)) {
            logger.error("Route conflict detected: url={} already registered with verb={}", url, verb);
            throw new ServletException(
                "Conflit de route détecté : l'URL '" + url + 
                "' est déjà utilisée avec le verbe HTTP '" + verb + "'"
            );
        }
        logger.trace("Route validated successfully: url={}, verb={}", url, verb);
    }
}