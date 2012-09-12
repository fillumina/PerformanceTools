package com.fillumina.performance.producer;

/**
 *
 * @author fra
 */
public interface InstrumentablePerformanceExecutor
        <T extends InstrumentablePerformanceExecutor<T>>
        extends PerformanceProducer<T> {

    <T extends PerformanceExecutorInstrumenter> T
            instrumentedBy(final T instrumenter);

    InstrumentablePerformanceExecutor<T> addTest(final String name, final Runnable test);
    
    LoopPerformancesHolder execute();
}
