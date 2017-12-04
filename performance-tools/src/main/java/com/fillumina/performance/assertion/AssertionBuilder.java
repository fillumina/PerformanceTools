package com.fillumina.performance.assertion;

import com.fillumina.performance.util.CallBackBuilder;
import com.fillumina.performance.util.stats.Ratio;
import java.io.Serializable;
import java.util.function.Consumer;

/**
 * Helper to build {@link Assertion}s.
 *
 * @param I self
 * @param C caller used for fluent interface
 *
 * @author Francesco Illuminati
 */
public class AssertionBuilder<I extends AssertionBuilder<I,C>, C>
        extends CallBackBuilder<C, AssertionBuilder<I,C>>
        implements Serializable {

    private static final long serialVersionUID = 1L;

    public static final Ratio DEFAULT_TOLERANCE = Ratio.percentage(5);
    public static final Ratio SAFE_TOLERANCE = Ratio.percentage(7);
    public static final Ratio SUPER_SAFE_TOLERANCE = Ratio.percentage(10);

    private final Consumer<Assertion> assertionConsumer;
    private Ratio tolerance = SAFE_TOLERANCE;

    public AssertionBuilder(Consumer<Assertion> assertionConsumer) {
        super();
        this.assertionConsumer = assertionConsumer;
    }

    public AssertionBuilder(C caller,
            Consumer<Assertion> assertionConsumer,
            Ratio tolerance) {
        super(caller);
        this.assertionConsumer = assertionConsumer;
        this.tolerance = tolerance;
    }

    public AssertionBuilder(C caller, Consumer<Assertion> assertionConsumer) {
        super(caller);
        this.assertionConsumer = assertionConsumer;
    }

    public AssertionBuilder(Setter<C, AssertionBuilder<I,C>> setter,
            Consumer<Assertion> assertionConsumer) {
        super(setter);
        this.assertionConsumer = assertionConsumer;
    }

    protected Ratio getTolerance() {
        return tolerance;
    }

    @Override
    public AssertionBuilder<I,C> build() {
        return this;
    }

    /**
     * Asserts the ratio of the specified measure compared to the reference
     * (usually the bigger measure).
     * <pre>
     * assertion.assertPercentage("some test").lessThan(35);
     * </pre>
     */
    public PercentageAssertionBuilder<I,C> assertPercentage(
            final CharSequence name) {
        return new PercentageAssertionBuilder<>(this, name, tolerance);
    }

    /**
     * Asserts the relative order of a measure in respect to the other.
     * <pre>
     * assertion.assertOrder("some test").lessThan("other test");
     * </pre>
     */
    public OrderAssertionBuilder<I,C> assertOrder(final CharSequence name) {
        return new OrderAssertionBuilder<>(this, name, tolerance);
    }

    /**
     * Asserts the mean value of a measure.
     * <pre>
     * assertion.assertValue("some test").lessThan(12.3);
     * </pre>
     */
    public ValueAssertionBuilder<I,C> assertValue(final CharSequence name) {
        return new ValueAssertionBuilder<>(this, name, tolerance);
    }

    /**
     * @param assertion A consumer that should implement a condition to check.
     */
    @SuppressWarnings("unchecked")
    public I addAssertion(Assertion assertion) {
        assertionConsumer.accept(assertion);
        return (I) this;
    }

    /** Set the test tolerance. */
    @SuppressWarnings("unchecked")
    public I setTolerance(final Ratio tolerance) {
        this.tolerance = tolerance;
        return (I) this;
    }
}
