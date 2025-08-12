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
import java.util.List;

@WebServlet(name = "FrontController", urlPatterns = "/*", loadOnStartup = 1)
public class FrontController extends HttpServlet {

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);

        // Récupère le paramètre d'initialisation du package
        String basePackage = config.getInitParameter("base-package");
        if (basePackage == null || basePackage.isEmpty()) {
            throw new ServletException("Paramètre 'base-package' manquant dans web.xml");
        }

        try {
            // Scan des classes du package
            List<Class<?>> classes = RouteScanner.scan(basePackage, getClass().getClassLoader());

            System.out.println("[Sprint] Classes détectées dans le package :");
          for (Class<?> cls : classes) {
            if (cls.isAnnotationPresent(Controller.class)) {
                for (Method method : cls.getDeclaredMethods()) {
                    if (method.isAnnotationPresent(Route.class)) {
                        String path = method.getAnnotation(Route.class).path();
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
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
        throws ServletException, IOException {
        resp.getWriter().println("Sprint Framework FrontController OK");
    }
}
