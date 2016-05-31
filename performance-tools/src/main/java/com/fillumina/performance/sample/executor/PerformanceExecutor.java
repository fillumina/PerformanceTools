package com.fillumina.performance.sample.executor;

import com.fillumina.performance.infrastructure.Testable;
import com.fillumina.performance.sample.PerformanceSample;
import java.util.Map;

/**
 * Executes the required tests. This interface is useful to separate the
 * code that actually performs the test from the definition part so that
 * different methods can be used for example for testing in a single
 * or in a multi-threaded environment.
 *
 * @author Francesco Illuminati
 */
public interface PerformanceExecutor {

    /** Executes the passed tests for the given number of iterations. */
    PerformanceSample executeTests(final Map<String, Testable> tests,
            final int iterations);
}
