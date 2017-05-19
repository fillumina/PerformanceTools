package com.fillumina.performance.mock;

/**
 * Do nothing run, use only with mocks.
 * <b>This run will be evicted by the JVM.</b>
 *
 * @author Francesco Illuminati
 */
public class NullRunnable implements Runnable {
    public static final NullRunnable INSTANCE = new NullRunnable();

    private NullRunnable() {}

    @Override
    public void run() {
    }
}
