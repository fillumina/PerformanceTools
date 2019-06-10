package com.fillumina.performance.assertion;

import com.fillumina.performance.util.RelativeOrder;
import com.fillumina.performance.util.stats.Ratio;
import java.util.function.BiPredicate;


/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class RatioValueInfo
        extends AbstractAssertionErrorInfo<Number> {

    private final RatioInfo evaluator;
    private final boolean isDecimal;

    public static AbstractAssertionErrorInfo<Number> createWithPercentage(
            AssertableExperiment assertable,
            CharSequence firstTestName,
            RelativeOrder order,
            Number percentageRatio,
            Ratio tolerance) {

        return new RatioValueInfo(assertable, firstTestName, order,
                percentageRatio, tolerance, false);
    }

    public static AbstractAssertionErrorInfo<Number> createWithDecimal(
            AssertableExperiment assertable,
            CharSequence firstTestName,
            RelativeOrder order,
            Number decimalRatio,
            Ratio tolerance) {

        return new RatioValueInfo(assertable, firstTestName, order,
                decimalRatio, tolerance, true);
    }

    protected RatioValueInfo(AssertableExperiment assertable,
            CharSequence firstTestName,
            RelativeOrder relativeOrder,
            Number value,
            Ratio tolerance,
            boolean decimal) {
        super(assertable, firstTestName, relativeOrder, value, tolerance);

        final Ratio ratio = decimal ?
                Ratio.decimal(value) : Ratio.percentage(value);

        isDecimal = decimal;
        evaluator = new RatioInfo(
                assertable, firstTestName, relativeOrder, ratio, tolerance);
    }

    @Override
    protected BiPredicate<RelativeOrder, Ratio> getEvaluator() {
        return evaluator.getEvaluator();
    }

    @Override
    public Number getAssertionValue() {
        Ratio ratio = evaluator.getAssertionValue();
        return isDecimal ? ratio.getDecimal() : ratio.getPercentage();
    }

    @Override
    public String toString() {
        return evaluator.toString();
    }
}
