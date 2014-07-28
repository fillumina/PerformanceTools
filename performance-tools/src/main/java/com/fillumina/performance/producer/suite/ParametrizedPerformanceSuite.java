package com.fillumina.performance.producer.suite;

import com.fillumina.performance.producer.PerformanceExecutorInstrumenter;
import com.fillumina.performance.producer.timer.InitializingRunnable;
import com.fillumina.performance.producer.LoopPerformances;
import com.fillumina.performance.producer.LoopPerformancesHolder;

/**
 * Instrumenter that allows to execute a parametrized test.
 * If a test has been already added to the
 * {@link com.fillumina.performance.producer.timer.PerformanceTimer}
 * it will be executed alongside the parametrized
 * one defined by this class.
 * Applying this class to the right instrumenter allows to execute the tests
 * in a single-threaded or multi-threaded environment
 * (see {@link com.fillumina.performance.PerformanceTimerFactory}).
 *
 * @author Francesco Illuminati
 */
public class ParametrizedPerformanceSuite<T>
        extends AbstractParametrizedInstrumenterSuite
            <ParametrizedPerformanceSuite<T>, T>
        implements PerformanceExecutorInstrumenter, ParametrizedExecutor<T> {
    private static final long serialVersionUID = 1L;

    private ParametrizedRunnable<T> actualTest;

    @Override
    @SuppressWarnings("unchecked")
    protected Runnable wrap(final Object parameter) {
        return new InnerRunnable((T)parameter);
    }

    /**
     * Executes the given test against the previously added parameters.
     *
     * @return the same performance given to the consumer.
     */
    @Override
    public LoopPerformancesHolder executeTest(
            final ParametrizedRunnable<? extends T> test) {
        return executeTest(null, test);
    }

    @Override
    public LoopPerformancesHolder ignoreTest(
            final ParametrizedRunnable<? extends T> test) {
        return LoopPerformancesHolder.empty();
    }

    /**
     * Executes the given named test against the previously added parameters.
     *
     * @return the same performance given to the consumer.
     */
    @SuppressWarnings("unchecked")
    @Override
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

    @Override
    public LoopPerformancesHolder ignoreTest(final String name,
            final ParametrizedRunnable<? extends T> test) {
        return LoopPerformancesHolder.empty();
    }

    private class InnerRunnable implements InitializingRunnable {

        private final T t;

        private InnerRunnable(final T t) {
            this.t = t;
        }

        @Override
        public void setUp() {
            actualTest.setUp(t);
        }

        @Override
        public void run() {
            actualTest.call(t);
        }
    }
}
