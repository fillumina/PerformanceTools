package com.fillumina.performance.producer.timer;

import com.fillumina.performance.producer.LoopPerformances;
import java.util.Map;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public interface PerformanceExecutor {

    LoopPerformances executeTests(final long iterations,
            final Map<String, Runnable> tests);
}
