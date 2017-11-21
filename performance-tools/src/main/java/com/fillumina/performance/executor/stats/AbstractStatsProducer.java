package com.fillumina.performance.executor.stats;

import com.fillumina.performance.executor.AbstractTestExecutor;
import com.fillumina.performance.util.instrument.Instrumenter;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public abstract class AbstractStatsProducer<I extends StatsProducer<I>>
    extends AbstractTestExecutor<I, Stats, Runnable, MixedStatsHolder>
    implements StatsProducer<I> {

    @Override
    public <T extends Instrumenter<StatsProducer<?>>> T instrumentedBy(
            T instrumenter) {
        instrumenter.instrument(this);
        return instrumenter;
    }
}
