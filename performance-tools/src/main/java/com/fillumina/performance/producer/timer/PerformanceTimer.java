package com.fillumina.performance.producer.timer;

import com.fillumina.performance.producer.LoopPerformances;
import com.fillumina.performance.producer.LoopPerformancesHolder;

/**
 * This is the base class for all the performance tests. It delegates
 * the test execution to a given {@link PerformanceExecutor}.
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class PerformanceTimer
        extends AbstractPerformanceTimer<PerformanceTimer> {
    private static final long serialVersionUID = 1L;

    protected final PerformanceTestExecutor executor;

    /**
     * Executes the tests using the specified executor.
     *
     * @see MultiThreadPerformanceTestExecutor
     * @see SingleThreadPerformanceTestExecutor
     */
    public PerformanceTimer(final PerformanceTestExecutor executor) {
        this.executor = executor;
    }

    /**
     * Execute the performance test.
     * Instead of specifying the number of iterations
     * (with {@link #setIterations(long) }) and than {@link #execute()}
     * you may use the shorter (and recommended) {@link #iterate(int) }.
     * <br />
     * <b>Hint:</b>It may be convenient to run a small amount of iterations
     * before the actual test
     * to warm up the JVM and let it do the necessary optimizations
     * up front ({@link AbstractPerformanceTimer#warmup(int) }).
     *
     * @see AbstractPerformanceTimer#iterate(int)
     * @see AbstractPerformanceTimer#warmup(int)
     */
    @Override
    public LoopPerformancesHolder execute() {
        return execute(true);
    }

    /**
     * Run exactly the same tests as {@link #execute()} without taking
     * any statistics. It's used to warm up the JVM into compiling the code.
     */
    @Override
    public PerformanceTimer warmup() {
        execute(false);
        return this;
    }

    private LoopPerformancesHolder execute(final boolean reportStatistics) {
        final long iterations = getIterations();
        assert iterations > 0;

        initTests();

        final LoopPerformances loopPerformances =
                executor.executeTests(iterations, getTests());

        if (reportStatistics) {
            consume(null, loopPerformances);
            return new LoopPerformancesHolder(loopPerformances);
        }
        return null;
    }
}
