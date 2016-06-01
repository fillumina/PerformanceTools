package com.fillumina.performance.sample.executor;

import com.fillumina.performance.sample.Testable;
import com.fillumina.performance.sample.PerformanceSample;
import java.util.Map;

/**
 * Test executor.
 *
 * @author Francesco Illuminati
 */
public interface PerformanceExecutor {

    /** Executes the passed tests for the given number of iterations. */
    PerformanceSample executeTests(final Map<String, Testable> tests,
            final int iterations);
}
