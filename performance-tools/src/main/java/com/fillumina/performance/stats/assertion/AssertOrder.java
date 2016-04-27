package com.fillumina.performance.stats.assertion;

import com.fillumina.performance.stats.PerformanceStats;
import com.fillumina.performance.stats.PerformanceStatsConsumer;
import com.fillumina.performance.util.StringHelper;
import com.fillumina.performance.util.stats.OnlineMeasure;
import com.fillumina.performance.util.stats.MeasureComparator;
import java.io.Serializable;

/**
 *
 * @author Francesco Illuminati
 */
public class AssertOrder implements Serializable {
    private static final long serialVersionUID = 1L;
    private static enum Condition { EQUALS, FASTER, SLOWER}

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
                new AssertOrderCondition(Condition.EQUALS, other));
    }

    public PerformanceAssertion slowerThan(final String other) {
        return assertPerformance.addCondition(
                new AssertOrderCondition(Condition.SLOWER, other));
    }

    public PerformanceAssertion fasterThan(final String other) {
        return assertPerformance.addCondition(
                new AssertOrderCondition(Condition.FASTER, other));
    }

    private class AssertOrderCondition
            implements PerformanceStatsConsumer, Serializable {
        private static final long serialVersionUID = 1L;

        private final Condition condition;
        private final String other;

        public AssertOrderCondition(final Condition condition,
                final String other) {
            this.condition = condition;
            this.other = other;
        }

        @Override
        public void consume(final String message, final PerformanceStats stats) {
            if (stats != null) {
                new AssertOrderChecker(message, stats).check();
            }
        }

        private class AssertOrderChecker {
            private final String message;
            private final OnlineMeasure actualMeasure;
            private final OnlineMeasure otherMeasure;
            private final double tolerance;

            public AssertOrderChecker(String message, PerformanceStats stats) {
                this.message = message;
                this.actualMeasure = getPerformance(stats, prefix + name);
                this.otherMeasure = getPerformance(stats, prefix + other);
                this.tolerance = assertPerformance.getTolerancePercentage();
            }

            private OnlineMeasure getPerformance(PerformanceStats stats,
                    String testName)
                    throws IllegalStateException {
                try {
                    return stats.getTestPerformances()
                            .get(testName).getElapsedNanosecondsPerCycle();
                } catch (NullPointerException e) {
                    throw new IllegalStateException(
                            "Test '" + testName +
                            "' not found, valid tests are: " +
                            stats.getTestPerformances().keySet().toString(), e);
                }
            }

            public void check() {
                double confidence = 1 - tolerance / 100.0;
                int compare = new MeasureComparator(confidence)
                        .compare(actualMeasure, otherMeasure);
                switch (condition) {
                    case EQUALS:
                        if (compare != 0) {
                            throwAssertException(actualMeasure, otherMeasure,
                                    "not equals to");
                        }
                        break;

                    case SLOWER:
                        if (compare == -1) {
                            throwAssertException(actualMeasure, otherMeasure,
                                    "faster than");
                        }
                        break;

                    case FASTER:
                        if (compare == 1) {
                            throwAssertException(actualMeasure, otherMeasure,
                                    "slower than");
                        }
                        break;
                }
            }

            private void throwAssertException(final OnlineMeasure actualPercentage,
                    final OnlineMeasure otherPercentage,
                    final String errorMessage) {
                throw new AssertionError(StringHelper.emptyOnNull(message) +
                        " '" + prefix + name + "' (" +
                        actualPercentage.toString() +
                        " ns) was " + errorMessage + " '" + prefix + other +
                        "' (" + otherPercentage.toString() + " ns)" +
                        " with a tolerance of " +
                        assertPerformance.getTolerancePercentage() + " %");
            }
        }
    }
}
