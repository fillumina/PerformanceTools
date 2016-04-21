package com.fillumina.performance.sample;

import com.fillumina.performance.sample.executor.PerformanceExecutor;

/**
 * This is the base class for all performance tests. It delegates
 * the test execution to a given {@link PerformanceExecutor} and can be
 * instrumented to execute tests in a specific way (i.e. repeat the test
 * until a target result stability is reached).
 *
 * <p>
 * <b>WARNING:</b>
 * Performance tests are subject to many factors that may
 * hinder accuracy:
 * <ul>
 * <li>System load;
 * <li>CPUs heat level;
 * <li>JDK version and brand;
 * <li>JVM Garbage Collector
 * </ul>
 * The only way to marginalize these factors is to run the test long enough
 * so that those disturbances fade away.
 * A performance test might fail randomly: try to increase the iteration number,
 * relax the tolerance and close demanding background processes.
 *
 * @author Francesco Illuminati
 */
public class DefaultPerformanceTimer
        extends AbstractPerformanceTimer<DefaultPerformanceTimer, Testable> {
    private final PerformanceExecutor executor;
    private boolean testInitialized;

    /**
     * Executes the tests using the specified executor.
     */
    public DefaultPerformanceTimer(final PerformanceExecutor executor) {
        this.executor = executor;
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
     * up front (see {@link DefaultPerformanceTimer#warmup(int) }). At any
     * rate if you use
     * {@link com.fillumina.performance.producer.progression.AutoProgressionPerformanceInstrumenter}
     * the value will be found automatically.
     *
     * @see DefaultPerformanceTimer#iterate(int)
     * @see DefaultPerformanceTimer#warmup(int)
     */
    @Override
    public PerformanceSample execute(int iterations) {
        PerformanceSample performanceSample = performTests(iterations);
        dispatchToConsumers(performanceSample);
        return performanceSample;
    }

    /**
     * Run exactly the same tests as {@link #execute()} without taking
     * any statistics. It's used to warm up the JVM into optimizing the code.
     */
    @Override
    public DefaultPerformanceTimer warmup(int iterations) {
        performTests(iterations);
        return this;
    }

    private PerformanceSample performTests(int iterations) throws
            IllegalStateException {
        if (iterations <= 0) {
            throw new IllegalStateException(
                    "invalid iteration number = " + iterations);
        }
        initTests();
        final PerformanceSample performanceSample =
                executor.executeTests(getTests(), iterations);
        if (performanceSample == null) {
            throw new AssertionError("no test performed");
        }
        return performanceSample;
    }

    protected void initTests() {
        if (!testInitialized) {
            for (Testable testable: getTests().values()) {
                testable.setUp();
            }
            testInitialized = true;
        }
    }
}
