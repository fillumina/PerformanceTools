package com.fillumina.performance.assertion;

import com.fillumina.performance.util.TName;
import com.fillumina.performance.util.stats.ConfidenceInterval;
import com.fillumina.performance.util.stats.Measure;
import com.fillumina.performance.util.stats.Ratio;
import com.fillumina.performance.util.stats.ToleranceEvaluator;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class OrderAssertionError extends AbstractAssertionError {
    private static final long serialVersionUID = 1L;
    private final TName firstTestName;
    private final Measure firstMeasure;
    private final TName secondTestName;
    private final Measure secondMeasure;
    private final Assertable assertableMultiTest;

    public OrderAssertionError(
            TName title,
            TName firstTestName,
            Measure first,
            TName secondTestName,
            Measure second,
            Ratio tolerance,
            EqCondition requiredCondition,
            Assertable assertableMultiTest) {
        super(title, requiredCondition, tolerance);
        this.firstTestName = firstTestName;
        this.firstMeasure = first;
        this.secondTestName = secondTestName;
        this.secondMeasure = second;
        this.assertableMultiTest = assertableMultiTest;
    }

    @Override
    public boolean isConditionSatisfied(EqCondition condition,
            Ratio tolerance) {
        ConfidenceInterval aci = firstMeasure.getConfidenceInterval(Ratio.P_99);
        double aLower = aci.getLowerBound();
        double aUpper = aci.getUpperBound();
        ConfidenceInterval bci = secondMeasure.getConfidenceInterval(Ratio.P_99);
        double bLower = bci.getLowerBound();
        double bUpper = bci.getUpperBound();
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
    }

    public TName getFirstTestName() {
        return firstTestName;
    }

    public Measure getFirstMeasure() {
        return firstMeasure;
    }

    public TName getSecondTestName() {
        return secondTestName;
    }

    public Measure getSecondMeasure() {
        return secondMeasure;
    }

    @Override
    public String getMessage() {
        StringBuilder buf = new StringBuilder();
        appendTitle(buf);
        buf.append('\'').append(firstTestName)
                .append("' (").append(firstMeasure).append(") ")
                .append("expected ").append(getCondition().getMessage())
                .append(' ')
                .append('\'').append(secondTestName)
                .append("' (").append(secondMeasure).append(") ")
                .append(" with a tolerance of ")
                .append(getTolerance())
                .append(System.lineSeparator());
                appendWhatIfTolerance(buf);
                buf.append(assertableMultiTest.toString());
        return buf.toString();
    }
}
