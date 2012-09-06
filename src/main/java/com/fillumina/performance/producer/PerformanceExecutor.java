package com.fillumina.performance.producer;

/**
 *
 * @author fra
 */
public interface PerformanceExecutor<T extends PerformanceExecutor<?>>
        extends PerformanceProducer<T> {

    PerformanceExecutor<T> addTest(final String name, final Runnable test);
    LoopPerformancesHolder execute();
}
