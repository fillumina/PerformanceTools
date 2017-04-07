package com.fillumina.performance.speed.sample;

import com.fillumina.performance.infrastructure.PerformanceProducer;
import com.fillumina.performance.infrastructure.TestContainer;
import com.fillumina.performance.infrastructure.Testable;
import com.fillumina.performance.util.instrument.Instrumentable;

/**
 * A {@link PerformanceProducer} that executes tests and returns their
 * execution time.
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public interface PerformanceTimer
        extends
            PerformanceProducer<SpeedSample, Testable>,
            TestContainer<Testable>,
            Instrumentable<PerformanceTimer> {

    /**
     * Executes the performance tests.
     * @param iterations the number of iterations to complete for every test.
     * @return a test sample
     */
    SpeedSample execute(int iterations);

    /**
     * Executes the performance tests.
     * @param iterations indexed iterations for each test considered with their
     *        insertion order.
     * @return a test sample
     */
    SpeedSample execute(int[] iterations);

    /**
     * This execution is not very reliable and should be used only as
     * a rough estimation of how many iterations can be done in a given time.
     *
     * @param milliseconds time to iterate for each test
     * @return number of iterations executed (not very accurate)
     */
    int[] iterationTimeEstimatorMs(long milliseconds);

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
