package com.fillumina.performance.suite;

/**
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

    public void beforeTest(P param, S sequence, int iterations) {}

    public abstract Object test(P param, S sequence);
}
