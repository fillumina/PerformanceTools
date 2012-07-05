package com.fillumina.performance.producer;

import com.fillumina.performance.consumer.PerformanceConsumer;

/**
 *
 * @author fra
 */
public interface ChainablePerformanceProducer extends PerformanceProducer {

    <T extends PerformanceConsumer> T use(T consumer);
}
