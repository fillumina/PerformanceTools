package com.fillumina.performance.assertion;

import com.fillumina.performance.util.ReturningToCallerImpl;
import java.io.Serializable;

/**
 *
 * @author Francesco Illuminati
 */
public class AssertOrder<C, A extends AssertableMultiStats>
        extends ReturningToCallerImpl<AssertPerformance<?, A>>
        implements Serializable {
    private static final long serialVersionUID = 1L;

    private final AssertPerformance<C, A> assertPerformance;
    private final String name;

    public AssertOrder(final AssertPerformance<C, A> assertPerformance,
            final String name) {
        super(assertPerformance);
        this.assertPerformance = assertPerformance;
        this.name = name;
    }

    public AssertPerformance<C,A> sameAs(final String other) {
        return assertPerformance.addCondition(
                new AssertOrderCondition<A>(EqualityCondition.SAME,
                        name,
                        other,
                        assertPerformance.getTolerancePercentage()));
    }

    public AssertPerformance<C,A> greaterThan(final String other) {
        return assertPerformance.addCondition(
                new AssertOrderCondition<A>(EqualityCondition.GREATER,
                        name,
                        other,
                        assertPerformance.getTolerancePercentage()));
    }

    public AssertPerformance<C,A> lessThan(final String other) {
        return assertPerformance.addCondition(
                new AssertOrderCondition<A>(EqualityCondition.LESS,
                        name,
                        other,
                        assertPerformance.getTolerancePercentage()));
    }
}
