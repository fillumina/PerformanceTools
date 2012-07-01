package com.fillumina.performance;

import java.util.concurrent.TimeUnit;

/**
 *
 * @author fra
 */
public class PerformanceSuite<T> {
    private final PerformanceTimer performanceTimer;
    private ParametrizedRunnable<T> callable;

    //TODO create a builder to easy this long constructor
    public PerformanceSuite(final PerformanceTimer performanceTimer) {
        this.performanceTimer = performanceTimer;
    }

    public void addObjectToTest(final String message, final T t) {
        performanceTimer.addTest(message, new InnerRunnable(t));
    }

    public void execute(final String message,
            final int loops,
            final ParametrizedRunnable<T> test) {
        setTest(test);
        performanceTimer.clear();
        performanceTimer.iterate(loops);
        new StringTablePresenter(performanceTimer)
                .setMessage(message)
                .getTable()
                .println();
    }

    private void setTest(final ParametrizedRunnable<T> callable) {
        this.callable = callable;
    }

    public class InnerRunnable implements InitializingRunnable {

        private final T t;

        private InnerRunnable(final T t) {
            this.t = t;
        }

        @Override
        public void init() {
            callable.setUp(t);
        }

        @Override
        public void run() {
            callable.call(t);
        }
    }

}
