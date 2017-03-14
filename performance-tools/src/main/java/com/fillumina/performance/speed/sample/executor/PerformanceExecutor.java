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

    /** Executes the passed tests for the given number of iterations. */
    SpeedSample executeTests(final Map<String, Testable> tests,
            final int[] iterations);
}
