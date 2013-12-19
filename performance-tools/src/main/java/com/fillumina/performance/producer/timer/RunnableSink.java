package com.fillumina.performance.producer.timer;

/**
 * It's a {@link Runnable} that avoids dead code eviction by the JVM taking
 * care of the results of the tested calculation in a way that the calculation
 * itself will not be evicted.
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public abstract class RunnableSink implements Runnable {
    private static final Object REFERENCE = new Object();

    /**
     * Just return the result of the operation under test and the code
     * related to it will not be evicted. The calculation about the
     * sinked value is very simple and light.
     */
    public abstract Object sink();

    @Override
    public void run() {
        final Object obj = sink();
        // force obj to be evaluated and so the code returning it
        // will not be evicted. The comparation is always false.
        if (obj == REFERENCE) {
            throw new IllegalStateException();
        }
    }
}
