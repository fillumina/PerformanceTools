package com.fillumina.performance.consumer.assertion;

import com.fillumina.performance.consumer.PerformanceConsumer;
import com.fillumina.performance.producer.timer.LoopPerformances;
import static com.fillumina.performance.util.FormatterUtils.*;
import java.io.Serializable;

/**
 *
 * @author fra
 */
public class AssertOrder implements Serializable {
    private static final long serialVersionUID = 1L;

    private static enum Condition { EQUALS, FASTER, SLOWER}

    private final AssertPerformance assertPerformance;
    private final String name;

    public AssertOrder(final AssertPerformance assertPerformance,
            final String name) {
        this.assertPerformance = assertPerformance;
        this.name = name;
    }

    public AssertPerformance equalsTo(final String other) {
        return assertPerformance.addCondition(new AssertOrderCondition(
                Condition.EQUALS, other));
    }

    public AssertPerformance slowerThan(final String other) {
        return assertPerformance.addCondition(new AssertOrderCondition(
                Condition.SLOWER, other));
    }

    public AssertPerformance fasterThan(final String other) {
        return assertPerformance.addCondition(new AssertOrderCondition(
                Condition.FASTER, other));
    }

    private class AssertOrderCondition
            implements PerformanceConsumer, Serializable {
        private static final long serialVersionUID = 1L;

        private final Condition condition;
        private final String other;

        public AssertOrderCondition(final Condition condition,
                final String other) {
            this.condition = condition;
            this.other = other;
        }

        @Override
        public void consume(final String message,
                final LoopPerformances loopPerformances) {
            if (loopPerformances != null) {
                new AssertOrderChecker(message, loopPerformances).check();
            }
        }

        private class AssertOrderChecker {
            private final String message;
            private final float actualPercentage;
            private final float otherPercentage;
            private final float tolerance;

            public AssertOrderChecker(String message,
                    LoopPerformances loopPerformances) {
                this.message = message;
                this.actualPercentage = loopPerformances.getPercentageFor(name);
                this.otherPercentage = loopPerformances.getPercentageFor(other);
                this.tolerance = assertPerformance.getTolerancePercentage();
            }

            public void check() {
                switch (condition) {
                    case EQUALS:
                        checkEquals();
                        break;

                    case SLOWER:
                        checkSlower();
                        break;

                    case FASTER:
                        checkFaster();
                        break;
                }
            }

            private void checkEquals() {
                checkFaster();
                checkSlower();
            }

            private void checkFaster() throws AssertionError {
                if (actualPercentage > otherPercentage + tolerance) {
                    throwAssertException(actualPercentage, otherPercentage, "slower");
                }
            }

            private void checkSlower() throws AssertionError {
                if (actualPercentage < otherPercentage - tolerance) {
                    throwAssertException(actualPercentage, otherPercentage, "faster");
                }
            }

            private void throwAssertException(final float actualPercentage,
                    final float otherPercentage,
                    final String errorMessage) {
                throw new AssertionError(message +
                        " '" + name + "' (" + formatPercentage(actualPercentage) +
                        ") was " + errorMessage + " than '" + other +
                        "' (" + formatPercentage(otherPercentage) + ")" +
                        " with tolerance of " +
                        assertPerformance.getTolerancePercentage() + " %");
            }
        }
    }
}
