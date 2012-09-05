package com.fillumina.performance.producer.suite;

import com.fillumina.performance.producer.AbstractPerformanceProducer;
import com.fillumina.performance.producer.PerformanceExecutor;
import com.fillumina.performance.producer.PerformanceExecutorInstrumenter;
import com.fillumina.performance.producer.timer.InitializableRunnable;
import com.fillumina.performance.producer.timer.LoopPerformances;
import com.fillumina.performance.producer.timer.LoopPerformancesHolder;

/**
 *
 * @author fra
 */
public class ParametrizedPerformanceSuite<T>
        extends AbstractPerformanceProducer<ParametrizedPerformanceSuite<?>>
        implements PerformanceExecutorInstrumenter {
    private static final long serialVersionUID = 1L;

    private PerformanceExecutor<?> performanceExecutor;
    private ParametrizedRunnable<T> callable;

    @Override
    public PerformanceExecutorInstrumenter instrument(
            final PerformanceExecutor<?> performanceExecutor) {
        this.performanceExecutor = performanceExecutor;
        return this;
    }

    public ParametrizedPerformanceSuite<T> addObjectToTest(final String name,
            final T object) {
        performanceExecutor.addTest(name, new InnerRunnable(object));
        return this;
    }

    @SuppressWarnings("unchecked")
    public LoopPerformancesHolder executeTest(final String name,
            final ParametrizedRunnable<? extends T> test) {
        setActualTest((ParametrizedRunnable<T>)test);
        final LoopPerformances loopPerformances =
                performanceExecutor.execute().getLoopPerformances();
        processConsumers(name, loopPerformances);
        return new LoopPerformancesHolder(loopPerformances);
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
