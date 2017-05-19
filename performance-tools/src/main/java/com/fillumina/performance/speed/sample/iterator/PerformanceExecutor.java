package com.fillumina.performance.speed.sample.iterator;

import com.fillumina.performance.speed.sample.SpeedSample;
import com.fillumina.performance.util.TName;
import com.fillumina.performance.util.collection.LinkedMap;

/**
 * Test executor.
 *
 * @author Francesco Illuminati
 */
public interface PerformanceExecutor {

    /**
     * Executes the passed tests for the given number of iterations.
     *
     * @param tests ordered map of tests
     * @param iterations number of iterations to execute for each test or
     *              time to execute depending on the implementation.
     */
    SpeedSample executeTests(
            final LinkedMap<TName, Runnable> tests,
            final int[] iterations);

}
