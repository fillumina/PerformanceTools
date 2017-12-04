package com.fillumina.performance.assertion;

import com.fillumina.performance.util.ExpBinarySearcher;
import com.fillumina.performance.util.RelativeOrder;
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

    private final RelativeOrder relativeOrder;
    private final Ratio tolerance;

    public AbstractAssertionError(
            RelativeOrder requiredOrder,
            Ratio tolerance) {
        super();
        this.relativeOrder = requiredOrder;
        this.tolerance = tolerance;
    }

    public abstract boolean isConditionSatisfied(RelativeOrder relativeOrder,
            Ratio tolerance);

    public void checkAndThrowExceptionIfNotSatisfied() {
        if (!isConditionSatisfied()) {
            throw this;
        }
    }

    public boolean isConditionSatisfied() {
        return isConditionSatisfied(relativeOrder, tolerance);
    }

    public Ratio getTolerance() {
        return tolerance;
    }

    public RelativeOrder getRelativeOrder() {
        return relativeOrder;
    }

    /** What if scenario proposed as solution for the error. */
    protected void appendWhatIfTolerance(StringBuilder buf) {
        buf.append(TableFormatter.title("Would have been:", '-'));
        for (Map.Entry<RelativeOrder, Ratio> e :
                getWhatIfToleranceMap().entrySet()) {
            Ratio t = e.getValue();
            buf.append(e.getKey().name().toLowerCase())
                    .append(" if tolerance >= ")
                    .append(t)
                    .append(System.lineSeparator());
        }
        buf.append(System.lineSeparator());
    }

    public Map<RelativeOrder, Ratio> getWhatIfToleranceMap() {
        Map<RelativeOrder, Ratio> map =
                new EnumMap<>(RelativeOrder.class);
        for (RelativeOrder oc : RelativeOrder.values()) {
            Ratio tr = findToleranceRequiredToSatisfyCondition(oc);
            if (!tr.isZero()) {
                map.put(oc, tr);
            }
        }
        return map;
    }

    private Ratio findToleranceRequiredToSatisfyCondition(
            final RelativeOrder o) {

        int p = ExpBinarySearcher.searchGreaterOrEquals(0, Integer.MAX_VALUE,
                (int v) -> isConditionSatisfied(o, Ratio.percentage(v)));

        if (p == -1) {
            return Ratio.MAX;
        } else if (p == 0) {
            return Ratio.ZERO;
        }
        return Ratio.percentage(p);
    }
}
