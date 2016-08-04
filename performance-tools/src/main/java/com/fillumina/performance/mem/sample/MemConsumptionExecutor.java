package com.fillumina.performance.mem.sample;

import com.fillumina.performance.infrastructure.PerformanceConsumerNotifier;
import com.fillumina.performance.speed.sample.Testable;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public interface MemConsumptionExecutor
        extends PerformanceConsumerNotifier<MemSample> {

    long execute(String testName, Testable testable);
}
