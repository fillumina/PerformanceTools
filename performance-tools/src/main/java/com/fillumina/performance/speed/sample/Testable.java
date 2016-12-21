package com.fillumina.performance.speed.sample;

/**
 * Defines a test.
 *
 * @author Francesco Illuminati
 */
public interface Testable {

    /** Do nothing Test. Use as baseline. */
    Testable NULL = new Testable() {
        @Override public void setUp() {}
        @Override public void onBeforeSample(int iterations) {}
        @Override public Object test() {return null;}
    };

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
    Object test();
}
