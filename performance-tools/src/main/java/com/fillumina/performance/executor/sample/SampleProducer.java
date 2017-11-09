package com.fillumina.performance.executor.sample;

import com.fillumina.performance.executor.stats.Stats;
import com.fillumina.performance.util.instrument.Instrumentable;
import java.util.Map;
import com.fillumina.performance.executor.TestExecutor;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public interface SampleProducer
                        <I extends SampleProducer<I,S>,
                         S extends AbstractSample<S,
                                                  ? extends SampleValue,
                                                  ? extends Stats<?>>>
    extends TestExecutor<I, S, Runnable, Map<Class<?>,S>>,
            Instrumentable<SampleProducer<?,S>> {

    Map<Class<?>,S> executeWithIterations(int... iterations);
}
