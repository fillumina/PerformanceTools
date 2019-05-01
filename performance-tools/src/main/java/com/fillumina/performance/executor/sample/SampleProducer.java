package com.fillumina.performance.executor.sample;

import com.fillumina.performance.executor.TestExecutor;
import com.fillumina.performance.executor.stats.StatsType;
import com.fillumina.performance.util.instrument.Instrumentable;
import java.util.Map;

/**
 *
 * @param I self
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public interface SampleProducer<I extends SampleProducer<I>>
    extends TestExecutor<I, Sample, Runnable, Map<StatsType,Sample>>,
            Instrumentable<SampleProducer<?>> {

    /**
     * Execute a bunch of tests each with the specified ordered number
     * of iterations.
     *
     * @param iterations the ordered array of iterations
     * @return
     */
    Map<StatsType,Sample> executeWithIterations(int... iterations);
}
