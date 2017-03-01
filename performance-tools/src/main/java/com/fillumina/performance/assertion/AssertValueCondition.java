package com.fillumina.performance.assertion;

import com.fillumina.performance.infrastructure.PHolder;
import com.fillumina.performance.util.StaticPath;
import com.fillumina.performance.util.stats.ConfidenceInterval;
import com.fillumina.performance.util.stats.Measure;
import com.fillumina.performance.util.stats.Ratio;
import com.fillumina.performance.util.stats.ToleranceEvaluator;
import java.io.Serializable;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
class AssertValueCondition<A extends Assertable>
        extends AbstractAssertion<A>
        implements Serializable {

    private static final long serialVersionUID = 1L;
    private final String testName;
    private final double expectedValue;
    private final Ratio tolerance;
    private final OrderCondition condition;

    public AssertValueCondition(final String testName,
            final OrderCondition condition,
            final double expectedValue,
            final Ratio tolerance) {
        this.testName = testName;
        this.condition = condition;
        this.expectedValue = expectedValue;
        this.tolerance = tolerance;
    }

    @Override
    public void consume(final PHolder<A> assertable) {
        if (assertable != null) {
            check(assertable, tolerance);
        }
    }

    public void check(final PHolder<A> assertableHolder,
            final Ratio tolerance) {
        final StaticPath name = assertableHolder.getName();
        final Assertable assertable = assertableHolder.getStats();
        Measure actualValue = assertable.getValue(testName);

        if (!comply(actualValue, expectedValue, tolerance, condition)) {
            throw new ValueAssertionError(name, testName, actualValue,
                    expectedValue, tolerance, condition, assertable);
        }
    }

    public static boolean comply(Measure actual,
            double expected,
            Ratio tolerance,
            OrderCondition condition) {
        ConfidenceInterval interval = actual.getConfidenceInterval(Ratio.P_99);
        double lower = interval.getLowerBound();
        double upper = interval.getUpperBound();
        ToleranceEvaluator.Value expectedValue =
                new ToleranceEvaluator(tolerance).value(expected);
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
        buf.append('\'').append(testName)
                .append("' (")
                .append(assertable.getValue(testName))
                .append(") ")
                .append(" is ")
                .append(condition.getMessage())
                .append(' ')
                .append(expectedValue)
                .append(" with a tolerance of ")
                .append(tolerance);
        return buf.toString();
    }

}
