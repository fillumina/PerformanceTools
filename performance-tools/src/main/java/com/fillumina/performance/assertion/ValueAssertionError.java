package com.fillumina.performance.assertion;

import com.fillumina.performance.util.StaticPath;
import com.fillumina.performance.util.stats.ConfidenceInterval;
import com.fillumina.performance.util.stats.Measure;
import com.fillumina.performance.util.stats.Ratio;
import com.fillumina.performance.util.stats.ToleranceEvaluator;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class ValueAssertionError extends AbstractAssertionError {
    private static final long serialVersionUID = 1L;
    private final String testName;
    private final Measure actualValue;
    private final double expected;
    private final Assertable assertableMultiTest;

    public ValueAssertionError(StaticPath executionTestName,
            String testName,
            Measure actualValue,
            double expectedPercentage,
            Ratio tolerance,
            OrderCondition requiredCondition,
            Assertable assertableMultiTest) {
        super(executionTestName, requiredCondition, tolerance);
        this.testName = testName;
        this.actualValue = actualValue;
        this.expected = expectedPercentage;
        this.assertableMultiTest = assertableMultiTest;
    }

    @Override
    protected boolean isConditionSatisfied(OrderCondition condition,
            Ratio tolerance) {
        ConfidenceInterval interval = actualValue.getConfidenceInterval(Ratio.P_99);
        double lower = interval.getLowerBound();
        double upper = interval.getUpperBound();
        ToleranceEvaluator.Value expectedValue =
                new ToleranceEvaluator(tolerance).value(expected);
        switch (condition) {
            case EQUALS:
                return expectedValue.between(lower, upper);
            case GREATER:
                return expectedValue.lessThan(lower);
            case LESS:
                return expectedValue.greaterThan(upper);
        }
        throw new AssertionError("not managed condition: " + condition);
    }

    public String getTestName() {
        return testName;
    }

    public Assertable getAssertableMultiTest() {
        return assertableMultiTest;
    }

    public Measure getActualValue() {
        return actualValue;
    }

    public double getExpected() {
        return expected;
    }

    @Override
    public String getMessage() {
        StringBuilder buf = new StringBuilder();
        appendTitle(buf);
        buf.append('\'').append(testName).append('\'')
                .append(" expected ")
                .append(getCondition())
                .append(' ')
                .append(expected)
                .append(", found ")
                .append(actualValue.toStringForConfidence(getTolerance()))
                .append(" with a tolerance of ")
                .append(getTolerance())
                .append(System.lineSeparator());
                appendWhatIfTolerance(buf);
                buf.append(assertableMultiTest.toString());
        return buf.toString();
    }

}
