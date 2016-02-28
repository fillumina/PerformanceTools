package com.fillumina.performance.producer.suite;

import com.fillumina.performance.producer.LoopPerformances;
import com.fillumina.performance.producer.LoopPerformancesHolder;
import com.fillumina.performance.producer.PerformanceExecutorInstrumenter;
import com.fillumina.performance.producer.timer.Testable;

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

    private ParametrizedTestable<T> actualTest;

    @Override
    @SuppressWarnings("unchecked")
    protected Testable wrap(final Object parameter) {
        return new InnerTestable((T)parameter);
    }

    /**
     * Executes the given test against the previously added parameters.
     *
     * @return the same performance given to the consumer.
     */
    @Override
    public LoopPerformancesHolder executeTest(
            final ParametrizedTestable<? extends T> test) {
        return executeTest(null, test);
    }

    @Override
    public LoopPerformancesHolder ignoreTest(
            final ParametrizedTestable<? extends T> test) {
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
            final ParametrizedTestable<? extends T> test) {
        addTestsToPerformanceExecutor();
        this.actualTest = (ParametrizedTestable<T>) test;

        final LoopPerformances loopPerformances =
                getPerformanceExecutor().execute().getLoopPerformances();

        consume(name, loopPerformances);
        addTestLoopPerformances(name, loopPerformances);

        return new LoopPerformancesHolder(name, loopPerformances);
    }

    @Override
    public LoopPerformancesHolder ignoreTest(final String name,
            final ParametrizedTestable<? extends T> test) {
        return LoopPerformancesHolder.empty();
    }

    private class InnerTestable implements Testable {

        private final T t;

        private InnerTestable(final T t) {
            this.t = t;
        }

        @Override
        public void setUp() {
            actualTest.setUp(t);
        }

        @Override
        public void beforeTest(int iterations) {
            actualTest.beforeTest(t, iterations);
        }

        @Override
        public Object test() {
            return actualTest.test(t);
        }
    }
}
