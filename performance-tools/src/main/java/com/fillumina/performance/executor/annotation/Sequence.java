package com.fillumina.performance.executor.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * A sequence repeat the test with the new values.
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
@Retention(value=RetentionPolicy.RUNTIME)
@Target(value=ElementType.FIELD)
public @interface Sequence {
    String value() default "";
}
