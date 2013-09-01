package com.fillumina.performance.producer.timer;

/**
 * It's a {@link Runnable} that avoid dead code eviction.
 *
 * @author fra
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
        // will not been evicted
        if (obj == REFERENCE) {
            throw new IllegalStateException();
        }
    }

}
