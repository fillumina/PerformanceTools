package com.fillumina.performance.assertion;

import com.fillumina.performance.infrastructure.TN;
import com.fillumina.performance.util.EqCondition;
import com.fillumina.performance.util.TName;
import com.fillumina.performance.util.stats.Ratio;
import java.io.Serializable;

/**
 * Part of the {@link AssertStats} builder that creates assertions
 * based on order (which one is first).
 *
 * @author Francesco Illuminati
 */
public class OrderConditionBuilder
            <I extends AssertionSelector<I,C,A>, C, A extends Assertable>
        implements Serializable {
    private static final long serialVersionUID = 1L;

    private final AssertionSelector<I,C,A> selector;
    private final TName name;
    private final Ratio tolerance;

    public OrderConditionBuilder(
            final AssertionSelector<I,C,A> selector,
            final TName name,
            final Ratio tolerance) {
        this.selector = selector;
        this.name = name;
        this.tolerance = tolerance;
    }

    public I is(EqCondition equality, TName other) {
        switch(equality) {
            case EQUALS: return sameAs(other);
            case LESS: return lessThan(other);
            case GREATER: return greaterThan(other);
        }
        throw new AssertionError("unexpected case: " + equality);
    }

    public I sameAs(final String... other) {
        return sameAs(TN.tname(other));
    }

    public I sameAs(final TName other) {
        return selector.addAssertion(new AssertOrderCondition<>(
                        name, other, EqCondition.EQUALS,
                        tolerance));
    }

    public I greaterThan(final String... other) {
        return greaterThan(TN.tname(other));
    }

    public I greaterThan(final TName other) {
        return selector.addAssertion(new AssertOrderCondition<>(
                        name, other, EqCondition.GREATER,
                        tolerance));
    }

    public I lessThan(final String... other) {
        return lessThan(TN.tname(other));
    }

    public I lessThan(final TName other) {
        return selector.addAssertion(new AssertOrderCondition<>(
                        name, other, EqCondition.LESS,
                        tolerance));
    }
}
