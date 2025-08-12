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
import mg.sprint.framework.core.ModelView;
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
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        processRequest(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        processRequest(req, resp);
    }

    private void processRequest(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = req.getRequestURI().substring(req.getContextPath().length());

        Mapping mapping = RouteRegistry.getMapping(path);

        if (mapping == null) {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            resp.getWriter().write("404 - Route not found for " + path);
            return;
        }

        try {
            Object controllerInstance = mapping.controllerClass.getDeclaredConstructor().newInstance();
            Object result = mapping.method.invoke(controllerInstance);

            if (result == null) {
                resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
                return;
            }

            if (result instanceof String) {
                resp.setContentType("text/plain;charset=UTF-8");
                resp.getWriter().write(result.toString());

            } else if (result instanceof ModelView) {
                ModelView mv = (ModelView)result;
                for (Map.Entry<String, Object> entry : mv.getData().entrySet()) {
                    req.setAttribute(entry.getKey(), entry.getValue());
                }
                // Dispatch vers la vue (jsp)
                req.getRequestDispatcher(mv.getUrl()).forward(req, resp);

            } else {
                resp.setContentType("text/plain;charset=UTF-8");
                resp.getWriter().write("Méthode de retour non reconnue");
            }

        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("500 - Internal Server Error\n");
            e.printStackTrace(resp.getWriter());
        }
    }

}
