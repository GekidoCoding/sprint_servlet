package mg.sprint.framework.core.manager;



import java.util.HashMap;
import java.util.Map;

public class ValidationManager {
    private static final ValidationManager instance = new ValidationManager();

    private final Map<String, String> errors = new HashMap<>();
    private final Map<String, String> values = new HashMap<>();

    // Constructeur privé pour singleton
    private ValidationManager() {}

    // Accès au singleton
    public static ValidationManager getInstance() {
        return instance;
    }

    // Ajouter une erreur
    public void addError(String inputName, String message) {
        errors.put(inputName, message);
    }

    // Ajouter une valeur valide
    public void addValue(String inputName, String value) {
        values.put(inputName, value);
    }

    // Obtenir les erreurs
    public Map<String, String> getErrors() {
        return errors;
    }

    // Obtenir les valeurs valides
    public Map<String, String> getValues() {
        return values;
    }

    // Obtenir une erreur précise
    public String getError(String inputName) {
        return errors.get(inputName);
    }

    // Obtenir une valeur précise
    public String getValue(String inputName) {
        return values.get(inputName);
    }

    // Nettoyer tout
    public void clear() {
        errors.clear();
        values.clear();
    }

    // Vérifie s’il y a des erreurs
    public boolean hasErrors() {
        return !errors.isEmpty();
    }
}
