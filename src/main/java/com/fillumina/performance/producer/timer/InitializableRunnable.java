package com.fillumina.performance.producer.timer;

/**
 * It allows to initialize the test.
 *
 * @author fra
 */
public interface InitializableRunnable extends Runnable {

    /** Called once before starting to iterate over {@link #run()}. */
    void init();
}
