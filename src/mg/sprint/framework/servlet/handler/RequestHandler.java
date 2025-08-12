package mg.sprint.framework.servlet.handler;

import mg.sprint.framework.annotation.auth.AuthMethod;
import mg.sprint.framework.annotation.controller.RestAPI;
import mg.sprint.framework.core.object.Mapping;
import mg.sprint.framework.core.manager.ValidationManager;
import mg.sprint.framework.exception.UnauthorizedException;
import mg.sprint.framework.page.Error;
import mg.sprint.framework.servlet.argument.ArgumentResolver;
import mg.sprint.framework.servlet.response.ResponseHandler;
import mg.sprint.framework.servlet.session.SessionManager;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Properties;

public class RequestHandler {
    private final Map<String, Mapping> routes;
    private final ArgumentResolver argumentResolver;
    private final ResponseHandler responseHandler;
    private final SessionManager sessionManager;
    private  String authKey;

    public RequestHandler(Map<String, Mapping> routes) {
        this.routes = routes;
        this.argumentResolver = new ArgumentResolver();
        this.responseHandler = new ResponseHandler();
        this.sessionManager = new SessionManager();

        // Charger la clé d'authentification depuis sprint.properties
        Properties properties = new Properties();
        try {
            properties.load(getClass().getClassLoader().getResourceAsStream("sprint.properties"));
            this.authKey = properties.getProperty("key.authentification", "userLevel");
        } catch (Exception e) {
            this.authKey = "userLevel"; // Valeur par défaut
        }
    }

    public void processRequest(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        String path = extractPath(req);
        Mapping mapping = routes.get(path);

        if (mapping == null) {
            new Error().displayErrorPage(resp, new Exception("Erreur 404 : URL non trouvée"));
            return;
        }

        sessionManager.updatePreviousUrl(req, path);

        Method method = getMethodForRequest(mapping, req);
        checkAuthorization(method, mapping, req);

        Object controllerInstance = createControllerInstance(mapping);

        ValidationManager validationManager = new ValidationManager();
        Object[] args = argumentResolver.buildMethodArguments(method, req, validationManager);

        if (validationManager.hasErrors()) {
            handleValidationErrors(req, resp, validationManager);
            return;
        }

        Object result = method.invoke(controllerInstance, args);
        boolean isRestAPI = method.isAnnotationPresent(RestAPI.class);

        responseHandler.handleResponse(result, isRestAPI, req, resp);
    }

    private void checkAuthorization(Method method, Mapping mapping, HttpServletRequest req) throws UnauthorizedException {
        if (method.isSynthetic()) {
            return; // Skip synthetic methods to avoid IllegalAccessException
        }
        // Récupérer le niveau de @AuthController
        int authControllerLevel = mapping.getControllerAuthLevel();
        // Récupérer le niveau de @AuthMethod
        int authMethodLevel = method.isAnnotationPresent(AuthMethod.class) 
            ? method.getAnnotation(AuthMethod.class).level() 
            : -1;

        // Prendre le niveau maximum entre @AuthController et @AuthMethod
        int requiredLevel = Math.max(authControllerLevel, authMethodLevel);

        // Si un niveau est requis
        if (requiredLevel >= 0) {
            Integer userLevel = (Integer) req.getSession().getAttribute(authKey);

            if (userLevel == null || userLevel < requiredLevel) {
                throw new UnauthorizedException(
                    "Accès non autorisé : niveau requis " + requiredLevel +
                    ", niveau utilisateur " + (userLevel != null ? userLevel : "non connecté")
                );
            }
        }
    }

    private String extractPath(HttpServletRequest req) {
        String contextPath = req.getContextPath();
        String uri = req.getRequestURI();
        return uri.substring(contextPath.length());
    }

    private Method getMethodForRequest(Mapping mapping, HttpServletRequest req) throws Exception {
        String httpMethod = req.getMethod();
        try {
            return mapping.getMethodByVerb(httpMethod);
        } catch (NoSuchMethodException e) {
            throw new Exception("Erreur 405 : Méthode HTTP non autorisée pour cette URL");
        }
    }

    private Object createControllerInstance(Mapping mapping) throws Exception {
        return mapping.getControllerClass().getDeclaredConstructor().newInstance();
    }

    private void handleValidationErrors(HttpServletRequest req, HttpServletResponse resp,
                                       ValidationManager validationManager) throws Exception {
        String previousUrl = (String) req.getSession().getAttribute("previousUrl");
        String previousMethod = (String) req.getSession().getAttribute("previousMethod");

        // Ajouter les erreurs aux attributs de la requête
        addErrorsToRequest(req, validationManager);
        addValuesToRequest(req, validationManager);

        if (previousUrl != null && previousMethod != null) {
            if (previousMethod.equalsIgnoreCase(req.getMethod())) {
                req.getRequestDispatcher(previousUrl).forward(req, resp);
                return;
            } else {
                throw new ServletException(
                    "Méthode HTTP du précédent appel (" + previousMethod +
                    ") ne correspond pas à la requête actuelle (" + req.getMethod() + ")"
                );
            }
        }
    }

    private void addErrorsToRequest(HttpServletRequest req, ValidationManager validationManager) {
        for (Map.Entry<String, java.util.List<String>> entry : validationManager.getFieldErrors().entrySet()) {
            req.setAttribute("error_" + entry.getKey(), entry.getValue());
        }
    }

    private void addValuesToRequest(HttpServletRequest req, ValidationManager validationManager) {
        for (Map.Entry<String, String> entry : validationManager.getFieldValues().entrySet()) {
            req.setAttribute(entry.getKey(), entry.getValue());
        }
    }
}