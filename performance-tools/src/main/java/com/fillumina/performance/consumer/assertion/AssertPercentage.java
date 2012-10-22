package com.fillumina.performance.consumer.assertion;

import com.fillumina.performance.consumer.PerformanceConsumer;
import com.fillumina.performance.producer.LoopPerformances;
import static com.fillumina.performance.util.FormatterUtils.*;
import com.fillumina.performance.util.StringHelper;
import java.io.Serializable;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class AssertPercentage implements Serializable {
    private static final long serialVersionUID = 1L;

    private static enum Condition { EQUALS, LESS, GREATER}

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
    public AssertPerformance sameAs(final float expectedPercentage) {
        return assertPerformance.addCondition(new AssertPercentageCondition(
                Condition.EQUALS, expectedPercentage));
    }

    public AssertPerformance lessThan(final float expectedPercentage) {
        return assertPerformance.addCondition(new AssertPercentageCondition(
                Condition.LESS, expectedPercentage));
    }

    public AssertPerformance greaterThan(final float expectedPercentage) {
        return assertPerformance.addCondition(new AssertPercentageCondition(
                Condition.GREATER, expectedPercentage));
    }

    private class AssertPercentageCondition
            implements PerformanceConsumer, Serializable {
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
                final LoopPerformances loopPerformances) {
            if (loopPerformances != null) {
                new AssertPercentageChecker(message, loopPerformances).check();
            }
        }

        private class AssertPercentageChecker implements Serializable {
            private static final long serialVersionUID = 1L;

            private final String message;
            private final float actualPercentage;
            private final float tolerance;

            public AssertPercentageChecker(final String message,
                    final LoopPerformances loopPerformances) {
                this.message = message;
                this.actualPercentage = loopPerformances.getPercentageFor(name);
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
                if (actualPercentage < expectedPercentage - tolerance) {
                    throwAssertException(actualPercentage, "greater than ");
                }
            }

            private void checkLess() throws AssertionError {
                if (actualPercentage > expectedPercentage + tolerance) {
                    throwAssertException(actualPercentage, "lesser than ");
                }
            }

            private void throwAssertException(final float actualPercentage,
                    final String errorMessage) {
                throw new AssertionError(StringHelper.emptyOnNull(message) +
                        " '" + name + "' expected " + errorMessage +
                        formatPercentage(expectedPercentage) +
                        ", found " + formatPercentage(actualPercentage) +
                        " with a tolerance of " +
                    assertPerformance.getTolerancePercentage() + " %");
            }
        }
    }
}
