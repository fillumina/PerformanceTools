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
@Deprecated
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

    public I is(boolean negate, RelativeOrder equality, double expected) {
        return is(negate, equality, Absolute.UNIT.quantity(expected));
    }

    public I is(boolean negate, RelativeOrder equality, Quantity<?> expected) {
        if (negate) {
            switch(equality) {
                case EQUALS: return notEqualsTo(expected);
                case LESS: return greaterThanOrEquals(expected);
                case GREATER: return lessThanOrEquals(expected);
            }
        } else {
            switch(equality) {
                case EQUALS: return equalsTo(expected);
                case LESS: return lessThan(expected);
                case GREATER: return greaterThan(expected);
            }
        }
        throw new AssertionError("unexpected case: " + equality);
    }

    /** It compares against {@link Absolute} quantities only. */
    public I notEqualsTo(final double expectedValue) {
        return notEqualsTo(Absolute.UNIT.quantity(expectedValue));
    }

    public I notEqualsTo(final Quantity<?> expectedValue) {
        final ValueAssertion valueAssertion = new ValueAssertion(
                name, RelativeOrder.EQUALS, expectedValue, tolerance);
        final NegateExperimentAssertion negate =
                new NegateExperimentAssertion(valueAssertion);
        return assertPerformance.accept(negate);
    }

    /** It compares against {@link Absolute} quantities only. */
    public I equalsTo(final double expectedValue) {
        return equalsTo(Absolute.UNIT.quantity(expectedValue));
    }

    public I equalsTo(final Quantity<?> expectedValue) {
        final ValueAssertion valueAssertion = new ValueAssertion(
                name, RelativeOrder.EQUALS, expectedValue, tolerance);
        return assertPerformance.accept(valueAssertion);
    }

    /** It compares against {@link Absolute} quantities only. */
    public I lessThan(final double expectedValue) {
        return lessThan(Absolute.UNIT.quantity(expectedValue));
    }

    public I lessThan(final Quantity<?> expectedValue) {
        final ValueAssertion valueAssertion = new ValueAssertion(
                name, RelativeOrder.LESS, expectedValue, tolerance);
        return assertPerformance.accept(valueAssertion);
    }

    /** It compares against {@link Absolute} quantities only. */
    public I greaterThanOrEquals(final double expectedValue) {
        return greaterThanOrEquals(Absolute.UNIT.quantity(expectedValue));
    }

    public I greaterThanOrEquals(final Quantity<?> expectedValue) {
        final ValueAssertion valueAssertion = new ValueAssertion(
                name, RelativeOrder.LESS, expectedValue, tolerance);
        final NegateExperimentAssertion negate =
                new NegateExperimentAssertion(valueAssertion);
        return assertPerformance.accept(negate);
    }

    /** It compares against {@link Absolute} quantities only. */
    public I lessThanOrEquals(final double expectedValue) {
        return lessThanOrEquals(Absolute.UNIT.quantity(expectedValue));
    }

    public I lessThanOrEquals(final Quantity<?> expectedValue) {
        final ValueAssertion valueAssertion = new ValueAssertion(
                name, RelativeOrder.GREATER, expectedValue, tolerance);
        final NegateExperimentAssertion negate =
                new NegateExperimentAssertion(valueAssertion);
        return assertPerformance.accept(negate);
    }

    /** It compares against {@link Absolute} quantities only. */
    public I greaterThan(final double expectedValue) {
        return greaterThan(Absolute.UNIT.quantity(expectedValue));
    }

    public I greaterThan(final Quantity<?> expectedValue) {
        final ValueAssertion valueAssertion = new ValueAssertion(
                name, RelativeOrder.GREATER, expectedValue, tolerance);
        return assertPerformance.accept(valueAssertion);
    }

}
