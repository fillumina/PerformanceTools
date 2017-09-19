package com.fillumina.performance.assertion;

import com.fillumina.performance.util.EqCondition;
import com.fillumina.performance.util.stats.Ratio;
import java.io.Serializable;

/**
 * Part of the {@link AssertStats} builder that creates assertions
 * based on value.
 *
 * @author Francesco Illuminati
 */
public class ValueAssertionBuilder<I extends AssertionBuilder<I,C>, C>
        implements Serializable {
    private static final long serialVersionUID = 1L;

    private final AssertionBuilder<I,C> assertPerformance;
    private final CharSequence name;
    private final Ratio tolerance;

    public ValueAssertionBuilder(
            final AssertionBuilder<I,C> assertPerformance,
            final CharSequence name,
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
                new ValueAssertion(name,
                        EqCondition.EQUALS,
                        expectedValue,
                        tolerance));
    }

    public I lessThan(final double expectedValue) {
        return assertPerformance.addAssertion(
                new ValueAssertion(name,
                        EqCondition.LESS,
                        expectedValue,
                        tolerance));
    }

    public I greaterThan(final double expectedValue) {
        return assertPerformance.addAssertion(
                new ValueAssertion(name,
                        EqCondition.GREATER,
                        expectedValue,
                        tolerance));
    }

}
