package com.fillumina.performance.assertion;

import com.fillumina.performance.infrastructure.PHolder;
import com.fillumina.performance.util.StaticPath;
import com.fillumina.performance.util.stats.ConfidenceInterval;
import com.fillumina.performance.util.stats.Measure;
import com.fillumina.performance.util.stats.Ratio;
import com.fillumina.performance.util.stats.ToleranceEvaluator;
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
    private final OrderCondition condition;
    private final String firstTestName;
    private final String secondTestName;
    private final Ratio tolerance;

    public AssertOrderCondition(final OrderCondition condition,
            final String firstTestName,
            final String secondTestName,
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
            Measure firstMeasure = assertable.getValue(firstTestName);
            Measure secondMeasure = assertable.getValue(secondTestName);
            if (!comply(firstMeasure, secondMeasure, tolerance, condition)) {
                throw new OrderAssertionError(message, firstTestName,
                        firstMeasure, secondTestName, secondMeasure, tolerance,
                        condition, assertable);
            }
        }
    }

    static boolean comply(Measure a, Measure b,
            Ratio tolerance,
            OrderCondition condition) throws OrderAssertionError {
        ConfidenceInterval aci = a.getConfidenceInterval(Ratio.P_99);
        double aLower = aci.getLowerBound();
        double aUpper = aci.getUpperBound();
        ConfidenceInterval bci = b.getConfidenceInterval(Ratio.P_99);
        double bLower = bci.getLowerBound();
        double bUpper = bci.getUpperBound();
        ToleranceEvaluator ev = new ToleranceEvaluator(tolerance);
        switch (condition) {
            case SAME:
                return ev.value(aLower).between(bLower, bUpper) ||
                        ev.value(aUpper).between(bLower, bUpper);
            case GREATER:
                return ev.value(bUpper).lessThan(aLower);
            case LESS:
                return ev.value(aUpper).lessThan(bLower);
        }
        throw new AssertionError("not managed condition: " + condition);
    }

    @Override
    public String toString(PHolder<A> assertableHolder) {
        StaticPath name = assertableHolder.getName();
        Assertable assertable = assertableHolder.getStats();
        StringBuilder buf = new StringBuilder();
        if (name != null && !name.isEmpty()) {
            buf.append(name).append(System.lineSeparator());
        }
        Measure firstMeasure = assertable.getValue(firstTestName);
        Measure secondMeasure = assertable.getValue(secondTestName);
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

}
