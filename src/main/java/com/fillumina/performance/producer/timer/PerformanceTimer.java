package com.fillumina.performance.producer.timer;

import com.fillumina.performance.producer.AbstractChainablePerformanceProducer;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class PerformanceTimer
        extends AbstractChainablePerformanceProducer<PerformanceTimer> {
    private static final long serialVersionUID = 1L;

    private final Map<String, Runnable> tests = new LinkedHashMap<>();
    private final RunningPerformances performances = new RunningPerformances();
    private final PerformanceTestExecutor executor;

    public PerformanceTimer(final PerformanceTestExecutor executor) {
        this.executor = executor;
    }

    public PerformanceTimer ignoreTest(final String name,
            final Runnable executor) {
        return this;
    }

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
