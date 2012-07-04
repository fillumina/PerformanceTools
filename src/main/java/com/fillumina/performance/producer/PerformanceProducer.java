package com.fillumina.performance.producer;

import com.fillumina.performance.consumer.PerformanceConsumer;

/**
 *
 * @author fra
 */
public interface PerformanceProducer {

    <T extends PerformanceConsumer> T use(T consumer);

    PerformanceProducer setPerformanceConsumer(PerformanceConsumer consumer);
}
