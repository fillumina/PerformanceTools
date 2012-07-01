package com.fillumina.performance;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class PerformanceTimer implements Serializable {
    private static final long serialVersionUID = 1L;

    private final Map<String, Runnable> tests = new LinkedHashMap<>();
    private final RunningPerformances performance = new RunningPerformances();
    private final PerformanceTestExecutor executor;

    public PerformanceTimer(final PerformanceTestExecutor executor) {
        this.executor = executor;
    }

    public PerformanceTimer ignoreTest(final String msg,
            final Runnable executor) {
        return this;
    }

    public PerformanceTimer addTest(final String msg,
            final Runnable test) {
        tests.put(msg, test);
        return this;
    }

    public PerformanceTimer iterate(final int times) {
        assertTimesNotNegative(times);
        performance.incrementIterationsBy(times);
        setupTests();
        executor.executeTests(times, tests, performance);
        return this;
    }

    public <P extends PerformanceConsumer> P apply(final P presenter) {
        assertIterationsHaveBeenMade();
        presenter.setPerformances(getLoopPerformances());
        return presenter;
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

    public String toString(final String message) {
        return message + ":\n" + toString();
    }

    @Override
    public String toString() {
        return apply(new StringTablePresenter()).getTable().toString();
    }

    private void assertIterationsHaveBeenMade() {
        if (performance.getIterations() == 0) {
            throw new RuntimeException(getClass().getCanonicalName() + ": " +
                    "Before using the results with apply() " +
                    "you should have iterate() first.");
        }
    }

}
