package com.fillumina.performance.assertion;

import com.fillumina.performance.util.StaticPath;
import com.fillumina.performance.util.stats.Measure;
import com.fillumina.performance.util.stats.Ratio;
import java.util.Objects;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class OrderAssertionError extends AbstractAssertionError {
    private static final long serialVersionUID = 1L;
    private final StaticPath testName;
    private final String firstTestName;
    private final Measure firstMeasure;
    private final String secondTestName;
    private final Measure secondMeasure;
    private final Ratio tolerance;
    private final OrderCondition requiredCondition;
    private final Assertable assertableMultiTest;

    public OrderAssertionError(
            StaticPath testName,
            String firstTestName,
            Measure first,
            String secondTestName,
            Measure second,
            Ratio tolerance,
            OrderCondition requiredCondition,
            Assertable assertableMultiTest) {
        this.testName = testName;
        this.firstTestName = firstTestName;
        this.firstMeasure = first;
        this.secondTestName = secondTestName;
        this.secondMeasure = second;
        this.tolerance = tolerance;
        this.requiredCondition = requiredCondition;
        this.assertableMultiTest = assertableMultiTest;
    }

    @Override
    public String getMessage() {
        if (testName == null) {
            return super.getMessage();
        }
        return testName.toString();
    }

    public String getFirstTestName() {
        return firstTestName;
    }

    public Measure getFirstMeasure() {
        return firstMeasure;
    }

    public String getSecondTestName() {
        return secondTestName;
    }

    public Measure getSecondMeasure() {
        return secondMeasure;
    }

    public Ratio getTolerance() {
        return tolerance;
    }

    public OrderCondition getRequiredCondition() {
        return requiredCondition;
    }

    @Override
    protected boolean checkWithTolerance(OrderCondition eq, Ratio t) {
        return AssertOrderCondition.comply(firstMeasure, secondMeasure, t, eq);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 43 * hash + Objects.hashCode(this.testName);
        hash = 43 * hash + Objects.hashCode(this.firstTestName);
        hash = 43 * hash + Objects.hashCode(this.firstMeasure);
        hash = 43 * hash + Objects.hashCode(this.secondTestName);
        hash = 43 * hash + Objects.hashCode(this.secondMeasure);
        hash = 43 * hash + Objects.hashCode(this.tolerance);
        hash = 43 * hash + Objects.hashCode(this.requiredCondition);
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
        final OrderAssertionError other = (OrderAssertionError) obj;
        if (!Objects.equals(this.tolerance, other.tolerance)) {
            return false;
        }
        if (!Objects.equals(this.testName, other.testName)) {
            return false;
        }
        if (!Objects.equals(this.firstTestName, other.firstTestName)) {
            return false;
        }
        if (!Objects.equals(this.secondTestName, other.secondTestName)) {
            return false;
        }
        if (!Objects.equals(this.firstMeasure, other.firstMeasure)) {
            return false;
        }
        if (!Objects.equals(this.secondMeasure, other.secondMeasure)) {
            return false;
        }
        if (this.requiredCondition != other.requiredCondition) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder();
        if (testName != null && !testName.isEmpty()) {
            buf.append(testName).append(": ");
        }
        buf.append('\'').append(firstTestName)
                .append("' (").append(firstMeasure).append(") ")
                .append("expected ").append(requiredCondition.getMessage())
                .append(' ')
                .append('\'').append(secondTestName)
                .append("' (").append(secondMeasure).append(") ")
                .append(" with a tolerance of ")
                .append(tolerance)
                .append(System.lineSeparator());
                wouldBeIfTolerance(buf);
                buf.append(assertableMultiTest.toString());
        return buf.toString();
    }
}
