package mg.sprint.framework.page;

import mg.sprint.framework.exception.UnauthorizedException;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class Error {
    public void displayErrorPage(HttpServletResponse resp, Exception e) throws IOException {
        resp.setContentType("text/html; charset=UTF-8");

        String errorCode = "ERROR";
        String errorTitle = "Oops! Quelque chose s'est mal passé";
        if (e instanceof UnauthorizedException) {
            resp.setStatus(HttpServletResponse.SC_FORBIDDEN);
            errorCode = "403";
            errorTitle = "Accès non autorisé";
        } else if (e.getMessage() != null && e.getMessage().contains("404")) {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            errorCode = "404";
            errorTitle = "Page non trouvée";
        } else if (e.getMessage() != null && e.getMessage().contains("405")) {
            resp.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
            errorCode = "405";
            errorTitle = "Méthode non autorisée";
        }

        resp.getWriter().println(
            "<!DOCTYPE html>\n" +
            "<html lang=\"fr\">\n" +
            "<head>\n" +
            "<meta charset=\"UTF-8\">\n" +
            "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
            "<title>" + errorTitle + "</title>\n" +
            "</head>\n" +
            "<body>\n" +
            "<div>\n" +
            "<h1>" + errorCode + "</h1>\n" +
            "<h2>" + errorTitle + "</h2>\n" +
            "<p>" + (e != null && e.getMessage() != null ? e.getMessage() : "Aucun message d'erreur disponible") + "</p>\n" +
            "<div>\n" +
            "<button onclick=\"window.location.reload()\">Réessayer</button>\n" +
            "<button onclick=\"window.history.back()\">Retour</button>\n" +
            "</div>\n" +
            "</div>\n" +
            "</body>\n" +
            "</html>"
        );
    }
}