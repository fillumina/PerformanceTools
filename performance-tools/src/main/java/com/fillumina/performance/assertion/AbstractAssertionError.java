package com.fillumina.performance.assertion;

import com.fillumina.performance.util.formatter.TableFormatter;
import com.fillumina.performance.util.stats.Ratio;

/**
 * Contains the mechanism for the what-if scenario.
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public abstract class AbstractAssertionError extends AssertionError {
    private static final long serialVersionUID = 1L;

    protected abstract boolean checkWithTolerance(OrderCondition condition,
            Ratio tolerance);

    // FIXME not working!!!
    /** What if scenario proposed as solution for the error. */
    public void wouldBeIfTolerance(StringBuilder buf) {
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
            if (checkWithTolerance(condition, Ratio.percentage(t))) {
                return t;
            }
        }
        return -1;
    }
}
