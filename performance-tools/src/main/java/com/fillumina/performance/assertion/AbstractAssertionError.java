package com.fillumina.performance.assertion;

import com.fillumina.performance.util.ExpBinarySearcher;
import com.fillumina.performance.util.ExpBinarySearcher.Condition;
import com.fillumina.performance.util.TName;
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

    private final TName title;
    private final EqCondition condition;
    private final Ratio tolerance;

    public AbstractAssertionError(TName title,
            EqCondition condition,
            Ratio tolerance) {
        super();
        this.title = title;
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

    @Override
    public String getMessage() {
        if (title == null) {
            return super.getMessage();
        }
        return title.toString();
    }

    public Ratio getTolerance() {
        return tolerance;
    }


    public TName getTitle() {
        return title;
    }

    public EqCondition getCondition() {
        return condition;
    }

    protected void appendTitle(StringBuilder buf) {
        if (title != null && !title.isEmpty()) {
            buf.append(title).append(System.lineSeparator());
        }
    }

    /** What if scenario proposed as solution for the error. */
    protected void appendWhatIfTolerance(StringBuilder buf) {
        buf.append(TableFormatter.title("Would have been:", '-'));
        for (Map.Entry<EqCondition, ToleranceRequired> e :
                getWhatIfToleranceMap().entrySet()) {
            ToleranceRequired t = e.getValue();
            buf.append(e.getKey().name().toLowerCase())
                    .append(" if tolerance >= ")
                    .append(t)
                    .append(System.lineSeparator());
        }
        buf.append(System.lineSeparator());
    }

    public Map<EqCondition, ToleranceRequired> getWhatIfToleranceMap() {
        Map<EqCondition, ToleranceRequired> map =
                new EnumMap<>(EqCondition.class);
        for (EqCondition oc : EqCondition.values()) {
            ToleranceRequired tr = findToleranceRequiredToSatisfyCondition(oc);
            if (!tr.isZero()) {
                map.put(oc, tr);
            }
        }
        return map;
    }

    ToleranceRequired findToleranceRequiredToSatisfyCondition(
            final EqCondition oc) {

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
}
