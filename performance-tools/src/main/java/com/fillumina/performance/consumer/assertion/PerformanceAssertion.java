package com.fillumina.performance.consumer.assertion;

/**
 *
 * @author fra
 */
public interface PerformanceAssertion {
    AssertPercentage assertPercentageFor(final String name);

    AssertOrder assertTest(final String name);

    PerformanceAssertion setTolerancePercentage(final float tolerancePercentage);
}
