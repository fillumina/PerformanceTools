package com.fillumina.performance.assertion;

import com.fillumina.performance.util.ReentrantFluidInterfaceImpl;
import java.io.Serializable;

/**
 * Part of the {@link StatsAssertion} builder that creates assertions
 * based on the ratio between a test and the one with the higher value
 * expressed in percentage.
 * This type of measurement is very interesting because it is less dependent
 * on a specific environment (relative differences tend to be more stable
 * across systems/environments).
 *
 * @author Francesco Illuminati
 */
public class AssertPercentage<C, A extends Assertable>
        extends ReentrantFluidInterfaceImpl<AssertPerformance<C, A>>
        implements Serializable {
    private static final long serialVersionUID = 1L;

    private final AssertPerformance<C, A> assertPerformance;
    private final String name;

    public AssertPercentage(final AssertPerformance<C, A> assertPerformance,
            final String name) {
        super(assertPerformance);
        this.assertPerformance = assertPerformance;
        this.name = name;
    }

    /**
     * <i>NOTE: The old name equalsTo() was too prone to be mistaken with
     * equals().</i>
     */
    public AssertPerformance<C,A> sameAs(final double expectedPercentage) {
        return assertPerformance.addCondition(
                new AssertPercentageCondition<A>(name,
                        EqualityCondition.SAME,
                        expectedPercentage,
                        assertPerformance.getTolerancePercentage()));
    }

    public AssertPerformance<C,A> lessThan(final double expectedPercentage) {
        return assertPerformance.addCondition(
                new AssertPercentageCondition<A>(name,
                        EqualityCondition.LESS,
                        expectedPercentage,
                        assertPerformance.getTolerancePercentage()));
    }

    public AssertPerformance<C,A> greaterThan(final double expectedPercentage) {
        return assertPerformance.addCondition(
                new AssertPercentageCondition<A>(name,
                        EqualityCondition.GREATER,
                        expectedPercentage,
                        assertPerformance.getTolerancePercentage()));
    }

}
