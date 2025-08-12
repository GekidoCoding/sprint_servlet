package mg.sprint.framework.annotation.method;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Url {
    String path();
}
