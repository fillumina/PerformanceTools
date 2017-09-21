package com.fillumina.performance.sample;

import com.fillumina.performance.executor.AbstractNamedTestExecutor;
import com.fillumina.performance.stats.Stats;
import java.util.Map;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public abstract class AbstractSampleProducer
                        <I extends SampleProducer<I,S>,
                         S extends AbstractSample<S,
                                                  ? extends SampleValue,
                                                  ? extends Stats<?>>>
    extends AbstractNamedTestExecutor<I, S, Runnable, Map<Class<?>,S>>
    implements SampleProducer<I,S> {

}
