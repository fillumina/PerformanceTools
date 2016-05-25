package com.fillumina.performance.stats.assertion;

import com.fillumina.performance.infrastructure.PerformanceConsumer;
import com.fillumina.performance.infrastructure.PerformanceFormatter;
import com.fillumina.performance.stats.PerformanceStats;
import com.fillumina.performance.util.ComposedName;
import com.fillumina.performance.util.stats.ConfidenceInterval;
import com.fillumina.performance.util.stats.Measure;
import java.io.Serializable;

/**
 *
 * @author Francesco Illuminati
 */
public class AssertOrder implements Serializable {
    private static final long serialVersionUID = 1L;

    private final AssertPerformance assertPerformance;
    private final String name;

    public AssertOrder(final AssertPerformance assertPerformance,
            final String name) {
        this.assertPerformance = assertPerformance;
        this.name = name;
    }

    public PerformanceAssertion sameAs(final String other) {
        return assertPerformance.addCondition(
                new AssertOrderCondition(OrderCondition.SAME,
                        name,
                        other,
                        assertPerformance.getTolerancePercentage()));
    }

    public PerformanceAssertion slowerThan(final String other) {
        return assertPerformance.addCondition(
                new AssertOrderCondition(OrderCondition.SLOWER,
                        name,
                        other,
                        assertPerformance.getTolerancePercentage()));
    }

    public PerformanceAssertion fasterThan(final String other) {
        return assertPerformance.addCondition(
                new AssertOrderCondition(OrderCondition.FASTER,
                        name,
                        other,
                        assertPerformance.getTolerancePercentage()));
    }

    static class AssertOrderCondition
            implements PerformanceConsumer<PerformanceStats>,
                PerformanceFormatter<PerformanceStats>,
                Serializable {
        private static final long serialVersionUID = 1L;

        private final OrderCondition condition;
        private final String firstTestName;
        private final String secondTestName;
        private final double tolerance;

        public AssertOrderCondition(final OrderCondition condition,
                final String firstTestName,
                final String secondTestName,
                final double tolerance) {
            this.condition = condition;
            this.firstTestName = firstTestName;
            this.secondTestName = secondTestName;
            this.tolerance = tolerance;
        }

        @Override
        public void consume(final ComposedName message,
                final PerformanceStats stats) {
            if (stats != null) {
                check(message, stats);
            }
        }

        private void check(ComposedName message, PerformanceStats stats) {
            Measure firstMeasure = stats.getPerformance(firstTestName);
            Measure secondMeasure = stats.getPerformance(secondTestName);
            if (!comply(firstMeasure, secondMeasure, tolerance, condition)) {
                throw new OrderAssertionError(
                        message,
                        firstTestName,
                        firstMeasure,
                        secondTestName,
                        secondMeasure,
                        tolerance,
                        condition,
                        stats);
            }
        }

        static boolean comply(Measure a,
                Measure b,
                final double tolerance,
                OrderCondition condition)
                throws OrderAssertionError {
            double confidence = (100.0 - tolerance) / 100.0;
            double factor = 1.0 + (tolerance / 100.0);
            ConfidenceInterval aci = a.getConfidenceInterval(confidence);
            double aLower = aci.getLowerBound() * confidence;
            double aUpper = aci.getUpperBound() * factor;
            ConfidenceInterval bci = b.getConfidenceInterval(confidence);
            double bLower = bci.getLowerBound();
            double bUpper = bci.getUpperBound();
            switch (condition) {
                case SAME:
                    return (bLower > aLower && bUpper < aUpper) ||
                            (bLower > aLower && bLower < aUpper) ||
                            (bUpper > aLower && bUpper < aUpper) ||
                            (bLower < aLower && bUpper > aUpper);
                case SLOWER:
                    return bUpper < aLower;

                case FASTER:
                    return aUpper < bLower;
            }
            throw new AssertionError("condition not managed: " + condition);
        }

        @Override
        public String toString(ComposedName testName, PerformanceStats stats) {
            StringBuilder buf = new StringBuilder();
            if (testName != null) {
                buf.append(testName).append(System.lineSeparator());
            }
            Measure firstMeasure = stats.getPerformance(firstTestName);
            Measure secondMeasure = stats.getPerformance(secondTestName);
            buf
                    .append('\'').append(firstTestName)
                    .append("' (").append(firstMeasure).append(" ns) ")
                    .append(" is ").append(condition.getMessage())
                    .append(' ')
                    .append('\'').append(secondTestName)
                    .append("' (").append(secondMeasure).append(" ns) ")
                    .append(" with a tolerance of ")
                    .append(tolerance).append(" %")
                    .append(System.lineSeparator());
            return buf.toString();
        }

        @Override
        public String toString(PerformanceStats performance) {
            return toString(null, performance);
        }
    }
}
