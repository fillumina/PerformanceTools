package com.fillumina.performance.consumer;

import com.fillumina.performance.producer.timer.LoopPerformances;

/**
 *
 * @author fra
 */
public interface PerformanceConsumer {

    void consume(final String message, final LoopPerformances loopPerformances);
}
