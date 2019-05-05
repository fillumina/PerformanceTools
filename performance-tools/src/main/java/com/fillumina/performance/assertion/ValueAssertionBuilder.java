package com.fillumina.performance.assertion;

import com.fillumina.performance.util.RelativeOrder;
import com.fillumina.performance.util.stats.Ratio;
import com.fillumina.performance.util.unit.Absolute;
import com.fillumina.performance.util.unit.Quantity;
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

    public I is(RelativeOrder equality, double expected) {
        return is(equality, Absolute.UNIT.quantity(expected));
    }

    public I is(RelativeOrder equality, Quantity<?> expected) {
        switch(equality) {
            case EQUALS: return equalsTo(expected);
            case LESS: return lessThan(expected);
            case GREATER: return greaterThan(expected);
        }
        throw new AssertionError("unexpected case: " + equality);
    }

    /** It compares against {@link Absolute} quantities only. */
    public I equalsTo(final double expectedValue) {
        return equalsTo(Absolute.UNIT.quantity(expectedValue));
    }

    /** It compares against {@link Absolute} quantities only. */
    public I lessThan(final double expectedValue) {
        return lessThan(Absolute.UNIT.quantity(expectedValue));
    }

    /** It compares against {@link Absolute} quantities only. */
    public I greaterThan(final double expectedValue) {
        return greaterThan(Absolute.UNIT.quantity(expectedValue));
    }

    public I equalsTo(final Quantity<?> expectedValue) {
        final ValueAssertion valueAssertion = new ValueAssertion(
                name, RelativeOrder.EQUALS, expectedValue, tolerance);
        return assertPerformance.accept(valueAssertion);
    }

    public I lessThan(final Quantity<?> expectedValue) {
        final ValueAssertion valueAssertion = new ValueAssertion(
                name, RelativeOrder.LESS, expectedValue, tolerance);
        return assertPerformance.accept(valueAssertion);
    }

    public I greaterThan(final Quantity<?> expectedValue) {
        final ValueAssertion valueAssertion = new ValueAssertion(
                name, RelativeOrder.GREATER, expectedValue, tolerance);
        return assertPerformance.accept(valueAssertion);
    }

}
