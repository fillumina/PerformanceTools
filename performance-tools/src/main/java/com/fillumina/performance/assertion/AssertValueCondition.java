package com.fillumina.performance.assertion;

import com.fillumina.performance.util.AppendableWrapper;
import com.fillumina.performance.util.EqCondition;
import com.fillumina.performance.util.TName;
import com.fillumina.performance.util.stats.Measure;
import com.fillumina.performance.util.stats.Ratio;
import java.io.Serializable;

/**
 * Asserts the output of a test to be a specific value (within the given
 * tolerance).
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
class AssertValueCondition<A extends Assertable>
        extends AbstractAssertion<A>
        implements Serializable {

    private static final long serialVersionUID = 1L;
    private final TName testName;
    private final double expectedValue;
    private final Ratio tolerance;
    private final EqCondition condition;

    public AssertValueCondition(final TName testName,
            final EqCondition condition,
            final double expectedValue,
            final Ratio tolerance) {
        this.testName = testName;
        this.condition = condition;
        this.expectedValue = expectedValue;
        this.tolerance = tolerance;
    }

    @Override
    public void consume(A assertable) {
        if (assertable != null) {
            check(assertable, tolerance);
        }
    }

    public void check(final A assertable, final Ratio tolerance) {
        if (assertable != null) {
            Measure actualValue = assertable.getMeasure(testName);

            new ValueAssertionError(testName, actualValue,
                        expectedValue, tolerance, condition, assertable)
                    .checkAndThrowExceptionIfNotSatisfied();
        }
    }

    @Override
    public void toString(Appendable appendable, A assertable) {
        AppendableWrapper buf = new AppendableWrapper(appendable);
        buf.append('\'').append(testName)
                .append("' (")
                .append(assertable.getMeasure(testName))
                .append(") ")
                .append(" is ")
                .append(condition.getMessage())
                .append(' ')
                .append(expectedValue)
                .append(" with a tolerance of ")
                .append(tolerance);
    }

}
