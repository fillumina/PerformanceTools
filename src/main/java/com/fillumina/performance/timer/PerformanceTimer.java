package com.fillumina.performance.timer;

import com.fillumina.performance.AbstractPerformanceProducer;
import com.fillumina.performance.InitializingRunnable;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class PerformanceTimer
        extends AbstractPerformanceProducer<PerformanceTimer> {
    private static final long serialVersionUID = 1L;

    private final Map<String, Runnable> tests = new LinkedHashMap<>();
    private final RunningPerformances performance = new RunningPerformances();
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

    public PerformanceTimer iterate(final int times) {
        assertTimesNotNegative(times);
        performance.incrementIterationsBy(times);
        setupTests();
        executor.executeTests(times, tests, performance);
        processConsumer(null, getLoopPerformances());
        return this;
    }

    private void setupTests() {
        for (Map.Entry<String, Runnable> entry: tests.entrySet()) {
            final Runnable runnable = entry.getValue();
            if (runnable instanceof InitializingRunnable) {
                ((InitializingRunnable)runnable).init();
            }
        }
    }

    public void clear() {
        performance.clear();
    }

    public LoopPerformances getLoopPerformances() {
        return performance.getLoopPerformances();
    }

    private void assertTimesNotNegative(final long times) {
        if (times < 0) {
            throw new IllegalArgumentException(
                    "Cannot manage negative numbers: " + times);
        }
    }

}
