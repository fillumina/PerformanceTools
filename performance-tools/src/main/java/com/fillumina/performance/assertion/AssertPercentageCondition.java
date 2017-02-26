package com.fillumina.performance.assertion;

import com.fillumina.performance.infrastructure.PHolder;
import com.fillumina.performance.util.ComposedName;
import com.fillumina.performance.util.stats.MeasureRatio;
import com.fillumina.performance.util.stats.Ratio;
import java.io.Serializable;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
class AssertPercentageCondition<A extends Assertable>
        implements Assertion<A>, Serializable {

    private static final long serialVersionUID = 1L;
    private final String testName;
    private final Ratio expectedPercentage;
    private final Ratio tolerance;
    private final OrderCondition condition;

    AssertPercentageCondition(final String testName,
            final OrderCondition condition,
            final Ratio expectedPercentage,
            final Ratio tolerance) {
        this.testName = testName;
        this.condition = condition;
        this.expectedPercentage = expectedPercentage;
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
            final Ratio tolerance) {
        final ComposedName name = assertableHolder.getName();
        final Assertable assertable = assertableHolder.getStats();
        MeasureRatio actualPercentage = assertable.getRatioWithSlowestTest(testName);
        if (!comply(actualPercentage, expectedPercentage, tolerance, condition)) {
            throw new PercentageAssertionError(name, testName, actualPercentage,
                    expectedPercentage, tolerance, condition, assertable);
        }
    }

    public static boolean comply(MeasureRatio actualPercentage,
            Ratio expectedPercentage, Ratio tolerance,
            OrderCondition condition) {
        double expextedP = expectedPercentage.getPercentage();
        double toleranceP = tolerance.getPercentage();
        switch (condition) {
            case SAME:
                return checkSameAs(actualPercentage, expextedP, toleranceP);
            case GREATER:
                return checkGreater(actualPercentage, expextedP, toleranceP);
            case LESS:
                return checkLess(actualPercentage, expextedP, toleranceP);
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
    public String toString(PHolder<A> assertableHolder) {
        ComposedName name = assertableHolder.getName();
        Assertable assertable = assertableHolder.getStats();
        StringBuilder buf = new StringBuilder();
        if (name != null && !name.isEmpty()) {
            buf.append(name).append(':').append(System.lineSeparator());
        }
        buf.append('\'')
                .append(testName)
                .append("' (")
                .append(assertable.getRatioWithSlowestTest(testName)
                        .toStringAsPercentage())
                .append(") ")
                .append(" is ")
                .append(condition.getMessage())
                .append(' ')
                .append(expectedPercentage)
                .append(" with a tolerance of ")
                .append(tolerance)
                .append(" %");
        return buf.toString();
    }

}
