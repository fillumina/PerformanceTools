package com.fillumina.performance.consumer.assertion;

import static com.fillumina.performance.utils.FormatterUtils.*;
import java.io.Serializable;

/**
 *
 * @author fra
 */
public class AssertPercentage implements Testable, Serializable {
    private static final long serialVersionUID = 1L;
    private static enum Condition { EQUALS, LESS, GREATER}

    private final AssertPerformance assertPerformance;
    private final String name;

    private Condition condition;
    private float expectedPercentage;

    public AssertPercentage(final AssertPerformance assertPerformance,
            final String name) {
        this.assertPerformance = assertPerformance;
        this.name = name;
    }

    public AssertPerformance equals(final float percentage) {
        this.expectedPercentage = percentage;
        this.condition = Condition.EQUALS;
        checkEquals();
        return assertPerformance;
    }

    public AssertPerformance lessThan(final float expectedPercentage) {
        this.expectedPercentage = expectedPercentage;
        this.condition = Condition.LESS;
        checkLess();
        return assertPerformance;
    }

    public AssertPerformance greaterThan(final float expectedPercentage) {
        this.expectedPercentage = expectedPercentage;
        this.condition = Condition.GREATER;
        checkGreater();
        return assertPerformance;
    }

    @Override
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
        if (assertPerformance.isPerformancesAvailable()) {
            checkLess();
            checkGreater();
        }
    }

    private void checkGreater() throws AssertionError {
        if (assertPerformance.isPerformancesAvailable()) {
            final float actualPercentage = assertPerformance.getPercentageFor(name);
            final float tolerance = assertPerformance.getTolerancePercentage();

            if (actualPercentage < expectedPercentage - tolerance) {
                throw new AssertionError("'" + name +
                        "' expected greater than : " +
                        formatPercentage(expectedPercentage) +
                        ", found : " + formatPercentage(actualPercentage));
            }
        }
    }

    private void checkLess() throws AssertionError {
        if (assertPerformance.isPerformancesAvailable()) {
            final float actualPercentage = assertPerformance.getPercentageFor(name);
            final float tolerance = assertPerformance.getTolerancePercentage();

            if (actualPercentage > expectedPercentage + tolerance) {
                throw new AssertionError("'" + name +
                        "' expected less than : " +
                        formatPercentage(expectedPercentage) +
                        ", found : " + formatPercentage(actualPercentage));
            }
        }
    }

}
