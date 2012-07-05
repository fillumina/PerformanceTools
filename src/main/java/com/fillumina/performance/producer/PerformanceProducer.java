package com.fillumina.performance.producer;

import com.fillumina.performance.consumer.PerformanceConsumer;

/**
 *
 * @author fra
 */
public interface PerformanceProducer {

    PerformanceProducer setPerformanceConsumer(PerformanceConsumer consumer);

}
