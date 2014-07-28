package com.fillumina.performance.util;

/**
 *
 * @author Francesco Illuminati
 */
public class EmptyRunnable implements Runnable {

    public static final EmptyRunnable INSTANCE = new EmptyRunnable();

    private EmptyRunnable() {}

    @Override
    public void run() {
        // nothing here
    }
}
