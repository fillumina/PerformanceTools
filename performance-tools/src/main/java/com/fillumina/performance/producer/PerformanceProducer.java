package com.fillumina.performance.producer;

import com.fillumina.performance.consumer.PerformanceConsumer;

/**
 * A {@link PerformanceProducer} contains none or some
 * {@link PerformanceConsumer}s that may notify about the performances it
 * collects.
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public interface PerformanceProducer<T extends PerformanceProducer<?>> {

    T addPerformanceConsumer(final PerformanceConsumer... consumers);
    T removePerformanceConsumer(final PerformanceConsumer... consumers);
}
