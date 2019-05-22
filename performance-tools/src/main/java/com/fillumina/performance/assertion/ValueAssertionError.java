package com.fillumina.performance.assertion;

import com.fillumina.performance.util.RelativeOrder;
import com.fillumina.performance.util.stats.ConfidenceInterval;
import com.fillumina.performance.util.stats.Measure;
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
public class ValueAssertionError extends AbstractExperimentAssertionError {
    private static final long serialVersionUID = 1L;
    private final CharSequence testName;
    private final DimensionalMeasure actualMeasure;
    private final Quantity<?> expectedQuantity;
    private final AssertableExperiment assertableMultiTest;

    private static enum SpecialUnit implements Unit<SpecialUnit> {
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

    public static ValueAssertionError createValue(
            AssertableExperiment assertable,
            CharSequence testName,
            Number expectedValue,
            RelativeOrder order,
            Ratio tolerance) {

        return createQuantity(assertable, testName,
                Quantity.of(expectedValue, SpecialUnit.UNIT),
                order,
                tolerance);
    }

    public static ValueAssertionError createQuantity(
            AssertableExperiment assertable,
            CharSequence testName,
            Quantity<?> expectedValue,
            RelativeOrder order,
            Ratio tolerance) {
        DimensionalMeasure actualValue = assertable.getMeasure(testName);

        return new ValueAssertionError(testName, actualValue,
                    expectedValue, tolerance, order, assertable);
    }

    public ValueAssertionError(
            CharSequence testName,
            DimensionalMeasure actualMeasure,
            Quantity<?> expectedQuantity,
            Ratio tolerance,
            RelativeOrder requiredCondition,
            AssertableExperiment assertableMultiTest) {
        super(requiredCondition, tolerance);
        this.testName = testName;
        this.actualMeasure = actualMeasure;
        this.expectedQuantity = expectedQuantity;
        this.assertableMultiTest = assertableMultiTest;
    }

    @Override
    protected BiPredicate<RelativeOrder,Ratio> getPredicate() {

        Unit<?> actualUnit = actualMeasure.getUnit();
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

    public CharSequence getTestName() {
        return testName;
    }

    public AssertableExperiment getAssertableMultiTest() {
        return assertableMultiTest;
    }

    public Measure getActualValue() {
        return actualMeasure;
    }

    public Quantity<?> getExpected() {
        return expectedQuantity;
    }

    @Override
    public String getMessage() {
        StringBuilder buf = new StringBuilder();
        buf.append('\'').append(testName).append('\'')
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
                buf.append(assertableMultiTest.toString());
        return buf.toString();
    }
}
