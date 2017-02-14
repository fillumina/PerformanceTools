package com.fillumina.performance.infrastructure;

import com.fillumina.performance.infrastructure.type.AssertableStats;
import com.fillumina.performance.speed.sample.Testable;
import com.fillumina.performance.util.instrument.Instrumentable;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public interface StatsProducer<A extends AssertableStats>
        extends PerformanceProducer<A, Testable>,
                Instrumentable<StatsProducer<A>> {

}
