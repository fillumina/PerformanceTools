package com.fillumina.performance.assertion;

import java.io.Serializable;

/**
 *
 * @author Francesco Illuminati
 */
public class AssertPercentage<A extends AssertableMultiTest>
        implements Serializable {
    private static final long serialVersionUID = 1L;

    private final AssertPerformance<A> assertPerformance;
    private final String name;

    public AssertPercentage(final AssertPerformance<A> assertPerformance,
            final String name) {
        this.assertPerformance = assertPerformance;
        this.name = name;
    }

    /**
     * <i>NOTE: The old name equalsTo() was too prone to be mistaken with
     * equals().</i>
     */
    public StatsAssertion<A> sameAs(final double expectedPercentage) {
        return assertPerformance.addCondition(
                new AssertPercentageCondition<A>(name,
                        EqualityCondition.SAME,
                        expectedPercentage,
                        assertPerformance.getTolerancePercentage()));
    }

    public StatsAssertion<A> lessThan(final double expectedPercentage) {
        return assertPerformance.addCondition(
                new AssertPercentageCondition<A>(name,
                        EqualityCondition.LESSER,
                        expectedPercentage,
                        assertPerformance.getTolerancePercentage()));
    }

    public StatsAssertion<A> greaterThan(final double expectedPercentage) {
        return assertPerformance.addCondition(
                new AssertPercentageCondition<A>(name,
                        EqualityCondition.GREATER,
                        expectedPercentage,
                        assertPerformance.getTolerancePercentage()));
    }

}
