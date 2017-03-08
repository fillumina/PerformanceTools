package com.fillumina.performance.assertion;

import com.fillumina.performance.util.ExpBinarySearcher;
import com.fillumina.performance.util.ExpBinarySearcher.Condition;
import com.fillumina.performance.util.StaticPath;
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

    private final StaticPath title;
    private final OrderCondition condition;
    private final Ratio tolerance;

    public AbstractAssertionError(StaticPath title,
            OrderCondition condition,
            Ratio tolerance) {
        this.title = title;
        this.condition = condition;
        this.tolerance = tolerance;
    }

    protected abstract boolean isConditionSatisfied(OrderCondition condition,
            Ratio tolerance);

    @Override
    public String getMessage() {
        if (title == null) {
            return super.getMessage();
        }
        return title.toString();
    }

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


    public StaticPath getTitle() {
        return title;
    }

    public OrderCondition getCondition() {
        return condition;
    }

    /** What if scenario proposed as solution for the error. */
    protected void appendWhatIfTolerance(StringBuilder buf) {
        buf.append(TableFormatter.title("Would have been:", '-'));
        for (Map.Entry<OrderCondition, ToleranceRequired> e :
                getWhatIfToleranceMap().entrySet()) {
            ToleranceRequired t = e.getValue();
            buf.append(e.getKey().name().toLowerCase())
                    .append(" if tolerance >= ")
                    .append(t)
                    .append(System.lineSeparator());
        }
        buf.append(System.lineSeparator());
    }

    public Map<OrderCondition, ToleranceRequired> getWhatIfToleranceMap() {
        Map<OrderCondition, ToleranceRequired> map =
                new EnumMap<>(OrderCondition.class);
        for (OrderCondition oc : OrderCondition.values()) {
            ToleranceRequired tr = findToleranceRequiredToSatisfyCondition(oc);
            if (!tr.isZero()) {
                map.put(oc, tr);
            }
        }
        return map;
    }

    ToleranceRequired findToleranceRequiredToSatisfyCondition(
            final OrderCondition oc) {

        int p = ExpBinarySearcher.searchGreaterOrEquals(0, Integer.MAX_VALUE,
                new Condition() {
                    @Override
                    public boolean isSatisfied(int value) {
                        return isConditionSatisfied(oc, Ratio.percentage(value));
                    }
                });

        if (p == -1) {
            return ToleranceRequired.tooHigh();
        } else if (p == 0) {
            return ToleranceRequired.zero();
        }
        return new ToleranceRequired(p);
    }

    protected void appendTitle(StringBuilder buf) {
        if (title != null && !title.isEmpty()) {
            buf.append(title).append(System.lineSeparator());
        }
    }
}
