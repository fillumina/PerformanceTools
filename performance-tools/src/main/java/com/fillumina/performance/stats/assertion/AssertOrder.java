package com.fillumina.performance.stats.assertion;

import com.fillumina.performance.infrastructure.PerformanceAssertion;
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

    public PerformanceStatsAssertion sameAs(final String other) {
        return assertPerformance.addCondition(
                new AssertOrderCondition(OrderCondition.SAME,
                        name,
                        other,
                        assertPerformance.getTolerancePercentage()));
    }

    public PerformanceStatsAssertion slowerThan(final String other) {
        return assertPerformance.addCondition(
                new AssertOrderCondition(OrderCondition.SLOWER,
                        name,
                        other,
                        assertPerformance.getTolerancePercentage()));
    }

    public PerformanceStatsAssertion fasterThan(final String other) {
        return assertPerformance.addCondition(
                new AssertOrderCondition(OrderCondition.FASTER,
                        name,
                        other,
                        assertPerformance.getTolerancePercentage()));
    }

    static class AssertOrderCondition
            implements PerformanceAssertion<PerformanceStats>,
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
        public void check(PerformanceStats stats) {
            consume(null, stats);
        }

        @Override
        public void consume(final ComposedName message,
                final PerformanceStats stats) {
            if (stats != null) {
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
        }

        static boolean comply(Measure a,
                Measure b,
                final double tolerance,
                OrderCondition condition)
                throws OrderAssertionError {
            double confidence = (100.0 - tolerance) / 100.0;
            ConfidenceInterval aci = a.getConfidenceInterval(confidence);
            double aLower = aci.getLowerBound();
            double aUpper = aci.getUpperBound();
            ConfidenceInterval bci = b.getConfidenceInterval(confidence);
            double bLower = bci.getLowerBound();
            double bUpper = bci.getUpperBound();
            ConfidenceOrder co = new ConfidenceOrder(tolerance);
            switch (condition) {
                case SAME:
                    return (co.gt(bLower, aLower) && co.lt(bUpper, aUpper)) ||
                            (co.gt(bLower, aLower) && co.lt(bLower, aUpper)) ||
                            (co.gt(bUpper, aLower) && co.lt(bUpper, aUpper)) ||
                            (co.lt(bLower, aLower) && co.gt(bUpper, aUpper));

                case SLOWER: // bUpper < aLower
                    return co.lt(bUpper, aLower);

                case FASTER: // aUpper < bLower
                    return co.lt(aUpper, bLower);
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

    static class ConfidenceOrder {
        private final double confidence;

        public ConfidenceOrder(double tolerance) {
            this.confidence = (100.0 + tolerance) / 100.0;
        }

        public boolean lt(double a, double b) {
            return (a / b) <= confidence;
        }

        public boolean gt(double a, double b) {
            return (b / a) <= confidence;
        }
    }
}
