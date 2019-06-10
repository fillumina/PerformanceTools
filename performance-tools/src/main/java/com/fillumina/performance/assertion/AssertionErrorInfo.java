package com.fillumina.performance.assertion;

import com.fillumina.performance.util.RelativeOrder;
import com.fillumina.performance.util.stats.Ratio;
import java.util.Map;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public interface AssertionErrorInfo<T> extends AssertionEvaluator {

    AssertableExperiment getAssertable();

    CharSequence getFirstTestName();

    RelativeOrder getRelativeOrder();

    Ratio getTolerance();

    T getValue();

    Map<RelativeOrder, Ratio> getWhatIfToleranceMap();
}
