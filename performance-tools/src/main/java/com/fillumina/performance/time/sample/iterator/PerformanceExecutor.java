package com.fillumina.performance.time.sample.iterator;

import com.fillumina.performance.time.sample.TimeSampleBuilder;
import com.fillumina.performance.util.collection.IndexedHashMap;
import com.fillumina.performance.util.tname.TName;

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
    TimeSampleBuilder executeIterations(
            final IndexedHashMap<TName, Runnable> tests,
            final int[] iterations);
}
