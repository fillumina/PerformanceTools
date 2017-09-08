package com.fillumina.performance.infrastructure.stats;

import com.fillumina.performance.infrastructure.AbstractPerformanceProducer;
import com.fillumina.performance.infrastructure.MixedAssertableHolder;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public abstract class AbstractStatsProducer
                        <I extends StatsProducer<I,S>,
                         S extends Stats<?>>
    extends AbstractPerformanceProducer<I, S, Runnable, MixedAssertableHolder>
    implements StatsProducer<I,S> {

}
