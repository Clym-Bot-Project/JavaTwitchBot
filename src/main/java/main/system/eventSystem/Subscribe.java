package main.system.eventSystem;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Documented
public @interface Subscribe {

    Class<?> EventClass();

//    @AliasFor(annotation = SuppressWarnings.class, attribute = "value")
//    String[] value() default {"unused"};
}
