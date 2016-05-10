package com.fillumina.performance.stats.assertion;

import com.fillumina.performance.stats.PerformanceStats;
import com.fillumina.performance.stats.PerformanceStatsConsumer;
import com.fillumina.performance.util.StringHelper;
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
    private final String prefix;
    private final String name;

    public AssertOrder(final AssertPerformance assertPerformance,
            final String prefix, final String name) {
        this.assertPerformance = assertPerformance;
        this.prefix = prefix;
        this.name = name;
    }

    public PerformanceAssertion sameAs(final String other) {
        return assertPerformance.addCondition(
                new AssertOrderCondition(OrderCondition.SAME,
                        prefix + name,
                        prefix + other,
                        assertPerformance.getTolerancePercentage()));
    }

    public PerformanceAssertion slowerThan(final String other) {
        return assertPerformance.addCondition(
                new AssertOrderCondition(OrderCondition.SLOWER,
                        prefix + name,
                        prefix + other,
                        assertPerformance.getTolerancePercentage()));
    }

    public PerformanceAssertion fasterThan(final String other) {
        return assertPerformance.addCondition(
                new AssertOrderCondition(OrderCondition.FASTER,
                        prefix + name,
                        prefix + other,
                        assertPerformance.getTolerancePercentage()));
    }

    static class AssertOrderCondition
            implements PerformanceStatsConsumer, Serializable {
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
        public void consume(final String message, final PerformanceStats stats) {
            if (stats != null) {
                check(message, stats);
            }
        }

        private void check(String message, PerformanceStats stats) {
            Measure firstMeasure = stats.getPerformance(firstTestName);
            Measure secondMeasure = stats.getPerformance(secondTestName);
            if (!comply(firstMeasure, secondMeasure, tolerance, condition)) {
                throw new OrderAssertionError(
                        StringHelper.emptyOnNull(message),
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
            double aLower = aci.getLowerBound();
            double aUpper = aci.getUpperBound();
            ConfidenceInterval bci = b.getConfidenceInterval(confidence);
            double bLower = bci.getLowerBound();
            double bUpper = bci.getUpperBound();
            switch (condition) {
                case SAME:
                    return !(bUpper * factor < aLower) &&
                            !(aUpper * factor < bLower);
                case SLOWER:
                    return bUpper * factor < aLower;

                case FASTER:
                    return aUpper * factor < bLower;
            }
            throw new AssertionError("condition not managed: " + condition);
        }
    }
}
