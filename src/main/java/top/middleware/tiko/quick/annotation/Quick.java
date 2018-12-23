package top.middleware.tiko.quick.annotation;

import org.springframework.stereotype.Component;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Component
@Target(TYPE)
@Retention(RUNTIME)
public @interface Quick {
    String[] requestMapping();
}
