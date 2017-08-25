package com.fillumina.performance.assertion;

import com.fillumina.performance.util.EqCondition;
import com.fillumina.performance.util.tname.TName;
import com.fillumina.performance.util.stats.Ratio;
import java.io.Serializable;

/**
 * Part of the {@link AssertStats} builder that creates assertions
 * based on value.
 *
 * @author Francesco Illuminati
 */
public class ValueConditionBuilder<I extends AssertionSelector<I,C>, C>
        implements Serializable {
    private static final long serialVersionUID = 1L;

    private final AssertionSelector<I,C> assertPerformance;
    private final TName name;
    private final Ratio tolerance;

    public ValueConditionBuilder(
            final AssertionSelector<I,C> assertPerformance,
            final TName name,
            final Ratio tolerance) {
        this.assertPerformance = assertPerformance;
        this.name = name;
        this.tolerance = tolerance;
    }

    public I is(EqCondition equality, double expected) {
        switch(equality) {
            case EQUALS: return sameAs(expected);
            case LESS: return lessThan(expected);
            case GREATER: return greaterThan(expected);
        }
        throw new AssertionError("unexpected case: " + equality);
    }

    public I sameAs(final double expectedValue) {
        return assertPerformance.addAssertion(
                new AssertValueCondition(name,
                        EqCondition.EQUALS,
                        expectedValue,
                        tolerance));
    }

    public I lessThan(final double expectedValue) {
        return assertPerformance.addAssertion(
                new AssertValueCondition(name,
                        EqCondition.LESS,
                        expectedValue,
                        tolerance));
    }

    public I greaterThan(final double expectedValue) {
        return assertPerformance.addAssertion(
                new AssertValueCondition(name,
                        EqCondition.GREATER,
                        expectedValue,
                        tolerance));
    }

}
