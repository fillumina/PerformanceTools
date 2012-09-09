package com.fillumina.performance.producer.suite;

import com.fillumina.performance.producer.PerformanceExecutorInstrumenter;
import com.fillumina.performance.producer.timer.InitializableRunnable;
import com.fillumina.performance.producer.LoopPerformances;
import com.fillumina.performance.producer.LoopPerformancesHolder;

/**
 *
 * @author fra
 */
public class ParametrizedPerformanceSuite<T>
        extends ParametrizedInstrumenterSuite<ParametrizedPerformanceSuite<T>>
        implements PerformanceExecutorInstrumenter {
    private static final long serialVersionUID = 1L;

    private ParametrizedRunnable<T> callable;

    public ParametrizedPerformanceSuite<T> addObjectToTest(final String name,
            final T object) {
        getPerformanceExecutor().addTest(name, new InnerRunnable(object));
        return this;
    }

    @SuppressWarnings("unchecked")
    public LoopPerformancesHolder executeTest(final String name,
            final ParametrizedRunnable<? extends T> test) {
        setActualTest((ParametrizedRunnable<T>)test);

        final LoopPerformances loopPerformances =
                getPerformanceExecutor().execute().getLoopPerformances();

        processConsumers(name, loopPerformances);
        addTestLoopPerformances(name, loopPerformances);

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
