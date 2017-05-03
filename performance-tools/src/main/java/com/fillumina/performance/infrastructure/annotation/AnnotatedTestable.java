package com.fillumina.performance.infrastructure.annotation;

import com.fillumina.performance.util.AnnotationHelper;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
@Deprecated
public class AnnotatedTestable implements Runnable {

    private final Runnable runnable;

    public AnnotatedTestable(Runnable runnable) {
        this.runnable = runnable;
    }

    /**
     * Called at every initialization of the run (might be more than once,
 i.e. if warmup is required). Its execution time is not accounted.
     */
    public void setUp() {
        AnnotationHelper.callMethods(runnable, SetUp.class);
    }

    /**
     * Called before every sample (number of iterations accounted for a single
     * measure) of {@link #run()}, its execution time is not accounted.
     *
     * @param iterations number of iterations to be performed.
     */
    public void onBeforeSample(int iterations) {
        AnnotationHelper.callMethods(runnable, BeforeSample.class);
        AnnotationHelper.callMethods(runnable, BeforeSample.class, iterations);
    }

    /**
     * Executes the run for the number of iterations specified in
 {@link #onBeforeSample(int) }.
     * <p>
     * To avoid dead code eviction use one of the {@link Sink#drain(Object)}
     * methods.
     */
    public void run() {
        runnable.run();
    }

    /**
     * Executed after the sample.
     *
     * @param iterations executed
     */
    public void onAfterSample(int iterations) {
        AnnotationHelper.callMethods(runnable, AfterSample.class);
        AnnotationHelper.callMethods(runnable, AfterSample.class, iterations);
    }

    /**
     * Executed when run is done. Can be called more than once but always
     * after {@link #setUp() }.
     */
    public void tearDown() {
        AnnotationHelper.callMethods(runnable, TearDown.class);
    }
}
