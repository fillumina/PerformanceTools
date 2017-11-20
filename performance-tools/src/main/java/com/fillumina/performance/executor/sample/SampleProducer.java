package com.fillumina.performance.executor.sample;

import com.fillumina.performance.executor.TestExecutor;
import com.fillumina.performance.executor.stats.Stats;
import com.fillumina.performance.util.instrument.Instrumentable;
import java.util.Map;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public interface SampleProducer<I extends SampleProducer<I>>
    extends TestExecutor<I, Sample, Runnable, Map<Stats.Type,Sample>>,
            Instrumentable<SampleProducer<?>> {

    Map<Stats.Type,Sample> executeWithIterations(int... iterations);
}
