package com.fillumina.performance.producer.timer;

/**
 * It's a {@link Runnable} that avoids dead code eviction by the JVM taking
 * care of the results of the tested calculation in a way that the calculation
 * itself will not be evicted.
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
    public abstract Object test();
}
