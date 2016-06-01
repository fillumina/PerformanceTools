package com.fillumina.performance.sample;

/**
 * Defines a test.
 *
 * @author Francesco Illuminati
 */
public interface Testable {

    /** Called once when initializing the test. */
    void setUp();

    /**
     * Executed before every sample (number of iterations accounted for a single
     * measure) of {@link #test()}, its execution time is not accounted.
     */
    void onBeforeSample(int iterations);

    /**
     * Executes the test.
     * @return the result of the operation under test so the code
     *  related to it will not be evicted by JVM the dead code optimization.
     */
    public Object test();
}
