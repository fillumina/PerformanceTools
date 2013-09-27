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

    private final List<PerformanceConsumer> tests = new ArrayList<>();
    private float tolerancePercentage = SAFE_TOLERANCE;

    public static AssertPerformance withTolerance(final float tolerance) {
        return new AssertPerformance().setPercentageTolerance(tolerance);
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
     * @return          {@code this} to allow for
     *                  <i><a href='http://en.wikipedia.org/wiki/Fluent_interface'>
     *                  fluent interface</a></i>.
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
    public AssertPerformance setPercentageTolerance(
            final float tolerancePercentage) {
        this.tolerancePercentage = tolerancePercentage;
        return this;
    }

    public float getTolerancePercentage() {
        return tolerancePercentage;
    }
}
