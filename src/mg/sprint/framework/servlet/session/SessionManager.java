package mg.sprint.framework.servlet.session;

import javax.servlet.http.HttpServletRequest;

public class SessionManager {
    private static final String PREVIOUS_URL_SESSION_KEY = "previous_url";

    public void updatePreviousUrl(HttpServletRequest req, String currentPath) {
        String previousUrl = (String) req.getSession().getAttribute(PREVIOUS_URL_SESSION_KEY);
        
        if (previousUrl == null || !previousUrl.equals(currentPath)) {
            req.getSession().setAttribute(PREVIOUS_URL_SESSION_KEY, currentPath);
        }
    }

    public String getPreviousUrl(HttpServletRequest req) {
        return (String) req.getSession().getAttribute(PREVIOUS_URL_SESSION_KEY);
    }
}