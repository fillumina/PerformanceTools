package com.fillumina.performance.stats.assertion;

import com.fillumina.performance.stats.PerformanceStats;
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
    private final ComposedName message;
    private final String testName;
    private final MeasureRatio ratio;
    private final double expected;
    private final double tolerance;
    private final PercentageCondition requiredCondition;
    private final PerformanceStats stats;

    public PercentageAssertionError(ComposedName message,
            String testName,
            MeasureRatio actualPercentage,
            double expectedPercentage,
            double tolerance,
            PercentageCondition requiredCondition,
            PerformanceStats stats) {
        this.message = message;
        this.testName = testName;
        this.ratio = actualPercentage;
        this.expected = expectedPercentage;
        this.tolerance = tolerance;
        this.requiredCondition = requiredCondition;
        this.stats = stats;
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
        hash = 41 * hash + Objects.hashCode(this.message);
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
        hash = 41 * hash + Objects.hashCode(this.stats);
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
        if (!Objects.equals(this.message, other.message)) {
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
        if (!Objects.equals(this.stats, other.stats)) {
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
                .append(" %")
                .append(System.lineSeparator())
                .append(stats.toString());
        return buf.toString();
    }

}
