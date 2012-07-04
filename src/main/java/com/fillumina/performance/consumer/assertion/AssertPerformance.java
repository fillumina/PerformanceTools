package com.fillumina.performance.consumer.assertion;

import com.fillumina.performance.producer.timer.LoopPerformances;
import com.fillumina.performance.consumer.PerformanceConsumer;
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

    private List<Testable> tests = new ArrayList<>();
    private LoopPerformances loopPerformances;
    private float tolerancePercentage = 5F;

    public AssertPerformance setTolerancePercentage(final float tolerancePercentage) {
        this.tolerancePercentage = tolerancePercentage;
        return this;
    }

    public AssertPercentage assertPercentageFor(final String name) {
        final AssertPercentage assertPercentage = new AssertPercentage(this, name);
        tests.add(assertPercentage);
        return assertPercentage;
    }

    public AssertOrder assertTest(final String name) {
        final AssertOrder assertOrder = new AssertOrder(this, name);
        tests.add(assertOrder);
        return assertOrder;
    }

    @Override
    public AssertPerformance setMessage(final String message) {
        // ignored
        return this;
    }

    @Override
    public AssertPerformance setPerformances(final LoopPerformances performances) {
        this.loopPerformances = performances;
        return this;
    }

    @Override
    public void process() {
        for (Testable test: tests) {
            test.check();
        }
    }

    public boolean isPerformancesAvailable() {
        return loopPerformances != null;
    }

    public float getPercentageFor(final String name) {
        return loopPerformances.get(name).getPercentage();
    }

    public float getTolerancePercentage() {
        return tolerancePercentage;
    }

}
