package mg.sprint.framework.filter;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PreviousUrlFilter implements Filter {
    private static final Logger logger = LoggerFactory.getLogger(PreviousUrlFilter.class);

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpSession session = req.getSession();

        if (!isStaticOrAjax(req)) {
            String contextPath = req.getContextPath();
            String uri = req.getRequestURI();
            String path = uri.substring(contextPath.length());

            String query = req.getQueryString();
            String newCurrentUrl = (query != null) ? path + "?" + query : path;
            String method = req.getMethod();

            String oldCurrentUrl = (String) session.getAttribute("currentUrl");
            String oldCurrentMethod = (String) session.getAttribute("currentMethod");

            session.setAttribute("previousUrl", oldCurrentUrl);
            session.setAttribute("previousMethod", oldCurrentMethod);
            logger.info("Previous URL: {} ({})", oldCurrentUrl, oldCurrentMethod);

            session.setAttribute("currentUrl", newCurrentUrl);
            session.setAttribute("currentMethod", method);
            logger.debug("Updated current URL: {} ({})", newCurrentUrl, method);
        }

        chain.doFilter(request, response);
    }

    private boolean isStaticOrAjax(HttpServletRequest req) {
        String uri = req.getRequestURI();
        boolean isStaticOrAjax = uri.endsWith(".css") || uri.endsWith(".js") || uri.endsWith(".png")
                || uri.endsWith(".jpg") || uri.endsWith(".ico") 
                || "XMLHttpRequest".equals(req.getHeader("X-Requested-With"));
        logger.trace("Checked if request is static or AJAX: uri={}, result={}", uri, isStaticOrAjax);
        return isStaticOrAjax;
    }
}