package mg.sprint.framework.servlet.response;

import com.google.gson.Gson;
import mg.sprint.framework.core.object.ModelView;
import mg.sprint.framework.page.Error;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ResponseHandler {
    private static final Logger logger = LoggerFactory.getLogger(ResponseHandler.class);
    private final Gson gson = new Gson();

    public void handleResponse(Object result, boolean isRestAPI, 
                              HttpServletRequest req, HttpServletResponse resp) throws Exception {
        logger.debug("Handling response: isRestAPI={}, resultType={}", 
            isRestAPI, result != null ? result.getClass().getSimpleName() : "null");
        
        if (isRestAPI) {
            handleRestApiResponse(result, resp);
        } else {
            handleWebResponse(result, req, resp);
        }
    }

    private void handleRestApiResponse(Object result, HttpServletResponse resp) throws Exception {
        logger.debug("Handling REST API response");
        resp.setContentType("application/json;charset=UTF-8");
        
        if (result instanceof ModelView) {
            ModelView mv = (ModelView) result;
            String json = gson.toJson(mv.getData());
            resp.getWriter().println(json);
            logger.trace("Sent JSON response: {}", json);
        } else {
            String json = gson.toJson(result);
            resp.getWriter().println(json);
            logger.trace("Sent JSON response: {}", json);
        }
    }

    private void handleWebResponse(Object result, HttpServletRequest req, 
                                  HttpServletResponse resp) throws Exception {
        logger.debug("Handling web response");
        if (result instanceof String) {
            resp.getWriter().println((String) result);
            logger.trace("Sent string response: {}", result);
        } else if (result instanceof ModelView) {
            handleModelViewResponse((ModelView) result, req, resp);
        } else {
            logger.error("Unrecognized return type: {}", result != null ? result.getClass().getName() : "null");
            new Error().displayErrorPage(resp, 
                new Exception("Type de retour non reconnu : " + (result != null ? result.getClass().getName() : "null")));
        }
    }

    private void handleModelViewResponse(ModelView mv, HttpServletRequest req, 
                                       HttpServletResponse resp) throws Exception {
        logger.debug("Handling ModelView response: url={}", mv.getUrl());
        for (Map.Entry<String, Object> entry : mv.getData().entrySet()) {
            req.setAttribute(entry.getKey(), entry.getValue());
            logger.trace("Set request attribute: {}={}", entry.getKey(), entry.getValue());
        }
        req.getRequestDispatcher(mv.getUrl()).forward(req, resp);
        logger.info("Forwarded to URL: {}", mv.getUrl());
    }
}