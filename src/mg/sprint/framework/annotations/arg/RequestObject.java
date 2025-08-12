package mg.sprint.framework.annotations.arg;

import java.lang.annotation.*;

@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface RequestObject {
    String name();  
}
