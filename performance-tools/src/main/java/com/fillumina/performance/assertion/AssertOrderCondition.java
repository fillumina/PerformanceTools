package com.fillumina.performance.assertion;

import com.fillumina.performance.util.AppendableWrapper;
import com.fillumina.performance.util.EqCondition;
import com.fillumina.performance.util.stats.Measure;
import com.fillumina.performance.util.stats.Ratio;
import com.fillumina.performance.util.tname.TName;
import java.io.Serializable;

/**
 * It uses the standard margin of error of the measures with confidence of 99 %
 * and than it evaluates if their ratio is within the required tolerance.
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
class AssertOrderCondition
        implements Assertion, Serializable {

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
    public void accept(Assertable assertable) {
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
    public void appendTo(Appendable appendable, Assertable assertable) {
        Measure firstMeasure = assertable.getMeasure(firstTestName);
        Measure secondMeasure = assertable.getMeasure(secondTestName);
        if (firstMeasure != null && secondMeasure != null) {
            new AppendableWrapper(appendable)
                    .print('\'').print(firstTestName).print("' (")
                    .print(firstMeasure).print(") ")
                    .print(satisfy(assertable) ? " is " : "is not ")
                    .print(condition.getMessage())
                    .print(" \'").print(secondTestName).print("' (")
                    .print(secondMeasure).print(") ")
                    .print(" with a tolerance of ")
                    .print(tolerance);
        }
    }

    @Override
    public String toString() {
        return firstTestName +
                " " + condition.getSymbol() + " " +
                secondTestName + " (" + tolerance.toString() + ")";
    }
}
