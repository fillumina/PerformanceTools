package com.fillumina.performance.assertion;

import com.fillumina.performance.util.EqCondition;
import com.fillumina.performance.util.stats.Ratio;
import com.fillumina.performance.util.tname.TName;
import java.io.Serializable;

/**
 * Part of the {@link AssertStats} builder that creates assertions
 * based on order (which one is first).
 *
 * @author Francesco Illuminati
 */
public class OrderConditionBuilder<I extends AssertionFactory<I,C>, C>
        implements Serializable {
    private static final long serialVersionUID = 1L;

    private final AssertionFactory<I,C> selector;
    private final CharSequence name;
    private final Ratio tolerance;

    public OrderConditionBuilder(
            final AssertionFactory<I,C> selector,
            final CharSequence name,
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

    public I sameAs(final CharSequence other) {
        return selector.addAssertion(new AssertOrderCondition(
                        name, other, EqCondition.EQUALS,
                        tolerance));
    }

    public I greaterThan(final CharSequence other) {
        return selector.addAssertion(new AssertOrderCondition(
                        name, other, EqCondition.GREATER,
                        tolerance));
    }

    public I lessThan(final CharSequence other) {
        return selector.addAssertion(new AssertOrderCondition(
                        name, other, EqCondition.LESS,
                        tolerance));
    }
}
