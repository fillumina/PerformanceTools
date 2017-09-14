package com.fillumina.performance.infrastructure.stats;

import com.fillumina.performance.infrastructure.AbstractPerformanceProducer;
import com.fillumina.performance.infrastructure.MixedAssertableHolder;
import com.fillumina.performance.util.instrument.Instrumenter;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public abstract class AbstractStatsProducer
                        <I extends StatsProducer<I,S>,
                         S extends Stats<?>>
    extends AbstractPerformanceProducer<I, S, Runnable, MixedAssertableHolder>
    implements StatsProducer<I,S> {

    @Override
    @SuppressWarnings("unchecked")
    public <T extends Instrumenter<StatsProducer<?, ?>>> T instrumentedBy(
            T instrumenter) {
        instrumenter.instrument((I)this);
        return instrumenter;
    }
}
