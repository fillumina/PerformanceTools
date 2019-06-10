package com.fillumina.performance.assertion;

import com.fillumina.performance.util.FluentBuilder;
import com.fillumina.performance.util.stats.Ratio;
import com.fillumina.performance.util.unit.Quantity;
import java.io.Serializable;
import java.util.function.Consumer;

/**
 * Helper to build {@link ExperimentAssertion}s.
 *
 * @param I self
 * @param C caller used for fluent interface
 *
 * @author Francesco Illuminati
 */
public class AssertionBuilder<I extends AssertionBuilder<I,C>, C>
        extends FluentBuilder<C, AssertionBuilder<I,C>>
        implements Serializable {

    private static final long serialVersionUID = 1L;

    private static final Ratio DEFAULT_TOLERANCE = Ratio.percentage(7);

    private final Consumer<ExperimentAssertion> assertionConsumer;
    private Ratio tolerance = DEFAULT_TOLERANCE;

    public AssertionBuilder(Consumer<ExperimentAssertion> assertionConsumer) {
        super();
        this.assertionConsumer = assertionConsumer;
    }

    public AssertionBuilder(C caller,
            Consumer<ExperimentAssertion> assertionConsumer,
            Ratio tolerance) {
        super(caller);
        this.assertionConsumer = assertionConsumer;
        this.tolerance = tolerance;
    }

    public AssertionBuilder(C caller,
            Consumer<ExperimentAssertion> assertionConsumer) {
        super(caller);
        this.assertionConsumer = assertionConsumer;
    }

    public AssertionBuilder(Setter<C, AssertionBuilder<I,C>> setter,
            Consumer<ExperimentAssertion> assertionConsumer) {
        super(setter);
        this.assertionConsumer = assertionConsumer;
    }

    public Ratio getTolerance() {
        return tolerance;
    }

    @Override
    protected AssertionBuilder<I,C> build() {
        return this;
    }

    /**
     * Asserts the ratio of the specified measure compared to the reference
     * (usually the bigger measure).
     * <pre>
 assertion.assertRatioPercentage("some test").lessThan(35);
 </pre>
     */
    public FluentAssertionBuilder<I,Number> assertRatioPercentage(
            final CharSequence name) {
        return assertEvaluator(name, RatioValueInfo::createWithPercentage);
    }

    public FluentAssertionBuilder<I,Number> assertRatioDecimal(
            final CharSequence name) {
        return assertEvaluator(name, RatioValueInfo::createWithDecimal);
    }

    /**
     * Asserts the ratio of the specified measure compared to the reference
     * (usually the bigger measure).
     * <pre>
 assertion.assertRatioPercentage("some test").lessThan(35);
 </pre>
     */
    public FluentAssertionBuilder<I,Ratio> assertRatio(
            final CharSequence name) {
        return assertEvaluator(name, RatioInfo::new);
    }

    /**
     * Asserts the relative order of a measure in respect to the other.
     * <pre>
     * assertion.assertOrder("some test").lessThan("other test");
     * </pre>
     */
    public FluentAssertionBuilder<I, CharSequence> assertOrder(
            final CharSequence name) {
        return assertEvaluator(name, OrderInfo::new);
    }

    /**
     * Asserts the mean value of a measure.
     * <pre>
     * assertion.assertValue("some test").lessThan(12.3);
     * </pre>
     */
    public FluentAssertionBuilder<I,Number> assertValue(
            final CharSequence name) {
        return assertEvaluator(name, ValueInfo::new);
    }

    /**
     * Asserts the mean value of a measure.
     * <pre>
     * assertion.assertValue("some test").lessThan(12.3);
     * </pre>
     */
    public FluentAssertionBuilder<I,Quantity<?>> assertQuantity(
            final CharSequence name) {
        return assertEvaluator(name, QuantityInfo::new);
    }

    public <T> FluentAssertionBuilder<I, T> assertEvaluator(
            final CharSequence name,
            final AssertionErrorInfoCreator<T> creator) {
        return new FluentAssertionBuilder<>(this, name, tolerance, creator);
    }

    /**
     * @param assertion A consumer that should implement a condition to check.
     */
    @SuppressWarnings("unchecked")
    public I accept(ExperimentAssertion assertion) {
        assertionConsumer.accept(assertion);
        return (I) this;
    }

    /**
     * Set the test tolerance. The new tolerance holds for successive
     * assertions only.
     */
    @SuppressWarnings("unchecked")
    public I setTolerance(final Ratio tolerance) {
        this.tolerance = tolerance;
        return (I) this;
    }
}
