package mg.sprint.framework.core.object;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mg.sprint.framework.annotation.http.Get;
import mg.sprint.framework.annotation.http.Post;

public class Mapping {
    private static final Logger logger = LoggerFactory.getLogger(Mapping.class);
    private Class<?> controllerClass;
    private List<VerbAction> verbActions = new ArrayList<>();
    private final int controllerAuthLevel;

    public Mapping(Class<?> controllerClass, Method method) {
        this.controllerClass = controllerClass;
        this.controllerAuthLevel = -1;
        String verb = "GET";
        if (method.isAnnotationPresent(Post.class)) {
            verb = "POST";
        } else if (method.isAnnotationPresent(Get.class)) {
            verb = "GET";
        }
        this.verbActions.add(new VerbAction(verb, method.getName()));
        logger.debug("Created mapping for controller {} with method {} and verb {}", 
            controllerClass.getName(), method.getName(), verb);
    }

    public Mapping(Class<?> controllerClass, Method method, int controllerAuthLevel) {
        this.controllerClass = controllerClass;
        this.controllerAuthLevel = controllerAuthLevel;
        String verb = "GET";
        if (method.isAnnotationPresent(Post.class)) {
            verb = "POST";
        } else if (method.isAnnotationPresent(Get.class)) {
            verb = "GET";
        }
        this.verbActions.add(new VerbAction(verb, method.getName()));
        logger.debug("Created mapping for controller {} with method {}, verb {}, and auth level {}", 
            controllerClass.getName(), method.getName(), verb, controllerAuthLevel);
    }

    public Class<?> getControllerClass() {
        return controllerClass;
    }

    public List<VerbAction> getVerbActions() {
        return verbActions;
    }

    public void addVerbAction(String verb, String methodName) {
        this.verbActions.add(new VerbAction(verb, methodName));
        logger.debug("Added verb action: verb={}, method={}", verb, methodName);
    }

    public Method getMethodByVerb(String verb) throws NoSuchMethodException {
        for (VerbAction va : verbActions) {
            if (va.getVerb().equalsIgnoreCase(verb)) {
                for (Method m : controllerClass.getDeclaredMethods()) {
                    if (m.getName().equals(va.getMethodName())) {
                        logger.debug("Found method {} for verb {}", m.getName(), verb);
                        return m;
                    }
                }
            }
        }
        logger.error("No method found for verb {} in controller {}", verb, controllerClass.getName());
        throw new NoSuchMethodException("Méthode non trouvée pour le verbe HTTP : " + verb);
    }

    public int getControllerAuthLevel() {
        return controllerAuthLevel;
    }
}