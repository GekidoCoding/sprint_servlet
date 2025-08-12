package mg.sprint.framework.servlet;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import mg.sprint.framework.annotations.Controller;
import mg.sprint.framework.annotations.Route;
import mg.sprint.framework.core.Mapping;
import mg.sprint.framework.core.RouteRegistry;
import mg.sprint.framework.core.RouteScanner;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
@WebServlet(name = "FrontController", urlPatterns = "/*", loadOnStartup = 1)
public class FrontController extends HttpServlet {

    private Map<String, Mapping> routes = new HashMap<>();

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        String basePackage = config.getInitParameter("base-package");
        if (basePackage == null || basePackage.isEmpty()) {
            throw new ServletException("Paramètre 'base-package' manquant dans web.xml");
        }

        try {
            List<Class<?>> classes = RouteScanner.scan(basePackage, getClass().getClassLoader());

            System.out.println("[Sprint] Classes détectées dans le package :");
            for (Class<?> cls : classes) {
                if (cls.isAnnotationPresent(Controller.class)) {
                    for (Method method : cls.getDeclaredMethods()) {
                        if (method.isAnnotationPresent(Route.class)) {
                            String path = method.getAnnotation(Route.class).path();
                            Mapping mapping = new Mapping(cls, method);
                            routes.put(path, mapping);  // Copie locale
                            RouteRegistry.register(path, mapping); // Registre global
                            System.out.println("[Sprint] Route enregistrée : " + path + " -> " + cls.getName() + "." + method.getName());
                        }
                    }
                }
            }

        } catch (Exception e) {
            throw new ServletException("Erreur lors du scan du package : " + basePackage, e);
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String path = req.getRequestURI().substring(req.getContextPath().length());
        System.out.println("current path->"+path);

        if (path == null || path.isEmpty()) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Path vide");
            return;
        }

        Mapping mapping = routes.get(path); // utilisation de la copie locale
        if (mapping == null) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Route non trouvée : " + path);
            return;
        }

        try {
            Object controller = mapping.controllerClass.getDeclaredConstructor().newInstance();
            Object result = mapping.method.invoke(controller);
            resp.setContentType("text/plain");
            resp.getWriter().write(result.toString());
        } catch (Exception e) {
            throw new ServletException("Erreur lors du traitement de la route : " + path, e);
        }
    }
}
