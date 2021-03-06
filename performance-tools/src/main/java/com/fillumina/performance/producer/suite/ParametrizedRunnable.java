package com.fillumina.performance.producer.suite;

/**
 * Passes a parameter to the code under test.
 *
 * @author Francesco Illuminati
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
