package com.fillumina.performance.assertion;

import com.fillumina.performance.util.StaticPath;
import com.fillumina.performance.util.stats.MeasureRatio;
import com.fillumina.performance.util.stats.Ratio;
import java.util.Objects;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class PercentageAssertionError extends AbstractAssertionError {
    private static final long serialVersionUID = 1L;
    private final StaticPath executionTestName;
    private final String testName;
    private final MeasureRatio ratio;
    private final Ratio expected;
    private final Ratio tolerance;
    private final OrderCondition requiredCondition;
    private final Assertable assertableMultiTest;

    public PercentageAssertionError(StaticPath executionTestName,
            String testName,
            MeasureRatio actualPercentage,
            Ratio expectedPercentage,
            Ratio tolerance,
            OrderCondition requiredCondition,
            Assertable assertableMultiTest) {
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

    public Ratio getExpected() {
        return expected;
    }

    public Ratio getTolerance() {
        return tolerance;
    }

    public OrderCondition getRequiredCondition() {
        return requiredCondition;
    }

    @Override
    protected boolean checkWithTolerance(OrderCondition eq, Ratio t) {
        return AssertPercentageCondition.comply(ratio, expected, t, eq);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 41 * hash + Objects.hashCode(this.executionTestName);
        hash = 41 * hash + Objects.hashCode(this.testName);
        hash = 41 * hash + Objects.hashCode(this.ratio);
        hash = 41 * hash + Objects.hashCode(this.expected);
        hash = 41 * hash + Objects.hashCode(this.tolerance);
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
        if (!Objects.equals(this.expected, other.expected)) {
            return false;
        }
        if (!Objects.equals(this.tolerance, other.tolerance)) {
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
                .append(expected)
                .append(", found ")
                .append(ratio.toStringAsPercentage())
                .append(" with a tolerance of ")
                .append(tolerance)
                .append(System.lineSeparator());
                wouldBeIfTolerance(buf);
                buf.append(assertableMultiTest.toString());
        return buf.toString();
    }

}
