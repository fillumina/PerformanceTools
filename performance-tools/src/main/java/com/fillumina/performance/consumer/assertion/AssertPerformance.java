package com.fillumina.performance.consumer.assertion;

import com.fillumina.performance.consumer.PerformanceConsumer;
import com.fillumina.performance.producer.LoopPerformances;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Allow to easily build performance tests.
 *
 * <p>
 * <b>WARNING:</b>
 * Performance tests are subject to many factors that may
 * hinder their accuracy:
 * <ul>
 * <li>System load;
 * <li>CPUs heat level;
 * <li>JDK version and brand;
 * </ul>
 * So if a test fails randomly try to increase the iteration number,
 * relax the tolerance and close demanding background processes.
 *
 * @author fra
 */
public class AssertPerformance implements PerformanceConsumer, Serializable {
    private static final long serialVersionUID = 1L;
    public static final float SAFE_TOLERANCE = 7F;
    public static final float SUPER_SAFE_TOLERANCE = 10F;

    private final List<PerformanceConsumer> tests = new ArrayList<>();
    private float tolerancePercentage;

    public static AssertPerformance withTolerance(final float tolerance) {
        return new AssertPerformance().setTolerancePercentage(tolerance);
    }

    public AssertPercentage assertPercentageFor(final String name) {
        return new AssertPercentage(this, name);
    }

    public AssertOrder assertTest(final String name) {
        return new AssertOrder(this, name);
    }

    public AssertPerformance addCondition(final PerformanceConsumer condition) {
        tests.add(condition);
        return this;
    }

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

    public AssertPerformance setTolerancePercentage(
            final float tolerancePercentage) {
        this.tolerancePercentage = tolerancePercentage;
        return this;
    }

    public float getTolerancePercentage() {
        return tolerancePercentage;
    }
}
