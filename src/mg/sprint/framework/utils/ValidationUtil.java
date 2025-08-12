package mg.sprint.framework.utils;

import java.lang.reflect.Field;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

import mg.sprint.framework.annotation.validation.BooleanField;
import mg.sprint.framework.annotation.validation.DateFormat;
import mg.sprint.framework.annotation.validation.Decimal;
import mg.sprint.framework.annotation.validation.Email;
import mg.sprint.framework.annotation.validation.In;
import mg.sprint.framework.annotation.validation.Max;
import mg.sprint.framework.annotation.validation.Min;
import mg.sprint.framework.annotation.validation.Numeric;
import mg.sprint.framework.annotation.validation.Phone;
import mg.sprint.framework.annotation.validation.Regex;
import mg.sprint.framework.annotation.validation.Required;
import mg.sprint.framework.annotation.validation.Size;
import mg.sprint.framework.core.manager.ValidationManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ValidationUtil {
    private static final Logger logger = LoggerFactory.getLogger(ValidationUtil.class);

    public static ValidationManager validate(Object obj) {
        ValidationManager validationManager = new ValidationManager();
        logger.info("Starting validation for object: {}", obj.getClass().getName());

        for (Field field : obj.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            Object value;
            String stringValue = "";

            try {
                value = field.get(obj);
                stringValue = (value != null) ? value.toString().trim() : "";
                logger.trace("Field: {} | Raw value: {} | Processed value: {}", 
                    field.getName(), value, stringValue);
                
                validationManager.addValue(field.getName(), stringValue);
            } catch (IllegalAccessException e) {
                logger.error("Error accessing field: {}", field.getName(), e);
                continue;
            }

            if (field.isAnnotationPresent(Required.class)) {
                logger.debug("Validating @Required for: {}", field.getName());
                if (stringValue.isEmpty()) {
                    logger.warn("Required field '{}' is empty", field.getName());
                    validationManager.addError(field.getName(), "Le champ '" + field.getName() + "' est requis.");
                }
            }

            if (stringValue.isEmpty() && !field.isAnnotationPresent(Required.class)) {
                logger.debug("Field {} is empty and not required, skipping further validations", field.getName());
                continue;
            }

            if (field.isAnnotationPresent(Numeric.class)) {
                logger.debug("Validating @Numeric for: {}", field.getName());
                if (!stringValue.matches("\\d+")) {
                    logger.warn("Field '{}' is not numeric: {}", field.getName(), stringValue);
                    validationManager.addError(field.getName(), "Le champ '" + field.getName() + "' doit être numérique.");
                }
            }

            if (field.isAnnotationPresent(Decimal.class)) {
                logger.debug("Validating @Decimal for: {}", field.getName());
                if (!stringValue.matches("[-+]?[0-9]*\\.?[0-9]+")) {
                    logger.warn("Field '{}' is not a valid decimal: {}", field.getName(), stringValue);
                    validationManager.addError(field.getName(), "Le champ '" + field.getName() + "' doit être un nombre décimal.");
                }
            }

            if (field.isAnnotationPresent(Min.class)) {
                logger.debug("Validating @Min for: {}", field.getName());
                try {
                    double min = field.getAnnotation(Min.class).value();
                    double val = Double.parseDouble(stringValue);
                    if (val < min) {
                        logger.warn("Field '{}' value {} is less than min {}", field.getName(), val, min);
                        validationManager.addError(field.getName(), "Le champ '" + field.getName() + "' doit être ≥ " + min);
                    }
                } catch (NumberFormatException e) {
                    logger.warn("Field '{}' cannot be converted to number for @Min: {}", field.getName(), stringValue);
                    validationManager.addError(field.getName(), "Le champ '" + field.getName() + "' doit être un nombre valide pour la validation Min.");
                }
            }

            if (field.isAnnotationPresent(Max.class)) {
                logger.debug("Validating @Max for: {}", field.getName());
                try {
                    double max = field.getAnnotation(Max.class).value();
                    double val = Double.parseDouble(stringValue);
                    if (val > max) {
                        logger.warn("Field '{}' value {} is greater than max {}", field.getName(), val, max);
                        validationManager.addError(field.getName(), "Le champ '" + field.getName() + "' doit être ≤ " + max);
                    }
                } catch (NumberFormatException e) {
                    logger.warn("Field '{}' cannot be converted to number for @Max: {}", field.getName(), stringValue);
                    validationManager.addError(field.getName(), "Le champ '" + field.getName() + "' doit être un nombre valide pour la validation Max.");
                }
            }

            if (field.isAnnotationPresent(Size.class)) {
                logger.debug("Validating @Size for: {}", field.getName());
                int len = stringValue.length();
                Size size = field.getAnnotation(Size.class);
                if (len < size.min() || len > size.max()) {
                    logger.warn("Field '{}' length {} is outside range [min={}, max={}]", 
                        field.getName(), len, size.min(), size.max());
                    validationManager.addError(field.getName(), 
                        "Le champ '" + field.getName() + "' doit avoir entre " + size.min() + " et " + size.max() + " caractères.");
                }
            }

            if (field.isAnnotationPresent(Regex.class)) {
                logger.debug("Validating @Regex for: {}", field.getName());
                String pattern = field.getAnnotation(Regex.class).pattern();
                if (!stringValue.matches(pattern)) {
                    logger.warn("Field '{}' does not match pattern '{}': {}", field.getName(), pattern, stringValue);
                    validationManager.addError(field.getName(), "Le champ '" + field.getName() + "' ne correspond pas au format requis.");
                }
            }

            if (field.isAnnotationPresent(In.class)) {
                logger.debug("Validating @In for: {}", field.getName());
                String[] values = field.getAnnotation(In.class).value();
                if (!Arrays.asList(values).contains(stringValue)) {
                    logger.warn("Field '{}' value {} is not in {}", field.getName(), stringValue, Arrays.toString(values));
                    validationManager.addError(field.getName(), "Le champ '" + field.getName() + "' doit être dans " + Arrays.toString(values));
                }
            }

            if (field.isAnnotationPresent(Phone.class)) {
                logger.debug("Validating @Phone for: {}", field.getName());
                if (!stringValue.matches("^\\+?[0-9]{7,15}$")) {
                    logger.warn("Field '{}' is not a valid phone number: {}", field.getName(), stringValue);
                    validationManager.addError(field.getName(), "Le champ '" + field.getName() + "' n'est pas un numéro de téléphone valide.");
                }
            }

            if (field.isAnnotationPresent(BooleanField.class)) {
                logger.debug("Validating @BooleanField for: {}", field.getName());
                if (!stringValue.equalsIgnoreCase("true") && !stringValue.equalsIgnoreCase("false")) {
                    logger.warn("Field '{}' is not a valid boolean: {}", field.getName(), stringValue);
                    validationManager.addError(field.getName(), "Le champ '" + field.getName() + "' doit être true ou false.");
                }
            }

            if (field.isAnnotationPresent(DateFormat.class)) {
                logger.debug("Validating @DateFormat for: {}", field.getName());
                String pattern = field.getAnnotation(DateFormat.class).pattern();
                SimpleDateFormat sdf = new SimpleDateFormat(pattern);
                sdf.setLenient(false);

                try {
                    Date date = sdf.parse(stringValue);
                    String reformatted = sdf.format(date);
                    if (!stringValue.equals(reformatted)) {
                        throw new ParseException("Le format exact ne correspond pas.", 0);
                    }
                } catch (ParseException e) {
                    logger.warn("Field '{}' has invalid date format, expected '{}': {}", 
                        field.getName(), pattern, stringValue);
                    validationManager.addError(
                        field.getName(),
                        "Le champ '" + field.getName() + "' doit respecter exactement le format : " + pattern
                    );
                }
            }

            if (field.isAnnotationPresent(Email.class)) {
                logger.debug("Validating @Email for: {}", field.getName());
                if (!stringValue.matches("^[\\w-.]+@[\\w-]+\\.[a-zA-Z]{2,}$")) {
                    logger.warn("Field '{}' is not a valid email: {}", field.getName(), stringValue);
                    validationManager.addError(field.getName(), "Le champ '" + field.getName() + "' doit être un email valide.");
                }
            }
        }

        logger.info("Completed validation for object: {}", obj.getClass().getName());
        return validationManager;
    }
}