package com.fillumina.performance.assertion;

import com.fillumina.performance.util.ReturningToCallerImpl;
import java.io.Serializable;

/**
 *
 * @author Francesco Illuminati
 */
public class AssertPercentage<C, A extends AssertableMultiStats>
        extends ReturningToCallerImpl<AssertPerformance<C, A>>
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
