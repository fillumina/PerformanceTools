package com.fillumina.performance.producer.timer;

import com.fillumina.performance.producer.LoopPerformances;
import com.fillumina.performance.producer.LoopPerformancesHolder;

/**
 * This is the base class for all the performance tests. It delegates
 * the test execution to a given {@link PerformanceExecutor} and can be
 * instrumented to execute tests in a specific way (i.e. monitoring
 * results for stability).
 *
 * <p>
 * <b>WARNING:</b>
 * Performance tests are subject to many factors that may
 * hinder their accuracy:
 * <ul>
 * <li>System load;
 * <li>CPUs heat level;
 * <li>JDK version and brand;
 * <li>Garbage collector
 * </ul>
 * So if a test fails randomly try to increase the iteration number,
 * relax the tolerance and close demanding background processes.
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class PerformanceTimer
        extends AbstractPerformanceTimer<PerformanceTimer> {
    private static final long serialVersionUID = 1L;

    private final PerformanceExecutor executor;

    /**
     * Executes the tests using the specified executor.
     *
     * @see MultiThreadPerformanceTestExecutor
     * @see SingleThreadPerformanceTestExecutor
     */
    public PerformanceTimer(final PerformanceExecutor executor) {
        this.executor = executor;
    }

    /**
     * Run exactly the same tests as {@link #execute()} without taking
     * any statistics. It's used to warm up the JVM into compiling the code.
     */
    @Override
    public PerformanceTimer warmup() {
        final LoopPerformances lp = executeTests();
        // this check avoids JVM cutting out dead code
        if (lp.getStatistics().min() < 0) {
            throw new AssertionError("elapsed time cannot be negative");
        }
        return this;
    }

    /**
     * Executes the performance test.
     * Instead of specifying the number of iterations
     * (with {@link #setIterations(long) }) and than {@link #execute()}
     * you may use the shorter (and recommended) {@link #iterate(int) }.
     * <p>
     * <b>Hint:</b>It may be convenient to run a small amount of iterations
     * before the actual test
     * to warm up the JVM and let it do the necessary optimizations
     * up front (see {@link AbstractPerformanceTimer#warmup(int) }).
     *
     * @see AbstractPerformanceTimer#iterate(int)
     * @see AbstractPerformanceTimer#warmup(int)
     */
    @Override
    public LoopPerformancesHolder execute() {
        LoopPerformances loopPerformances = executeTests();
        // makes loopPerformances available to consumers
        consume(null, loopPerformances);
        return new LoopPerformancesHolder(loopPerformances);
    }

    private LoopPerformances executeTests() {
        final long iterations = getIterations();
        assert iterations > 0;
        initTests();
        final LoopPerformances loopPerformances =
                executor.executeTests(iterations, getTests());
        return loopPerformances;
    }
}
