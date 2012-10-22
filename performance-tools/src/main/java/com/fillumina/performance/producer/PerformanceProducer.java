package com.fillumina.performance.producer;

import com.fillumina.performance.consumer.PerformanceConsumer;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public interface PerformanceProducer<T extends PerformanceProducer<?>> {

    T addPerformanceConsumer(final PerformanceConsumer... consumers);
}
