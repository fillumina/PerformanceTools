package com.fillumina.performance.assertion;

import com.fillumina.performance.infrastructure.TN;
import com.fillumina.performance.util.ReentrantFluidInterfaceImpl;
import com.fillumina.performance.util.TName;
import java.io.Serializable;

/**
 * Part of the {@link AssertStats} builder that creates assertions
 * based on order (which one is first).
 *
 * @author Francesco Illuminati
 */
public class OrderConditionBuilder<C, A extends Assertable>
        extends ReentrantFluidInterfaceImpl<AssertStats<?, A>>
        implements Serializable {
    private static final long serialVersionUID = 1L;

    private final AssertStats<C, A> assertPerformance;
    private final TName name;

    public OrderConditionBuilder(final AssertStats<C, A> assertPerformance,
            final TName name) {
        super(assertPerformance);
        this.assertPerformance = assertPerformance;
        this.name = name;
    }

    public AssertStats<C,A> sameAs(final String other) {
        return sameAs(TN.n(other));
    }

    public AssertStats<C,A> sameAs(final TName other) {
        return assertPerformance.addAssertion(new AssertOrderCondition<>(
                        name, other, EqCondition.EQUALS,
                        assertPerformance.getTolerance()));
    }

    public AssertStats<C,A> greaterThan(final String other) {
        return greaterThan(TN.n(other));
    }

    public AssertStats<C,A> greaterThan(final TName other) {
        return assertPerformance.addAssertion(new AssertOrderCondition<>(
                        name, other, EqCondition.GREATER,
                        assertPerformance.getTolerance()));
    }

    public AssertStats<C,A> lessThan(final String other) {
        return lessThan(TN.n(other));
    }

    public AssertStats<C,A> lessThan(final TName other) {
        return assertPerformance.addAssertion(new AssertOrderCondition<>(
                        name, other, EqCondition.LESS,
                        assertPerformance.getTolerance()));
    }
}
