package com.fillumina.performance.assertion;

import com.fillumina.performance.infrastructure.PHolder;
import com.fillumina.performance.util.ComposedName;
import com.fillumina.performance.util.stats.Measure;
import java.io.Serializable;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
class AssertValueCondition<A extends Assertable>
        implements Assertion<A>, Serializable {

    private static final long serialVersionUID = 1L;
    private final String testName;
    private final double expectedValue;
    private final double tolerance;
    private final OrderCondition condition;

    public AssertValueCondition(final String testName,
            final OrderCondition condition,
            final double expectedValue,
            final double tolerance) {
        this.testName = testName;
        this.condition = condition;
        this.expectedValue = expectedValue;
        this.tolerance = tolerance;
    }

    @Override
    public void check(PHolder<A> assertable) {
        consume(assertable);
    }

    @Override
    public void consume(final PHolder<A> assertable) {
        if (assertable != null) {
            check(assertable, tolerance);
        }
    }

    public void check(final PHolder<A> assertableHolder,
            final double tolerance) {
        final ComposedName name = assertableHolder.getName();
        final Assertable assertable = assertableHolder.getStats();
        Measure actualValue = assertable.getValue(testName);

        if (!comply(actualValue, expectedValue, tolerance, condition)) {
            throw new ValueAssertionError(name, testName, actualValue,
                    expectedValue, tolerance, condition, assertable);
        }
    }

    public static boolean comply(Measure actualValueMeasure,
            double expectedPercentage, double tolerance,
            OrderCondition condition) {
        double actualValue = actualValueMeasure.getMean();
        switch (condition) {
            case SAME:
                return checkSameAs(actualValue, expectedPercentage,
                        tolerance);
            case GREATER:
                return checkGreater(actualValue, expectedPercentage,
                        tolerance);
            case LESS:
                return checkLess(actualValue, expectedPercentage, tolerance);
        }
        throw new AssertionError("condition not managed: " + condition);
    }

    private static boolean checkSameAs(double actualValue,
            double expectedValue, double tolerance) {
        final boolean greater =
                checkGreater(actualValue, expectedValue, tolerance);
        final boolean lesser =
                checkLess(actualValue, expectedValue, tolerance);
        return !(greater ^ lesser);
    }

    private static boolean checkGreater(double actualValue,
            double expectedValue, double tolerance) {
        return actualValue * (1 + tolerance / 100.0) > expectedValue;
    }

    private static boolean checkLess(double actualValue,
            double expectedValue, double tolerance) {
        return actualValue * (1 - tolerance / 100.0) < expectedValue;
    }

    @Override
    public String toString(PHolder<A> assertableHolder) {
        ComposedName name = assertableHolder.getName();
        Assertable assertable = assertableHolder.getStats();
        StringBuilder buf = new StringBuilder();
        if (name != null && !name.isEmpty()) {
            buf.append(name).append(':').append(System.lineSeparator());
        }
        buf.append('\'').append(testName)
                .append("' (")
                .append(assertable.getValue(testName))
                .append(") ")
                .append(" is ")
                .append(condition.getMessage())
                .append(' ')
                .append(expectedValue)
                .append(" with a tolerance of ")
                .append(tolerance).append(" %");
        return buf.toString();
    }

}
