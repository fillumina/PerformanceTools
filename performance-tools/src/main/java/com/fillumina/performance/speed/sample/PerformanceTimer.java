package com.fillumina.performance.speed.sample;

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
        extends PerformanceProducer<SpeedSample, SpeedSample, Testable>,
            TestContainer<Testable>, Instrumentable<PerformanceTimer> {

    /**
     * Executes the performance test.
     * It uses the same number of iterations for all tests.
     */
    SpeedSample execute(int iterations);

    /** Executes the performance test. */
    SpeedSample execute(int[] iterations);

    /**
     * This execution is not very reliable and should be used only as
     * a rough estimation of how many iterations can be done in a given time
     * with very few or no optimizations at all.
     *
     * @param milliseconds
     * @return number of iterations executed (not very accurate)
     */
    int[] iterationTimeEstimator(long milliseconds);

    /**
     * Run exactly the same tests as {@link #execute()} without taking
     * any statistics. It's used to warm up the JVM into optimizing the code.
     * It uses the same number of iterations for all tests.
     */
    DefaultPerformanceTimer warmup(int iterations);

    /**
     * Run exactly the same tests as {@link #execute()} without taking
     * any statistics. It's used to warm up the JVM into optimizing the code.
     */
    DefaultPerformanceTimer warmup(int[] iterations);
}
