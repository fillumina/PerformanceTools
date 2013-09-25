package com.fillumina.performance.producer.suite;

import com.fillumina.performance.producer.PerformanceExecutorInstrumenter;
import com.fillumina.performance.producer.timer.InitializingRunnable;
import com.fillumina.performance.producer.LoopPerformances;
import com.fillumina.performance.producer.LoopPerformancesHolder;

/**
 * Executes a test with different parameters.
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class ParametrizedPerformanceSuite<T>
        extends AbstractParametrizedInstrumenterSuite
            <ParametrizedPerformanceSuite<T>, T>
        implements PerformanceExecutorInstrumenter {
    private static final long serialVersionUID = 1L;

    private ParametrizedRunnable<T> actualTest;

    @Override
    @SuppressWarnings("unchecked")
    protected Runnable wrap(final Object parameter) {
        return new InnerRunnable((T)parameter);
    }

    /** Executes the given test against the previously added parameters. */
    public LoopPerformancesHolder executeTest(
            final ParametrizedRunnable<? extends T> test) {
        return executeTest(null, test);
    }

    /** Executes the given named test against the previously added parameters. */
    @SuppressWarnings("unchecked")
    public LoopPerformancesHolder executeTest(final String name,
            final ParametrizedRunnable<? extends T> test) {
        addTestsToPerformanceExecutor();
        this.actualTest = (ParametrizedRunnable<T>) test;

        final LoopPerformances loopPerformances =
                getPerformanceExecutor().execute().getLoopPerformances();

        consume(name, loopPerformances);
        addTestLoopPerformances(name, loopPerformances);

        return new LoopPerformancesHolder(name, loopPerformances);
    }

    private class InnerRunnable implements InitializingRunnable {

        private final T t;

        private InnerRunnable(final T t) {
            this.t = t;
        }

        @Override
        public void init() {
            actualTest.setUp(t);
        }

        @Override
        public void run() {
            actualTest.call(t);
        }
    }
}
