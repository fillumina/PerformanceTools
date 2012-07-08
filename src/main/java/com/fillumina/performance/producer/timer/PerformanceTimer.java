package com.fillumina.performance.producer.timer;

import com.fillumina.performance.producer.BaseFluentPerformanceProducer;
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
        extends BaseFluentPerformanceProducer<PerformanceTimer>
        implements PerformanceProducerInstrumentable {
    private static final long serialVersionUID = 1L;

    private final Map<String, Runnable> tests = new LinkedHashMap<>();
    private final RunningLoopPerformances performances = new RunningLoopPerformances();
    private final PerformanceTestExecutor executor;

    public PerformanceTimer(final PerformanceTestExecutor executor) {
        this.executor = executor;
    }

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

    public PerformanceTimer iterate(final int iterations) {
        assertTimesNotNegative(iterations);
        performances.incrementIterationsBy(iterations);
        initTests();
        executor.executeTests(iterations, tests, performances);
        processConsumer(null, getLoopPerformances());
        return this;
    }

    public void clear() {
        performances.clear();
    }

    @Override
    public <T extends PerformanceProducerInstrumenter<?>> T instrumentedBy(
            final T instrumenter) {
        instrumenter.setPerformanceTimer(this);
        return instrumenter;
    }

    @Override
    public LoopPerformances getLoopPerformances() {
        return performances.getLoopPerformances();
    }

    private void initTests() {
        for (Runnable runnable: tests.values()) {
            if (runnable instanceof InitializingRunnable) {
                ((InitializingRunnable)runnable).init();
            }
        }
    }

    private void assertTimesNotNegative(final long times) {
        if (times < 0) {
            throw new IllegalArgumentException(
                    "Cannot manage negative numbers: " + times);
        }
    }
}
