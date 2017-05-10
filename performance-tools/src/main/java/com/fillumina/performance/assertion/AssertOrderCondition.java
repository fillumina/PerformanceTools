package com.fillumina.performance.assertion;

import com.fillumina.performance.util.AppendableWrapper;
import com.fillumina.performance.util.EqCondition;
import com.fillumina.performance.util.TName;
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
    private final TName firstTestName;
    private final TName secondTestName;
    private final Ratio tolerance;

    public AssertOrderCondition(
            final TName firstTestName,
            final TName secondTestName,
            final EqCondition condition,
            final Ratio tolerance) {
        this.condition = condition;
        this.firstTestName = firstTestName;
        this.secondTestName = secondTestName;
        this.tolerance = tolerance;
    }

    @Override
    public void consume(A assertable) {
        if (assertable != null) {
            Measure firstMeasure = assertable.getMeasure(firstTestName);
            Measure secondMeasure = assertable.getMeasure(secondTestName);

            if (firstMeasure == null) {
                throw new TestNotFoundException(firstTestName);
            }
            if (secondMeasure == null) {
                throw new TestNotFoundException(secondTestName);
            }
            new OrderAssertionError(
                    firstTestName, firstMeasure,
                    secondTestName, secondMeasure,
                    tolerance, condition, assertable)
                    .checkAndThrowExceptionIfNotSatisfied();
        }
    }

    @Override
    public void toString(Appendable appendable, A assertable) {
        Measure firstMeasure = assertable.getMeasure(firstTestName);
        Measure secondMeasure = assertable.getMeasure(secondTestName);
        if (firstMeasure != null && secondMeasure != null) {
            new AppendableWrapper(appendable)
                    .append('\'').append(firstTestName).append("' (")
                    .append(firstMeasure).append(") ")
                    .append(" is ")
                    .append(condition.getMessage())
                    .append(" \'").append(secondTestName).append("' (")
                    .append(secondMeasure).append(") ")
                    .append(" with a tolerance of ")
                    .append(tolerance);
        }
    }
}
