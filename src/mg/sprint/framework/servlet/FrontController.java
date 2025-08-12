package mg.sprint.framework.servlet;

import com.google.gson.Gson;
import com.thoughtworks.paranamer.BytecodeReadingParanamer;
import com.thoughtworks.paranamer.Paranamer;

import mg.sprint.framework.annotations.Controller;
import mg.sprint.framework.annotations.RequestField;
import mg.sprint.framework.annotations.RequestObject;
import mg.sprint.framework.annotations.RequestParam;
import mg.sprint.framework.annotations.RestAPI;
import mg.sprint.framework.annotations.Route;
import mg.sprint.framework.core.Mapping;
import mg.sprint.framework.core.ModelView;
import mg.sprint.framework.core.RouteRegistry;
import mg.sprint.framework.core.RouteScanner;
import mg.sprint.framework.utils.ConvertUtil;

import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;

@WebServlet(name = "FrontController", urlPatterns = "/*", loadOnStartup = 1)
public class FrontController extends HttpServlet {
    private final Map<String, Mapping> routes = new HashMap<>();

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
                throw new ServletException("Aucune classe trouvée dans le package " + basePackage);
            }

            Set<String> allPaths = new HashSet<>();

            for (Class<?> cls : classes) {
                if (cls.isAnnotationPresent(Controller.class)) {
                    for (Method method : cls.getDeclaredMethods()) {
                        if (method.isAnnotationPresent(Route.class)) {
                            String path = method.getAnnotation(Route.class).path();

                            if (allPaths.contains(path)) {
                                throw new ServletException("Route dupliquée détectée pour le chemin : " + path);
                            }

                            Mapping mapping = new Mapping(cls, method);
                            routes.put(path, mapping);
                            RouteRegistry.register(path, mapping);
                            allPaths.add(path);
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
        try {
            processRequest(req, resp);
        } catch (Exception e) {
            e.printStackTrace(resp.getWriter());
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
        throws ServletException, IOException {
        try {
            processRequest(req, resp);
        } catch (Exception e) {
            e.printStackTrace(resp.getWriter());
        }
    }

    
    private void processRequest(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        String contextPath = req.getContextPath();
        String uri = req.getRequestURI();
        String path = uri.substring(contextPath.length());

        Mapping mapping = routes.get(path);
        if (mapping == null) {
            resp.getWriter().println("Erreur 404 : URL non trouvée");
            return;
        }

        Method method = mapping.method;
        Object controllerInstance = mapping.controllerClass.getDeclaredConstructor().newInstance();
        Object[] args = buildMethodArguments(method, req);

        Object result = method.invoke(controllerInstance, args);

        boolean isRestAPI = method.isAnnotationPresent(RestAPI.class);

        if (isRestAPI) {
            resp.setContentType("application/json;charset=UTF-8");
            Gson gson = new Gson();

            if (result instanceof ModelView) {
                ModelView mv = (ModelView) result;
                String json = gson.toJson(mv.getData());
                resp.getWriter().println(json);
            } else {
                String json = gson.toJson(result);
                resp.getWriter().println(json);
            }
        } else {
            if (result instanceof String) {
                resp.getWriter().println((String) result);
            } else if (result instanceof ModelView) {
                ModelView mv = (ModelView) result;
                for (Map.Entry<String, Object> entry : mv.getData().entrySet()) {
                    req.setAttribute(entry.getKey(), entry.getValue());
                }
                req.getRequestDispatcher(mv.getUrl()).forward(req, resp);
            } else {
                resp.getWriter().println("Type de retour non reconnu : " + result.getClass().getName());
            }
        }
    }
// import mg.sprint.framework.session.MySession;

    private Object[] buildMethodArguments(Method method, HttpServletRequest req) throws Exception {
        Parameter[] parameters = method.getParameters();
        Object[] args = new Object[parameters.length];

        Paranamer paranamer = new BytecodeReadingParanamer();
        String[] paramNames = paranamer.lookupParameterNames(method, false);

        for (int i = 0; i < parameters.length; i++) {
            Parameter param = parameters[i];

            if (param.getType().equals(mg.sprint.framework.session.MySession.class)) {
                args[i] = new mg.sprint.framework.session.MySession(req.getSession());
            }
            else if (param.isAnnotationPresent(RequestObject.class)) {
                Object obj = param.getType().getDeclaredConstructor().newInstance();
                for (Field field : obj.getClass().getDeclaredFields()) {
                    String fieldName = field.getName();
                    if (field.isAnnotationPresent(RequestField.class)) {
                        fieldName = field.getAnnotation(RequestField.class).value();
                    }
                    String value = req.getParameter(fieldName);
                    if (value != null) {
                        field.setAccessible(true);
                        field.set(obj, ConvertUtil.convertValue(value, field.getType()));
                    }
                }
                args[i] = obj;
            }
            else {
                String name = null;

                if (param.isAnnotationPresent(RequestParam.class)) {
                    name = param.getAnnotation(RequestParam.class).value();
                } else if (paramNames != null && i < paramNames.length) {
                    name = paramNames[i];
                } else {
                    throw new IllegalArgumentException("Nom du paramètre introuvable pour le paramètre #" + i);
                }

                String value = req.getParameter(name);
                if (value == null) {
                    throw new IllegalArgumentException("Paramètre '" + name + "' manquant dans la requête");
                }

                args[i] = ConvertUtil.convertValue(value, param.getType());
            }
        }

        return args;
    }


}
