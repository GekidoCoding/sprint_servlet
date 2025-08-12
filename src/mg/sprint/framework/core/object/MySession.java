package mg.sprint.framework.core.object;

import javax.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MySession {
    private static final Logger logger = LoggerFactory.getLogger(MySession.class);
    private final HttpSession session;

    public MySession(HttpSession session) {
        this.session = session;
        logger.debug("Initialized MySession");
    }

    public Object get(String key) {
        Object value = session.getAttribute(key);
        logger.trace("Retrieved session attribute: key={}, value={}", key, value);
        return value;
    }

    public void add(String key, Object object) {
        session.setAttribute(key, object);
        logger.debug("Added session attribute: key={}, value={}", key, object);
    }

    public void delete(String key) {
        session.removeAttribute(key);
        logger.debug("Removed session attribute: key={}", key);
    }
}