package com.fillumina.performance.stats.assertion;

import com.fillumina.performance.infrastructure.PerformanceAssertion;
import com.fillumina.performance.stats.PerformanceStats;

/**
 *
 * @author Francesco Illuminati
 */
public interface PerformanceStatsAssertion
        extends PerformanceAssertion<PerformanceStats> {
    
    double DEFAULT_TOLERANCE = 5F;
    double SAFE_TOLERANCE = 7F;
    double SUPER_SAFE_TOLERANCE = 10F;

    /** Asserts against the percentage of the given test. */
    AssertPercentage assertPercentage(final String name);

    /** Asserts against the relative order of the given test. */
    AssertOrder assertSpeed(final String name);

    /**
     * Set the accepted tolerance percentage. i.e. 5 means 5%.
     * Choose values between 5 to 10 for normal tests and 1 or 2 if you
     * need a very precise measurement. Don't forget to set an appropriate
     * timeout.
     */
    PerformanceStatsAssertion withPercentageTolerance(final double percentage);

    /**
     * It checks the given performance against its assertions.
     * It delegates to
     * {@link PerformanceSampleConsumer#consume(java.lang.String, com.fillumina.performance.producer.LoopPerformances) }.
     */
    void check(final PerformanceStats stats);
}
