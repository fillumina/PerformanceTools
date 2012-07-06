package com.fillumina.performance.producer.timer;

import java.util.Map;

/**
 *
 * @author fra
 */
public interface PerformanceExecutor {

    void executeTests(final int times,
            final Map<String, Runnable> executors,
            final RunningPerformances timeMap);
}
