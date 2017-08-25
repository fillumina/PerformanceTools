package com.fillumina.performance.assertion;

import com.fillumina.performance.util.EqCondition;
import com.fillumina.performance.util.tname.TName;
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
public class PercentageConditionBuilder<I extends AssertionSelector<I,C>, C>
        implements Serializable {
    private static final long serialVersionUID = 1L;

    private final AssertionSelector<I,C> selector;
    private final TName name;
    private final Ratio tolerance;

    public PercentageConditionBuilder(
            final AssertionSelector<I,C> assertPerformance,
            final TName name,
            final Ratio tolerance) {
        this.selector = assertPerformance;
        this.name = name;
        this.tolerance = tolerance;
    }

    public I is(EqCondition equality, double expected) {
        switch(equality) {
            case EQUALS: return sameAs(expected);
            case LESS: return lessThan(expected);
            case GREATER: return greaterThan(expected);
        }
        throw new AssertionError("unexpected case: " + equality);
    }

    public I sameAs(final double expectedPercentage) {
        return selector.addAssertion(new AssertPercentageCondition(
                name,
                EqCondition.EQUALS,
                Ratio.percentage(expectedPercentage),
                tolerance));
    }

    public I lessThan(final double expectedPercentage) {
        return selector.addAssertion(new AssertPercentageCondition(
                name,
                EqCondition.LESS,
                Ratio.percentage(expectedPercentage),
                tolerance));
    }

    public I greaterThan(final double expectedPercentage) {
        return selector.addAssertion(new AssertPercentageCondition(
                name,
                EqCondition.GREATER,
                Ratio.percentage(expectedPercentage),
                tolerance));
    }

}
