package com.fillumina.performance.assertion;

import com.fillumina.performance.util.RelativeOrder;
import com.fillumina.performance.util.stats.ConfidenceInterval;
import com.fillumina.performance.util.stats.Ratio;
import com.fillumina.performance.util.stats.ToleranceEvaluator;
import com.fillumina.performance.util.unit.DimensionalMeasure;
import com.fillumina.performance.util.unit.Unit;
import java.util.function.BiPredicate;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class OrderInfo
        extends AbstractAssertionErrorInfo<CharSequence> {

    private final DimensionalMeasure firstMeasure;
    private final DimensionalMeasure secondMeasure;
    private final BiPredicate<RelativeOrder,Ratio> evaluator;

    public OrderInfo(
            AssertableExperiment assertable,
            CharSequence firstTestName,
            RelativeOrder order,
            CharSequence secondTestName,
            Ratio tolerance) {
        super(assertable, firstTestName, order, secondTestName, tolerance);
        DimensionalMeasure m1 = assertable.getMeasure(getFirstTestName());
        DimensionalMeasure m2 = assertable.getMeasure(getAssertionValue());
        Unit<?> bestUnit = DimensionalMeasure.bestUnit(m1, m2);
        firstMeasure = m1.in(bestUnit);
        secondMeasure = m2.in(bestUnit);
        evaluator = createEvaluator();
    }

    @Override
    protected BiPredicate<RelativeOrder, Ratio> getEvaluator() {
        return evaluator;
    }

    private BiPredicate<RelativeOrder,Ratio> createEvaluator() {
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

    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder();
        buf.append('\'').append(getFirstTestName())
                .append("' (").append(firstMeasure).append(") ")
                .append("expected ").append(getRelativeOrder().getMessage())
                .append(' ')
                .append('\'').append(getAssertionValue())
                .append("' (").append(secondMeasure).append(") ")
                .append(" with a tolerance of ")
                .append(getTolerance())
                .append(System.lineSeparator());
                appendWhatIfTolerance(buf);
                buf.append(getAssertable().toString());
        return buf.toString();
    }
}
