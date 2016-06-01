package com.fillumina.performance.sample;

import com.fillumina.performance.infrastructure.PerformanceProducer;
import com.fillumina.performance.infrastructure.TestContainer;
import com.fillumina.performance.util.instrument.Instrumentable;

/**
 * A {@link PerformanceProducer} that executes the given tests and returns
 * a sample.
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public interface PerformanceTimer
        extends PerformanceProducer<PerformanceSample, Testable>,
            TestContainer<Testable>, Instrumentable<PerformanceTimer> {

    /**
     * Executes the performance test.
     */
    PerformanceSample execute(int iterations);

    /**
     * This execution is not very reliable and should be used only as
     * a rough estimation of how many iterations can be done in a given time
     * with very few or no optimizations at all.
     *
     * @param milliseconds
     * @return number of iterations executed (not very accurate)
     */
    int iterationTimeEstimator(long milliseconds);

    /**
     * Run exactly the same tests as {@link #execute()} without taking
     * any statistics. It's used to warm up the JVM into optimizing the code.
     */
    //TODO it's really useful? can we just use execute() and ignore its results?
    DefaultPerformanceTimer warmup(int iterations);
}
