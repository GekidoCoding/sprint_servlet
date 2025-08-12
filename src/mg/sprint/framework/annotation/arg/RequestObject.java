package mg.sprint.framework.annotation.arg;

import java.lang.annotation.*;

@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface RequestObject {
    String name();  
}
