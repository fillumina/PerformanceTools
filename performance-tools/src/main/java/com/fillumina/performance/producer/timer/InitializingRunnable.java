package com.fillumina.performance.producer.timer;

/**
 * Allows to initialize the test.
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public interface InitializingRunnable extends Runnable {

    /**
     * Called once before starting to iterate over {@link #run()}.
     * Note that this method can be called several times
     * (one for each new iteration).
     * The setUp() time is expected to be accounted but will fade out with
     * the number of tests performed.
     */
    void setUp();
}
