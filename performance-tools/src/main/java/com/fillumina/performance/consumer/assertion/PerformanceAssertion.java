package com.fillumina.performance.consumer.assertion;

import com.fillumina.performance.consumer.PerformanceConsumer;
import com.fillumina.performance.producer.LoopPerformances;

/**
 *
 * @author fra
 */
public interface PerformanceAssertion extends PerformanceConsumer {
    float SAFE_TOLERANCE = 7F;
    float SUPER_SAFE_TOLERANCE = 10F;

    /** Asserts against the percentage of the given test. */
    AssertPercentage assertPercentageFor(final String name);

    /** Asserts against the relative order of the given test. */
    AssertOrder assertTest(final String name);

    /** Set the accepted tolerance percentage. i.e. 5 means 5% */
    PerformanceAssertion setPercentageTolerance(final float percentage);

    /**
     * It checks the given performance against its assertions.
     * It delegates to
     * {@link PerformanceConsumer#consume(java.lang.String, com.fillumina.performance.producer.LoopPerformances) }.
     */
    void check(final LoopPerformances loopPerformances);
}
