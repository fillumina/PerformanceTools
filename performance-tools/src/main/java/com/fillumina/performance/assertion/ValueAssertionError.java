package com.fillumina.performance.assertion;

import com.fillumina.performance.util.ComposedName;
import com.fillumina.performance.util.stats.Measure;
import java.util.Objects;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class ValueAssertionError extends AbstractAssertionError {
    private static final long serialVersionUID = 1L;
    private final ComposedName executionTestName;
    private final String testName;
    private final Measure actualValue;
    private final double expected;
    private final double tolerance;
    private final EqualityCondition requiredCondition;
    private final AssertableMultiStats assertableMultiTest;

    public ValueAssertionError(ComposedName executionTestName,
            String testName,
            Measure actualValue,
            double expectedPercentage,
            double tolerance,
            EqualityCondition requiredCondition,
            AssertableMultiStats assertableMultiTest) {
        this.executionTestName = executionTestName;
        this.testName = testName;
        this.actualValue = actualValue;
        this.expected = expectedPercentage;
        this.tolerance = tolerance;
        this.requiredCondition = requiredCondition;
        this.assertableMultiTest = assertableMultiTest;
    }

    public String getTestName() {
        return testName;
    }

    public Measure getActualValue() {
        return actualValue;
    }

    public double getExpected() {
        return expected;
    }

    public double getTolerance() {
        return tolerance;
    }

    public EqualityCondition getRequiredCondition() {
        return requiredCondition;
    }

    @Override
    protected boolean checkWithTolerance(EqualityCondition eq, double t) {
        return AssertValueCondition.comply(actualValue, expected, t, eq);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 41 * hash + Objects.hashCode(this.executionTestName);
        hash = 41 * hash + Objects.hashCode(this.testName);
        hash = 41 * hash + Objects.hashCode(this.actualValue);
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
        final ValueAssertionError other = (ValueAssertionError) obj;
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
        if (!Objects.equals(this.actualValue, other.actualValue)) {
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
                .append(expected)
                .append(", found ")
                .append(actualValue.toStringForConfidence(tolerance))
                .append(" with a tolerance of ")
                .append(tolerance)
                .append(" %")
                .append(System.lineSeparator());
                wouldBeIfTolerance(buf);
                buf.append(assertableMultiTest.toString());
        return buf.toString();
    }

}
