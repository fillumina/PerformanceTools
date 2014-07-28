package com.fillumina.performance.producer.suite;

/**
 *
 * @author Francesco Illuminati
 */
public abstract class ParametrizedSequenceRunnable<P,S> {
    private static final Object REFERENCE = new Object();

    public static final ParametrizedSequenceRunnable<?,?> NULL =
            new ParametrizedSequenceRunnable<Object, Object>() {

        @Override
        public Object sink(final Object param, final Object sequence) {
            return null;
        }
    };

    /** Called before each test to allow for initialization. */
    public void setUp(P param, S sequence) {}

    public abstract Object sink(P param, S sequence);

    /**
     * The test code. You should use the
     * {@link ParametrizedRunnableSink} to avoid the test code to
     * be cut out by the dead code removal optimization of the JVM.
     */
    public void call(P param, S sequence) {
        final Object obj = sink(param, sequence);
        // force obj to be evaluated and so the code returning it
        // will not be evicted
        if (obj == REFERENCE) {
            throw new AssertionError();
        }
    }
}
