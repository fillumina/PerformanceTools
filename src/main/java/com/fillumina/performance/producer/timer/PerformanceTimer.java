package com.fillumina.performance.producer.timer;

import com.fillumina.performance.producer.AbstractPerformanceProducer;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * This is the base class for all the performance tests. It delegates
 * the test execution to a given {@link PerformanceExecutor} using the strategy
 * pattern.
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class PerformanceTimer
        extends AbstractPerformanceProducer<PerformanceTimer> {
    private static final long serialVersionUID = 1L;

    private final Map<String, Runnable> tests = new LinkedHashMap<>();
    private final PerformanceTestExecutor executor;

    public PerformanceTimer(final PerformanceTestExecutor executor) {
        this.executor = executor;
    }

    /**
     * Allows to ignore a test cleanly without having to comment it out.
     */
    public PerformanceTimer ignoreTest(final String name,
            final Runnable executor) {
        return this;
    }

    /**
     * If you need to perform some initialization use
     * {@link InitializingRunnable}, if you need a thread local object
     * use {@link ThreadLocalRunnable}.
     *
     * @see InitializingRunnable
     * @see ThreadLocalRunnable
     */
    public PerformanceTimer addTest(final String name,
            final Runnable test) {
        tests.put(name, test);
        return this;
    }

    public <T extends RequiringPerformanceTimer> T instrumentedBy(
            final T instrumenter) {
        instrumenter.setPerformanceTimer(this);
        return instrumenter;
    }

    /**
     * It may be convenient to run a small amount of iterations
     * before the actual test
     * to warm up the JVM and let it do the necessary optimizations
     * up front.
     */
    public LoopPerformancesHolder iterate(final int iterations) {
        assert iterations > 0;
        initTests();
        final LoopPerformances loopPerformances =
                executor.executeTests(iterations, tests);
        processConsumers(null, loopPerformances);
        return new LoopPerformancesHolder(loopPerformances);
    }

    private void initTests() {
        for (Runnable runnable: tests.values()) {
            if (runnable instanceof InitializableRunnable) {
                ((InitializableRunnable)runnable).init();
            }
        }
    }
}
