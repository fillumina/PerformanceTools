package com.fillumina.performance.producer.suite;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public abstract class ParametrizedSequenceRunnable<P,S> {

    public static final ParametrizedSequenceRunnable<?,?> NULL =
            new ParametrizedSequenceRunnable<Object, Object>() {

        @Override
        public void call(final Object param, final Object sequence) {
            // do nothing;
        }
    };

    /** Called before each test to allow for initialization. */
    public void setUp(P param, S sequence) {}

    /**
     * The test code. You should use the
     * {@link ParametrizedRunnableSink} to avoid the test code to
     * be cut out by the dead code removal optimization of the JVM.
     */
    public abstract void call(P param, S sequence);
}
