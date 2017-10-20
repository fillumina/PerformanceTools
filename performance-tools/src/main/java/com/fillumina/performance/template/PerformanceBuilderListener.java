package com.fillumina.performance.template;

import com.fillumina.performance.util.unit.IntervalUnit;
import com.fillumina.performance.util.unit.Quantity;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public interface PerformanceBuilderListener {

    void onResults(MixedConfiguration configuration,
            MixedAssertionableResult<?> mixedStats,
            Quantity<IntervalUnit> elapsed);

    void onConfiguration(MixedConfiguration configuration);
}
