package com.fillumina.performance.producer;

/**
 * An instrumenter is able to make a {@link PerformanceExecutor} to
 * execute its tests.
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public interface PerformanceExecutorInstrumenter {

    /**
     * Embed an {@link InstrumentablePerformanceExecutor}.
     *
     * @see AbstractInstrumentablePerformanceProducer#instrumentedBy(com.fillumina.performance.producer.PerformanceExecutorInstrumenter)
     * @return this to allows for <i>fluid interface</i>
     */
    PerformanceExecutorInstrumenter instrument(
            final InstrumentablePerformanceExecutor<?> performanceExecutor);
}
