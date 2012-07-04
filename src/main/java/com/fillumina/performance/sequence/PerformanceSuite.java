package com.fillumina.performance.sequence;

import com.fillumina.performance.AbstractPerformanceProducer;
import com.fillumina.performance.timer.InitializingRunnable;
import com.fillumina.performance.timer.PerformanceTimer;

/**
 *
 * @author fra
 */
public class PerformanceSuite<T>
        extends AbstractPerformanceProducer<PerformanceSuite<T>> {
    private static final long serialVersionUID = 1L;

    private final PerformanceTimer performanceTimer;
    private ParametrizedRunnable<T> callable;

    public PerformanceSuite(final PerformanceTimer performanceTimer) {
        this.performanceTimer = performanceTimer;
    }

    public PerformanceSuite<T> addObjectToTest(final String message, final T t) {
        performanceTimer.addTest(message, new InnerRunnable(t));
        return this;
    }

    public PerformanceTimer execute(final String message,
            final int loops,
            final ParametrizedRunnable<T> test) {
        setTest(test);
        performanceTimer.clear();
        performanceTimer.iterate(loops);
        processConsumer(message, performanceTimer.getLoopPerformances());
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
