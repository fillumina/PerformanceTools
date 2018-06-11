package com.fillumina.performance.assertion;

import com.fillumina.performance.util.AppendableWrapper;
import com.fillumina.performance.util.RelativeOrder;
import com.fillumina.performance.util.stats.Measure;
import com.fillumina.performance.util.stats.Ratio;
import java.io.Serializable;

/**
 * It uses the standard margin of error of the measures
 * and than it evaluates if their ratio is within the required tolerance.
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
class OrderAssertion
        implements ExperimentAssertion, Serializable {

    private static final long serialVersionUID = 1L;
    private final RelativeOrder condition;
    private final CharSequence firstTestName;
    private final CharSequence secondTestName;
    private final Ratio tolerance;

    public OrderAssertion(
            final CharSequence firstTestName,
            final CharSequence secondTestName,
            final RelativeOrder condition,
            final Ratio tolerance) {
        this.condition = condition;
        this.firstTestName = firstTestName;
        this.secondTestName = secondTestName;
        this.tolerance = tolerance;
    }

    @Override
    public void accept(AssertableExperiment assertable) throws MeasureNotFoundException {
        if (assertable != null) {
            Measure firstMeasure = assertable.getMeasure(firstTestName);
            Measure secondMeasure = assertable.getMeasure(secondTestName);

            if (firstMeasure == null) {
                throw new MeasureNotFoundException(firstTestName);
            }
            if (secondMeasure == null) {
                throw new MeasureNotFoundException(secondTestName);
            }
            new OrderAssertionError(
                    firstTestName, firstMeasure,
                    secondTestName, secondMeasure,
                    tolerance, condition, assertable)
                    .checkAndThrowExceptionIfNotSatisfied();
        }
    }

    @Override
    public void appendTo(Appendable appendable, AssertableExperiment assertable) {
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
        return firstTestName + " " + condition.getSymbol() + " " +
                secondTestName + " (" + tolerance.toString() + ")";
    }
}
