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





public class ValidationUtil {

    public static void validate(Object obj) throws Exception {
        for (Field field : obj.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            Object value = field.get(obj);
            String stringValue = (value != null) ? value.toString().trim() : "";

            if (field.isAnnotationPresent(Required.class) && stringValue.isEmpty()) {
                throw new Exception("Le champ '" + field.getName() + "' est requis.");
            }

            if (field.isAnnotationPresent(Numeric.class) && !stringValue.matches("\\d+")) {
                throw new Exception("Le champ '" + field.getName() + "' doit être numérique.");
            }

            if (field.isAnnotationPresent(Decimal.class) && !stringValue.matches("[-+]?[0-9]*\\.?[0-9]+")) {
                throw new Exception("Le champ '" + field.getName() + "' doit être un nombre décimal.");
            }

            if (field.isAnnotationPresent(Min.class)) {
                double min = field.getAnnotation(Min.class).value();
                if (Double.parseDouble(stringValue) < min) {
                    throw new Exception("Le champ '" + field.getName() + "' doit être ≥ " + min);
                }
            }

            if (field.isAnnotationPresent(Max.class)) {
                double max = field.getAnnotation(Max.class).value();
                if (Double.parseDouble(stringValue) > max) {
                    throw new Exception("Le champ '" + field.getName() + "' doit être ≤ " + max);
                }
            }

            if (field.isAnnotationPresent(Size.class)) {
                int len = stringValue.length();
                Size size = field.getAnnotation(Size.class);
                if (len < size.min() || len > size.max()) {
                    throw new Exception("Le champ '" + field.getName() + "' doit avoir entre " + size.min() + " et " + size.max() + " caractères.");
                }
            }

            if (field.isAnnotationPresent(Regex.class)) {
                String pattern = field.getAnnotation(Regex.class).pattern();
                if (!stringValue.matches(pattern)) {
                    throw new Exception("Le champ '" + field.getName() + "' ne correspond pas au format requis.");
                }
            }

            if (field.isAnnotationPresent(In.class)) {
                String[] values = field.getAnnotation(In.class).value();
                if (!Arrays.asList(values).contains(stringValue)) {
                    throw new Exception("Le champ '" + field.getName() + "' doit être dans " + Arrays.toString(values));
                }
            }

            if (field.isAnnotationPresent(Phone.class)) {
                if (!stringValue.matches("^\\+?[0-9]{7,15}$")) {
                    throw new Exception("Le champ '" + field.getName() + "' n'est pas un numéro de téléphone valide.");
                }
            }

            if (field.isAnnotationPresent(BooleanField.class)) {
                if (!stringValue.equalsIgnoreCase("true") && !stringValue.equalsIgnoreCase("false")) {
                    throw new Exception("Le champ '" + field.getName() + "' doit être true ou false.");
                }
            }

            if (field.isAnnotationPresent(DateFormat.class)) {
                String pattern = field.getAnnotation(DateFormat.class).pattern();
                try {
                    new SimpleDateFormat(pattern).parse(stringValue);
                } catch (ParseException e) {
                    throw new Exception("Le champ '" + field.getName() + "' doit respecter le format de date : " + pattern);
                }
            }

            if (field.isAnnotationPresent(Email.class)) {
                if (!stringValue.matches("^[\\w-.]+@[\\w-]+\\.[a-zA-Z]{2,}$")) {
                    throw new Exception("Le champ '" + field.getName() + "' doit être un email valide.");
                }
            }
        }
    }
}
