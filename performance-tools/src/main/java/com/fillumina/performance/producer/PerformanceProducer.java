package com.fillumina.performance.producer;

import com.fillumina.performance.consumer.PerformanceConsumer;

/**
 * A {@link PerformanceProducer} contains none or some
 * {@link PerformanceConsumer}s that it notifies about the performances it
 * collects.
 *
 * @author Francesco Illuminati
 */
public interface PerformanceProducer {

    PerformanceProducer addPerformanceConsumer(
            final PerformanceConsumer... consumers);

    PerformanceProducer removePerformanceConsumer(
            final PerformanceConsumer... consumers);
}
