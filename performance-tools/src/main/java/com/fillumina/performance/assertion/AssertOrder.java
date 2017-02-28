package com.fillumina.performance.assertion;

import com.fillumina.performance.util.ReentrantFluidInterfaceImpl;
import java.io.Serializable;

/**
 * Part of the {@link AssertStats} builder that creates assertions
 * based on order (which one is first).
 *
 * @author Francesco Illuminati
 */
public class AssertOrder<C, A extends Assertable>
        extends ReentrantFluidInterfaceImpl<AssertStats<?, A>>
        implements Serializable {
    private static final long serialVersionUID = 1L;

    private final AssertStats<C, A> assertPerformance;
    private final String name;

    public AssertOrder(final AssertStats<C, A> assertPerformance,
            final String name) {
        super(assertPerformance);
        this.assertPerformance = assertPerformance;
        this.name = name;
    }

    public AssertStats<C,A> sameAs(final String other) {
        return assertPerformance.addCondition(
                new AssertOrderCondition<A>(OrderCondition.SAME,
                        name,
                        other,
                        assertPerformance.getTolerance()));
    }

    public AssertStats<C,A> greaterThan(final String other) {
        return assertPerformance.addCondition(
                new AssertOrderCondition<A>(OrderCondition.GREATER,
                        name,
                        other,
                        assertPerformance.getTolerance()));
    }

    public AssertStats<C,A> lessThan(final String other) {
        return assertPerformance.addCondition(
                new AssertOrderCondition<A>(OrderCondition.LESS,
                        name,
                        other,
                        assertPerformance.getTolerance()));
    }
}
