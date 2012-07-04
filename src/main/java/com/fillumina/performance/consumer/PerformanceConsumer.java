package com.fillumina.performance.consumer;

import com.fillumina.performance.producer.timer.LoopPerformances;

/**
 *
 * @author fra
 */
public interface PerformanceConsumer {

    PerformanceConsumer setMessage(final String message);

    PerformanceConsumer setPerformances(LoopPerformances performances);

    void process();

}
