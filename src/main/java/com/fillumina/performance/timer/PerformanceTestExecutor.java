package com.fillumina.performance.timer;

import java.util.Map;

/**
 *
 * @author fra
 */
public interface PerformanceTestExecutor {

    void executeTests(final int times,
            final Map<String, Runnable> executors,
            final RunningPerformances timeMap);
}