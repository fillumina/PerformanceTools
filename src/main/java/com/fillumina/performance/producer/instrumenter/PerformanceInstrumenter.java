package com.fillumina.performance.producer.instrumenter;

import com.fillumina.performance.consumer.PerformanceConsumer;
import com.fillumina.performance.producer.PerformanceProducer;
import com.fillumina.performance.producer.timer.LoopPerformancesHolder;

/**
 *
 * @author fra
 */
public interface PerformanceInstrumenter<T extends PerformanceInstrumenter<?>>
        extends PerformanceProducer<T> {

    LoopPerformancesHolder executeSequence();

    @Override
    T addPerformanceConsumer(final PerformanceConsumer consumer);
}