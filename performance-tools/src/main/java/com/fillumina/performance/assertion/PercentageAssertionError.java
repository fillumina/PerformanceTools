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
public class PercentageAssertionError extends AbstractExperimentAssertionError {
    private static final long serialVersionUID = 1L;
    private final CharSequence testName;
    private final MeasureRatio actualRatio;
    private final Ratio expected;
    private final AssertableExperiment assertableMultiTest;

    public static PercentageAssertionError createWithPercentage(
            AssertableExperiment assertable,
            CharSequence firstTestName,
            Number expectedPercentage,
            RelativeOrder order,
            Ratio tolerance) {
        return createWithRatio(assertable, firstTestName,
                Ratio.percentage(expectedPercentage.doubleValue()), order,
                tolerance);
    }

    public static PercentageAssertionError createWithRatio(
            AssertableExperiment assertable,
            CharSequence firstTestName,
            Ratio expectedRatio,
            RelativeOrder order,
            Ratio tolerance) {
        Ratio confidence = Ratio.decimal(1 - tolerance.getDecimal());
        RatioAgainstBiggerMeasureCalculator ratios =
                new RatioAgainstBiggerMeasureCalculator(assertable);
        MeasureRatio actualRatio = ratios.getRatio(firstTestName, confidence);

        if (actualRatio == null) {
            throw new MeasureNotFoundException(firstTestName);
        }
        return new PercentageAssertionError(firstTestName,
                actualRatio, expectedRatio,
                tolerance, order, assertable);
    }

    public PercentageAssertionError(
            CharSequence testName,
            MeasureRatio actualRatio,
            Ratio expectedRatio,
            Ratio tolerance,
            RelativeOrder requiredCondition,
            AssertableExperiment assertableMultiTest) {
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

    public AssertableExperiment getAssertableMultiTest() {
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
