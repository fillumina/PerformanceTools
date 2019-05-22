package com.fillumina.performance.assertion;

import com.fillumina.performance.util.RelativeOrder;
import com.fillumina.performance.util.stats.ConfidenceInterval;
import com.fillumina.performance.util.stats.Measure;
import com.fillumina.performance.util.stats.Ratio;
import com.fillumina.performance.util.stats.ToleranceEvaluator;
import com.fillumina.performance.util.unit.DimensionalMeasure;
import com.fillumina.performance.util.unit.Unit;
import java.util.function.BiPredicate;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class OrderAssertionError extends AbstractExperimentAssertionError {
    private static final long serialVersionUID = 1L;
    private final CharSequence firstTestName;
    private final DimensionalMeasure firstMeasure;
    private final CharSequence secondTestName;
    private final DimensionalMeasure secondMeasure;
    private final AssertableExperiment assertable;

    public static OrderAssertionError create(
            AssertableExperiment assertable,
            CharSequence firstTestName,
            CharSequence secondTestName,
            RelativeOrder order,
            Ratio tolerance) {
        DimensionalMeasure m1 = assertable.getMeasure(firstTestName);
        DimensionalMeasure m2 = assertable.getMeasure(secondTestName);
        Unit<?> bestUnit = DimensionalMeasure.bestUnit(m1, m2);
        return new OrderAssertionError(
                firstTestName, m1.in(bestUnit),
                secondTestName, m2.in(bestUnit),
                tolerance, order, assertable);
    }

    public OrderAssertionError(
            CharSequence firstTestName,
            DimensionalMeasure firstMeasure,
            CharSequence secondTestName,
            DimensionalMeasure secondMeasure,
            Ratio tolerance,
            RelativeOrder requiredCondition,
            AssertableExperiment assertable) {
        super(requiredCondition, tolerance);
        this.firstTestName = firstTestName;
        this.firstMeasure = firstMeasure;
        this.secondTestName = secondTestName;
        this.secondMeasure = secondMeasure;
        this.assertable = assertable;
    }

    @Override
    protected BiPredicate<RelativeOrder,Ratio> getPredicate() {
        ConfidenceInterval aci = firstMeasure.getConfidenceInterval(Ratio.P_99);
        double aLower = aci.getLowerBound();
        double aUpper = aci.getUpperBound();
        ConfidenceInterval bci = secondMeasure.getConfidenceInterval(Ratio.P_99);
        double bLower = bci.getLowerBound();
        double bUpper = bci.getUpperBound();

        return (condition, tolerance) -> {
            ToleranceEvaluator ev = new ToleranceEvaluator(tolerance);
            switch (condition) {
                case EQUALS:
                    return ev.value(aLower).between(bLower, bUpper) ||
                            ev.value(aUpper).between(bLower, bUpper);
                case GREATER:
                    return ev.value(bUpper).lessThan(aLower);
                case LESS:
                    return ev.value(aUpper).lessThan(bLower);
            }
            throw new AssertionError("not managed condition: " + condition);
        };
    }

    public CharSequence getFirstTestName() {
        return firstTestName;
    }

    public Measure getFirstMeasure() {
        return firstMeasure;
    }

    public CharSequence getSecondTestName() {
        return secondTestName;
    }

    public Measure getSecondMeasure() {
        return secondMeasure;
    }

    @Override
    public String getMessage() {
        StringBuilder buf = new StringBuilder();
        buf.append('\'').append(firstTestName)
                .append("' (").append(firstMeasure).append(") ")
                .append("expected ").append(getRelativeOrder().getMessage())
                .append(' ')
                .append('\'').append(secondTestName)
                .append("' (").append(secondMeasure).append(") ")
                .append(" with a tolerance of ")
                .append(getTolerance())
                .append(System.lineSeparator());
                appendWhatIfTolerance(buf);
                buf.append(assertable.toString());
        return buf.toString();
    }
}
