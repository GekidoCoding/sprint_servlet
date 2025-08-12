package mg.sprint.framework.core;

import java.lang.reflect.Method;

public class Mapping {
    public Class<?> controllerClass;
    public Method method;

    public Mapping(Class<?> controllerClass, Method method) {
        this.controllerClass = controllerClass;
        this.method = method;
    }
}
