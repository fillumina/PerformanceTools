package com.fillumina.performance.assertion;

import com.fillumina.performance.infrastructure.PHolder;
import com.fillumina.performance.util.StaticPath;
import com.fillumina.performance.util.stats.MeasureRatio;
import com.fillumina.performance.util.stats.Ratio;
import com.fillumina.performance.util.stats.ToleranceEvaluator;
import java.io.Serializable;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
class AssertPercentageCondition<A extends Assertable>
        extends AbstractAssertion<A>
        implements Serializable {

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
    public void consume(final PHolder<A> assertableHolder) {
        if (assertableHolder != null) {
            check(assertableHolder, tolerance);
        }
    }

    public void check(final PHolder<A> assertableHolder,
            final Ratio tolerance) {
        final StaticPath name = assertableHolder.getName();
        final Assertable assertable = assertableHolder.getStats();
        MeasureRatio actualRatio = assertable.getRatioWithSlowestTest(testName);
        if (!comply(actualRatio, expectedPercentage, tolerance, condition)) {
            throw new PercentageAssertionError(name, testName, actualRatio,
                    expectedPercentage, tolerance, condition, assertable);
        }
    }

    public static boolean comply(MeasureRatio actualRatio,
            Ratio expectedRatio,
            Ratio tolerance,
            OrderCondition condition) {
        double lower = actualRatio.getLowerBound();
        double upper = actualRatio.getUpperBound();
        ToleranceEvaluator.Value expectedValue =
                new ToleranceEvaluator(tolerance)
                        .value(expectedRatio.getDecimal());
        switch (condition) {
            case SAME:
                return expectedValue.between(lower, upper);
            case GREATER:
                return expectedValue.lessThan(lower);
            case LESS:
                return expectedValue.greaterThan(upper);
        }
        throw new AssertionError("not managed condition: " + condition);
    }

    @Override
    public String toString(PHolder<A> assertableHolder) {
        StaticPath name = assertableHolder.getName();
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
