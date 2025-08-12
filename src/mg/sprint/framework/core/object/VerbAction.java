package mg.sprint.framework.core.object;

public class VerbAction {
    private final String verb;
    private final String methodName;

    public VerbAction(String verb, String methodName) {
        this.verb = verb;
        this.methodName = methodName;
    }

    public String getVerb() {
        return verb;
    }

    public String getMethodName() {
        return methodName;
    }
}