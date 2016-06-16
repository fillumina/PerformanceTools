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
                new AssertOrderCondition<A>(OrderCondition.SAME,
                        name,
                        other,
                        assertPerformance.getTolerancePercentage()));
    }

    public StatsAssertion<A> slowerThan(final String other) {
        return assertPerformance.addCondition(
                new AssertOrderCondition<A>(OrderCondition.SLOWER,
                        name,
                        other,
                        assertPerformance.getTolerancePercentage()));
    }

    public StatsAssertion<A> fasterThan(final String other) {
        return assertPerformance.addCondition(
                new AssertOrderCondition<A>(OrderCondition.FASTER,
                        name,
                        other,
                        assertPerformance.getTolerancePercentage()));
    }
}
