package com.fillumina.performance.assertion;

import com.fillumina.performance.util.RelativeOrder;
import com.fillumina.performance.util.RequiredTolerance;
import com.fillumina.performance.util.stats.Ratio;
import java.util.Map;
import java.util.function.BiPredicate;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public abstract class AbstractAssertionError extends AssertionError {
    private static final long serialVersionUID = 1L;

    private final RelativeOrder relativeOrder;
    private final Ratio tolerance;
    private RequiredTolerance requiredTolerance;

    public AbstractAssertionError(
            RelativeOrder requiredOrder,
            Ratio tolerance) {
        super();
        this.relativeOrder = requiredOrder;
        this.tolerance = tolerance;
    }

    protected abstract BiPredicate<RelativeOrder,Ratio> getPredicate();

    public void checkAndThrowExceptionIfNotSatisfied() {
        if (!isConditionSatisfied()) {
            throw this;
        }
    }

    public boolean isConditionSatisfied() {
        return getPredicate().test(relativeOrder, tolerance);
    }

    public Ratio getTolerance() {
        return tolerance;
    }

    public RelativeOrder getRelativeOrder() {
        return relativeOrder;
    }

    protected void appendWhatIfTolerance(StringBuilder buf) {
        getRequiredTolerance().appendWhatIfTolerance(buf);
    }

    public Map<RelativeOrder, Ratio> getWhatIfToleranceMap() {
        return getRequiredTolerance().getMap();
    }

    private RequiredTolerance getRequiredTolerance() {
        if (requiredTolerance == null) {
            requiredTolerance = new RequiredTolerance(getPredicate());
        }
        return requiredTolerance;
    }
}
