package com.fillumina.performance.sample.suite;

/**
 * Passes a parameter to the code under test.
 *
 * @author Francesco Illuminati
 */
public abstract class ParametrizedTestable<P> {

    public static final ParametrizedTestable<?> NULL =
            new ParametrizedTestable<Object>() {

        @Override
        public Object test(final Object param) {
            return null;
        }
    };

    /** Called before each test run to initialize the {@code param}. */
    public void setUp(P param) {}

    /**
     * Called before each sample of tests, its time is not
     * accounted.
     */
    public void onBeforeSample(P param, int iterations) {}

    /** Contains the test. */
    public abstract Object test(P param);

}
