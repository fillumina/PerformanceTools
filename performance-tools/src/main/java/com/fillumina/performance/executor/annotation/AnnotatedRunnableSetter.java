package com.fillumina.performance.executor.annotation;

import com.fillumina.performance.util.AnnotationHelper;

/**
 * Ancillary class used to access package accessors of {@link Testable}.
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class AnnotatedRunnableSetter {

    public static final AnnotatedRunnableSetter INSTANCE =
            new AnnotatedRunnableSetter();

    public void setUp(Runnable runnable) {
        AnnotationHelper.callMethods(runnable, SetUp.class);
    }

    public void tearDown(Runnable runnable) {
        AnnotationHelper.callMethods(runnable, TearDown.class);
    }

    public void onBeforeSample(Runnable runnable, int iterations) {
        AnnotationHelper.callMethods(runnable, BeforeSample.class);
        AnnotationHelper.callMethods(runnable, BeforeSample.class, iterations);
    }

    public void onAfterSample(Runnable runnable, int iterations) {
        AnnotationHelper.callMethods(runnable, AfterSample.class);
        AnnotationHelper.callMethods(runnable, AfterSample.class, iterations);
    }
}
