package com.fillumina.performance.producer.timer;

import com.fillumina.performance.producer.LoopPerformances;
import java.util.Map;

/**
 *
 * @author fra
 */
public interface PerformanceTestExecutor {

    LoopPerformances executeTests(final int iterations,
            final Map<String, Runnable> tests);
}
