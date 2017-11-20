package com.fillumina.performance.time.sample;

import com.fillumina.performance.executor.sample.SampleProducer;

/**
 * A {@link AssertableProducer} that executes tests and returns their
 * execution time.
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public interface PerformanceTimer
        extends SampleProducer<PerformanceTimer> {

    /**
     * Measures the time it takes to perform the given iterations.
     * @param iterations the number of iterations to complete for every test.
     * @return a test sample
     */
    TimeSampleBuilder iterate(int iterations);

    /**
     * Measures the time it takes to perform the given iterations.
     * @param iterations indexed iterations for each test considered with their
     *        insertion order.
     * @return a test sample
     */
    TimeSampleBuilder iterate(int[] iterations);

    /**
     * This execution is not very reliable and should be used only as
     * a rough estimation of how many iterations can be done in a given time.
     *
     * @param milliseconds to iterate for each test
     * @return number of iterations executed (not very accurate)
     */
    int[] estimateIterations(long milliseconds);

    /**
     * Run exactly the same tests as {@link #execute()}.
     * It's used to warm up the JVM into pre-optimizing the code.
     * It uses the same number of iterations for all tests.
     * @param iterations the number of iterations to complete for every test.
     */
    PerformanceTimer warmup(int iterations);

    /**
     * Run exactly the same tests as {@link #execute()}.
     * It's used to warm up the JVM into pre-optimizing the code.
     * @param iterations indexed iterations for each test considered with their
     *        insertion order.
     */
    PerformanceTimer warmup(int[] iterations);
}
