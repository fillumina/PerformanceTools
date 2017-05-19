package com.fillumina.performance.assertion;

import com.fillumina.performance.util.EqCondition;
import com.fillumina.performance.util.ReentrantImpl;
import com.fillumina.performance.util.TName;
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
public class PercentageConditionBuilder<C, A extends Assertable>
        extends ReentrantImpl<AssertStats<C, A>>
        implements Serializable {
    private static final long serialVersionUID = 1L;

    private final AssertStats<C, A> assertPerformance;
    private final TName name;

    public PercentageConditionBuilder(final AssertStats<C, A> assertPerformance,
            final TName name) {
        super(assertPerformance);
        this.assertPerformance = assertPerformance;
        this.name = name;
    }

    public AssertStats<C,A> is(EqCondition equality, double expected) {
        switch(equality) {
            case EQUALS: return sameAs(expected);
            case LESS: return lessThan(expected);
            case GREATER: return greaterThan(expected);
        }
        throw new AssertionError("unexpected case: " + equality);
    }

    public AssertStats<C,A> sameAs(final double expectedPercentage) {
        return assertPerformance.addAssertion(new AssertPercentageCondition<>(
                name,
                EqCondition.EQUALS,
                Ratio.percentage(expectedPercentage),
                assertPerformance.getTolerance()));
    }

    public AssertStats<C,A> lessThan(final double expectedPercentage) {
        return assertPerformance.addAssertion(new AssertPercentageCondition<>(
                name,
                EqCondition.LESS,
                Ratio.percentage(expectedPercentage),
                assertPerformance.getTolerance()));
    }

    public AssertStats<C,A> greaterThan(final double expectedPercentage) {
        return assertPerformance.addAssertion(new AssertPercentageCondition<>(
                name,
                EqCondition.GREATER,
                Ratio.percentage(expectedPercentage),
                assertPerformance.getTolerance()));
    }

}
