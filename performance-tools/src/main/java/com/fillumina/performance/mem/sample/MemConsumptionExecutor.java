package com.fillumina.performance.mem.sample;

import com.fillumina.performance.infrastructure.AssertableConsumerNotifier;
import com.fillumina.performance.mem.MemPerformance;
import com.fillumina.performance.mem.MemStats;
import com.fillumina.performance.util.TName;
import java.util.Map;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public interface MemConsumptionExecutor extends AssertableConsumerNotifier {

    MemStats createStats(Map<TName, MemPerformance> map);

    long execute(TName testName, Runnable runnable);
}
