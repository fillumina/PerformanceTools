package com.fillumina.performance.mock;

/**
 * Do nothing run, use only with mocks.
 * <b>This run will be evicted by the JVM.</b>
 *
 * @author Francesco Illuminati
 */
public class NullTestable implements Runnable {
    public static final NullTestable INSTANCE = new NullTestable();

    private NullTestable() {}

    @Override
    public void run() {
    }
}
