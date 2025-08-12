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

public class ValidationUtil {

    public static ValidationManager validate(Object obj) {
        ValidationManager validationManager = new ValidationManager();
        System.out.println("=== Debut de la validation de l'objet: " + obj.getClass().getName() + " ===");

        for (Field field : obj.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            Object value;
            String stringValue = "";

            try {
                value = field.get(obj);
                stringValue = (value != null) ? value.toString().trim() : "";
                System.out.println("Champ: " + field.getName() + " | Valeur brute: " + value + " | Valeur traitee: " + stringValue);
                
                validationManager.addValue(field.getName(), stringValue);
            } catch (IllegalAccessException e) {
                System.out.println("Erreur d'accès au champ: " + field.getName());
                continue;
            }

            // @Required
            if (field.isAnnotationPresent(Required.class)) {
                System.out.println(" -> Validation @Required pour: " + field.getName());
                if (stringValue.isEmpty()) {
                    System.out.println("    ❌ Champ requis vide !");
                    validationManager.addError(field.getName(), "Le champ '" + field.getName() + "' est requis.");
                }
            }

            if (stringValue.isEmpty() && !field.isAnnotationPresent(Required.class)) {
                System.out.println("Champ " + field.getName() + " vide et non requis, skip les autres validations.");
                continue;
            }

            // @Numeric
            if (field.isAnnotationPresent(Numeric.class)) {
                System.out.println(" -> Validation @Numeric pour: " + field.getName());
                if (!stringValue.matches("\\d+")) {
                    System.out.println("    ❌ Non numérique !");
                    validationManager.addError(field.getName(), "Le champ '" + field.getName() + "' doit être numérique.");
                }
            }

            // @Decimal
            if (field.isAnnotationPresent(Decimal.class)) {
                System.out.println(" -> Validation @Decimal pour: " + field.getName());
                if (!stringValue.matches("[-+]?[0-9]*\\.?[0-9]+")) {
                    System.out.println("    ❌ Format décimal invalide !");
                    validationManager.addError(field.getName(), "Le champ '" + field.getName() + "' doit être un nombre décimal.");
                }
            }

            // @Min
            if (field.isAnnotationPresent(Min.class)) {
                System.out.println(" -> Validation @Min pour: " + field.getName());
                try {
                    double min = field.getAnnotation(Min.class).value();
                    double val = Double.parseDouble(stringValue);
                    if (val < min) {
                        System.out.println("    ❌ Valeur " + val + " < min " + min);
                        validationManager.addError(field.getName(), "Le champ '" + field.getName() + "' doit être ≥ " + min);
                    }
                } catch (NumberFormatException e) {
                    System.out.println("    ❌ Erreur de conversion pour @Min !");
                    validationManager.addError(field.getName(), "Le champ '" + field.getName() + "' doit être un nombre valide pour la validation Min.");
                }
            }

            // @Max
            if (field.isAnnotationPresent(Max.class)) {
                System.out.println(" -> Validation @Max pour: " + field.getName());
                try {
                    double max = field.getAnnotation(Max.class).value();
                    double val = Double.parseDouble(stringValue);
                    if (val > max) {
                        System.out.println("    ❌ Valeur " + val + " > max " + max);
                        validationManager.addError(field.getName(), "Le champ '" + field.getName() + "' doit être ≤ " + max);
                    }
                } catch (NumberFormatException e) {
                    System.out.println("    ❌ Erreur de conversion pour @Max !");
                    validationManager.addError(field.getName(), "Le champ '" + field.getName() + "' doit être un nombre valide pour la validation Max.");
                }
            }

            // @Size
            if (field.isAnnotationPresent(Size.class)) {
                System.out.println(" -> Validation @Size pour: " + field.getName());
                int len = stringValue.length();
                Size size = field.getAnnotation(Size.class);
                if (len < size.min() || len > size.max()) {
                    System.out.println("    ❌ Taille invalide: " + len + " (min=" + size.min() + ", max=" + size.max() + ")");
                    validationManager.addError(field.getName(), "Le champ '" + field.getName() + "' doit avoir entre " + size.min() + " et " + size.max() + " caractères.");
                }
            }

            // @Regex
            if (field.isAnnotationPresent(Regex.class)) {
                System.out.println(" -> Validation @Regex pour: " + field.getName());
                String pattern = field.getAnnotation(Regex.class).pattern();
                if (!stringValue.matches(pattern)) {
                    System.out.println("    ❌ Ne correspond pas au pattern: " + pattern);
                    validationManager.addError(field.getName(), "Le champ '" + field.getName() + "' ne correspond pas au format requis.");
                }
            }

            // @In
            if (field.isAnnotationPresent(In.class)) {
                System.out.println(" -> Validation @In pour: " + field.getName());
                String[] values = field.getAnnotation(In.class).value();
                if (!Arrays.asList(values).contains(stringValue)) {
                    System.out.println("    ❌ " + stringValue + " n'est pas dans " + Arrays.toString(values));
                    validationManager.addError(field.getName(), "Le champ '" + field.getName() + "' doit être dans " + Arrays.toString(values));
                }
            }

            // @Phone
            if (field.isAnnotationPresent(Phone.class)) {
                System.out.println(" -> Validation @Phone pour: " + field.getName());
                if (!stringValue.matches("^\\+?[0-9]{7,15}$")) {
                    System.out.println("    ❌ Numéro de téléphone invalide");
                    validationManager.addError(field.getName(), "Le champ '" + field.getName() + "' n'est pas un numéro de téléphone valide.");
                }
            }

            // @BooleanField
            if (field.isAnnotationPresent(BooleanField.class)) {
                System.out.println(" -> Validation @BooleanField pour: " + field.getName());
                if (!stringValue.equalsIgnoreCase("true") && !stringValue.equalsIgnoreCase("false")) {
                    System.out.println("    ❌ Valeur booléenne invalide");
                    validationManager.addError(field.getName(), "Le champ '" + field.getName() + "' doit être true ou false.");
                }
            }

         if (field.isAnnotationPresent(DateFormat.class)) {
            System.out.println(" -> Validation @DateFormat pour: " + field.getName());
            String pattern = field.getAnnotation(DateFormat.class).pattern();
            SimpleDateFormat sdf = new SimpleDateFormat(pattern);
            sdf.setLenient(false); // Rend la validation plus stricte

            try {
                Date date = sdf.parse(stringValue);

                // Vérification que la chaîne correspond exactement au pattern (ex: pas "2024-1-1" pour "yyyy-MM-dd")
                String reformatted = sdf.format(date);
                if (!stringValue.equals(reformatted)) {
                    throw new ParseException("Le format exact ne correspond pas.", 0);
                }

            } catch (ParseException e) {
                System.out.println("    ❌ Format de date invalide, attendu: " + pattern);
                validationManager.addError(
                    field.getName(),
                    "Le champ '" + field.getName() + "' doit respecter exactement le format : " + pattern
                );
            }
        }

            // @Email
            if (field.isAnnotationPresent(Email.class)) {
                System.out.println(" -> Validation @Email pour: " + field.getName());
                if (!stringValue.matches("^[\\w-.]+@[\\w-]+\\.[a-zA-Z]{2,}$")) {
                    System.out.println("    ❌ Adresse email invalide");
                    validationManager.addError(field.getName(), "Le champ '" + field.getName() + "' doit être un email valide.");
                }
            }
        }

        System.out.println("=== Fin de la validation ===");
        return validationManager;
    }

}