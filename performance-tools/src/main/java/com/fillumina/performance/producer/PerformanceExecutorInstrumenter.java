package com.fillumina.performance.producer;

/**
 * An instrumenter is a sort of pilot who is able to execute
 * performance tests on a instrumentable
 * ({@link InstrumentablePerformanceExecutor})
 * and read the results to perform its logic (i.e. repeat the tests until some
 * conditions verifies).
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public interface PerformanceExecutorInstrumenter {

    /**
     * Embed an {@link InstrumentablePerformanceExecutor}.
     *
     * @see AbstractInstrumentablePerformanceProducer#instrumentedBy(com.fillumina.performance.producer.PerformanceExecutorInstrumenter)
     * @return this to allows for
     *      <i><a href='http://en.wikipedia.org/wiki/Fluent_interface'>
     *      fluent interface</a></i>
     */
    PerformanceExecutorInstrumenter instrument(
            final InstrumentablePerformanceExecutor<?> performanceExecutor);
}
