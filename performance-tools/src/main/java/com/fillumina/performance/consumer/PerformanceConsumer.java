package com.fillumina.performance.consumer;

import com.fillumina.performance.producer.LoopPerformances;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public interface PerformanceConsumer {

    /**
     *
     * Developer: if {@link LoopPerformances#EMPTY} is received nothing should
     * be performed.
     *
     * @param message
     * @param loopPerformances
     */
    void consume(final String message, final LoopPerformances loopPerformances);
}
