package com.fillumina.performance.consumer.assertion;

import com.fillumina.performance.consumer.PerformanceConsumer;
import com.fillumina.performance.producer.timer.LoopPerformances;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Allow to easily build performance tests.
 *
 * <p>
 * <b>WARNING:</b><br />
 * Performance tests are subject to many factors that may
 * hinder their accuracy, i.e.: system load, CPUs heat level,
 * JDK version and brand etc.
 * So if a test fails randomly try to increase the iterations,
 * relax the tolerance and close background processes.
 *
 * @author fra
 */
public class AssertPerformance implements PerformanceConsumer, Serializable {
    private static final long serialVersionUID = 1L;
    public static final float SAFE_TOLERANCE = 7F;
    public static final float SUPER_SAFE_TOLERANCE = 10F;

    private final List<PerformanceConsumer> tests = new ArrayList<>();
    private final float percentageTolerance;

    public static AssertPerformance withTolerance(final float tolerance) {
        return new AssertPerformance(tolerance);
    }

    public AssertPerformance() {
        this(7F);
    }

    private AssertPerformance(final float percentageTolerance) {
        this.percentageTolerance = percentageTolerance;
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

    public float getTolerancePercentage() {
        return percentageTolerance;
    }
}
