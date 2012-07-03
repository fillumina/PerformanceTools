package com.fillumina.performance.sequence;

/**
 *
 * @author fra
 */
public abstract class SequencedParametrizedRunnable<P,S> {

    public static final SequencedParametrizedRunnable<?,?> NULL =
            new SequencedParametrizedRunnable<Object, Object>() {

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
