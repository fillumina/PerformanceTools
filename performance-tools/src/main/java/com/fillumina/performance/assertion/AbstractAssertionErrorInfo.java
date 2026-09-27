package com.fillumina.performance.assertion;

import com.fillumina.performance.util.RelativeOrder;
import com.fillumina.performance.util.RequiredTolerance;
import com.fillumina.performance.util.stats.Ratio;
import java.util.Map;
import java.util.function.BiPredicate;

/**
 *
 * @param <T> type of input
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public abstract class AbstractAssertionErrorInfo<T>
        implements AssertionErrorInfo<T> {

    private final AssertableExperiment assertable;
    private final CharSequence firstTestName;
    private final RelativeOrder relativeOrder;
    private final T value;
    private final Ratio tolerance;

    private RequiredTolerance requiredTolerance;

    public AbstractAssertionErrorInfo(
            AssertableExperiment assertable,
            CharSequence firstTestName,
            RelativeOrder relativeOrder,
            T value,
            Ratio tolerance) {
        this.assertable = assertable;
        this.firstTestName = firstTestName;
        this.relativeOrder = relativeOrder;
        this.value = value;
        this.tolerance = tolerance;
    }

    protected abstract BiPredicate<RelativeOrder, Ratio> getEvaluator();

    @Override
    public boolean isConditionSatisfied() {
        return getEvaluator().test(relativeOrder, tolerance);
    }

    protected void appendWhatIfTolerance(StringBuilder buf) {
        getRequiredTolerance().appendWhatIfTolerance(buf);
    }

    @Override
    public Map<RelativeOrder, Ratio> getWhatIfToleranceMap() {
        return getRequiredTolerance().getMap();
    }

    private RequiredTolerance getRequiredTolerance() {
        if (requiredTolerance == null) {
            requiredTolerance = new RequiredTolerance(getEvaluator());
        }
        return requiredTolerance;
    }

    @Override
    public AssertableExperiment getAssertable() {
        return assertable;
    }

    @Override
    public CharSequence getFirstTestName() {
        return firstTestName;
    }

    @Override
    public RelativeOrder getRelativeOrder() {
        return relativeOrder;
    }

    @Override
    public T getAssertionValue() {
        return value;
    }

    @Override
    public Ratio getTolerance() {
        return tolerance;
    }
}
