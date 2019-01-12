package com.fillumina.performance.assertion;

import com.fillumina.performance.util.RelativeOrder;
import com.fillumina.performance.util.stats.ConfidenceInterval;
import com.fillumina.performance.util.stats.Measure;
import com.fillumina.performance.util.stats.Ratio;
import com.fillumina.performance.util.stats.ToleranceEvaluator;
import com.fillumina.performance.util.unit.DimensionalMeasure;
import com.fillumina.performance.util.unit.Quantity;
import com.fillumina.performance.util.unit.Unit;
import java.util.function.BiPredicate;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class ValueAssertionError extends AbstractAssertionError {
    private static final long serialVersionUID = 1L;
    private final CharSequence testName;
    private final DimensionalMeasure actualValue;
    private final Quantity<?> expected;
    private final AssertableExperiment assertableMultiTest;

    public ValueAssertionError(
            CharSequence testName,
            DimensionalMeasure actualValue,
            Quantity<?> expectedValue,
            Ratio tolerance,
            RelativeOrder requiredCondition,
            AssertableExperiment assertableMultiTest) {
        super(requiredCondition, tolerance);
        this.testName = testName;
        this.actualValue = actualValue;
        this.expected = expectedValue;
        this.assertableMultiTest = assertableMultiTest;
    }

    @Override
    protected BiPredicate<RelativeOrder,Ratio> getPredicate() {
        ConfidenceInterval interval =
                actualValue.getConfidenceInterval(Ratio.P_99);
        double lower = interval.getLowerBound();
        double upper = interval.getUpperBound();

        Unit<?> actualUnit = actualValue.getUnit();
        Unit<?> expectedUnit = expected.getUnit();
        if (!actualUnit.isSameType(expectedUnit)) {
            throw new RuntimeException("value specified in the wrong unit, was " +
                    actualUnit.getUnitName() + " but " +
                    expectedUnit.getUnitName() + " was expected");
        }
        double expectedFigure = expected.as(actualUnit);

        return (condition, tolerance) -> {
            ToleranceEvaluator.Value expectedValue =
                    new ToleranceEvaluator(tolerance).value(expectedFigure);
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

    public AssertableExperiment getAssertableMultiTest() {
        return assertableMultiTest;
    }

    public Measure getActualValue() {
        return actualValue;
    }

    public Quantity<?> getExpected() {
        return expected;
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
                .append(actualValue.toStringForConfidence(getTolerance()))
                .append(" with a tolerance of ")
                .append(getTolerance())
                .append(System.lineSeparator());
                appendWhatIfTolerance(buf);
                buf.append(assertableMultiTest.toString());
        return buf.toString();
    }

}
