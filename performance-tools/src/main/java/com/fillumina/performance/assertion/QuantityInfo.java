package com.fillumina.performance.assertion;

import com.fillumina.performance.util.RelativeOrder;
import com.fillumina.performance.util.stats.ConfidenceInterval;
import com.fillumina.performance.util.stats.Ratio;
import com.fillumina.performance.util.stats.ToleranceEvaluator;
import com.fillumina.performance.util.unit.DimensionalMeasure;
import com.fillumina.performance.util.unit.Quantity;
import com.fillumina.performance.util.unit.Unit;
import com.fillumina.performance.util.unit.Units;
import java.util.function.BiPredicate;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class QuantityInfo
        extends AbstractAssertionErrorInfo<Quantity<?>> {

    static enum SpecialUnit implements Unit<SpecialUnit> {
        UNIT;

        public static final Units<SpecialUnit> UNITS = new Units<>(values());

        @Override
        public Units<SpecialUnit> units() {
            return UNITS;
        }

        @Override
        public double getFactor() {
            return 1.0;
        }
    }

    private final BiPredicate<RelativeOrder,Ratio> evaluator;

    public QuantityInfo(
            AssertableExperiment assertable,
            CharSequence testName,
            RelativeOrder order,
            Quantity<?> expectedValue,
            Ratio tolerance) {
        super(assertable, testName, order, expectedValue, tolerance);
        evaluator = createEvaluator();
    }

    @Override
    protected BiPredicate<RelativeOrder, Ratio> getEvaluator() {
        return evaluator;
    }

    private BiPredicate<RelativeOrder,Ratio> createEvaluator() {
        DimensionalMeasure actualMeasure =
                getAssertable().getMeasure(getFirstTestName());
        Unit<?> actualUnit = actualMeasure.getUnit();
        Quantity<?> expectedQuantity = getAssertionValue();
        Unit<?> expectedUnit = expectedQuantity.getUnit();

        double expectedValue;

        if (expectedUnit.isSameType(SpecialUnit.UNIT)) {
            expectedValue = expectedQuantity.getValue();

        } else if (!actualUnit.isSameType(expectedUnit)) {
            throw new RuntimeException("value specified in the wrong unit, was " +
                    actualUnit.getUnitName() + " but " +
                    expectedUnit.getUnitName() + " was expected");

        } else {
            expectedValue = expectedQuantity.as(actualUnit);

        }


        ConfidenceInterval interval =
                actualMeasure.getConfidenceInterval(Ratio.P_99);
        double lower = interval.getLowerBound();
        double upper = interval.getUpperBound();

        return (condition, tolerance) -> {
            ToleranceEvaluator.Value toleratedValue =
                    new ToleranceEvaluator(tolerance).value(expectedValue);
            switch (condition) {
                case EQUALS:
                    return toleratedValue.between(lower, upper);
                case GREATER:
                    return toleratedValue.lessThan(lower);
                case LESS:
                    return toleratedValue.greaterThan(upper);
            }
            throw new AssertionError("not managed condition: " + condition);
        };
    }

    @Override
    public String toString() {
        DimensionalMeasure actualMeasure =
                getAssertable().getMeasure(getFirstTestName());
        Quantity<?> expectedQuantity = getAssertionValue();

        StringBuilder buf = new StringBuilder();
        buf.append('\'').append(getFirstTestName()).append('\'')
                .append(" expected ")
                .append(getRelativeOrder())
                .append(' ')
                .append(expectedQuantity)
                .append(", found ")
                .append(actualMeasure.toStringForConfidence(getTolerance()))
                .append(" with a tolerance of ")
                .append(getTolerance())
                .append(System.lineSeparator());
                appendWhatIfTolerance(buf);
                buf.append(getAssertable().toString());
        return buf.toString();
    }
}
