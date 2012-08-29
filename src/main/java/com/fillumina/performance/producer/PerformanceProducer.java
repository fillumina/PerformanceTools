package com.fillumina.performance.producer;

import com.fillumina.performance.consumer.PerformanceConsumer;

/**
 *
 * @author fra
 */
public interface PerformanceProducer<T extends PerformanceProducer<?>> {

    T addPerformanceConsumer(final PerformanceConsumer... consumers);
}
