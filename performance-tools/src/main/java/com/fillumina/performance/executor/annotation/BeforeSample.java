package com.fillumina.performance.executor.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * The annotated method is executed before each sample and its time will not be
 * added with the test execution time.
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
@Retention(value=RetentionPolicy.RUNTIME)
@Target(value=ElementType.METHOD)
public @interface BeforeSample {

}
