package com.fillumina.performance.time.sample;

import com.fillumina.performance.infrastructure.PerformanceProducer;
import com.fillumina.performance.infrastructure.TestContainer;
import com.fillumina.performance.util.instrument.Instrumentable;

/**
 * A {@link PerformanceProducer} that executes tests and returns their
 * execution time.
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public interface PerformanceTimer
        extends
            PerformanceProducer<TimeSample, Runnable>,
            TestContainer<Runnable>,
            Instrumentable<PerformanceTimer> {

    interface Warmup {
        long[] warmup();
    }

    /**
     * Measures the time it takes to perform the given iterations.
     * @param iterations the number of iterations to complete for every test.
     * @return a test sample
     */
    TimeSample iterate(int iterations);

    /**
     * Measures the time it takes to perform the given iterations.
     * @param iterations indexed iterations for each test considered with their
     *        insertion order.
     * @return a test sample
     */
    TimeSample iterate(int[] iterations);

    /**
     * This execution is not very reliable and should be used only as
     * a rough estimation of how many iterations can be done on a given time.
     *
     * @param warmupRepetitions how many warmup should be done
     * @param milliseconds to iterate for each test
     * @return number of iterations executed (not very accurate)
     */
    int[] estimateIterations(long milliseconds);

    /**
     * Runs each test approximately for the given amount of milliseconds.
     * It should be called <i>before</i> {@link #estimateIterations(long) }.
     * In order to properly warmup it is recommended to call this method at
     * least 5 times each one for 1_000 ms. Each test would be executed for
     * the same amount of iterations.
     *
     */
    Warmup warmUpMillis(long millis);

    /**
     * Run exactly the same tests as {@link #execute()} without taking
     * any statistics. It's used to warm up the JVM into optimizing the code.
     * It uses the same number of iterations for all tests.
     * @param iterations the number of iterations to complete for every test.
     */
    PerformanceTimer warmup(int iterations);

    /**
     * Run exactly the same tests as {@link #execute()} without taking
     * any statistics. It's used to warm up the JVM into optimizing the code.
     * @param iterations indexed iterations for each test considered with their
     *        insertion order.
     */
    PerformanceTimer warmup(int[] iterations);
}
