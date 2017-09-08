package com.fillumina.performance.infrastructure.stats;

import com.fillumina.performance.infrastructure.MixedAssertableHolder;
import com.fillumina.performance.infrastructure.PerformanceProducer;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public interface StatsProducer
                        <I extends StatsProducer<I,S>,
                         S extends Stats<?>>
    extends PerformanceProducer
                    <I,
                     S,
                     Runnable,
                     MixedAssertableHolder> {

}
