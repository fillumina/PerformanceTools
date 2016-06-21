package com.fillumina.performance.assertion;

import java.io.Serializable;

/**
 *
 * @author Francesco Illuminati
 */
public class AssertValue<A extends AssertableMultiTest>
        implements Serializable {
    private static final long serialVersionUID = 1L;

    private final AssertPerformance<A> assertPerformance;
    private final String name;

    public AssertValue(final AssertPerformance<A> assertPerformance,
            final String name) {
        this.assertPerformance = assertPerformance;
        this.name = name;
    }

    /**
     * <i>NOTE: The old name equalsTo() was too prone to be mistaken with
     * equals().</i>
     */
    public StatsAssertion<A> sameAs(final double expectedValue) {
        return assertPerformance.addCondition(
                new AssertValueCondition<A>(name,
                        EqualityCondition.EQUALS,
                        expectedValue,
                        assertPerformance.getTolerancePercentage()));
    }

    public StatsAssertion<A> lessThan(final double expectedValue) {
        return assertPerformance.addCondition(
                new AssertValueCondition<A>(name,
                        EqualityCondition.LESSER,
                        expectedValue,
                        assertPerformance.getTolerancePercentage()));
    }

    public StatsAssertion<A> greaterThan(final double expectedValue) {
        return assertPerformance.addCondition(
                new AssertValueCondition<A>(name,
                        EqualityCondition.GREATER,
                        expectedValue,
                        assertPerformance.getTolerancePercentage()));
    }

}
