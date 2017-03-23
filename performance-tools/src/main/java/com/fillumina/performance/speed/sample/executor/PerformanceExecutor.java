package com.fillumina.performance.speed.sample.executor;

import com.fillumina.performance.infrastructure.Testable;
import com.fillumina.performance.speed.sample.SpeedSample;
import java.util.Map;

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
     * @param iterations number of iterations to execute for each test
     */
    SpeedSample executeTests(
            final Map<String, Testable> tests,
            final int[] iterations);
}
