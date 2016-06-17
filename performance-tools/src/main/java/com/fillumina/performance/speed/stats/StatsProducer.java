package com.fillumina.performance.speed.stats;

import com.fillumina.performance.infrastructure.PerformanceProducer;
import com.fillumina.performance.speed.sample.Testable;
import com.fillumina.performance.util.instrument.Instrumentable;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
//TODO can be moved to infrastructure
public interface StatsProducer<A>
        extends PerformanceProducer<A, Testable>,
            Instrumentable<StatsProducer<A>> {

}
