package com.fillumina.performance.assertion;

import com.fillumina.performance.util.RelativeOrder;
import com.fillumina.performance.util.stats.Ratio;
import java.io.Serializable;

/**
 * Part of the {@link AssertStats} builder that creates assertions
 * based on the decimal between a test and the one with the higher decimal
 * expressed in percentage.
 * This type of measurement is very interesting because it is less dependent
 * on a specific environment (relative differences tend to be more stable
 * across systems/environments).
 *
 * @author Francesco Illuminati
 */
@Deprecated
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

    public I is(boolean negate, RelativeOrder equality, double expected) {
        return is(negate, equality, Ratio.percentage(expected));
    }

    public I is(boolean negate, RelativeOrder equality, Ratio expected) {
        if (negate) {
            switch(equality) {
                case EQUALS: return notSameAs(expected);
                case LESS: return greaterThanOrEquals(expected);
                case GREATER: return lessThanOrEquals(expected);
            }
        } else {
            switch(equality) {
                case EQUALS: return sameAs(expected);
                case LESS: return lessThan(expected);
                case GREATER: return greaterThan(expected);
            }
        }
        throw new AssertionError("unexpected case: " + equality);
    }

    public I sameAs(final double expectedPercentage) {
        return sameAs(Ratio.percentage(expectedPercentage));
    }

    public I sameAs(final Ratio expectedPercentage) {
        final PercentageAssertion percentageAssertion =
                new PercentageAssertion(
                        name,
                        RelativeOrder.EQUALS,
                        expectedPercentage,
                        tolerance);
        return selector.accept(percentageAssertion);
    }

    public I notSameAs(final double expectedPercentage) {
        return notSameAs(Ratio.percentage(expectedPercentage));
    }

    public I notSameAs(final Ratio expectedPercentage) {
        final PercentageAssertion percentageAssertion =
                new PercentageAssertion(
                        name,
                        RelativeOrder.EQUALS,
                        expectedPercentage,
                        tolerance);
        final NegateExperimentAssertion negate =
                new NegateExperimentAssertion(percentageAssertion);
        return selector.accept(negate);
    }

    public I lessThan(final double expectedPercentage) {
        return lessThan(Ratio.percentage(expectedPercentage));
    }

    public I lessThan(final Ratio expectedPercentage) {
        final PercentageAssertion percentageAssertion =
                new PercentageAssertion(
                        name,
                        RelativeOrder.LESS,
                        expectedPercentage,
                        tolerance);
        return selector.accept(percentageAssertion);
    }

    public I greaterThanOrEquals(final double expectedPercentage) {
        return greaterThanOrEquals(Ratio.percentage(expectedPercentage));
    }

    public I greaterThanOrEquals(final Ratio expectedPercentage) {
        final PercentageAssertion percentageAssertion =
                new PercentageAssertion(
                        name,
                        RelativeOrder.LESS,
                        expectedPercentage,
                        tolerance);
        final NegateExperimentAssertion negate =
                new NegateExperimentAssertion(percentageAssertion);
        return selector.accept(negate);
    }

    public I greaterThan(final double expectedPercentage) {
        return greaterThan(Ratio.percentage(expectedPercentage));
    }

    public I greaterThan(final Ratio expectedPercentage) {
        final PercentageAssertion percentageAssertion =
                new PercentageAssertion(
                        name,
                        RelativeOrder.GREATER,
                        expectedPercentage,
                        tolerance);
        return selector.accept(percentageAssertion);
    }

    public I lessThanOrEquals(final double expectedPercentage) {
        return lessThanOrEquals(Ratio.percentage(expectedPercentage));
    }

    public I lessThanOrEquals(final Ratio expectedPercentage) {
        final PercentageAssertion percentageAssertion =
                new PercentageAssertion(
                        name,
                        RelativeOrder.GREATER,
                        expectedPercentage,
                        tolerance);
        final NegateExperimentAssertion negate =
                new NegateExperimentAssertion(percentageAssertion);
        return selector.accept(negate);
    }

}
