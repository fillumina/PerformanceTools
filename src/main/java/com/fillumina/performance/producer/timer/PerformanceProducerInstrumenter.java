package com.fillumina.performance.producer.timer;

import com.fillumina.performance.consumer.PerformanceConsumer;

/**
 *
 * @author fra
 */
public interface PerformanceProducerInstrumenter
        <T extends PerformanceProducerInstrumenter<?>> {

    void setPerformanceTimer(final PerformanceTimer performanceTimer);
    T setInnerPerformanceConsumer(final PerformanceConsumer consumer);
}
