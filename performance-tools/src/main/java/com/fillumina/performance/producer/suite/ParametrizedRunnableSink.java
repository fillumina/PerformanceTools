package com.fillumina.performance.producer.suite;

/**
 * It's a {@link ParametrizedRunnable} that avoids dead code eviction.
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public abstract class ParametrizedRunnableSink<P>
        extends ParametrizedRunnable<P> {
    private static final Object REFERENCE = new Object();

    /**
     * Override this method to provide the test's code.
     * The returned value is managed so the code used to get it is not
     * removed by the optimizations of the JVM.
     */
    public abstract Object sink(final P param);

    @Override
    public void call(final P param) {
        final Object obj = sink(param);
        // force obj to be evaluated and so the code returning it
        // will not be evicted
        if (obj == REFERENCE) {
            throw new AssertionError();
        }
    }
}
