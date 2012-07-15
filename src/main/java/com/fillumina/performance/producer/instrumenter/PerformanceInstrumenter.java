package com.fillumina.performance.producer.instrumenter;

import com.fillumina.performance.consumer.PerformanceConsumer;
import com.fillumina.performance.producer.PerformanceProducer;
import com.fillumina.performance.producer.timer.LoopPerformances;

/**
 *
 * @author fra
 */
public interface PerformanceInstrumenter<T extends PerformanceInstrumenter<?>>
        extends PerformanceProducer<T> {

    LoopPerformances executeSequence();

    @Override
    T addPerformanceConsumer(final PerformanceConsumer consumer);
}
