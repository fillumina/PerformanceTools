package com.fillumina.performance.producer.sequence;

/**
 *
 * @author fra
 */
public abstract class ParametrizedSequenceRunnable<P,S> {

    public static final ParametrizedSequenceRunnable<?,?> NULL =
            new ParametrizedSequenceRunnable<Object, Object>() {

        @Override
        public void call(final Object param, final Object sequence) {
            // do nothing;
        }
    };

    /** Called before each test to initialize the {@code param}. */
    public void setUp(P param, S sequence) {}

    /** Contains the test. */
    public abstract void call(P param, S sequence);
}
