package com.fillumina.performance.timer;

/**
 *
 *
 * @author fra
 */
public interface InitializingRunnable extends Runnable {

    /** Called once before starting to iterate over {@link #run()}. */
    void init();
}
