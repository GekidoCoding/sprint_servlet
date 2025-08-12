package mg.sprint.framework.servlet.argument;

import com.thoughtworks.paranamer.BytecodeReadingParanamer;
import com.thoughtworks.paranamer.Paranamer;
import mg.sprint.framework.core.object.MySession;
import mg.sprint.framework.core.manager.ValidationManager;
import mg.sprint.framework.utils.ConvertUtil;
import mg.sprint.framework.utils.ValidationUtil;
import mg.sprint.framework.annotation.arg.RequestObject;
import mg.sprint.framework.annotation.arg.RequestParam;
import mg.sprint.framework.annotation.field.FormName;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Part;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.List;
import java.util.Map;

public class ArgumentResolver {
    private final Paranamer paranamer = new BytecodeReadingParanamer();

    public Object[] buildMethodArguments(Method method, HttpServletRequest req, 
                                       ValidationManager validationManager) throws Exception {
        Parameter[] parameters = method.getParameters();
        Object[] args = new Object[parameters.length];
        String[] paramNames = paranamer.lookupParameterNames(method, false);

        for (int i = 0; i < parameters.length; i++) {
            Parameter param = parameters[i];
            args[i] = resolveArgument(param, req, paramNames, i, validationManager);
        }

        return args;
    }

    private Object resolveArgument(Parameter param, HttpServletRequest req, String[] paramNames, 
                                 int index, ValidationManager validationManager) throws Exception {
        
        if (param.getType().equals(MySession.class)) {
            return new MySession(req.getSession());
        }
        
        if (param.isAnnotationPresent(RequestObject.class)) {
            return resolveRequestObject(param, req, validationManager);
        }
        
        if (param.getType().equals(Part.class)) {
            return resolvePartParameter(param, req, paramNames, index);
        }
        
        return resolveSimpleParameter(param, req, paramNames, index);
    }

    private Object resolveRequestObject(Parameter param, HttpServletRequest req, 
                                      ValidationManager validationManager) throws Exception {
        RequestObject requestObjectAnn = param.getAnnotation(RequestObject.class);
        String prefix = requestObjectAnn.name();
        Object obj = param.getType().getDeclaredConstructor().newInstance();

        for (Field field : obj.getClass().getDeclaredFields()) {
            String fieldName = getFieldName(field);
            String paramValue = req.getParameter(prefix + "." + fieldName);
            
            if (paramValue != null) {
                field.setAccessible(true);
                field.set(obj, ConvertUtil.convertValue(paramValue, field.getType()));
            }
        }

        validateRequestObject(obj, prefix, validationManager);
        return obj;
    }

    private String getFieldName(Field field) {
        if (field.isAnnotationPresent(FormName.class)) {
            return field.getAnnotation(FormName.class).value();
        }
        return field.getName();
    }

    private void validateRequestObject(Object obj, String prefix, ValidationManager validationManager) {
        ValidationManager objValidationManager = ValidationUtil.validate(obj);
        
        if (objValidationManager.hasErrors()) {
            // Ajouter les erreurs avec le préfixe
            for (Map.Entry<String, List<String>> entry : objValidationManager.getFieldErrors().entrySet()) {
                String fieldKey = prefix + "." + entry.getKey();
                for (String error : entry.getValue()) {
                    validationManager.addError(fieldKey, error);
                }
            }

            // Ajouter les valeurs avec le préfixe
            for (Map.Entry<String, String> entry : objValidationManager.getFieldValues().entrySet()) {
                String fieldKey = prefix + "." + entry.getKey();
                validationManager.addValue(fieldKey, entry.getValue());
            }
        }
    }

    private Part resolvePartParameter(Parameter param, HttpServletRequest req, 
                                    String[] paramNames, int index) throws Exception {
        String name = getParameterName(param, paramNames, index);
        Part part = req.getPart(name);
        
        if (part == null) {
            throw new IllegalArgumentException("Fichier '" + name + "' manquant dans le formulaire");
        }
        
        return part;
    }

    private Object resolveSimpleParameter(Parameter param, HttpServletRequest req, 
                                        String[] paramNames, int index) throws Exception {
        String name = getParameterName(param, paramNames, index);
        String value = req.getParameter(name);
        
        if (value == null) {
            throw new IllegalArgumentException("Paramètre '" + name + "' manquant dans la requête");
        }
        
        return ConvertUtil.convertValue(value, param.getType());
    }

    private String getParameterName(Parameter param, String[] paramNames, int index) {
        if (param.isAnnotationPresent(RequestParam.class)) {
            return param.getAnnotation(RequestParam.class).value();
        }
        
        if (paramNames != null && index < paramNames.length) {
            return paramNames[index];
        }
        
        throw new IllegalArgumentException("Nom du paramètre introuvable pour le paramètre #" + index);
    }
}