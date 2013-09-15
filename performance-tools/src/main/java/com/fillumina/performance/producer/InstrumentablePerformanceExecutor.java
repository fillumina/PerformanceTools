package com.fillumina.performance.producer;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public interface InstrumentablePerformanceExecutor
        <T extends InstrumentablePerformanceExecutor<T>>
        extends PerformanceProducer<T>, TestsContainer<T> {

    <T extends PerformanceExecutorInstrumenter> T
            instrumentedBy(final T instrumenter);

    /** Execute the test. */
    LoopPerformancesHolder execute();

    /** Execute the test without getting statistics. */
    InstrumentablePerformanceExecutor<T> warmup();
}
