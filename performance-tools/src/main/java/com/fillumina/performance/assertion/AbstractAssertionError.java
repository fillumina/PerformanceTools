package com.fillumina.performance.assertion;

import com.fillumina.performance.util.StaticPath;
import com.fillumina.performance.util.formatter.TableFormatter;
import com.fillumina.performance.util.stats.Ratio;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public abstract class AbstractAssertionError extends AssertionError {
    private static final long serialVersionUID = 1L;

    private final StaticPath title;
    private final OrderCondition condition;
    private final Ratio tolerance;

    public AbstractAssertionError(StaticPath title, OrderCondition condition,
            Ratio tolerance) {
        this.title = title;
        this.condition = condition;
        this.tolerance = tolerance;
    }

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

    protected abstract boolean isConditionSatisfied(OrderCondition condition,
            Ratio tolerance);

    // FIXME not working!!!
    /** What if scenario proposed as solution for the error. */
    public void whatIfTolerance(StringBuilder buf) {
        buf.append(TableFormatter.title("Would have been:", '-'));
        for (OrderCondition ec : OrderCondition.values()) {
            double t = findMinimumTolerance(ec);
            if (t != -1) {
                buf.append(ec.name())
                        .append(" if tolerance >= ")
                        .append(t)
                        .append(" %")
                        .append(System.lineSeparator());
            }
        }
        buf.append(System.lineSeparator());
    }

    public double findMinimumTolerance(OrderCondition condition) {
        double t;
        for (t = 0; t < 100.0; t += 1) {
            if (isConditionSatisfied(condition, Ratio.percentage(t))) {
                return t;
            }
        }
        return -1;
    }

    protected void appendTitle(StringBuilder buf) {
        if (title != null && !title.isEmpty()) {
            buf.append(title).append(System.lineSeparator());
        }
    }
}
