package com.fillumina.performance.speed.sample;

/**
 * Defines a test.
 *
 * @author Francesco Illuminati
 */
public interface Testable {

    /** Do nothing Test. Use as baseline. */
    Testable FASTEST = new Testable() {
        @Override public void setUp() {}
        @Override public void onBeforeSample(int iterations) {}
        @Override public Object test() {return null;}
    };

    /**
     * No memory used test. Use as baseline. It's also quite fast too while
     * trying to not be evicted by the JVM optimizations.
     */
    Testable NO_MEM = new Testable() {
        private int counter = 0;
        @Override public void setUp() {}
        @Override public void onBeforeSample(int iterations) {}
        @Override public Object test() {return null;}
        public int getCounter() {return counter;}
    };

    /** Called once when initializing the test. */
    void setUp();

    /**
     * Executed before every sample (number of iterations accounted for a single
     * measure) of {@link #test()}, its execution time is not accounted.
     */
    void onBeforeSample(int iterations);

    /**
     * Executes the test for the number of iterations specified in
     * {@link #onBeforeSample(int) }.
     *
     * @return the result of the operation under test so the code
     *  related to it will not be evicted by JVM the dead code optimization.
     */
    Object test();
}
