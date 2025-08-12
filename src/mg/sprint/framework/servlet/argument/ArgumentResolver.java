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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ArgumentResolver {
    private static final Logger logger = LoggerFactory.getLogger(ArgumentResolver.class);
    private final Paranamer paranamer = new BytecodeReadingParanamer();

    public Object[] buildMethodArguments(Method method, HttpServletRequest req, 
                                       ValidationManager validationManager) throws Exception {
        logger.debug("Building arguments for method: {}", method.getName());
        Parameter[] parameters = method.getParameters();
        Object[] args = new Object[parameters.length];
        String[] paramNames = paranamer.lookupParameterNames(method, false);

        for (int i = 0; i < parameters.length; i++) {
            Parameter param = parameters[i];
            args[i] = resolveArgument(param, req, paramNames, i, validationManager);
            logger.trace("Resolved argument #{}: {}", i, args[i]);
        }

        return args;
    }

    private Object resolveArgument(Parameter param, HttpServletRequest req, String[] paramNames, 
                                 int index, ValidationManager validationManager) throws Exception {
        logger.debug("Resolving argument: type={}, index={}", param.getType().getSimpleName(), index);
        
        if (param.getType().equals(MySession.class)) {
            MySession session = new MySession(req.getSession());
            logger.debug("Resolved MySession argument");
            return session;
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
        logger.debug("Resolving RequestObject with prefix: {}", prefix);

        for (Field field : obj.getClass().getDeclaredFields()) {
            String fieldName = getFieldName(field);
            String paramValue = req.getParameter(prefix + "." + fieldName);
            logger.trace("Processing field: {}.{}", prefix, fieldName);
            
            if (paramValue != null) {
                field.setAccessible(true);
                field.set(obj, ConvertUtil.convertValue(paramValue, field.getType()));
                logger.trace("Set field {}.{} to value: {}", prefix, fieldName, paramValue);
            }
        }

        validateRequestObject(obj, prefix, validationManager);
        return obj;
    }

    private String getFieldName(Field field) {
        String fieldName = field.isAnnotationPresent(FormName.class) 
            ? field.getAnnotation(FormName.class).value() 
            : field.getName();
        logger.trace("Determined field name: {}", fieldName);
        return fieldName;
    }

    private void validateRequestObject(Object obj, String prefix, ValidationManager validationManager) {
        logger.debug("Validating RequestObject: {}", obj.getClass().getName());
        ValidationManager objValidationManager = ValidationUtil.validate(obj);
        
        if (objValidationManager.hasErrors()) {
            for (Map.Entry<String, List<String>> entry : objValidationManager.getFieldErrors().entrySet()) {
                String fieldKey = prefix + "." + entry.getKey();
                for (String error : entry.getValue()) {
                    validationManager.addError(fieldKey, error);
                    logger.warn("Validation error for {}.{}: {}", prefix, entry.getKey(), error);
                }
            }

            for (Map.Entry<String, String> entry : objValidationManager.getFieldValues().entrySet()) {
                String fieldKey = prefix + "." + entry.getKey();
                validationManager.addValue(fieldKey, entry.getValue());
                logger.trace("Added validation value for {}.{}: {}", prefix, entry.getKey(), entry.getValue());
            }
        }
    }

    private Part resolvePartParameter(Parameter param, HttpServletRequest req, 
                                    String[] paramNames, int index) throws Exception {
        String name = getParameterName(param, paramNames, index);
        Part part = req.getPart(name);
        
        if (part == null) {
            logger.error("File '{}' missing in form", name);
            throw new IllegalArgumentException("Fichier '" + name + "' manquant dans le formulaire");
        }
        logger.debug("Resolved file part: {}", name);
        return part;
    }

    private Object resolveSimpleParameter(Parameter param, HttpServletRequest req, 
                                        String[] paramNames, int index) throws Exception {
        String name = getParameterName(param, paramNames, index);
        String value = req.getParameter(name);
        
        if (value == null) {
            logger.error("Parameter '{}' missing in request", name);
            throw new IllegalArgumentException("Paramètre '" + name + "' manquant dans la requête");
        }
        
        Object convertedValue = ConvertUtil.convertValue(value, param.getType());
        logger.trace("Resolved parameter {}: value={}", name, convertedValue);
        return convertedValue;
    }

    private String getParameterName(Parameter param, String[] paramNames, int index) {
        if (param.isAnnotationPresent(RequestParam.class)) {
            String paramName = param.getAnnotation(RequestParam.class).value();
            logger.trace("Using RequestParam annotation for parameter name: {}", paramName);
            return paramName;
        }
        
        if (paramNames != null && index < paramNames.length) {
            logger.trace("Using paranamer for parameter name: {}", paramNames[index]);
            return paramNames[index];
        }
        
        logger.error("Parameter name not found for index {}", index);
        throw new IllegalArgumentException("Nom du paramètre introuvable pour le paramètre #" + index);
    }
}