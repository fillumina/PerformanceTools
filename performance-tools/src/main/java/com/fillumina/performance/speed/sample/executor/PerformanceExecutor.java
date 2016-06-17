package com.fillumina.performance.speed.sample.executor;

import com.fillumina.performance.speed.sample.PerformanceSample;
import com.fillumina.performance.speed.sample.Testable;
import java.util.Map;

/**
 * Test executor.
 *
 * @author Francesco Illuminati
 */
public interface PerformanceExecutor {

    /** Executes the passed tests for the given number of iterations. */
    PerformanceSample executeTests(final Map<String, Testable> tests,
            final int[] iterations);
}
