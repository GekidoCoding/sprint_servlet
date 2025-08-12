package mg.sprint.framework.servlet.session;

import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SessionManager {
    private static final Logger logger = LoggerFactory.getLogger(SessionManager.class);
    private static final String PREVIOUS_URL_SESSION_KEY = "previous_url";

    public void updatePreviousUrl(HttpServletRequest req, String currentPath) {
        String previousUrl = (String) req.getSession().getAttribute(PREVIOUS_URL_SESSION_KEY);
        
        if (previousUrl == null || !previousUrl.equals(currentPath)) {
            req.getSession().setAttribute(PREVIOUS_URL_SESSION_KEY, currentPath);
            logger.debug("Updated previous URL: {}", currentPath);
        } else {
            logger.trace("Previous URL unchanged: {}", currentPath);
        }
    }

    public String getPreviousUrl(HttpServletRequest req) {
        String previousUrl = (String) req.getSession().getAttribute(PREVIOUS_URL_SESSION_KEY);
        logger.trace("Retrieved previous URL: {}", previousUrl);
        return previousUrl;
    }
}