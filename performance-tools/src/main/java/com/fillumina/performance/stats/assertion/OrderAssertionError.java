package com.fillumina.performance.stats.assertion;

import com.fillumina.performance.util.stats.Measure;
import java.util.Objects;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class OrderAssertionError extends AssertionError {
    private static final long serialVersionUID = 1L;
    private final String message;
    private final String firstTestName;
    private final Measure firstMeasure;
    private final String secondTestName;
    private final Measure secondMeasure;
    private final double tolerance;
    private final OrderCondition requiredCondition;

    public OrderAssertionError(
            String message,
            String firstTestName,
            Measure first,
            String secondTestName,
            Measure second,
            double tolerance,
            OrderCondition requiredCondition) {
        this.message = message;
        this.firstTestName = firstTestName;
        this.firstMeasure = first;
        this.secondTestName = secondTestName;
        this.secondMeasure = second;
        this.tolerance = tolerance;
        this.requiredCondition = requiredCondition;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public String getMessage() {
        return message;
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

    public double getTolerance() {
        return tolerance;
    }

    public OrderCondition getRequiredCondition() {
        return requiredCondition;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 43 * hash + Objects.hashCode(this.message);
        hash = 43 * hash + Objects.hashCode(this.firstTestName);
        hash = 43 * hash + Objects.hashCode(this.firstMeasure);
        hash = 43 * hash + Objects.hashCode(this.secondTestName);
        hash = 43 * hash + Objects.hashCode(this.secondMeasure);
        hash
                = 43 * hash +
                (int) (Double.doubleToLongBits(this.tolerance) ^
                (Double.doubleToLongBits(this.tolerance) >>> 32));
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
        if (Double.doubleToLongBits(this.tolerance) !=
                Double.doubleToLongBits(other.tolerance)) {
            return false;
        }
        if (!Objects.equals(this.message, other.message)) {
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
        if (message != null) {
            buf.append(message).append(": ");
        }
        buf.append(firstTestName)
                .append(" (").append(firstMeasure).append(" ns) ")
                .append("expected ").append(requiredCondition.getMessage())
                .append(' ')
                .append(secondTestName)
                .append("' (").append(secondMeasure).append(" ns) ")
                .append(" with a tolerance of ")
                .append(tolerance);
        return buf.toString();
    }
}
