package com.fillumina.performance.assertion;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public abstract class AbstractAssertionError extends AssertionError {
    private static final long serialVersionUID = 1L;

    protected abstract boolean checkWithTolerance(EqualityCondition condition,
            double tolerance);

    public void wouldBeIfTolerance(StringBuilder buf) {
        for (EqualityCondition ec : EqualityCondition.values()) {
            double t = findMinimumTolerance(ec);
            if (t != -1) {
                buf.append("would have been ").append(ec.name()).
                        append(" if tolerance >= ").append(t).
                        append(System.lineSeparator());
            }
        }
    }

    public double findMinimumTolerance(EqualityCondition condition) {
        double t;
        for (t = 1; t < 100.0; t += 1) {
            if (checkWithTolerance(condition, t)) {
                return t;
            }
        }
        return -1;
    }
}
