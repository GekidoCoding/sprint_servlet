package mg.sprint.framework.annotation.controller;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RestAPI {
}
