package com.fillumina.performance.assertion;

import com.fillumina.performance.util.ComposedName;
import com.fillumina.performance.util.formatter.FormatterUtils;
import com.fillumina.performance.util.stats.MeasureRatio;
import java.io.Serializable;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
class AssertPercentageCondition<A extends AssertableMultiTest>
        implements Assertion<A>, Serializable {

    private static final long serialVersionUID = 1L;
    private final String testName;
    private final double expectedPercentage;
    private final double tolerance;
    private final EqualityCondition condition;

    AssertPercentageCondition(final String testName,
            final EqualityCondition condition, final double expectedPercentage,
            final double tolerance) {
        this.testName = testName;
        this.condition = condition;
        this.expectedPercentage = expectedPercentage;
        this.tolerance = tolerance;
    }

    @Override
    public void check(A assertable) {
        consume(null, assertable);
    }

    @Override
    public void consume(final ComposedName name, final A assertable) {
        if (assertable != null) {
            check(name, assertable, tolerance);
        }
    }

    @SuppressWarnings("unchecked")
    public void check(final ComposedName name, final A assertable,
            final double tolerance) {
        MeasureRatio actualPercentage = assertable.getRatioWithSlowestTest(testName);
        if (!comply(actualPercentage, expectedPercentage, tolerance, condition)) {
            throw new PercentageAssertionError(name, testName, actualPercentage,
                    expectedPercentage, tolerance, condition, assertable);
        }
    }

    public static boolean comply(MeasureRatio actualPercentage,
            double expectedPercentage, double tolerance,
            EqualityCondition condition) {
        switch (condition) {
            case SAME:
                return checkSameAs(actualPercentage, expectedPercentage,
                        tolerance);
            case GREATER:
                return checkGreater(actualPercentage, expectedPercentage,
                        tolerance);
            case LESS:
                return checkLess(actualPercentage, expectedPercentage,
                        tolerance);
        }
        throw new AssertionError("condition not managed: " + condition);
    }

    private static boolean checkSameAs(MeasureRatio actualPercentage,
            double expectedPercentage, double tolerance) {
        final boolean greater =
                checkGreater(actualPercentage, expectedPercentage, tolerance);
        final boolean lesser =
                checkLess(actualPercentage, expectedPercentage, tolerance);
        return !(greater ^ lesser);
    }

    private static boolean checkGreater(MeasureRatio actualPercentage,
            double expectedPercentage, double tolerance) {
        return actualPercentage.getUpperBound() * 100.0 >
                expectedPercentage - tolerance;
    }

    private static boolean checkLess(MeasureRatio actualPercentage,
            double expectedPercentage, double tolerance) {
        return actualPercentage.getLowerBound() * 100.0 <
                expectedPercentage + tolerance;
    }

    @Override
    public String toString(A assertable) {
        return toString(null, assertable);
    }

    @Override
    public String toString(ComposedName name, A assertable) {
        StringBuilder buf = new StringBuilder();
        if (name != null) {
            buf.append(name).append(":\n");
        }
        buf.append('\'')
                .append(testName)
                .append('\'')
                .append("' (")
                .append(assertable.getRatioWithSlowestTest(testName).toString())
                .append(") ")
                .append(" is ")
                .append(condition.getMessage())
                .append(' ')
                .append(FormatterUtils.formatPercentage(expectedPercentage))
                .append(" with a tolerance of ")
                .append(tolerance)
                .append(" %");
        return buf.toString();
    }

}
