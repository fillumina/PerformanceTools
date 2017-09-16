package com.fillumina.performance.assertion;

import com.fillumina.performance.util.AppendableWrapper;
import com.fillumina.performance.util.EqCondition;
import com.fillumina.performance.util.stats.Measure;
import com.fillumina.performance.util.stats.Ratio;
import java.io.Serializable;

/**
 * Asserts the output of a test to be a specific value (within the given
 * tolerance).
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
class AssertValueCondition
        implements Assertion, Serializable {

    private static final long serialVersionUID = 1L;
    private final CharSequence testName;
    private final double expectedValue;
    private final Ratio tolerance;
    private final EqCondition condition;

    public AssertValueCondition(final CharSequence testName,
            final EqCondition condition,
            final double expectedValue,
            final Ratio tolerance) {
        this.testName = testName;
        this.condition = condition;
        this.expectedValue = expectedValue;
        this.tolerance = tolerance;
    }

    @Override
    public void accept(Assertable assertable) {
        if (assertable != null) {
            check(assertable, tolerance);
        }
    }

    public void check(final Assertable assertable, final Ratio tolerance) {
        if (assertable != null) {
            Measure actualValue = assertable.getMeasure(testName);

            if (actualValue == null) {
                throw new TestNotFoundException(testName);
            }
            new ValueAssertionError(testName, actualValue,
                        expectedValue, tolerance, condition, assertable)
                    .checkAndThrowExceptionIfNotSatisfied();
        }
    }

    @Override
    public void appendTo(Appendable appendable, Assertable assertable) {
        Measure actualValue = assertable.getMeasure(testName);
        if (actualValue != null) {
            new AppendableWrapper(appendable)
                    .print('\'').print(testName)
                    .print("' (")
                    .print(actualValue)
                    .print(") ")
                    .print(satisfy(assertable) ? " is " : " is not ")
                    .print(condition.getMessage())
                    .print(' ')
                    .print(expectedValue)
                    .print(" with a tolerance of ")
                    .print(tolerance);
        }
    }

    @Override
    public String toString() {
        return testName +
                " " + condition.getSymbol() + " " +
                expectedValue +
                " (" + tolerance.toString() + ")";
    }

}
