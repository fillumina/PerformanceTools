package com.fillumina.performance.executor.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * A parameter that will be specified by configuration. Each
 * different parameter creates a new test with the given value.
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
@Retention(value=RetentionPolicy.RUNTIME)
@Target(value=ElementType.FIELD)
public @interface Param {
    String value() default "";
}
