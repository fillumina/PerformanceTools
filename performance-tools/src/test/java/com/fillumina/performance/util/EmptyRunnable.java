package com.fillumina.performance.util;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class EmptyRunnable implements Runnable {

    public static final EmptyRunnable INSTANCE = new EmptyRunnable();

    private EmptyRunnable() {}

    @Override
    public void run() {
        // nothing here
    }
}
