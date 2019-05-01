package com.fillumina.performance.executor.stats;

import com.fillumina.performance.executor.AbstractTestExecutor;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public abstract class AbstractStatsProducer<I extends AbstractStatsProducer<I>>
    extends AbstractTestExecutor<I, Stats, Runnable, MixedStatsHolder>
    implements StatsProducer<I> {

}
