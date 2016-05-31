package com.fillumina.performance.stats;

import com.fillumina.performance.infrastructure.PerformanceProducer;
import com.fillumina.performance.infrastructure.Testable;
import com.fillumina.performance.util.instrument.Instrumentable;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public interface StatsProducer
        extends PerformanceProducer<PerformanceStats, Testable>,
            Instrumentable<StatsProducer> {

}
