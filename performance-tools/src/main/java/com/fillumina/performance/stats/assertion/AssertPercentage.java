package com.fillumina.performance.stats.assertion;

import com.fillumina.performance.infrastructure.PerformanceAssertion;
import com.fillumina.performance.stats.PerformanceStats;
import com.fillumina.performance.stats.TestPerformance;
import com.fillumina.performance.util.ComposedName;
import static com.fillumina.performance.util.FormatterUtils.formatPercentage;
import com.fillumina.performance.util.stats.MeasureRatio;
import java.io.Serializable;

/**
 *
 * @author Francesco Illuminati
 */
public class AssertPercentage implements Serializable {
    private static final long serialVersionUID = 1L;

    private final AssertPerformance assertPerformance;
    private final String name;

    public AssertPercentage(final AssertPerformance assertPerformance,
            final String name) {
        this.assertPerformance = assertPerformance;
        this.name = name;
    }

    /**
     * <i>NOTE: The old name equalsTo() was too prone to be mistaken with
     * equals().</i>
     */
    public PerformanceStatsAssertion sameAs(final double expectedPercentage) {
        return assertPerformance.addCondition(
                new AssertPercentageCondition(name,
                        PercentageCondition.EQUALS,
                        expectedPercentage,
                        assertPerformance.getTolerancePercentage()));
    }

    public PerformanceStatsAssertion lessThan(final double expectedPercentage) {
        return assertPerformance.addCondition(
                new AssertPercentageCondition(name,
                        PercentageCondition.LESS,
                        expectedPercentage,
                        assertPerformance.getTolerancePercentage()));
    }

    public PerformanceStatsAssertion greaterThan(final double expectedPercentage) {
        return assertPerformance.addCondition(
                new AssertPercentageCondition(name,
                        PercentageCondition.GREATER,
                        expectedPercentage,
                        assertPerformance.getTolerancePercentage()));
    }

    static class AssertPercentageCondition
            implements PerformanceAssertion<PerformanceStats>,
                Serializable {
        private static final long serialVersionUID = 1L;

        private final String testName;
        private final double expectedPercentage;
        private final double tolerance;
        private final PercentageCondition condition;

        public AssertPercentageCondition(
                final String testName,
                final PercentageCondition condition,
                final double expectedPercentage,
                final double tolerance) {
            this.testName = testName;
            this.condition = condition;
            this.expectedPercentage = expectedPercentage;
            this.tolerance = tolerance;
        }

        @Override
        public void check(PerformanceStats performance) {
            consume(null, performance);
        }

        @Override
        public void consume(final ComposedName name,
                final PerformanceStats stats) {
            if (stats != null) {
                check(name, stats, tolerance);
            }
        }

        public void check(final ComposedName name,
                final PerformanceStats stats,
                final double tolerance) {
            final TestPerformance testPerformances =
                    stats.getTestPerformances().get(testName);
            if (testPerformances == null) {
                throw new IllegalStateException(
                        "Test '" + testName + "' not found.");
            }
            MeasureRatio actualPercentage = testPerformances.getPercentage();
            if (!comply(actualPercentage, expectedPercentage, tolerance, condition)) {
                throw new PercentageAssertionError(
                        name,
                        testName,
                        actualPercentage,
                        expectedPercentage,
                        tolerance,
                        condition,
                        stats
                    );
            }
        }

        public static boolean comply(MeasureRatio actualPercentage,
                double expectedPercentage,
                double tolerance,
                PercentageCondition condition) {
            switch (condition) {
                case EQUALS: return checkSameAs(
                        actualPercentage, expectedPercentage, tolerance);

                case GREATER: return checkGreater(
                        actualPercentage, expectedPercentage, tolerance);

                case LESS: return checkLess(
                        actualPercentage, expectedPercentage, tolerance);
            }
            throw new AssertionError("condition not managed: " + condition);
        }

        private static boolean checkSameAs(MeasureRatio actualPercentage,
                double expectedPercentage,
                double tolerance) {
            final boolean greater =
                    checkGreater(actualPercentage, expectedPercentage, tolerance);
            final boolean lesser =
                    checkLess(actualPercentage, expectedPercentage, tolerance);
            return !(greater ^ lesser);
        }

        private static boolean checkGreater(MeasureRatio actualPercentage,
                double expectedPercentage,
                double tolerance) {
            return actualPercentage.getUpperBound() * 100.0 >
                    expectedPercentage - tolerance;
        }

        private static boolean checkLess(MeasureRatio actualPercentage,
                double expectedPercentage,
                double tolerance) {
            return actualPercentage.getLowerBound() * 100.0 <
                    expectedPercentage + tolerance;
        }

        @Override
        public String toString(PerformanceStats performance) {
            return toString(null, performance);
        }

        @Override
        public String toString(ComposedName name, PerformanceStats stats) {
            StringBuilder buf = new StringBuilder();
            if (name != null) {
                buf.append(name).append(":\n");
            }
            buf.append('\'').append(testName).append('\'')
                    .append(" is ")
                    .append(condition.getMessage())
                    .append(' ')
                    .append(formatPercentage(expectedPercentage))
                    .append(" with a tolerance of ")
                    .append(tolerance)
                    .append(" %");
            return buf.toString();
        }
    }
}
