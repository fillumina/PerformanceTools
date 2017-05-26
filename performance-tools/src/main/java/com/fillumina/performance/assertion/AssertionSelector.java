package com.fillumina.performance.assertion;

import com.fillumina.performance.infrastructure.TN;
import com.fillumina.performance.util.CallBackBuilder;
import com.fillumina.performance.util.TName;
import com.fillumina.performance.util.stats.Ratio;
import java.io.Serializable;

/**
 * Asserts conditions over the performance it consumes.
 *
 * @param C caller used for fluent interface
 * @param A {@link Assertable} returned
 *
 * @author Francesco Illuminati
 */
public class AssertionSelector
            <I extends AssertionSelector<I,C,A>, C, A extends Assertable>
        extends CallBackBuilder<C, AssertionSelector<I,C,A>>
        implements Serializable {

    private static final long serialVersionUID = 1L;

    public static final Ratio DEFAULT_TOLERANCE = Ratio.percentage(5);
    public static final Ratio SAFE_TOLERANCE = Ratio.percentage(7);
    public static final Ratio SUPER_SAFE_TOLERANCE = Ratio.percentage(10);

    private final AssertionContainer<A> assertionContainer;
    private Ratio tolerance = SAFE_TOLERANCE;

    public AssertionSelector(AssertionContainer<A> assertionContainer) {
        this.assertionContainer = assertionContainer;
    }

    public AssertionSelector(C caller,
            AssertionContainer<A> assertionContainer,
            Ratio tolerance) {
        super(caller);
        this.assertionContainer = assertionContainer;
        this.tolerance = tolerance;
    }

    public AssertionSelector(C caller, AssertionContainer<A> assertionContainer) {
        super(caller);
        this.assertionContainer = assertionContainer;
    }

    public AssertionSelector(Setter<C, AssertionSelector<I,C,A>> setter,
            AssertionContainer<A> assertionContainer) {
        super(setter);
        this.assertionContainer = assertionContainer;
    }

    protected AssertionContainer<A> getAssertionContainer() {
        return assertionContainer;
    }

    protected Ratio getTolerance() {
        return tolerance;
    }

    @Override
    public AssertionSelector<I,C,A> build() {
        return this;
    }


    /**
     * Asserts that a test is faster, slower or equals of a given target
     * percentage.
     * <pre>
     * assertion.assertPercentage("some test").lessThan(35);
     * </pre>
     */
    public PercentageConditionBuilder<I,C,A> assertPercentage(final TName name) {
        return new PercentageConditionBuilder<>(this, name, tolerance);
    }

    public PercentageConditionBuilder<I,C,A> assertPercentage(
            final String... name) {
        return new PercentageConditionBuilder<>(this, TN.tname(name), tolerance);
    }

    /**
     * Asserts the relative order (faster, same, slower) of a test in
     * respect to the others.
     * <pre>
     * assertion.assertOrder("some test").lessThan("other test);
     * </pre>
     */
    public OrderConditionBuilder<I,C,A> assertOrder(final TName name) {
        return new OrderConditionBuilder<>(this, name, tolerance);
    }

    public OrderConditionBuilder<I,C,A> assertOrder(final String... name) {
        return new OrderConditionBuilder<>(this, TN.tname(name), tolerance);
    }

    /**
     * Asserts the value of a specific test measurement.
     * <pre>
     * assertion.assertValue("some test").lessThan(12.3);
     * </pre>
     */
    public ValueConditionBuilder<I,C,A> assertValue(final TName name) {
        return new ValueConditionBuilder<>(this, name, tolerance);
    }

    public ValueConditionBuilder<I,C,A> assertValue(final String... name) {
        return new ValueConditionBuilder<>(this, TN.tname(name), tolerance);
    }

    /**
     *
     * @param condition A consumer that should implement a condition to check.
     * @return          {@code this} to allow for
     *                  <i><a href='http://en.wikipedia.org/wiki/Fluent_interface'>
     *                  fluent interface</a></i>.
     */
    @SuppressWarnings("unchecked")
    public I addAssertion(Assertion<A> condition) {
        assertionContainer.addAssertion(condition);
        return (I) this;
    }

    /** Set the test tolerance. */
    @SuppressWarnings("unchecked")
    public I tolerance(final Ratio tolerance) {
        this.tolerance = tolerance;
        return (I) this;
    }
}
