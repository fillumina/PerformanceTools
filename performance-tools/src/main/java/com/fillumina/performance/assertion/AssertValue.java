package com.fillumina.performance.assertion;

import com.fillumina.performance.util.ReentrantFluidInterfaceImpl;
import java.io.Serializable;

/**
 * Part of the {@link AssertStats} builder that creates assertions
 * based on value.
 *
 * @author Francesco Illuminati
 */
public class AssertValue<C, A extends Assertable>
        extends ReentrantFluidInterfaceImpl<AssertStats<?, A>>
        implements Serializable {
    private static final long serialVersionUID = 1L;

    private final AssertStats<C,A> assertPerformance;
    private final String name;

    public AssertValue(final AssertStats<C,A> assertPerformance,
            final String name) {
        super(assertPerformance);
        this.assertPerformance = assertPerformance;
        this.name = name;
    }

    public AssertStats<C,A> sameAs(final double expectedValue) {
        return assertPerformance.addCondition(
                new AssertValueCondition<A>(name,
                        OrderCondition.SAME,
                        expectedValue,
                        assertPerformance.getTolerance()));
    }

    public AssertStats<C,A> lessThan(final double expectedValue) {
        return assertPerformance.addCondition(
                new AssertValueCondition<A>(name,
                        OrderCondition.LESS,
                        expectedValue,
                        assertPerformance.getTolerance()));
    }

    public AssertStats<C,A> greaterThan(final double expectedValue) {
        return assertPerformance.addCondition(
                new AssertValueCondition<A>(name,
                        OrderCondition.GREATER,
                        expectedValue,
                        assertPerformance.getTolerance()));
    }

}
