package com.fillumina.performance.consumer.assertion;

import com.fillumina.performance.consumer.PerformanceConsumer;
import com.fillumina.performance.producer.timer.LoopPerformances;
import static com.fillumina.performance.utils.FormatterUtils.*;
import java.io.Serializable;

/**
 *
 * @author fra
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

    public AssertPerformance equals(final float expectedPercentage) {
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
                        checkEquals();
                        break;

                    case GREATER:
                        checkGreater();
                        break;

                    case LESS:
                        checkLess();
                        break;
                }
            }

            private void checkEquals() {
                checkLess();
                checkGreater();
            }

            private void checkGreater() throws AssertionError {
                if (actualPercentage < expectedPercentage - tolerance) {
                    throwAssertException(actualPercentage, "greater");
                }
            }

            private void checkLess() throws AssertionError {
                if (actualPercentage > expectedPercentage + tolerance) {
                    throwAssertException(actualPercentage, "lesser");
                }
            }

            private void throwAssertException(final float actualPercentage,
                    final String errorMessage) {
                throw new AssertionError(message +
                        " '" + name + "' expected " + errorMessage + " than : " +
                        formatPercentage(expectedPercentage) +
                        ", found : " + formatPercentage(actualPercentage) +
                        " with tolerance of " +
                        assertPerformance.getTolerancePercentage() + " %");
            }
        }
    }
}
