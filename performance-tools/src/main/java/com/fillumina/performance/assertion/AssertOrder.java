package com.fillumina.performance.assertion;

import java.io.Serializable;

/**
 *
 * @author Francesco Illuminati
 */
public class AssertOrder<A extends AssertableMultiTest> implements Serializable {
    private static final long serialVersionUID = 1L;

    private final AssertPerformance<A> assertPerformance;
    private final String name;

    public AssertOrder(final AssertPerformance<A> assertPerformance,
            final String name) {
        this.assertPerformance = assertPerformance;
        this.name = name;
    }

    public StatsAssertion<A> sameAs(final String other) {
        return assertPerformance.addCondition(
                new AssertOrderCondition<A>(EqualityCondition.SAME,
                        name,
                        other,
                        assertPerformance.getTolerancePercentage()));
    }

    public StatsAssertion<A> greaterThan(final String other) {
        return assertPerformance.addCondition(
                new AssertOrderCondition<A>(EqualityCondition.GREATER,
                        name,
                        other,
                        assertPerformance.getTolerancePercentage()));
    }

    public StatsAssertion<A> lessThan(final String other) {
        return assertPerformance.addCondition(
                new AssertOrderCondition<A>(EqualityCondition.LESSER,
                        name,
                        other,
                        assertPerformance.getTolerancePercentage()));
    }
}
