package com.fillumina.performance.producer.timer;

/**
 * This is the base class for all the performance tests. It delegates
 * the test execution to a given {@link PerformanceExecutor} using the strategy
 * pattern.
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class PerformanceTimer
        extends AbstractPerformanceTimer {
    private static final long serialVersionUID = 1L;

    protected final PerformanceTestExecutor executor;

    public PerformanceTimer(final PerformanceTestExecutor executor) {
        this.executor = executor;
    }

    /**
     * It may be convenient to run a small amount of iterations
     * before the actual test
     * to warm up the JVM and let it do the necessary optimizations
     * up front.
     */
    @Override
    public LoopPerformancesHolder execute() {
        final int iterations = getIterations();
        assert iterations > 0;
        initTests();
        final LoopPerformances loopPerformances =
                executor.executeTests(iterations, getTests());
        processConsumers(null, loopPerformances);
        return new LoopPerformancesHolder(loopPerformances);
    }
}
