package com.fillumina.performance.assertion;

import com.fillumina.performance.infrastructure.PerformanceConsumer;
import com.fillumina.performance.util.ComposedName;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Asserts specific conditions over the performance it consumes.
 *
 * @author Francesco Illuminati
 */
public class AssertPerformance<A extends AssertableMultiTest>
        implements StatsAssertion<A>, Serializable {
    private static final long serialVersionUID = 1L;
    private final List<Assertion<A>> conditions;

    private double tolerancePercentage = SAFE_TOLERANCE;

    /** @param tolerance expressed in percentage i.e. 10 means 10 %. */
    public static <A extends AssertableMultiTest> StatsAssertion<A> withTolerance(
            final double tolerance) {
        return new AssertPerformance<>(new ArrayList<Assertion<A>>())
                .withPercentageTolerance(tolerance);
    }

    public AssertPerformance(List<Assertion<A>> conditions) {
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
    public AssertPercentage<A> assertPercentage(final String name) {
        return new AssertPercentage<>(this, name);
    }

    /**
     * Asserts the relative order (faster, same, slower) of a test in
     * respect to the others.
     * <pre>
 assertion.assertOrder("some test").fasterThan("other test);
 </pre>
     */
    @Override
    public AssertOrder<A> assertOrder(final String name) {
        return new AssertOrder<>(this, name);
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
    public AssertPerformance<A> addCondition(Assertion<A> condition) {
        conditions.add(condition);
        return this;
    }

    /** Checks the given performances against the registered conditions. */
    @Override
    public void check(final A assertableMultiTest) {
        consume(null, assertableMultiTest);
    }

    /** Checks the given performances against the registered conditions. */
    @Override
    public void consume(final ComposedName name, final A assertable) {
        for (PerformanceConsumer<A> performanceConsumer: conditions) {
            performanceConsumer.consume(name, assertable);
        }
    }

    /**
     * Set the test tolerance. A tolerance is given as a percentage so that
     * a tolerance of 5 means that if the required performance is 20 and the
     * measured one is 25 than it's ok, but if the measured one is 26 or 19 than
     * the test fails.
     */
    @Override
    public StatsAssertion<A> withPercentageTolerance(
            final double tolerancePercentage) {
        this.tolerancePercentage = tolerancePercentage;
        return this;
    }

    public double getTolerancePercentage() {
        return tolerancePercentage;
    }

    @Override
    public String toString(A assertableMultiTest) {
        return toString(null, assertableMultiTest);
    }

    @Override
    public String toString(ComposedName testName, A assertable) {
        StringBuilder buf = new StringBuilder();
        for (Assertion<A> performanceConsumer : conditions) {
            buf.append(performanceConsumer.toString(null, assertable))
                .append(System.lineSeparator());
        }
        if (testName != null && buf.length() != 0) {
            return testName.toString() + System.lineSeparator() + buf.toString();
        }
        return buf.toString();
    }

}
