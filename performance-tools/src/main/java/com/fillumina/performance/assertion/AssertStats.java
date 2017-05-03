package com.fillumina.performance.assertion;

import com.fillumina.performance.infrastructure.PerformanceConsumer;
import com.fillumina.performance.util.ReentrantFluidInterfaceImpl;
import com.fillumina.performance.util.TName;
import com.fillumina.performance.util.stats.Ratio;
import java.io.IOException;
import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

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

    private final List<Assertion<A>> conditions = new CopyOnWriteArrayList<>();
    private Ratio tolerance = SAFE_TOLERANCE;

    public static <A extends Assertable> StatsAssertion<Void,A>
            withTolerance(final Ratio tolerance) {
        return new AssertStats<Void,A>().setTolerance(tolerance);
    }

    protected static <C, A extends Assertable> StatsAssertion<C,A>
            withTolerance(final C caller, final Ratio tolerance) {
        return new AssertStats<C,A>(caller).setTolerance(tolerance);
    }

    public AssertStats() {
        super(null);
    }

    public AssertStats(C caller) {
        super(caller);
    }

    public AssertStats(Collection<? extends Assertion<A>> conditions) {
        this(null, conditions);
    }

    public AssertStats(C caller, Collection<? extends Assertion<A>> conditions) {
        super(caller);
        this.conditions.addAll(conditions);
    }

    /**
     * Asserts that a test is faster, slower or equals of a given target
     * percentage.
     * <pre>
     * assertion.assertPercentage("some test").lessThan(35);
     * </pre>
     */
    @Override
    public PercentageConditionBuilder<C,A> assertPercentage(final TName name) {
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
    public OrderConditionBuilder<C,A> assertOrder(final TName name) {
        return new OrderConditionBuilder<>(this, name);
    }

    /**
     * Asserts the value of a specific test measurement.
     * <pre>
     * assertion.assertValue("some test").lessThan(12.3);
     * </pre>
     */
    @Override
    public ValueConditionBuilder<C,A> assertValue(final TName name) {
        return new ValueConditionBuilder<>(this, name);
    }

    /**
     *
     * @param condition A consumer that should implement a condition to check.
     * @return          {@code this} to allow for
     *                  <i><a href='http://en.wikipedia.org/wiki/Fluent_interface'>
     *                  fluent interface</a></i>.
     */
    @Override
    public AssertStats<C,A> addAssertion(Assertion<A> condition) {
        conditions.add(condition);
        return this;
    }

    /** Checks the given performances against the registered conditions. */
    @Override
    public void check(TName tname, A assertable) {
        consume(tname, assertable);
    }

    /** Checks the given performances against the registered conditions. */
    @Override
    public void consume(TName tname, A assertable) {
        for (PerformanceConsumer<A> performanceConsumer: conditions) {
            performanceConsumer.consume(tname, assertable);
        }
    }

    /** Set the test tolerance. */
    @Override
    public StatsAssertion<C,A> setTolerance(final Ratio tolerance) {
        this.tolerance = tolerance;
        return this;
    }

    public Ratio getTolerance() {
        return tolerance;
    }

    @Override
    public void toString(Appendable appendable, A assertable)
            throws IOException {
        for (Assertion<A> performanceConsumer : conditions) {
            performanceConsumer.toString(appendable, assertable);
        }
    }

}
