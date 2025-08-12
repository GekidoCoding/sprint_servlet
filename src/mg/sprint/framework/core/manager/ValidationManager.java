package mg.sprint.framework.core.manager;

import java.util.*;

public class ValidationManager {
    private Map<String, List<String>> fieldErrors;
    private Map<String, String> fieldValues;
    private static final String ERRORS_SESSION_KEY = "validation_errors";
    private static final String VALUES_SESSION_KEY = "validation_values";
    
    public ValidationManager() {
        this.fieldErrors = new HashMap<>();
        this.fieldValues = new HashMap<>();
    }
    
    public void addError(String fieldName, String errorMessage) {
        fieldErrors.computeIfAbsent(fieldName, k -> new ArrayList<>()).add(errorMessage);
    }
    
    public void addValue(String fieldName, String value) {
        fieldValues.put(fieldName, value);
    }
    
    public boolean hasErrors() {
        return !fieldErrors.isEmpty();
    }
    
    public Map<String, List<String>> getFieldErrors() {
        return fieldErrors;
    }
    
    public Map<String, String> getFieldValues() {
        return fieldValues;
    }
    
    public List<String> getErrors(String fieldName) {
        return fieldErrors.getOrDefault(fieldName, new ArrayList<>());
    }
    
    public String getValue(String fieldName) {
        return fieldValues.getOrDefault(fieldName, "");
    }
    
    public void clear() {
        fieldErrors.clear();
        fieldValues.clear();
    }
    
    public void storeInSession(javax.servlet.http.HttpSession session) {
        session.setAttribute(ERRORS_SESSION_KEY, fieldErrors);
        session.setAttribute(VALUES_SESSION_KEY, fieldValues);
    }
    
    @SuppressWarnings("unchecked")
    public static ValidationManager getFromSession(javax.servlet.http.HttpSession session) {
        ValidationManager manager = new ValidationManager();
        
        Map<String, List<String>> errors = (Map<String, List<String>>) session.getAttribute(ERRORS_SESSION_KEY);
        Map<String, String> values = (Map<String, String>) session.getAttribute(VALUES_SESSION_KEY);
        
        if (errors != null) {
            manager.fieldErrors = errors;
        }
        if (values != null) {
            manager.fieldValues = values;
        }
        
        return manager;
    }
    
    public static void clearFromSession(javax.servlet.http.HttpSession session) {
        session.removeAttribute(ERRORS_SESSION_KEY);
        session.removeAttribute(VALUES_SESSION_KEY);
    }
    

}