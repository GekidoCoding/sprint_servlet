package mg.sprint.framework.core.object;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import mg.sprint.framework.annotation.http.Get;
import mg.sprint.framework.annotation.http.Post;

public class Mapping {
    private Class<?> controllerClass;
    private List<VerbAction> verbActions = new ArrayList<>();

    public Mapping(Class<?> controllerClass, Method method) {
        this.controllerClass = controllerClass;
        String verb = "GET";
        if (method.isAnnotationPresent(Post.class)) {
            verb = "POST";
        } else if (method.isAnnotationPresent(Get.class)) {
            verb = "GET";
        }
        this.verbActions.add(new VerbAction(verb, method.getName()));
    }

    public Class<?> getControllerClass() {
        return controllerClass;
    }

    public List<VerbAction> getVerbActions() {
        return verbActions;
    }

    public void addVerbAction(String verb, String methodName) {
        this.verbActions.add(new VerbAction(verb, methodName));
    }

    // Retourne la méthode correspondant au verbe HTTP
    public Method getMethodByVerb(String verb) throws NoSuchMethodException {
        for (VerbAction va : verbActions) {
            if (va.getVerb().equalsIgnoreCase(verb)) {
                for (Method m : controllerClass.getDeclaredMethods()) {
                    if (m.getName().equals(va.getMethodName())) {
                        return m;
                    }
                }
            }
        }
        throw new NoSuchMethodException("Méthode non trouvée pour le verbe HTTP : " + verb);
    }
}
