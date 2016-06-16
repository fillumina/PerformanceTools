package com.fillumina.performance.assertion;

import com.fillumina.performance.util.ComposedName;
import static com.fillumina.performance.util.FormatterUtils.formatPercentage;
import com.fillumina.performance.util.stats.MeasureRatio;
import java.util.Objects;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class PercentageAssertionError extends AssertionError {
    private static final long serialVersionUID = 1L;
    private final ComposedName executionTestName;
    private final String testName;
    private final MeasureRatio ratio;
    private final double expected;
    private final double tolerance;
    private final PercentageCondition requiredCondition;
    private final AssertableMultiTest assertableMultiTest;

    public PercentageAssertionError(ComposedName executionTestName,
            String testName,
            MeasureRatio actualPercentage,
            double expectedPercentage,
            double tolerance,
            PercentageCondition requiredCondition,
            AssertableMultiTest assertableMultiTest) {
        this.executionTestName = executionTestName;
        this.testName = testName;
        this.ratio = actualPercentage;
        this.expected = expectedPercentage;
        this.tolerance = tolerance;
        this.requiredCondition = requiredCondition;
        this.assertableMultiTest = assertableMultiTest;
    }

    public String getTestName() {
        return testName;
    }

    public MeasureRatio getRatio() {
        return ratio;
    }

    public double getExpected() {
        return expected;
    }

    public double getTolerance() {
        return tolerance;
    }

    public PercentageCondition getRequiredCondition() {
        return requiredCondition;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 41 * hash + Objects.hashCode(this.executionTestName);
        hash = 41 * hash + Objects.hashCode(this.testName);
        hash = 41 * hash + Objects.hashCode(this.ratio);
        hash =
                41 * hash +
                (int) (Double.doubleToLongBits(this.expected) ^
                (Double.doubleToLongBits(this.expected) >>> 32));
        hash =
                41 * hash +
                (int) (Double.doubleToLongBits(this.tolerance) ^
                (Double.doubleToLongBits(this.tolerance) >>> 32));
        hash = 41 * hash + Objects.hashCode(this.requiredCondition);
        hash = 41 * hash + Objects.hashCode(this.assertableMultiTest);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final PercentageAssertionError other = (PercentageAssertionError) obj;
        if (Double.doubleToLongBits(this.expected) !=
                Double.doubleToLongBits(other.expected)) {
            return false;
        }
        if (Double.doubleToLongBits(this.tolerance) !=
                Double.doubleToLongBits(other.tolerance)) {
            return false;
        }
        if (!Objects.equals(this.executionTestName, other.executionTestName)) {
            return false;
        }
        if (!Objects.equals(this.testName, other.testName)) {
            return false;
        }
        if (!Objects.equals(this.ratio, other.ratio)) {
            return false;
        }
        if (this.requiredCondition != other.requiredCondition) {
            return false;
        }
        if (!Objects.equals(this.assertableMultiTest, other.assertableMultiTest)) {
            return false;
        }
        return true;
    }

    @Override
    public String getMessage() {
        StringBuilder buf = new StringBuilder();
        if (executionTestName != null) {
            buf.append(executionTestName).append(": ");
        }
        buf.append('\'').append(testName).append('\'')
                .append(" expected ")
                .append(requiredCondition.getMessage())
                .append(' ')
                .append(formatPercentage(expected))
                .append(", found ")
                .append(ratio.toStringAsPercentage())
                .append(" with a tolerance of ")
                .append(tolerance)
                .append(" %")
                .append(System.lineSeparator())
                .append(assertableMultiTest.toString());
        return buf.toString();
    }

}
