package com.fillumina.performance.assertion;

import com.fillumina.performance.util.EqCondition;
import com.fillumina.performance.util.ExpBinarySearcher;
import com.fillumina.performance.util.formatter.TableFormatter;
import com.fillumina.performance.util.stats.Ratio;
import java.util.EnumMap;
import java.util.Map;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public abstract class AbstractAssertionError extends AssertionError {
    private static final long serialVersionUID = 1L;

    private final EqCondition condition;
    private final Ratio tolerance;

    public AbstractAssertionError(
            EqCondition condition,
            Ratio tolerance) {
        super();
        this.condition = condition;
        this.tolerance = tolerance;
    }

    public abstract boolean isConditionSatisfied(EqCondition condition,
            Ratio tolerance);

    public void checkAndThrowExceptionIfNotSatisfied() {
        if (!isConditionSatisfied()) {
            throw this;
        }
    }

    public boolean isConditionSatisfied() {
        return isConditionSatisfied(condition, tolerance);
    }

    public Ratio getTolerance() {
        return tolerance;
    }

    public EqCondition getCondition() {
        return condition;
    }

    /** What if scenario proposed as solution for the error. */
    protected void appendWhatIfTolerance(StringBuilder buf) {
        buf.append(TableFormatter.title("Would have been:", '-'));
        for (Map.Entry<EqCondition, Tolerance> e :
                getWhatIfToleranceMap().entrySet()) {
            Tolerance t = e.getValue();
            buf.append(e.getKey().name().toLowerCase())
                    .append(" if tolerance >= ")
                    .append(t)
                    .append(System.lineSeparator());
        }
        buf.append(System.lineSeparator());
    }

    public Map<EqCondition, Tolerance> getWhatIfToleranceMap() {
        Map<EqCondition, Tolerance> map =
                new EnumMap<>(EqCondition.class);
        for (EqCondition oc : EqCondition.values()) {
            Tolerance tr = findToleranceRequiredToSatisfyCondition(oc);
            if (!tr.isZero()) {
                map.put(oc, tr);
            }
        }
        return map;
    }

    private Tolerance findToleranceRequiredToSatisfyCondition(
            final EqCondition oc) {

        int p = ExpBinarySearcher.searchGreaterOrEquals(0, Integer.MAX_VALUE,
                (int v) -> isConditionSatisfied(oc, Ratio.percentage(v)));

        if (p == -1) {
            return Tolerance.max();
        } else if (p == 0) {
            return Tolerance.zero();
        }
        return new Tolerance(p);
    }
}
