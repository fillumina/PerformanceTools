package com.fillumina.performance.producer.suite;

/**
 * It's a {@link ParametrizedRunnable} that avoids dead code eviction.
 *
 * @author fra
 */
public abstract class ParametrizedRunnableSink<P>
        extends ParametrizedRunnable<P> {
    private static final Object REFERENCE = new Object();

    public abstract Object sink(final P param);

    @Override
    public void call(final P param) {
        final Object obj = sink(param);
        // force obj to be evaluated and so the code returning it
        // will not be evicted
        if (obj == REFERENCE) {
            throw new IllegalStateException();
        }
    }
}
