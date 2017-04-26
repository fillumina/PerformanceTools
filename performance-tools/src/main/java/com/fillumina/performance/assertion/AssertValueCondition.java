package com.fillumina.performance.assertion;

import com.fillumina.performance.infrastructure.PHolder;
import com.fillumina.performance.util.TreeName;
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
    private final String testName;
    private final double expectedValue;
    private final Ratio tolerance;
    private final EqCondition condition;

    public AssertValueCondition(final String testName,
            final EqCondition condition,
            final double expectedValue,
            final Ratio tolerance) {
        this.testName = testName;
        this.condition = condition;
        this.expectedValue = expectedValue;
        this.tolerance = tolerance;
    }

    @Override
    public void consume(final PHolder<A> assertable) {
        if (assertable != null) {
            check(assertable, tolerance);
        }
    }

    public void check(final PHolder<A> assertableHolder,
            final Ratio tolerance) {
        final TreeName name = assertableHolder.getName();
        final Assertable assertable = assertableHolder.getStats();
        if (assertable != null) {
            Measure actualValue = assertable.getMeasure(testName);

            new ValueAssertionError(name, testName, actualValue,
                        expectedValue, tolerance, condition, assertable)
                    .checkAndThrowExceptionIfNotSatisfied();
        }
    }

    @Override
    public String toString(PHolder<A> assertableHolder) {
        TreeName name = assertableHolder.getName();
        Assertable assertable = assertableHolder.getStats();
        StringBuilder buf = new StringBuilder();
        if (name != null && !name.isEmpty()) {
            buf.append(name).append(':').append(System.lineSeparator());
        }
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
        return buf.toString();
    }

}
