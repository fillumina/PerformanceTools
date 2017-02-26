package com.fillumina.performance.assertion;

import com.fillumina.performance.infrastructure.PHolder;
import com.fillumina.performance.util.ComposedName;
import com.fillumina.performance.util.stats.ConfidenceInterval;
import com.fillumina.performance.util.stats.Measure;
import com.fillumina.performance.util.stats.Ratio;
import java.io.Serializable;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
class AssertOrderCondition<A extends Assertable>
        implements Assertion<A>, Serializable {

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
    public void check(PHolder<A> assertable) {
        consume(assertable);
    }

    @Override
    public void consume(final PHolder<A> assertableHolder) {
        final ComposedName message = assertableHolder.getName();
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
        ConfidenceInterval aci = a.getConfidenceInterval(tolerance);
        double aLower = aci.getLowerBound();
        double aUpper = aci.getUpperBound();
        ConfidenceInterval bci = b.getConfidenceInterval(tolerance);
        double bLower = bci.getLowerBound();
        double bUpper = bci.getUpperBound();
        ConfidenceOrder co = new ConfidenceOrder(tolerance);
        switch (condition) {
            case SAME:
                return aci.compareTo(bci) == 0 ||
                        (co.gt(bLower, aLower) && co.lt(bUpper, aUpper)) ||
                        (co.gt(bLower, aLower) && co.lt(bLower, aUpper)) ||
                        (co.gt(bUpper, aLower) && co.lt(bUpper, aUpper)) ||
                        (co.lt(bLower, aLower) && co.gt(bUpper, aUpper));
            case GREATER:
                // bUpper < aLower
                return co.lt(bUpper, aLower);
            case LESS:
                // aUpper < bLower
                return co.lt(aUpper, bLower);
        }
        throw new AssertionError("condition not managed: " + condition);
    }

    @Override
    public String toString(PHolder<A> assertableHolder) {
        ComposedName name = assertableHolder.getName();
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
