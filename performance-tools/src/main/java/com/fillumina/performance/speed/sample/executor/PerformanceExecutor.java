package com.fillumina.performance.speed.sample.executor;

import com.fillumina.performance.infrastructure.Testable;
import com.fillumina.performance.speed.sample.SpeedSample;
import java.util.LinkedHashMap;

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
     * @param bound number of iterations to execute for each test or
     *              time to execute depending on the implementation.
     */
    SpeedSample executeTests(
            final LinkedHashMap<String, Testable> tests,
            final int[] bound);

}
