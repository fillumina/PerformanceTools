package com.fillumina.performance.executor.sample;

import com.fillumina.performance.executor.NamedTestExecutor;
import com.fillumina.performance.executor.stats.Stats;
import com.fillumina.performance.util.instrument.Instrumentable;
import java.util.Map;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public interface SampleProducer
                        <I extends SampleProducer<I,S>,
                         S extends AbstractSample<S,
                                                  ? extends SampleValue,
                                                  ? extends Stats<?>>>
    extends NamedTestExecutor<I, S, Runnable, Map<Class<?>,S>>,
            Instrumentable<SampleProducer<?,S>> {

    Map<Class<?>,S> executeWithIterations(int... iterations);
}
