package com.fillumina.performance.sample;

/**
 * An <b>instrumenter</b> is a sort of pilot who is able to execute
 * performance tests on a
 * ({@link InstrumentablePerformanceExecutor})
 * and read the results to perform its logic (i.e. repeat the tests until some
 * conditions verifies).
 *
 * @author Francesco Illuminati
 */
public interface PerformanceSampleProducerInstrumenter {

    /**
     * Embed an {@link InstrumentablePerformanceExecutor}.
     *
     * @see AbstractInstrumentablePerformanceProducer#instrumentedBy(PerformanceExecutorInstrumenter)
     * @return this to allows for
     *      <i><a href='http://en.wikipedia.org/wiki/Fluent_interface'>
     *      fluent interface</a></i>
     */
    PerformanceSampleProducerInstrumenter instrument(
            final PerformanceSampleProducer performanceSampleProducer);
}
