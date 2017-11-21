package com.fillumina.performance.executor.stats;

import com.fillumina.performance.executor.TestExecutor;
import com.fillumina.performance.util.instrument.Instrumentable;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public interface StatsProducer<I extends StatsProducer<I>>
    extends TestExecutor
                    <I,
                     Stats,
                     Runnable,
                     MixedStatsHolder>,
            Instrumentable<StatsProducer<?>> {
}
