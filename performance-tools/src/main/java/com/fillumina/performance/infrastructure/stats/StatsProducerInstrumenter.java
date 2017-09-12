package com.fillumina.performance.infrastructure.stats;

import com.fillumina.performance.util.instrument.Instrumenter;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public interface StatsProducerInstrumenter<I extends StatsProducer<I,S>,
                                           S extends Stats<?>>
        extends StatsProducer<I,S>, Instrumenter<I> {

}
