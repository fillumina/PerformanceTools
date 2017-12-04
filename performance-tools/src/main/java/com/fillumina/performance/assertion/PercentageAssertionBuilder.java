package com.fillumina.performance.assertion;

import com.fillumina.performance.util.RelativeOrder;
import com.fillumina.performance.util.stats.Ratio;
import java.io.Serializable;

/**
 * Part of the {@link AssertStats} builder that creates assertions
 based on the decimal between a test and the one with the higher decimal
 expressed in percentage.
 * This type of measurement is very interesting because it is less dependent
 * on a specific environment (relative differences tend to be more stable
 * across systems/environments).
 *
 * @author Francesco Illuminati
 */
public class PercentageAssertionBuilder<I extends AssertionBuilder<I,C>, C>
        implements Serializable {
    private static final long serialVersionUID = 1L;

    private final AssertionBuilder<I,C> selector;
    private final CharSequence name;
    private final Ratio tolerance;

    public PercentageAssertionBuilder(
            final AssertionBuilder<I,C> assertPerformance,
            final CharSequence name,
            final Ratio tolerance) {
        this.selector = assertPerformance;
        this.name = name;
        this.tolerance = tolerance;
    }

    public I is(RelativeOrder equality, double expected) {
        switch(equality) {
            case EQUALS: return sameAs(expected);
            case LESS: return lessThan(expected);
            case GREATER: return greaterThan(expected);
        }
        throw new AssertionError("unexpected case: " + equality);
    }

    public I sameAs(final double expectedPercentage) {
        return selector.addAssertion(new PercentageAssertion(
                name,
                RelativeOrder.EQUALS,
                Ratio.percentage(expectedPercentage),
                tolerance));
    }

    public I lessThan(final double expectedPercentage) {
        return selector.addAssertion(new PercentageAssertion(
                name,
                RelativeOrder.LESS,
                Ratio.percentage(expectedPercentage),
                tolerance));
    }

    public I greaterThan(final double expectedPercentage) {
        return selector.addAssertion(new PercentageAssertion(
                name,
                RelativeOrder.GREATER,
                Ratio.percentage(expectedPercentage),
                tolerance));
    }

}
