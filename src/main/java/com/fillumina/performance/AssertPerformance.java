package com.fillumina.performance;

import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Even if using percentages (plus a tolerance) is much safer than
 * rough times performance tests are always tricky to execute and can
 * give different results on different systems.
 *
 * @author fra
 */
public class AssertPerformance {
    private final Map<String, Long> timeMap;
    private final Map<String, Double> percentageMap;
    private double tolerancePercentage = 5D;

    public AssertPerformance(final AbstractPerformanceTimer performanceTimer) {
        this(performanceTimer.getPerformance());
    }

    public AssertPerformance(final Performance performance) {
        this.percentageMap = performance.getPercentageMap();
        this.timeMap = performance.getTimeMap();
    }

    public AssertPerformance setTolerancePercentage(final double tolerancePercentage) {
        this.tolerancePercentage = tolerancePercentage;
        return this;
    }

    public AssertPerformance assertPercentage(final String msg,
            final double percentage) {
        final double actualPercentage = percentageMap.get(msg) * 100;

        if (actualPercentage < percentage - tolerancePercentage ||
                actualPercentage > percentage + tolerancePercentage) {
            throw new AssertionError(msg + " percentage mismatch," +
                " expected: " + formatPercentage(percentage) +
                ", found : " + formatPercentage(actualPercentage));
        }

        return this;
    }

    private String formatPercentage(final double percentage) {
        return String.format("%.2f %%", percentage);
    }

    /**
     * Avoid using this test because times could be very different
     * on different systems. Always prefer percentages.
     */
    public AssertPerformance assertLessThan(final String msg,
            final long time, final TimeUnit unit) {
        final long actual = timeMap.get(msg);
        final long expected = unit.toNanos(time);

        if (actual > expected) {
            throw new AssertionError(msg + " took longer than expected: " +
                " expected: " + formatElapsedTime(expected, unit) +
                ", found : " + formatElapsedTime(actual, unit));
        }

        return this;
    }

    /**
     * Avoid using this test because times could be very different
     * on different systems. Always prefer percentages.
     */
    public AssertPerformance assertEquals(final String msg,
            final long time, final TimeUnit unit) {
        final long actual = timeMap.get(msg);
        final long expected = unit.toNanos(time);
        final long toleranceTime = Math.round(expected * tolerancePercentage);

        if (actual < expected - toleranceTime ||
                actual > expected + toleranceTime) {
            throw new AssertionError(msg + " different than expected: " +
                " expected: " + formatElapsedTime(expected, unit) +
                ", found : " + formatElapsedTime(actual, unit));
        }

        return this;
    }

    private String formatElapsedTime(final long time, final TimeUnit unit) {
        return unit.convert(time, TimeUnit.NANOSECONDS) + " " + unit;
    }

}
