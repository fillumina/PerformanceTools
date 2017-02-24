package com.fillumina.performance.assertion;

import com.fillumina.performance.util.ReentrantFluidInterfaceImpl;
import java.io.Serializable;

/**
 * Part of the {@link StatsAssertion} builder that creates assertions
 * based on value.
 *
 * @author Francesco Illuminati
 */
public class AssertValue<C, A extends Assertable>
        extends ReentrantFluidInterfaceImpl<AssertPerformance<?, A>>
        implements Serializable {
    private static final long serialVersionUID = 1L;

    private final AssertPerformance<C,A> assertPerformance;
    private final String name;

    public AssertValue(final AssertPerformance<C,A> assertPerformance,
            final String name) {
        super(assertPerformance);
        this.assertPerformance = assertPerformance;
        this.name = name;
    }

    /**
     */
    public AssertPerformance<C,A> sameAs(final double expectedValue) {
        return assertPerformance.addCondition(
                new AssertValueCondition<A>(name,
                        EqualityCondition.SAME,
                        expectedValue,
                        assertPerformance.getTolerancePercentage()));
    }

    public AssertPerformance<C,A> lessThan(final double expectedValue) {
        return assertPerformance.addCondition(
                new AssertValueCondition<A>(name,
                        EqualityCondition.LESS,
                        expectedValue,
                        assertPerformance.getTolerancePercentage()));
    }

    public AssertPerformance<C,A> greaterThan(final double expectedValue) {
        return assertPerformance.addCondition(
                new AssertValueCondition<A>(name,
                        EqualityCondition.GREATER,
                        expectedValue,
                        assertPerformance.getTolerancePercentage()));
    }

}
