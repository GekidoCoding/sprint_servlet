package mg.sprint.framework.utils;

import java.lang.reflect.Field;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;

import mg.sprint.framework.annotations.validation.BooleanField;
import mg.sprint.framework.annotations.validation.DateFormat;
import mg.sprint.framework.annotations.validation.Decimal;
import mg.sprint.framework.annotations.validation.Email;
import mg.sprint.framework.annotations.validation.In;
import mg.sprint.framework.annotations.validation.Max;
import mg.sprint.framework.annotations.validation.Min;
import mg.sprint.framework.annotations.validation.Numeric;
import mg.sprint.framework.annotations.validation.Phone;
import mg.sprint.framework.annotations.validation.Regex;
import mg.sprint.framework.annotations.validation.Required;
import mg.sprint.framework.annotations.validation.Size;
import mg.sprint.framework.core.manager.ValidationManager;




import mg.sprint.framework.annotations.field.FormName;

public class ValidationProcessor {
     public static void validate(Object obj) throws IllegalAccessException {
        ValidationManager vm = ValidationManager.getInstance();
        vm.clear(); // Important : toujours nettoyer les erreurs précédentes

        Class<?> clazz = obj.getClass();

        for (Field field : clazz.getDeclaredFields()) {
            field.setAccessible(true);

            Object rawValue = field.get(obj);
            String stringValue = rawValue != null ? rawValue.toString().trim() : "";

            String inputName = field.getName();
            if (field.isAnnotationPresent(FormName.class)) {
                inputName = field.getAnnotation(FormName.class).value();
            }

            // Stocke la valeur valide (même si elle est vide)
            vm.addValue(inputName, stringValue);

            // Règles de validation
            if (field.isAnnotationPresent(Required.class) && stringValue.isEmpty()) {
                vm.addError(inputName, "Ce champ est requis.");
                continue;
            }

            if (stringValue.isEmpty()) continue; // Ne pas valider les autres contraintes si la valeur est vide

            if (field.isAnnotationPresent(Numeric.class) && !stringValue.matches("\\d+")) {
                vm.addError(inputName, "Ce champ doit être numérique.");
            }

            if (field.isAnnotationPresent(Decimal.class) && !stringValue.matches("[-+]?[0-9]*\\.?[0-9]+")) {
                vm.addError(inputName, "Ce champ doit être un nombre décimal.");
            }

            if (field.isAnnotationPresent(Min.class)) {
                double min = field.getAnnotation(Min.class).value();
                try {
                    if (Double.parseDouble(stringValue) < min) {
                        vm.addError(inputName, "Ce champ doit être ≥ " + min + ".");
                    }
                } catch (NumberFormatException e) {
                    vm.addError(inputName, "Valeur invalide pour un champ numérique.");
                }
            }

            if (field.isAnnotationPresent(Max.class)) {
                double max = field.getAnnotation(Max.class).value();
                try {
                    if (Double.parseDouble(stringValue) > max) {
                        vm.addError(inputName, "Ce champ doit être ≤ " + max + ".");
                    }
                } catch (NumberFormatException e) {
                    vm.addError(inputName, "Valeur invalide pour un champ numérique.");
                }
            }

            if (field.isAnnotationPresent(Size.class)) {
                int len = stringValue.length();
                Size size = field.getAnnotation(Size.class);
                if (len < size.min() || len > size.max()) {
                    vm.addError(inputName, "La taille doit être entre " + size.min() + " et " + size.max() + " caractères.");
                }
            }

            if (field.isAnnotationPresent(Regex.class)) {
                String pattern = field.getAnnotation(Regex.class).pattern();
                if (!stringValue.matches(pattern)) {
                    vm.addError(inputName, "Le format n'est pas valide.");
                }
            }

            if (field.isAnnotationPresent(In.class)) {
                String[] allowed = field.getAnnotation(In.class).value();
                if (!Arrays.asList(allowed).contains(stringValue)) {
                    vm.addError(inputName, "Valeur non autorisée.");
                }
            }

            if (field.isAnnotationPresent(Phone.class)) {
                if (!stringValue.matches("^\\+?[0-9]{7,15}$")) {
                    vm.addError(inputName, "Numéro de téléphone invalide.");
                }
            }

            if (field.isAnnotationPresent(BooleanField.class)) {
                if (!stringValue.equalsIgnoreCase("true") && !stringValue.equalsIgnoreCase("false")) {
                    vm.addError(inputName, "Ce champ doit être 'true' ou 'false'.");
                }
            }

            if (field.isAnnotationPresent(DateFormat.class)) {
                String pattern = field.getAnnotation(DateFormat.class).pattern();
                try {
                    SimpleDateFormat sdf = new SimpleDateFormat(pattern);
                    sdf.setLenient(false);
                    sdf.parse(stringValue);
                } catch (ParseException e) {
                    vm.addError(inputName, "Format de date invalide. Format attendu : " + pattern);
                }
            }

            if (field.isAnnotationPresent(Email.class)) {
                if (!stringValue.matches("^[\\w-.]+@[\\w-]+\\.[a-zA-Z]{2,}$")) {
                    vm.addError(inputName, "Adresse email invalide.");
                }
            }
        }
    }
}
