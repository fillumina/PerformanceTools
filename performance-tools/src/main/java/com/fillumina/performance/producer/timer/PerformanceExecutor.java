package com.fillumina.performance.producer.timer;

import com.fillumina.performance.producer.LoopPerformances;
import java.util.Map;

/**
 * Executes the required tests. This interface is useful to separate the
 * code that actually performs the test from the definition part so that
 * different methods can be used for example for testing in a single
 * or in a multi-threaded environment.
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public interface PerformanceExecutor {

    /** Executes the passed tests for the given number of iterations. */
    LoopPerformances executeTests(final long iterations,
            final Map<String, Runnable> tests);
}
