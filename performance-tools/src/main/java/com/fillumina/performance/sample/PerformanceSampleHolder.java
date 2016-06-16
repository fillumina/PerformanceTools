package com.fillumina.performance.sample;

import com.fillumina.performance.infrastructure.PerformanceHolder;
import com.fillumina.performance.infrastructure.StringGenerator;
import com.fillumina.performance.util.ComposedName;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class PerformanceSampleHolder
        extends PerformanceHolder<PerformanceSampleHolder, PerformanceSample> {
    private static final long serialVersionUID = 1L;

    public PerformanceSampleHolder(PerformanceSample stats) {
        super(stats);
    }

    public PerformanceSampleHolder(ComposedName name, PerformanceSample stats,
            StringGenerator<PerformanceSample> formatter) {
        super(name, stats, formatter);
    }
}
