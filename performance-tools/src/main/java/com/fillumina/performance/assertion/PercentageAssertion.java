package com.fillumina.performance.assertion;

import com.fillumina.performance.util.AppendableWrapper;
import com.fillumina.performance.util.RelativeOrder;
import com.fillumina.performance.util.stats.MeasureRatio;
import com.fillumina.performance.util.stats.Ratio;
import java.io.Serializable;

/**
 * Asserts if the performance ratio expressed as a percentage of the
 * given test and the  slower one is within the given tolerance.
 * It should be noted that
 * evaluating the performances with ratios between tests rather than with
 * absolute results allows for a much
 * stable, reproducible between different systems and meaningful measures
 * (i.e. expressing the improvement of a new version of an algorithm over
 * a previous version with a ratio is much more useful that stating a
 * timing measurement).
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
class PercentageAssertion
        implements ExperimentAssertion, Serializable {

    private static final long serialVersionUID = 1L;
    private final CharSequence testName;
    private final Ratio expectedRatio;
    private final Ratio tolerance;
    private final RelativeOrder condition;

    PercentageAssertion(final CharSequence testName,
            final RelativeOrder condition,
            final Ratio expectedPercentage,
            final Ratio tolerance) {
        this.testName = testName;
        this.condition = condition;
        this.expectedRatio = expectedPercentage;
        this.tolerance = tolerance;
    }

    @Override
    public void accept(AssertableExperiment assertable) {
        if (assertable != null) {
            check(assertable, tolerance);
        }
    }

    public void check(final AssertableExperiment assertable, final Ratio tolerance) {
        Ratio confidence = Ratio.decimal(1 - tolerance.getDecimal());
        if (assertable != null) {
            RatioAgainstBiggerMeasureCalculator ratios =
                    new RatioAgainstBiggerMeasureCalculator(assertable);
            MeasureRatio actualRatio = ratios.getRatio(testName, confidence);

            if (actualRatio == null) {
                throw new MeasureNotFoundException(testName);
            }
            new PercentageAssertionError(testName,
                    actualRatio, expectedRatio,
                    tolerance, condition, assertable)
                    .checkAndThrowExceptionIfNotSatisfied();
        }
    }

    @Override
    public void appendTo(Appendable appendable, AssertableExperiment assertable) {
        Ratio confidence = Ratio.decimal(1 - tolerance.getDecimal());
        RatioAgainstBiggerMeasureCalculator ratios = new RatioAgainstBiggerMeasureCalculator(assertable);
        MeasureRatio actualRatio = ratios.getRatio(testName, confidence);
        if (actualRatio != null) {
            new AppendableWrapper(appendable)
                    .print('\'')
                    .print(testName)
                    .print("' (")
                    .print(actualRatio.toStringAsPercentage())
                    .print(") ")
                    .print(satisfy(assertable) ? " is " : " is not ")
                    .print(condition.getMessage())
                    .print(' ')
                    .print(expectedRatio)
                    .print(" with a tolerance of ")
                    .print(tolerance);
        }
    }

    @Override
    public String toString() {
        return testName +
                " " + condition.getSymbol() + " " +
                expectedRatio.toString() +
                " (" + tolerance.toString() + ")";
    }
}
