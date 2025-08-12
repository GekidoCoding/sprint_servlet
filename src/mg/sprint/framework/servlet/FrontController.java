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
import java.util.*;

@WebServlet(name = "FrontController", urlPatterns = "/*", loadOnStartup = 1)
public class FrontController extends HttpServlet {

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);

        String basePackage = config.getInitParameter("base-package");
        if (basePackage == null || basePackage.isEmpty()) {
            throw new ServletException("Paramètre 'base-package' manquant dans web.xml");
        }

        try {
            List<Class<?>> classes = RouteScanner.scan(basePackage, getClass().getClassLoader());
            if (classes.isEmpty()) {
                throw new ServletException("Aucune classe trouvée dans le package : " + basePackage);
            }

            Set<String> paths = new HashSet<>();
            for (Class<?> cls : classes) {
                if (cls.isAnnotationPresent(Controller.class)) {
                    for (Method method : cls.getDeclaredMethods()) {
                        if (method.isAnnotationPresent(Route.class)) {
                            String path = method.getAnnotation(Route.class).path();
                            if (!paths.add(path)) {
                                throw new ServletException("Duplicate route path found: " + path);
                            }
                            Mapping mapping = new Mapping(cls, method);
                            RouteRegistry.register(path, mapping);
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
        String path = req.getRequestURI().replace(req.getContextPath(), "");
        Mapping mapping = RouteRegistry.get(path);

        if (mapping == null) {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            resp.getWriter().println("Erreur 404 - Page non trouvée : " + path);
            return;
        }

        try {
            Object controllerInstance = mapping.controllerClass.getDeclaredConstructor().newInstance();
            Object result = mapping.method.invoke(controllerInstance);

            if (result instanceof String) {
                resp.getWriter().println((String) result);
            } else if (result instanceof ModelView) {
                ModelView mv = (ModelView) result;
                for (Map.Entry<String, Object> entry : mv.getData().entrySet()) {
                    req.setAttribute(entry.getKey(), entry.getValue());
                }
                req.getRequestDispatcher(mv.getUrl()).forward(req, resp);
            } else {
                throw new ServletException("Type de retour non supporté pour la méthode : " + mapping.method.getName());
            }
        } catch (Exception e) {
            throw new ServletException("Erreur lors de l'exécution de la méthode du controller", e);
        }
    }
}