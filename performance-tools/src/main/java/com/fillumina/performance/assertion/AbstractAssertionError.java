package com.fillumina.performance.assertion;

import com.fillumina.performance.util.TableFormatter;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public abstract class AbstractAssertionError extends AssertionError {
    private static final long serialVersionUID = 1L;

    protected abstract boolean checkWithTolerance(EqualityCondition condition,
            double tolerance);

    public void wouldBeIfTolerance(StringBuilder buf) {
        buf.append(TableFormatter.title("Would have been:", '-'));
        for (EqualityCondition ec : EqualityCondition.values()) {
            double t = findMinimumTolerance(ec);
            if (t != -1) {
                buf.append(ec.name()).
                        append(" if tolerance >= ").append(t).
                        append(System.lineSeparator());
            }
        }
        buf.append(System.lineSeparator());
    }

    public double findMinimumTolerance(EqualityCondition condition) {
        double t;
        for (t = 0; t < 100.0; t += 1) {
            if (checkWithTolerance(condition, t)) {
                return t;
            }
        }
        return -1;
    }
}
