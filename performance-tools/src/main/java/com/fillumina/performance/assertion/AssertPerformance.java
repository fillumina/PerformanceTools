package com.fillumina.performance.assertion;

import com.fillumina.performance.infrastructure.PHolder;
import com.fillumina.performance.infrastructure.PerformanceConsumer;
import com.fillumina.performance.util.ComposedName;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Asserts conditions over the performance it consumes.
 *
 * @param C caller used for fluent interface
 * @param A {@link Assertable} returned
 *
 * @author Francesco Illuminati
 */
public class AssertPerformance<C, A extends Assertable>
        extends AbstractAssertionCondition<C, A>
        implements StatsAssertion<C, A>, Serializable {
    private static final long serialVersionUID = 1L;
    private final List<Assertion<A>> conditions;

    private double tolerancePercentage = SAFE_TOLERANCE;

    /** @param tolerance expressed as i.e. 10 means 10 %. */
    public static <A extends Assertable> StatsAssertion<Void,A>
            withPercentageTolerance(final double tolerance) {
        return new AssertPerformance<Void,A>(new ArrayList<Assertion<A>>())
                .withTolerance(tolerance);
    }

    protected static <C, A extends Assertable> StatsAssertion<C,A>
            withPercentageTolerance(final C caller, final double tolerance) {
        return new AssertPerformance<>(caller, new ArrayList<Assertion<A>>())
                .withTolerance(tolerance);
    }

    public AssertPerformance(List<Assertion<A>> conditions) {
        this(null, conditions);
    }

    public AssertPerformance(C caller, List<Assertion<A>> conditions) {
        super(caller);
        this.conditions = conditions;
    }

    /**
     * Asserts that a test is faster, slower or equals of a given target
     * percentage.
     * <pre>
     * assertion.assertPercentage("some test").lessThan(35);
     * </pre>
     */
    @Override
    public AssertPercentage<C,A> assertPercentage(final String name) {
        return new AssertPercentage<>(this, name);
    }

    /**
     * Asserts the relative order (faster, same, slower) of a test in
     * respect to the others.
     * <pre>
     * assertion.assertOrder("some test").lessThan("other test);
     * </pre>
     */
    @Override
    public AssertOrder<C,A> assertOrder(final String name) {
        return new AssertOrder<>(this, name);
    }

    @Override
    public AssertValue<C,A> assertValue(final String name) {
        return new AssertValue<>(this, name);
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
    public AssertPerformance<C,A> addCondition(Assertion<A> condition) {
        conditions.add(condition);
        return this;
    }

    /** Checks the given performances against the registered conditions. */
    @Override
    public void check(final PHolder<A> assertableMultiTest) {
        consume(assertableMultiTest);
    }

    /** Checks the given performances against the registered conditions. */
    @Override
    public void consume(final PHolder<A> assertable) {
        for (PerformanceConsumer<A> performanceConsumer: conditions) {
            performanceConsumer.consume(assertable);
        }
    }

    /**
     * Set the test tolerance. A tolerance is given as a percentage so that
     * a tolerance of 5 means that if the required performance is 20 and the
     * measured one is 25 than it's ok, but if the measured one is 26 or 19 than
     * the test fails.
     */
    @Override
    public StatsAssertion<C,A> withTolerance(
            final double tolerancePercentage) {
        this.tolerancePercentage = tolerancePercentage;
        return this;
    }

    public double getTolerancePercentage() {
        return tolerancePercentage;
    }

    @Override
    public String toString(PHolder<A> assertable) {
        ComposedName testName = assertable.getName();
        StringBuilder buf = new StringBuilder();
        for (Assertion<A> performanceConsumer : conditions) {
            buf.append(performanceConsumer.toString(assertable))
                .append(System.lineSeparator());
        }
        if (testName != null && buf.length() != 0) {
            return testName.toString() + System.lineSeparator() + buf.toString();
        }
        return buf.toString();
    }

}
