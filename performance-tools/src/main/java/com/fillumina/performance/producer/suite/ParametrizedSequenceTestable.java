package com.fillumina.performance.producer.suite;

/**
 *
 * @author Francesco Illuminati
 */
public abstract class ParametrizedSequenceTestable<P,S> {
    public static final ParametrizedSequenceTestable<?,?> NULL =
            new ParametrizedSequenceTestable<Object, Object>() {

        @Override
        public Object test(final Object param, final Object sequence) {
            return null;
        }
    };

    public void setUp(P param, S sequence) {}

    public void beforeTest(P param, S sequence) {}

    public abstract Object test(P param, S sequence);
}
