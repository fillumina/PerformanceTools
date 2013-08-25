package com.fillumina.performance.producer;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public interface InstrumentablePerformanceExecutor
        <T extends InstrumentablePerformanceExecutor<T>>
        extends PerformanceProducer<T> {

    <T extends PerformanceExecutorInstrumenter> T
            instrumentedBy(final T instrumenter);

    /**
     * The specified test will not be executed
     * (use this instead of comment out the line).
     */
    InstrumentablePerformanceExecutor<T> ignoreTest(final String name, final Runnable test);

    /**
     * Add a named test.
     */
    InstrumentablePerformanceExecutor<T> addTest(final String name, final Runnable test);

    /** Execute the test. */
    LoopPerformancesHolder execute();

    /** Execute the test without getting statistics. */
    InstrumentablePerformanceExecutor<T> warmup();
}
