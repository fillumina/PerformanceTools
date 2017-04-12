package com.fillumina.performance.assertion;

import com.fillumina.performance.infrastructure.PHolder;
import com.fillumina.performance.util.StaticPath;
import com.fillumina.performance.util.stats.Measure;
import com.fillumina.performance.util.stats.Ratio;
import java.io.Serializable;

/**
 * It uses the standard margin of error of the measures with confidence of 99 %
 * and than it evaluates if their ratio is within the required tolerance.
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
class AssertOrderCondition<A extends Assertable>
        extends AbstractAssertion<A>
        implements Serializable {

    private static final long serialVersionUID = 1L;
    private final EqCondition condition;
    private final String firstTestName;
    private final String secondTestName;
    private final Ratio tolerance;

    public AssertOrderCondition(final String firstTestName,
            final String secondTestName,
            final EqCondition condition,
            final Ratio tolerance) {
        this.condition = condition;
        this.firstTestName = firstTestName;
        this.secondTestName = secondTestName;
        this.tolerance = tolerance;
    }

    @Override
    public void consume(final PHolder<A> assertableHolder) {
        final StaticPath message = assertableHolder.getName();
        final Assertable assertable = assertableHolder.getStats();
        if (assertable != null) {
            Measure firstMeasure = assertable.getMeasure(firstTestName);
            Measure secondMeasure = assertable.getMeasure(secondTestName);

            new OrderAssertionError(message,
                    firstTestName, firstMeasure,
                    secondTestName, secondMeasure,
                    tolerance, condition, assertable)
                    .checkAndThrowExceptionIfNotSatisfied();
        }
    }

    @Override
    public String toString(PHolder<A> assertableHolder) {
        StringBuilder buf = new StringBuilder();
        Assertable assertable = assertableHolder.getStats();
        appendTitle(buf, assertableHolder);
        Measure firstMeasure = assertable.getMeasure(firstTestName);
        Measure secondMeasure = assertable.getMeasure(secondTestName);
        buf.append('\'').append(firstTestName).append("' (")
                .append(firstMeasure).append(") ")
                .append(" is ")
                .append(condition.getMessage())
                .append(" \'").append(secondTestName).append("' (")
                .append(secondMeasure).append(") ")
                .append(" with a tolerance of ")
                .append(tolerance).append(" %");
        return buf.toString();
    }

    protected void appendTitle(StringBuilder buf, PHolder<A> assertableHolder) {
        StaticPath name = assertableHolder.getName();
        if (name != null && !name.isEmpty()) {
            buf.append(name).append(System.lineSeparator());
        }
    }

}
