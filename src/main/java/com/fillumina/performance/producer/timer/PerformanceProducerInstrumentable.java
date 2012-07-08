package com.fillumina.performance.producer.timer;

/**
 *
 * @author fra
 */
public interface PerformanceProducerInstrumentable {

    <T extends PerformanceProducerInstrumenter<?>>
            T instrumentedBy(final T instrumenter);
}
