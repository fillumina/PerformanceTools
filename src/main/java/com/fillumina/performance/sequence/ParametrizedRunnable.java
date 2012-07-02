package com.fillumina.performance.sequence;

/**
 *
 * @author fra
 */
public abstract class ParametrizedRunnable<P> {

    public static final ParametrizedRunnable<?> NULL =
            new ParametrizedRunnable<Object>() {

        @Override
        public void call(final Object param) {
            // do nothing;
        }
    };

    /** Called before each test to initialize the {@code param}. */
    public void setUp(P param) {}

    /** Contains the test. */
    public abstract void call(P param);
}
