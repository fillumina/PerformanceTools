package com.fillumina.performance.stats.assertion;

import com.fillumina.performance.infrastructure.PerformanceAssertion;
import com.fillumina.performance.infrastructure.PerformanceConsumer;
import com.fillumina.performance.infrastructure.PerformanceFormatter;
import com.fillumina.performance.stats.PerformanceStats;
import com.fillumina.performance.util.ComposedName;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Asserts specific conditions over the performance it consumes.
 *
 * @author Francesco Illuminati
 */
public class AssertPerformance
        implements PerformanceStatsAssertion,
            PerformanceFormatter<PerformanceStats>, Serializable {
    private static final long serialVersionUID = 1L;
    private final List<PerformanceAssertion<PerformanceStats>> conditions;

    private double tolerancePercentage = SAFE_TOLERANCE;

    /** @param tolerance expressed in percentage i.e. 10 means 10 %. */
    public static AssertPerformance withTolerance(final double tolerance) {
        return new AssertPerformance(
                    new ArrayList<PerformanceAssertion<PerformanceStats>>())
                .withPercentageTolerance(tolerance);
    }

    private AssertPerformance(
            List<PerformanceAssertion<PerformanceStats>> conditions) {
        this.conditions = conditions;
    }

    /**
     * Asserts that a test is faster, slower or equals of a given target
     * percentage.
     * <pre>
 assertion.assertPercentage("some test").lessThan(35);
 </pre>
     */
    @Override
    public AssertPercentage assertPercentage(final String name) {
        return new AssertPercentage(this, name);
    }

    /**
     * Asserts the relative order (faster, same, slower) of a test in
     * respect to the others.
     * <pre>
 assertion.assertSpeed("some test").fasterThan("other test);
 </pre>
     */
    @Override
    public AssertOrder assertSpeed(final String name) {
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
    public AssertPerformance addCondition(
            PerformanceAssertion<PerformanceStats> condition) {
        conditions.add(condition);
        return this;
    }

    /** Checks the given performances against the registered conditions. */
    @Override
    public void check(final PerformanceStats stats) {
        consume(null, stats);
    }

    /** Checks the given performances against the registered conditions. */
    @Override
    public void consume(final ComposedName name, final PerformanceStats stats) {
        for (PerformanceConsumer<PerformanceStats> performanceConsumer: conditions) {
            performanceConsumer.consume(name, stats);
        }
    }

    /**
     * Set the test tolerance. A tolerance is given as a percentage so that
     * a tolerance of 5 means that if the required performance is 20 and the
     * measured one is 25 than it's ok, but if the measured one is 26 or 19 than
     * the test fails.
     */
    @Override
    public AssertPerformance withPercentageTolerance(
            final double tolerancePercentage) {
        this.tolerancePercentage = tolerancePercentage;
        return this;
    }

    public double getTolerancePercentage() {
        return tolerancePercentage;
    }

    @Override
    public String toString(PerformanceStats performance) {
        return toString(null, performance);
    }

    @Override
    public String toString(ComposedName testName, PerformanceStats stats) {
        StringBuilder buf = new StringBuilder();
        if (testName != null) {
            buf.append(testName.toString()).append(System.lineSeparator());
        }
        for (PerformanceAssertion<PerformanceStats> performanceConsumer :
                conditions) {
            buf.append(performanceConsumer.toString(null, stats))
                .append(System.lineSeparator());
        }
        return buf.toString();
    }

}
