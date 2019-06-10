package com.fillumina.performance.assertion;

import com.fillumina.performance.util.RelativeOrder;
import com.fillumina.performance.util.stats.Ratio;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public interface AssertionErrorInfoCreator<T> {

    AssertionErrorInfo<T> create(AssertableExperiment assertable,
            CharSequence testName, RelativeOrder order, T value, Ratio tolerance);

}
