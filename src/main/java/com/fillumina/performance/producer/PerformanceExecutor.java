package com.fillumina.performance.producer;

import com.fillumina.performance.producer.timer.LoopPerformancesHolder;

/**
 *
 * @author fra
 */
public interface PerformanceExecutor<T extends PerformanceExecutor<?>>
        extends PerformanceProducer<T> {

    LoopPerformancesHolder execute();
}
