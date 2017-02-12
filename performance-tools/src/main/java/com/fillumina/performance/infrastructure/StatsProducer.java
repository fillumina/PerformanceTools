package com.fillumina.performance.infrastructure;

import com.fillumina.performance.assertion.AssertableMultiStats;
import com.fillumina.performance.speed.sample.Testable;
import com.fillumina.performance.util.instrument.Instrumentable;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public interface StatsProducer<A extends AssertableMultiStats>
        extends PerformanceProducer<A, Testable>,
            Instrumentable<StatsProducer<A>> {

}
