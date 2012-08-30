package com.fillumina.performance.producer.suite;

import com.fillumina.performance.producer.AbstractPerformanceProducer;
import com.fillumina.performance.producer.timer.InitializableRunnable;
import com.fillumina.performance.producer.timer.LoopPerformances;
import com.fillumina.performance.producer.timer.PerformanceTimer;

/**
 *
 * @author fra
 */
public class ParametrizedPerformanceSuite<T>
        extends AbstractPerformanceProducer<ParametrizedPerformanceSuite<T>> {
    private static final long serialVersionUID = 1L;

    private final PerformanceTimer performanceTimer;
    private ParametrizedRunnable<T> callable;

    public ParametrizedPerformanceSuite(final PerformanceTimer performanceTimer) {
        this.performanceTimer = performanceTimer;
    }

    public ParametrizedPerformanceSuite<T> addObjectToTest(final String name,
            final T object) {
        performanceTimer.addTest(name, new InnerRunnable(object));
        return this;
    }

    public PerformanceTimer executeTest(final String name,
            final int iterationNumber,
            final ParametrizedRunnable<T> test) {
        setActualTest(test);
        final LoopPerformances loopPerformances =
                performanceTimer.iterate(iterationNumber).getLoopPerformances();
        processConsumers(name, loopPerformances);
        return performanceTimer;
    }

    private void setActualTest(final ParametrizedRunnable<T> callable) {
        this.callable = callable;
    }

    private class InnerRunnable implements InitializableRunnable {

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
