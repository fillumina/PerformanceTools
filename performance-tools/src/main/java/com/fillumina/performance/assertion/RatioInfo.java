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
public class RatioInfo
        extends AbstractAssertionErrorInfo<Ratio> {

    private final MeasureRatio actualRatio;
    private final BiPredicate<RelativeOrder,Ratio> evaluator;

    public RatioInfo(
            AssertableExperiment assertable,
            CharSequence firstTestName,
            RelativeOrder order,
            Ratio expectedPercentage,
            Ratio tolerance) {
        super(assertable, firstTestName, order, expectedPercentage, tolerance);
        Ratio confidence = Ratio.decimal(1 - tolerance.getDecimal());
        // TODO check this if can be included here
        RatioAgainstBiggerMeasureCalculator ratios =
                new RatioAgainstBiggerMeasureCalculator(assertable);

        actualRatio = ratios.getRatio(firstTestName, confidence);
        evaluator = createEvaluator();
    }

    private BiPredicate<RelativeOrder,Ratio> createEvaluator() {
        double lower = actualRatio.getLowerBound();
        double upper = actualRatio.getUpperBound();

        return (condition, tol) -> {
            ToleranceEvaluator.Value expectedValue =
                    new ToleranceEvaluator(tol)
                            .value(getAssertionValue().getDecimal());
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

    @Override
    public BiPredicate<RelativeOrder,Ratio> getEvaluator() {
        return evaluator;
    }

    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder();
        buf.append('\'').append(getFirstTestName()).append('\'')
                .append(" expected ")
                .append(getRelativeOrder())
                .append(' ')
                .append(getAssertionValue())
                .append(", found ")
                .append(actualRatio.toStringAsPercentage())
                .append(" with a tolerance of ")
                .append(getTolerance())
                .append(System.lineSeparator());
                appendWhatIfTolerance(buf);
                buf.append(getAssertable().toString());
        return buf.toString();
    }
}
