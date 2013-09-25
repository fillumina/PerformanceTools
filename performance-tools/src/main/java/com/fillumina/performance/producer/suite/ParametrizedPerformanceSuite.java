package com.fillumina.performance.producer.suite;

import com.fillumina.performance.producer.PerformanceExecutorInstrumenter;
import com.fillumina.performance.producer.timer.InitializingRunnable;
import com.fillumina.performance.producer.LoopPerformances;
import com.fillumina.performance.producer.LoopPerformancesHolder;

/**
 * Executes the same test with different values.
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class ParametrizedPerformanceSuite<T>
        extends AbstractParametrizedInstrumenterSuite
            <ParametrizedPerformanceSuite<T>, T>
        implements PerformanceExecutorInstrumenter {
    private static final long serialVersionUID = 1L;

    private ParametrizedRunnable<T> callable;

    @Override
    @SuppressWarnings("unchecked")
    protected Runnable wrap(final Object object) {
        return new InnerRunnable((T)object);
    }

    public LoopPerformancesHolder executeTest(
            final ParametrizedRunnable<? extends T> test) {
        return executeTest(null, test);
    }

    @SuppressWarnings("unchecked")
    public LoopPerformancesHolder executeTest(final String name,
            final ParametrizedRunnable<? extends T> test) {
        addTestsToPerformanceExecutor();
        setActualTest((ParametrizedRunnable<T>)test);

        final LoopPerformances loopPerformances =
                getPerformanceExecutor().execute().getLoopPerformances();

        consume(name, loopPerformances);
        addTestLoopPerformances(name, loopPerformances);

        return new LoopPerformancesHolder(name, loopPerformances);
    }

    private void setActualTest(final ParametrizedRunnable<T> callable) {
        this.callable = callable;
    }

    private class InnerRunnable implements InitializingRunnable {

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
