package mg.sprint.framework.annotation.controller;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface BaseUrl {
    String path() default "";
}
