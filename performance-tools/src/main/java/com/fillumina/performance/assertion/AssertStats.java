package com.fillumina.performance.assertion;

import com.fillumina.performance.infrastructure.PHolder;
import com.fillumina.performance.infrastructure.PerformanceConsumer;
import com.fillumina.performance.util.ReentrantFluidInterfaceImpl;
import com.fillumina.performance.util.StaticPath;
import com.fillumina.performance.util.stats.Ratio;
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
public class AssertStats<C, A extends Assertable>
        extends ReentrantFluidInterfaceImpl<C>
        implements StatsAssertion<C, A>, Serializable {
    private static final long serialVersionUID = 1L;
    private final List<Assertion<A>> conditions;

    private Ratio tolerance = SAFE_TOLERANCE;

    /**
     * @param tolerance expressed as i.e. 10 means 10 %.
     */
    public static <A extends Assertable> StatsAssertion<Void,A>
            withTolerance(final Ratio tolerance) {
        return new AssertStats<Void,A>(new ArrayList<Assertion<A>>())
                .setTolerance(tolerance);
    }

    protected static <C, A extends Assertable> StatsAssertion<C,A>
            withTolerance(final C caller, final Ratio tolerance) {
        return new AssertStats<>(caller, new ArrayList<Assertion<A>>())
                .setTolerance(tolerance);
    }

    public AssertStats(List<Assertion<A>> conditions) {
        this(null, conditions);
    }

    public AssertStats(C caller, List<Assertion<A>> conditions) {
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
    public PercentageConditionBuilder<C,A> assertPercentage(final String name) {
        return new PercentageConditionBuilder<>(this, name);
    }

    /**
     * Asserts the relative order (faster, same, slower) of a test in
     * respect to the others.
     * <pre>
     * assertion.assertOrder("some test").lessThan("other test);
     * </pre>
     */
    @Override
    public OrderConditionBuilder<C,A> assertOrder(final String name) {
        return new OrderConditionBuilder<>(this, name);
    }

    @Override
    public ValueConditionBuilder<C,A> assertValue(final String name) {
        return new ValueConditionBuilder<>(this, name);
    }

    /**
     * This method is basically used by {@link OrderConditionBuilder} and
     * {@link PercentageConditionBuilder} to register their conditions but may be
     * used by clients to specify customized conditions as well.
     *
     * @param condition A consumer that should implement a condition to check.
     * @return          {@code this} to allow for
     *                  <i><a href='http://en.wikipedia.org/wiki/Fluent_interface'>
     *                  fluent interface</a></i>.
     */
    public AssertStats<C,A> addCondition(Assertion<A> condition) {
        conditions.add(condition);
        return this;
    }

    /** Checks the given performances against the registered conditions. */
    @Override
    public void check(final PHolder<A> assertable) {
        consume(assertable);
    }

    /** Checks the given performances against the registered conditions. */
    @Override
    public void consume(final PHolder<A> assertable) {
        for (PerformanceConsumer<A> performanceConsumer: conditions) {
            performanceConsumer.consume(assertable);
        }
    }

    /**
     * Set the test withTolerance. A withTolerance is given as a percentage so that
     * a withTolerance of 5 means that if the required performance is 20 and the
     * measured one is 25 than it's ok, but if the measured one is 26 or 19 than
     * the test fails.
     */
    @Override
    public StatsAssertion<C,A> setTolerance(final Ratio tolerance) {
        this.tolerance = tolerance;
        return this;
    }

    public Ratio getTolerance() {
        return tolerance;
    }

    @Override
    public String toString(PHolder<A> assertable) {
        StaticPath testName = assertable.getName();
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
