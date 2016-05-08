package com.fillumina.performance.stats.assertion;

import static com.fillumina.performance.util.FormatterUtils.formatPercentage;
import com.fillumina.performance.util.stats.MeasureRatio;
import java.util.Objects;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class PercentageAssertionError extends AssertionError {
    private static final long serialVersionUID = 1L;
    private final String message;
    private final String testName;
    private final MeasureRatio ratio;
    private final float expected;
    private final double tolerance;
    private final PercentageCondition requiredCondition;

    public PercentageAssertionError(String message,
            String testName,
            MeasureRatio actualPercentage,
            float expectedPercentage,
            double tolerance,
            PercentageCondition requiredCondition) {
        this.message = message;
        this.testName = testName;
        this.ratio = actualPercentage;
        this.expected = expectedPercentage;
        this.tolerance = tolerance;
        this.requiredCondition = requiredCondition;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public String getTestName() {
        return testName;
    }

    public MeasureRatio getRatio() {
        return ratio;
    }

    public float getExpected() {
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
        hash = 47 * hash + Objects.hashCode(this.testName);
        hash = 47 * hash + Objects.hashCode(this.ratio);
        hash = 47 * hash + Float.floatToIntBits(this.expected);
        hash
                = 47 * hash +
                (int) (Double.doubleToLongBits(this.tolerance) ^
                (Double.doubleToLongBits(this.tolerance) >>> 32));
        hash = 47 * hash + Objects.hashCode(this.requiredCondition);
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
        if (Float.floatToIntBits(this.expected) !=
                Float.floatToIntBits(other.expected)) {
            return false;
        }
        if (Double.doubleToLongBits(this.tolerance) !=
                Double.doubleToLongBits(other.tolerance)) {
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
        return true;
    }

    @Override
    public String getMessage() {
        StringBuilder buf = new StringBuilder();
        if (message != null) {
            buf.append(message).append(": ");
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
                .append(" %");
        return buf.toString();
    }

}
