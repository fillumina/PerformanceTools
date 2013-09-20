package com.fillumina.performance.producer;

/**
 * Defines an executor able to take some tests as a {@link TestsContainer}
 * and execute them producing some performances as {@link LoopPerformances}
 * that will be passed to some
 * {@link com.fillumina.performance.consumer.PerformanceConsumer}s.
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public interface InstrumentablePerformanceExecutor
        <T extends InstrumentablePerformanceExecutor<T>>
        extends PerformanceProducer<T>, TestsContainer<T> {

    /**
     * Allows the executor to be piloted by another given class.
     * <p>
     * IMPORTANT: the returned object is the one passed as an argument so
     * in a <i>fluid interface</i> the next method refers to the argument:
     * <pre>
     *      first.instrumentedBy(second)
     *          .secondMethod();
     * </pre>
     * Is the same as:
     * <pre>
     *      first.instrumentedBy(second);
     *      second.secondMethod();
     * </pre>
     */
    <T extends PerformanceExecutorInstrumenter> T
            instrumentedBy(final T instrumenter);

    /** Execute the test. */
    LoopPerformancesHolder execute();

    /** Execute the test without getting statistics. */
    InstrumentablePerformanceExecutor<T> warmup();
}
