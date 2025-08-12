package mg.sprint.framework.core.manager;

import java.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ValidationManager {
    private static final Logger logger = LoggerFactory.getLogger(ValidationManager.class);
    private Map<String, List<String>> fieldErrors;
    private Map<String, String> fieldValues;

    
    public ValidationManager() {
        this.fieldErrors = new HashMap<>();
        this.fieldValues = new HashMap<>();
        logger.debug("Initialized new ValidationManager instance");
    }
    
    public void addError(String fieldName, String errorMessage) {
        fieldErrors.computeIfAbsent(fieldName, k -> new ArrayList<>()).add(errorMessage);
        logger.debug("Added error for field '{}': {}", fieldName, errorMessage);
    }
    
    public void addValue(String fieldName, String value) {
        fieldValues.put(fieldName, value);
        logger.trace("Added value for field '{}': {}", fieldName, value);
    }
    
    public boolean hasErrors() {
        boolean hasErrors = !fieldErrors.isEmpty();
        logger.debug("Checked for errors: {}", hasErrors);
        return hasErrors;
    }
    
    public Map<String, List<String>> getFieldErrors() {
        return fieldErrors;
    }
    
    public Map<String, String> getFieldValues() {
        return fieldValues;
    }
    
   
}