package mg.sprint.framework.servlet.response;

import com.google.gson.Gson;
import mg.sprint.framework.core.object.ModelView;
import mg.sprint.framework.page.Error;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

public class ResponseHandler {
    private final Gson gson = new Gson();

    public void handleResponse(Object result, boolean isRestAPI, 
                              HttpServletRequest req, HttpServletResponse resp) throws Exception {
        if (isRestAPI) {
            handleRestApiResponse(result, resp);
        } else {
            handleWebResponse(result, req, resp);
        }
    }

    private void handleRestApiResponse(Object result, HttpServletResponse resp) throws Exception {
        resp.setContentType("application/json;charset=UTF-8");
        
        if (result instanceof ModelView) {
            ModelView mv = (ModelView) result;
            String json = gson.toJson(mv.getData());
            resp.getWriter().println(json);
        } else {
            String json = gson.toJson(result);
            resp.getWriter().println(json);
        }
    }

    private void handleWebResponse(Object result, HttpServletRequest req, 
                                  HttpServletResponse resp) throws Exception {
        if (result instanceof String) {
            resp.getWriter().println((String) result);
        } else if (result instanceof ModelView) {
            handleModelViewResponse((ModelView) result, req, resp);
        } else {
            new Error().displayErrorPage(resp, 
                new Exception("Type de retour non reconnu : " + result.getClass().getName()));
        }
    }

    private void handleModelViewResponse(ModelView mv, HttpServletRequest req, 
                                       HttpServletResponse resp) throws Exception {
        for (Map.Entry<String, Object> entry : mv.getData().entrySet()) {
            req.setAttribute(entry.getKey(), entry.getValue());
        }
        req.getRequestDispatcher(mv.getUrl()).forward(req, resp);
    }
}