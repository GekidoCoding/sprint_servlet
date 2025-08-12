package mg.sprint.framework.exception;

public class ValidationException extends Exception {
    public ValidationException(String message) {
        super(message);
    }

    public ValidationException() {
        super("Erreur de validation");
    }
}
