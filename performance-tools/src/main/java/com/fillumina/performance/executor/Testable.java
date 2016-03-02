package com.fillumina.performance.executor;

/**
 * Defines a test.
 *
 * @author Francesco Illuminati
 */
public interface Testable {

    /** Called once when initializing the test. */
    void setUp();

    /**
     * Executed before every bunch of iterations of {@link #test()},
     * its time is not accounted.
     */
    void beforeTest(int iterations);

    /**
     * Executes the test.
     * @return the result of the operation under test so the code
     *  related to it will not be evicted by JVM the dead code optimization.
     */
    public Object test();
}
