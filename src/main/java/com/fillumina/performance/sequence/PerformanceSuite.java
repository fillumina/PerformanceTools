package com.fillumina.performance.sequence;

import com.fillumina.performance.InitializingRunnable;
import com.fillumina.performance.PerformanceConsumer;
import com.fillumina.performance.timer.PerformanceTimer;

/**
 *
 * @author fra
 */
public class PerformanceSuite<T> {
    private final PerformanceTimer performanceTimer;
    private ParametrizedRunnable<T> callable;
    private PerformanceConsumer consumer;

    public PerformanceSuite(final PerformanceTimer performanceTimer) {
        this.performanceTimer = performanceTimer;
    }

    public void addObjectToTest(final String message, final T t) {
        performanceTimer.addTest(message, new InnerRunnable(t));
    }

    public PerformanceSuite<T> setPerformanceConsumer(
            final PerformanceConsumer consumer) {
        this.consumer = consumer;
        return this;
    }

    public PerformanceTimer execute(final String message,
            final int loops,
            final ParametrizedRunnable<T> test) {
        setTest(test);
        performanceTimer.clear();
        performanceTimer.iterate(loops);
        if (consumer != null) {
            performanceTimer.use(consumer)
                .setMessage(message)
                .process();
        }
        return performanceTimer;
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
