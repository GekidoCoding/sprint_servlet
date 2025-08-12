package mg.sprint.framework.servlet;

import mg.sprint.framework.core.object.Mapping;
import mg.sprint.framework.servlet.handler.RequestHandler;
import mg.sprint.framework.servlet.handler.RouteInitializer;
import mg.sprint.framework.page.Error;

import javax.servlet.*;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@MultipartConfig
@WebServlet(name = "FrontController", urlPatterns = "/*", loadOnStartup = 1)
public class FrontController extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(FrontController.class);
    private Map<String, Mapping> routes;
    private RequestHandler requestHandler;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        String basePackage = config.getInitParameter("base-package");
        String modelPackage = config.getInitParameter("model-package");
        logger.info("Initializing FrontController with basePackage={}, modelPackage={}", 
            basePackage, modelPackage);

        if (basePackage == null || basePackage.isEmpty()) {
            logger.error("Missing base-package parameter in web.xml");
            throw new ServletException("Paramètre 'base-package' manquant dans web.xml");
        }

        try {
            RouteInitializer routeInitializer = new RouteInitializer();
            this.routes = routeInitializer.initializeRoutes(basePackage, modelPackage, getClass().getClassLoader());
            this.requestHandler = new RequestHandler(routes);
            logger.info("Successfully initialized routes and request handler");
        } catch (Exception e) {
            logger.error("Failed to initialize routes for package {}", basePackage, e);
            throw new ServletException("Erreur lors de l'initialisation des routes : " + basePackage, e);
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        logger.debug("Handling GET request: {}", req.getRequestURI());
        handleRequest(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        logger.debug("Handling POST request: {}", req.getRequestURI());
        handleRequest(req, resp);
    }

    private void handleRequest(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        try {
            requestHandler.processRequest(req, resp);
        } catch (Exception e) {
            logger.error("Error handling request: {}", req.getRequestURI(), e);
            new Error().displayErrorPage(resp, e);
        }
    }
}