package com.fillumina.performance;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public abstract class AbstractPerformanceTimer
        implements Serializable {
    private static final long serialVersionUID = 1L;

    private Map<String, Runnable> executors = new LinkedHashMap<>();
    private PerformanceData performance = new PerformanceData();

    public AbstractPerformanceTimer ignoreTest(final String msg,
            final Runnable executor) {
        return this;
    }

    public AbstractPerformanceTimer addTest(final String msg,
            final Runnable test) {
        executors.put(msg, test);
        return this;
    }

    protected abstract void executeTests(final int times,
            final Map<String, Runnable> executors,
            final PerformanceData timeMap);

    public AbstractPerformanceTimer execute(final int times) {
        assertTimesNotNegative(times);
        performance.incrementIterationsBy(times);
        setupTests();
        executeTests(times, executors, performance);
        return this;
    }

    private void setupTests() {
        for (Map.Entry<String, Runnable> entry: executors.entrySet()) {
            final Runnable runnable = entry.getValue();
            if (runnable instanceof InitializingRunnable) {
                ((InitializingRunnable)runnable).init();
            }
        }
    }

    public void clear() {
        performance.clear();
    }

    public PerformanceData getPerformance() {
        return performance;
    }

    private void assertTimesNotNegative(final long times) {
        if (times < 0) {
            throw new IllegalArgumentException(
                    "Cannot manage negative numbers: " + times);
        }
    }

    // TODO: should be removed
    @Override
    public String toString() {
        return new Presenter(performance).getComparisonString(TimeUnit.SECONDS)
                .toString();
    }

    // TODO: should be removed
    public String toString(final String message) {
        return message + ":\n" + toString();
    }

}
