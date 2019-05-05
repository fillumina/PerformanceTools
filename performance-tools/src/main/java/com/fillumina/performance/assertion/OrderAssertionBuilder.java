package com.fillumina.performance.assertion;

import com.fillumina.performance.util.RelativeOrder;
import com.fillumina.performance.util.stats.Ratio;
import java.io.Serializable;

/**
 * Part of the {@link AssertStats} builder that creates assertions
 * based on order (which one is first).
 *
 * @author Francesco Illuminati
 */
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

    public I is(RelativeOrder equality, CharSequence other) {
        switch(equality) {
            case EQUALS: return sameAs(other);
            case LESS: return lessThan(other);
            case GREATER: return greaterThan(other);
        }
        throw new AssertionError("unexpected case: " + equality);
    }

    public I sameAs(final CharSequence other) {
        final OrderAssertion orderAssertion =
                new OrderAssertion( name, other, RelativeOrder.EQUALS, tolerance);
        return assertionBuilder.accept(orderAssertion);
    }

    public I greaterThan(final CharSequence other) {
        final OrderAssertion orderAssertion =
                new OrderAssertion(name, other, RelativeOrder.GREATER, tolerance);
        return assertionBuilder.accept(orderAssertion);
    }

    public I lessThan(final CharSequence other) {
        final OrderAssertion orderAssertion =
                new OrderAssertion(name, other, RelativeOrder.LESS, tolerance);
        return assertionBuilder.accept(orderAssertion);
    }
}
