package com.fillumina.performance.consumer.assertion;

import static com.fillumina.performance.utils.FormatterUtils.*;
import java.io.Serializable;

/**
 *
 * @author fra
 */
public class AssertOrder implements Testable, Serializable {
    private static final long serialVersionUID = 1L;
    private static enum Condition { EQUALS, FASTER, SLOWER}

    private final AssertPerformance assertPerformance;
    private final String name;

    private Condition condition;
    private String other;

    public AssertOrder(final AssertPerformance assertPerformance,
            final String name) {
        this.assertPerformance = assertPerformance;
        this.name = name;
    }

    public AssertPerformance equalsTo(final String other) {
        this.other = other;
        this.condition = Condition.EQUALS;
        checkEquals();
        return assertPerformance;
    }

    public AssertPerformance slowerThan(final String other) {
        this.other = other;
        this.condition = Condition.SLOWER;
        checkSlower();
        return assertPerformance;
    }

    public AssertPerformance fasterThan(final String other) {
        this.other = other;
        this.condition = Condition.FASTER;
        checkFaster();
        return assertPerformance;
    }

    @Override
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
        if (assertPerformance.isPerformancesAvailable()) {
            checkFaster();
            checkSlower();
        }
    }

    private void checkFaster() throws AssertionError {
        if (assertPerformance.isPerformancesAvailable()) {
            final float actualPercentage = assertPerformance.getPercentageFor(name);
            final float otherPercentage = assertPerformance.getPercentageFor(other);
            final float tolerance = assertPerformance.getTolerancePercentage();

            if (actualPercentage > otherPercentage + tolerance) {
                throwAssertException(actualPercentage, otherPercentage, "slower");
            }
        }
    }

    private void checkSlower() throws AssertionError {
        if (assertPerformance.isPerformancesAvailable()) {
            final float actualPercentage = assertPerformance.getPercentageFor(name);
            final float otherPercentage = assertPerformance.getPercentageFor(other);
            final float tolerance = assertPerformance.getTolerancePercentage();

            if (actualPercentage < otherPercentage - tolerance) {
                throwAssertException(actualPercentage, otherPercentage, "faster");
            }
        }
    }

    private void throwAssertException(final float actualPercentage,
            final float otherPercentage,
            final String errorMessage) {
        throw new AssertionError(assertPerformance.getMessage() +
                " '" + name + "' (" + formatPercentage(actualPercentage) +
                ") was " + errorMessage + " than '" + other +
                "' (" + formatPercentage(otherPercentage) + ")" +
                " with tolerance of " +
                assertPerformance.getTolerancePercentage() + " %");
    }
}
