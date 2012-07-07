package com.fillumina.performance.consumer.assertion;

import com.fillumina.performance.consumer.AbstractPerformanceConsumer;

/**
 *
 * @author fra
 */
public abstract class AbstractAssertPerformanceSuite
            <T extends AbstractAssertPerformanceSuite<?>>
        extends AbstractPerformanceConsumer<T> {

    private float percentageTolerance = 5F;

    @SuppressWarnings("unchecked")
    public T setPercentageTolerance(final float percentageTolerance) {
        this.percentageTolerance = percentageTolerance;
        return (T) this;
    }

    protected void processAssertions(final AssertPerformance assertPerformance) {
        assertPerformanceAvailable();

        if (assertPerformance != null) {
            assertPerformance.setPerformances(getLoopPerformances());
            assertPerformance.process();
        }
    }

    private void assertPerformanceAvailable() {
        if (getLoopPerformances() == null) {
            throw new RuntimeException("no performances available");
        }
    }

    protected float getPercentageTolerance() {
        return percentageTolerance;
    }
}
