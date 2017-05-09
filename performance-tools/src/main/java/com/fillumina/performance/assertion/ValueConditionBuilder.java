package com.fillumina.performance.assertion;

import com.fillumina.performance.util.EqCondition;
import com.fillumina.performance.util.ReentrantFluidInterfaceImpl;
import com.fillumina.performance.util.TName;
import java.io.Serializable;

/**
 * Part of the {@link AssertStats} builder that creates assertions
 * based on value.
 *
 * @author Francesco Illuminati
 */
public class ValueConditionBuilder<C, A extends Assertable>
        extends ReentrantFluidInterfaceImpl<AssertStats<?, A>>
        implements Serializable {
    private static final long serialVersionUID = 1L;

    private final AssertStats<C,A> assertPerformance;
    private final TName name;

    public ValueConditionBuilder(final AssertStats<C,A> assertPerformance,
            final TName name) {
        super(assertPerformance);
        this.assertPerformance = assertPerformance;
        this.name = name;
    }

    public AssertStats<C,A> is(EqCondition equality, double expected) {
        switch(equality) {
            case EQUALS: return sameAs(expected);
            case LESS: return lessThan(expected);
            case GREATER: return greaterThan(expected);
        }
        throw new AssertionError("unexpected case: " + equality);
    }

    public AssertStats<C,A> sameAs(final double expectedValue) {
        return assertPerformance.addAssertion(
                new AssertValueCondition<>(name,
                        EqCondition.EQUALS,
                        expectedValue,
                        assertPerformance.getTolerance()));
    }

    public AssertStats<C,A> lessThan(final double expectedValue) {
        return assertPerformance.addAssertion(
                new AssertValueCondition<>(name,
                        EqCondition.LESS,
                        expectedValue,
                        assertPerformance.getTolerance()));
    }

    public AssertStats<C,A> greaterThan(final double expectedValue) {
        return assertPerformance.addAssertion(
                new AssertValueCondition<>(name,
                        EqCondition.GREATER,
                        expectedValue,
                        assertPerformance.getTolerance()));
    }

}
