package com.fillumina.performance;

import java.util.concurrent.TimeUnit;

/**
 * Allow to easily build performance tests.
 *
 * <p>
 * <b>WARNING:</b><br />
 * Performance tests are subjected to many factors that may
 * hinder their accuracy, i.e.: system load, CPUs heat level,
 * JDK version and brand etc.
 * So if a test fails randomly try to increase the iterations,
 * relax the tolerances and/or close background processes.
 *
 * @author fra
 */
public class AssertPerformance {
    private final LoopPerformances performances;
    private double tolerancePercentage = 5D;

    public AssertPerformance(final PerformanceTimer performanceTimer) {
        this(performanceTimer.getLoopPerformances());
    }

    public AssertPerformance(final LoopPerformances performances) {
        this.performances = performances;
    }

    public AssertPerformance setTolerancePercentage(final double tolerancePercentage) {
        this.tolerancePercentage = tolerancePercentage;
        return this;
    }

    public AssertPerformance assertPercentage(final String name,
            final double percentage) {
        final double actualPercentage = performances.get(name).getPercentage();

        if (actualPercentage < percentage - tolerancePercentage ||
                actualPercentage > percentage + tolerancePercentage) {
            throw new AssertionError(name + " percentage mismatch," +
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
    public AssertPerformance assertLessThan(final String name,
            final long time, final TimeUnit unit) {
        final long actual = performances.get(name).getElapsedNanoseconds();
        final long expected = unit.toNanos(time);

        if (actual > expected) {
            throw new AssertionError(name + " took longer than expected: " +
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
        final long actual = performances.get(msg).getElapsedNanoseconds();
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
