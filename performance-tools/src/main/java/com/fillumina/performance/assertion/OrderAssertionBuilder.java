package com.fillumina.performance.assertion;

import com.fillumina.performance.util.Comparison;
import com.fillumina.performance.util.RelativeOrder;
import com.fillumina.performance.util.stats.Ratio;
import java.io.Serializable;

/**
 * Part of the {@link AssertStats} builder that creates assertions
 * based on order (which one is first).
 *
 * @author Francesco Illuminati
 */
@Deprecated
public class OrderAssertionBuilder<I extends AssertionBuilder<I,C>, C>
        implements Serializable {
    private static final long serialVersionUID = 1L;

    private final AssertionBuilder<I,C> assertionBuilder;
    private final CharSequence name;
    private final Ratio tolerance;

    public OrderAssertionBuilder(
            final AssertionBuilder<I,C> assertionBuilder,
            final CharSequence name,
            final Ratio tolerance) {
        this.assertionBuilder = assertionBuilder;
        this.name = name;
        this.tolerance = tolerance;
    }

    public I is(Comparison comparison, CharSequence value) {
        switch (comparison) {
            case EQUALS: return sameAs(value);
            case NOT_EQUALS: return notSameAs(value);
            case GREATER: return greaterThan(value);
            case GREATER_OR_EQUALS: return greaterThanOrEquals(value);
            case LESS: return lessThan(value);
            case LESS_OR_EQUALS: return lessThanOrEquals(value);
        }
        throw new AssertionError("unexpected case: " + comparison);
    }

    public I sameAs(final CharSequence other) {
        final OrderAssertion orderAssertion =
                new OrderAssertion( name, other, RelativeOrder.EQUALS, tolerance);
        return assertionBuilder.accept(orderAssertion);
    }

    public I notSameAs(final CharSequence other) {
        final OrderAssertion orderAssertion =
                new OrderAssertion( name, other, RelativeOrder.EQUALS, tolerance);
        final NegateExperimentAssertion negate =
                new NegateExperimentAssertion(orderAssertion);
        return assertionBuilder.accept(negate);
    }

    public I greaterThan(final CharSequence other) {
        final OrderAssertion orderAssertion =
                new OrderAssertion(name, other, RelativeOrder.GREATER, tolerance);
        return assertionBuilder.accept(orderAssertion);
    }

    public I lessThanOrEquals(final CharSequence other) {
        final OrderAssertion orderAssertion =
                new OrderAssertion(name, other, RelativeOrder.GREATER, tolerance);
        final NegateExperimentAssertion negate =
                new NegateExperimentAssertion(orderAssertion);
        return assertionBuilder.accept(negate);
    }

    public I lessThan(final CharSequence other) {
        final OrderAssertion orderAssertion =
                new OrderAssertion(name, other, RelativeOrder.LESS, tolerance);
        return assertionBuilder.accept(orderAssertion);
    }

    public I greaterThanOrEquals(final CharSequence other) {
        final OrderAssertion orderAssertion =
                new OrderAssertion(name, other, RelativeOrder.LESS, tolerance);
        final NegateExperimentAssertion negate =
                new NegateExperimentAssertion(orderAssertion);
        return assertionBuilder.accept(negate);
    }
}
