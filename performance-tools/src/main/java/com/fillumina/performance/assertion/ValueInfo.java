package com.fillumina.performance.assertion;

import com.fillumina.performance.util.RelativeOrder;
import com.fillumina.performance.util.stats.Ratio;
import com.fillumina.performance.util.unit.Quantity;
import java.util.function.BiPredicate;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class ValueInfo
        extends AbstractAssertionErrorInfo<Number> {

    private final QuantityInfo evaluator;

    public ValueInfo(AssertableExperiment assertable,
            CharSequence firstTestName,
            RelativeOrder relativeOrder,
            Number value,
            Ratio tolerance) {
        super(assertable, firstTestName, relativeOrder, value, tolerance);

        final Quantity<?> quantity =
                Quantity.of(value, QuantityInfo.SpecialUnit.UNIT);

        evaluator = new QuantityInfo(
                assertable, firstTestName, relativeOrder, quantity, tolerance);
    }

    @Override
    protected BiPredicate<RelativeOrder, Ratio> getEvaluator() {
        return evaluator.getEvaluator();
    }

}
