package com.fillumina.performance.mem.sample;

import com.fillumina.performance.util.TName;
import com.fillumina.performance.infrastructure.AssertableConsumerNotifier;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public interface MemConsumptionExecutor
        extends AssertableConsumerNotifier<MemSample> {

    long execute(TName testName, Runnable runnable);
}
