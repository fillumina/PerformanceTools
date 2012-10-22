package com.fillumina.performance.consumer;

import com.fillumina.performance.producer.LoopPerformances;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public interface PerformanceConsumer {

    void consume(final String message, final LoopPerformances loopPerformances);
}
