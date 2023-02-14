package main.system.hookSystem;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Documented
public @interface Hook {

//    @AliasFor(annotation = SuppressWarnings.class, attribute = "value")
//    String[] value() default {"unused"};
}
