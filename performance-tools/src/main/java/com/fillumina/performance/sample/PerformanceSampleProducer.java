package com.fillumina.performance.sample;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public interface PerformanceSampleProducer {

    /** Used with fluid interface. */
    <I extends PerformanceSampleProducerInstrumenter> I instrumentedBy(
            I instrumenter);

    PerformanceSampleProducer addPerformanceSampleConsumerIf(boolean condition,
            PerformanceSampleConsumer... consumers);

    PerformanceSampleProducer addPerformanceSampleConsumer(
            PerformanceSampleConsumer... consumers);

    PerformanceSampleProducer warmup(int iterations);

    PerformanceSample execute(int iterations);

    int iterationTimeEstimator(long timoutMilliseconds);
}
