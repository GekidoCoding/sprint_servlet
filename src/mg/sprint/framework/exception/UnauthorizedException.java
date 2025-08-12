package mg.sprint.framework.exception;

public class UnauthorizedException extends Exception {
    public UnauthorizedException(String message) {
        super(message);
    }

    public UnauthorizedException() {
        super("Erreur 403 : Accès non autorisé");
    }
}
