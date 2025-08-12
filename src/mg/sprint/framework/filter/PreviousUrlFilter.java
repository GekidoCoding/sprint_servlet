package mg.sprint.framework.filter;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;
public class PreviousUrlFilter implements Filter {
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

            // Avant de mettre à jour, on sauvegarde l'ancien current comme previous
            String oldCurrentUrl = (String) session.getAttribute("currentUrl");
            String oldCurrentMethod = (String) session.getAttribute("currentMethod");

            session.setAttribute("previousUrl", oldCurrentUrl);
            session.setAttribute("previousMethod", oldCurrentMethod);
            System.out.println("Previous URL : " + oldCurrentUrl + " (" + oldCurrentMethod + ")");

            // Mise à jour des valeurs actuelles
            session.setAttribute("currentUrl", newCurrentUrl);
            session.setAttribute("currentMethod", method);
        }

        chain.doFilter(request, response);
    }


    private boolean isStaticOrAjax(HttpServletRequest req) {
        String uri = req.getRequestURI();
        return uri.endsWith(".css") || uri.endsWith(".js") || uri.endsWith(".png")
                || uri.endsWith(".jpg") || uri.endsWith(".ico") 
                || "XMLHttpRequest".equals(req.getHeader("X-Requested-With"));
    }
}
