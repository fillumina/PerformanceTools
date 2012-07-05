package com.fillumina.performance.producer.sequence;

import com.fillumina.performance.producer.AbstractChainablePerformanceProducer;
import com.fillumina.performance.producer.timer.InitializingRunnable;
import com.fillumina.performance.producer.timer.PerformanceTimer;

/**
 *
 * @author fra
 */
public class PerformanceSuite<T>
        extends AbstractChainablePerformanceProducer<PerformanceSuite<T>> {
    private static final long serialVersionUID = 1L;

    private final PerformanceTimer performanceTimer;
    private ParametrizedRunnable<T> callable;

    public PerformanceSuite(final PerformanceTimer performanceTimer) {
        this.performanceTimer = performanceTimer;
    }

    public PerformanceSuite<T> addObjectToTest(final String name, final T t) {
        performanceTimer.addTest(name, new InnerRunnable(t));
        return this;
    }

    public PerformanceTimer execute(final String name,
            final int loops,
            final ParametrizedRunnable<T> test) {
        setTest(test);
        performanceTimer.clear();
        performanceTimer.iterate(loops);
        processConsumer(name, performanceTimer.getLoopPerformances());
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
