package com.fillumina.performance.infrastructure;

import com.fillumina.performance.util.instrument.Instrumenter;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public interface PerformanceProducerInstrumenter
            <I extends PerformanceProducer<I,C,T,P>, C, T, P>
        extends PerformanceProducer<I,C,T,P>,
                Instrumenter<I> {

}
