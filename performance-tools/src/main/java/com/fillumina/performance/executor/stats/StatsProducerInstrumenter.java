package com.fillumina.performance.executor.stats;

import com.fillumina.performance.util.instrument.Instrumenter;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public interface StatsProducerInstrumenter<I extends StatsProducer<I>>
        extends StatsProducer<I>, Instrumenter<StatsProducer<?>> {

}
