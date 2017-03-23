package com.fillumina.performance.suite;

/**
 * @param P parameter
 * @param S sequence
 *
 * @author Francesco Illuminati
 */
public abstract class ParameterizedSequenceTestable<P,S> {
    public static final ParameterizedSequenceTestable<?,?> NULL =
            new ParameterizedSequenceTestable<Object, Object>() {

        @Override
        public Object test(final Object param, final Object sequence) {
            return null;
        }
    };

    public void setUp(P param, S sequence) {}

    public void onBeforeTest(P param, S sequence, int iterations) {}

    public abstract Object test(P param, S sequence);

    public void onAfterTest(P param, S sequence, int iterations) {}

    public void tearDown(P param, S sequence) {}
}
