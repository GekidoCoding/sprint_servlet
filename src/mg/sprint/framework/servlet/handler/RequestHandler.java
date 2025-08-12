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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RequestHandler {
    private static final Logger logger = LoggerFactory.getLogger(RequestHandler.class);
    private final Map<String, Mapping> routes;
    private final ArgumentResolver argumentResolver;
    private final ResponseHandler responseHandler;
    private final SessionManager sessionManager;
    private String authKey;

    public RequestHandler(Map<String, Mapping> routes) {
        this.routes = routes;
        this.argumentResolver = new ArgumentResolver();
        this.responseHandler = new ResponseHandler();
        this.sessionManager = new SessionManager();

        Properties properties = new Properties();
        try {
            properties.load(getClass().getClassLoader().getResourceAsStream("sprint.properties"));
            this.authKey = properties.getProperty("key.authentification", "userLevel");
            logger.info("Loaded auth key: {}", authKey);
        } catch (Exception e) {
            this.authKey = "userLevel";
            logger.warn("Failed to load sprint.properties, using default auth key: {}", authKey);
        }
    }

    public void processRequest(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        String path = extractPath(req);
        logger.info("Processing request: path={}, method={}", path, req.getMethod());

        Mapping mapping = routes.get(path);
        if (mapping == null) {
            logger.error("No mapping found for path: {}", path);
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
            logger.warn("Validation errors detected, handling errors");
            handleValidationErrors(req, resp, validationManager);
            return;
        }

        Object result = method.invoke(controllerInstance, args);
        boolean isRestAPI = method.isAnnotationPresent(RestAPI.class);
        logger.debug("Method {} invoked, isRestAPI={}", method.getName(), isRestAPI);

        responseHandler.handleResponse(result, isRestAPI, req, resp);
    }

    private void checkAuthorization(Method method, Mapping mapping, HttpServletRequest req) throws UnauthorizedException {
        if (method.isSynthetic()) {
            logger.debug("Skipping authorization for synthetic method: {}", method.getName());
            return;
        }
        int authControllerLevel = mapping.getControllerAuthLevel();
        int authMethodLevel = method.isAnnotationPresent(AuthMethod.class) 
            ? method.getAnnotation(AuthMethod.class).level() 
            : -1;

        int requiredLevel = Math.max(authControllerLevel, authMethodLevel);
        logger.debug("Required auth level: {}", requiredLevel);

        if (requiredLevel >= 0) {
            Integer userLevel = (Integer) req.getSession().getAttribute(authKey);
            if (userLevel == null || userLevel < requiredLevel) {
                logger.error("Unauthorized access: requiredLevel={}, userLevel={}", 
                    requiredLevel, userLevel != null ? userLevel : "non connecté");
                throw new UnauthorizedException(
                    "Accès non autorisé : niveau requis " + requiredLevel +
                    ", niveau utilisateur " + (userLevel != null ? userLevel : "non connecté")
                );
            }
            logger.debug("Authorization check passed: userLevel={}", userLevel);
        }
    }

    private String extractPath(HttpServletRequest req) {
        String contextPath = req.getContextPath();
        String uri = req.getRequestURI();
        String path = uri.substring(contextPath.length());
        logger.trace("Extracted path: {}", path);
        return path;
    }

    private Method getMethodForRequest(Mapping mapping, HttpServletRequest req) throws Exception {
        String httpMethod = req.getMethod();
        try {
            return mapping.getMethodByVerb(httpMethod);
        } catch (NoSuchMethodException e) {
            logger.error("HTTP method {} not allowed for path {}", httpMethod, req.getRequestURI());
            throw new Exception("Erreur 405 : Méthode HTTP non autorisée pour cette URL");
        }
    }

    private Object createControllerInstance(Mapping mapping) throws Exception {
        Object instance = mapping.getControllerClass().getDeclaredConstructor().newInstance();
        logger.debug("Created controller instance: {}", mapping.getControllerClass().getName());
        return instance;
    }

    private void handleValidationErrors(HttpServletRequest req, HttpServletResponse resp,
                                       ValidationManager validationManager) throws Exception {
        String previousUrl = (String) req.getSession().getAttribute("previousUrl");
        String previousMethod = (String) req.getSession().getAttribute("previousMethod");
        logger.debug("Handling validation errors, previousUrl={}, previousMethod={}", 
            previousUrl, previousMethod);

        addErrorsToRequest(req, validationManager);
        addValuesToRequest(req, validationManager);

        if (previousUrl != null && previousMethod != null) {
            if (previousMethod.equalsIgnoreCase(req.getMethod())) {
                logger.info("Forwarding to previous URL: {}", previousUrl);
                req.getRequestDispatcher(previousUrl).forward(req, resp);
                return;
            } else {
                logger.error("HTTP method mismatch: previousMethod={}, currentMethod={}", 
                    previousMethod, req.getMethod());
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
            logger.trace("Added error to request: {}={}", "error_" + entry.getKey(), entry.getValue());
        }
    }

    private void addValuesToRequest(HttpServletRequest req, ValidationManager validationManager) {
        for (Map.Entry<String, String> entry : validationManager.getFieldValues().entrySet()) {
            req.setAttribute(entry.getKey(), entry.getValue());
            logger.trace("Added value to request: {}={}", entry.getKey(), entry.getValue());
        }
    }
}