package com.fillumina.performance.assertion;

import com.fillumina.performance.util.AppendableWrapper;
import com.fillumina.performance.util.RelativeOrder;
import com.fillumina.performance.util.stats.Measure;
import com.fillumina.performance.util.stats.Ratio;
import com.fillumina.performance.util.unit.DimensionalMeasure;
import com.fillumina.performance.util.unit.Unit;
import java.io.Serializable;
import java.util.NoSuchElementException;

/**
 * It uses the standard margin of error of the measures
 * and than evaluates if their ratio is within the required tolerance.
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
@Deprecated
class OrderAssertion implements ExperimentAssertion, Serializable {

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
    public void check(AssertableExperiment assertable)
            throws MeasureNotFoundException {
        if (assertable != null) {
            getAssertionError(assertable)
                .checkAndThrowExceptionIfNotSatisfied();
        }
    }

    public AbstractExperimentAssertionError getAssertionError(
            AssertableExperiment assertable)
                throws NoSuchElementException {
        DimensionalMeasure firstMeasure = assertable.getMeasure(firstTestName);
        DimensionalMeasure secondMeasure = assertable.getMeasure(secondTestName);
        Unit<?> bestUnit = DimensionalMeasure.bestUnit(firstMeasure, secondMeasure);
        firstMeasure = firstMeasure.in(bestUnit);
        secondMeasure = secondMeasure.in(bestUnit);

        return new OrderAssertionError(
                firstTestName, firstMeasure,
                secondTestName, secondMeasure,
                tolerance, condition, assertable);
    }

    @Override
    public void appendTo(Appendable appendable, AssertableExperiment assertable) {
        Measure firstMeasure = assertable.getMeasure(firstTestName);
        Measure secondMeasure = assertable.getMeasure(secondTestName);
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

    @Override
    public String toString() {
        return firstTestName + " " + condition.getSymbol() + " " +
                secondTestName + " (" + tolerance.toString() + ")";
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public RelativeOrder getCondition() {
        return condition;
    }

    public CharSequence getFirstTestName() {
        return firstTestName;
    }

    public CharSequence getSecondTestName() {
        return secondTestName;
    }

    public Ratio getTolerance() {
        return tolerance;
    }
}
