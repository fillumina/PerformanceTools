package com.fillumina.performance.assertion;

import com.fillumina.performance.util.RelativeOrder;
import com.fillumina.performance.util.stats.MeasureRatio;
import com.fillumina.performance.util.stats.Ratio;
import com.fillumina.performance.util.stats.ToleranceEvaluator;
import java.util.function.BiPredicate;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class PercentageAssertionError extends AbstractAssertionError {
    private static final long serialVersionUID = 1L;
    private final CharSequence testName;
    private final MeasureRatio actualRatio;
    private final Ratio expected;
    private final Assertable assertableMultiTest;

    public PercentageAssertionError(
            CharSequence testName,
            MeasureRatio actualRatio,
            Ratio expectedRatio,
            Ratio tolerance,
            RelativeOrder requiredCondition,
            Assertable assertableMultiTest) {
        super(requiredCondition, tolerance);
        this.testName = testName;
        this.actualRatio = actualRatio;
        this.expected = expectedRatio;
        this.assertableMultiTest = assertableMultiTest;
    }

    @Override
    protected BiPredicate<RelativeOrder,Ratio> getPredicate() {
        double lower = actualRatio.getLowerBound();
        double upper = actualRatio.getUpperBound();

        return (condition, tolerance) -> {
            ToleranceEvaluator.Value expectedValue =
                    new ToleranceEvaluator(tolerance)
                            .value(expected.getDecimal());
            switch (condition) {
                case EQUALS:
                    return expectedValue.between(lower, upper);
                case GREATER:
                    return expectedValue.lessThan(lower);
                case LESS:
                    return expectedValue.greaterThan(upper);
            }
            throw new AssertionError("not managed condition: " + condition);
        };
    }

    public CharSequence getTestName() {
        return testName;
    }

    public MeasureRatio getRatio() {
        return actualRatio;
    }

    public Ratio getExpected() {
        return expected;
    }

    public Assertable getAssertableMultiTest() {
        return assertableMultiTest;
    }

    @Override
    public String getMessage() {
        StringBuilder buf = new StringBuilder();
        buf.append('\'').append(testName).append('\'')
                .append(" expected ")
                .append(getRelativeOrder())
                .append(' ')
                .append(expected)
                .append(", found ")
                .append(actualRatio.toStringAsPercentage())
                .append(" with a tolerance of ")
                .append(getTolerance())
                .append(System.lineSeparator());
                appendWhatIfTolerance(buf);
                buf.append(assertableMultiTest.toString());
        return buf.toString();
    }

}
