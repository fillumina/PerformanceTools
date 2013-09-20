package com.fillumina.performance.consumer.assertion;

import com.fillumina.performance.consumer.PerformanceConsumer;
import com.fillumina.performance.producer.LoopPerformances;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Asserts specific conditions over the performance it consumes.
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class AssertPerformance
        implements PerformanceConsumer, Serializable, PerformanceAssertion {
    private static final long serialVersionUID = 1L;
    public static final float SAFE_TOLERANCE = 7F;
    public static final float SUPER_SAFE_TOLERANCE = 10F;

    private final List<PerformanceConsumer> tests = new ArrayList<>();
    private float tolerancePercentage;

    public static AssertPerformance withTolerance(final float tolerance) {
        return new AssertPerformance().setTolerancePercentage(tolerance);
    }

    @Override
    public AssertPercentage assertPercentageFor(final String name) {
        return new AssertPercentage(this, name);
    }

    @Override
    public AssertOrder assertTest(final String name) {
        return new AssertOrder(this, name);
    }

    /**
     * This method is basically used by {@link AssertOrder} and
     * {@link AssertPercentage} to register their conditions but may be
     * used by clients to specify customized conditions as well.
     *
     * @param condition A consumer that should implement a condition to check.
     * @return          {@code this} to allow for fluid interfaces.
     */
    public AssertPerformance addCondition(final PerformanceConsumer condition) {
        tests.add(condition);
        return this;
    }

    @Override
    public void check(final LoopPerformances loopPerformances) {
        consume(null, loopPerformances);
    }

    @Override
    public void consume(final String message,
            final LoopPerformances loopPerformances) {
        for (final PerformanceConsumer performanceConsumer: tests) {
            performanceConsumer.consume(message, loopPerformances);
        }
    }

    @Override
    public AssertPerformance setTolerancePercentage(
            final float tolerancePercentage) {
        this.tolerancePercentage = tolerancePercentage;
        return this;
    }

    public float getTolerancePercentage() {
        return tolerancePercentage;
    }
}
