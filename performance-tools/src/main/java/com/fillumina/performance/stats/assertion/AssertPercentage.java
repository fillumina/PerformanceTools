package com.fillumina.performance.stats.assertion;

import com.fillumina.performance.stats.PerformanceStats;
import com.fillumina.performance.stats.PerformanceStatsConsumer;
import com.fillumina.performance.stats.TestPerformances;
import static com.fillumina.performance.util.FormatterUtils.*;
import com.fillumina.performance.util.StringHelper;
import com.fillumina.performance.util.stats.MeasureRatio;
import java.io.Serializable;

/**
 *
 * @author Francesco Illuminati
 */
public class AssertPercentage implements Serializable {
    private static final long serialVersionUID = 1L;

    private static enum Condition { EQUALS, LESS, GREATER}

    private final AssertPerformance assertPerformance;
    private final String name;

    public AssertPercentage(final AssertPerformance assertPerformance,
            final String prefix, final String name) {
        this.assertPerformance = assertPerformance;
        this.name = prefix + name;
    }

    /**
     * <i>NOTE: The old name equalsTo() was too prone to be mistaken with
     * equals().</i>
     */
    public PerformanceAssertion sameAs(final float expectedPercentage) {
        return assertPerformance.addCondition(new AssertPercentageCondition(
                Condition.EQUALS, expectedPercentage));
    }

    public PerformanceAssertion lessThan(final float expectedPercentage) {
        return assertPerformance.addCondition(new AssertPercentageCondition(
                Condition.LESS, expectedPercentage));
    }

    public PerformanceAssertion greaterThan(final float expectedPercentage) {
        return assertPerformance.addCondition(new AssertPercentageCondition(
                Condition.GREATER, expectedPercentage));
    }

    private class AssertPercentageCondition
            implements PerformanceStatsConsumer, Serializable {
        private static final long serialVersionUID = 1L;

        private final Condition condition;
        private final float expectedPercentage;

        public AssertPercentageCondition(final Condition condition,
                final float expectedPercentage) {
            this.condition = condition;
            this.expectedPercentage = expectedPercentage;
        }

        @Override
        public void consume(final String message,
                final PerformanceStats stats) {
            if (stats != null) {
                new AssertPercentageChecker(message, stats).check();
            }
        }

        private class AssertPercentageChecker implements Serializable {
            private static final long serialVersionUID = 1L;

            private final String message;
            private final MeasureRatio actualPercentage;
            private final double tolerance;

            public AssertPercentageChecker(final String message,
                    final PerformanceStats stats) {
                this.message = message;
                final TestPerformances testPerformances
                        = stats.getTestPerformances().get(name);
                if (testPerformances == null) {
                    throw new IllegalStateException(
                            "Test '" + name + "' not found.");
                }
                this.actualPercentage = testPerformances.getPercentage();
                this.tolerance = assertPerformance.getTolerancePercentage();
            }

            public void check() {
                switch (condition) {
                    case EQUALS:
                        checkSameAs();
                        break;

                    case GREATER:
                        checkGreater();
                        break;

                    case LESS:
                        checkLess();
                        break;
                }
            }

            private void checkSameAs() {
                try {
                    checkGreater();
                    checkLess();
                } catch (AssertionError e) {
                    throwAssertException(actualPercentage, "equals to ");
                }
            }

            private void checkGreater() throws AssertionError {
                if (actualPercentage.getUpperBound() * 100.0 <
                        expectedPercentage - tolerance) {
                    throwAssertException(actualPercentage, "greater than ");
                }
            }

            private void checkLess() throws AssertionError {
                if (actualPercentage.getLowerBound() * 100.0 >
                        expectedPercentage + tolerance) {
                    throwAssertException(actualPercentage, "lesser than ");
                }
            }

            private void throwAssertException(
                    final MeasureRatio actualPercentage,
                    final String errorMessage) {
                throw new AssertionError(StringHelper.emptyOnNull(message) +
                        " '" + name + "' expected " + errorMessage +
                        formatPercentage(expectedPercentage) +
                        ", found " + actualPercentage.toStringAsPercentage() +
                        " with a tolerance of " +
                    assertPerformance.getTolerancePercentage() + " %");
            }
        }
    }
}
