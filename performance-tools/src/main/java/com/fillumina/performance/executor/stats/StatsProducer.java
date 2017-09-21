package com.fillumina.performance.executor.stats;

import com.fillumina.performance.executor.MixedAssertableHolder;
import com.fillumina.performance.util.instrument.Instrumentable;
import com.fillumina.performance.executor.NamedTestExecutor;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public interface StatsProducer
                        <I extends StatsProducer<I,S>,
                         S extends Stats<?>>
    extends NamedTestExecutor
                    <I,
                     S,
                     Runnable,
                     MixedAssertableHolder>,
            Instrumentable<StatsProducer<?,?>> {
}
