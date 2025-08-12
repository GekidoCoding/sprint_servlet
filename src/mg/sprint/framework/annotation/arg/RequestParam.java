package mg.sprint.framework.annotation.arg;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface RequestParam {
    String value(); // nom du paramètre attendu dans la requête
}
