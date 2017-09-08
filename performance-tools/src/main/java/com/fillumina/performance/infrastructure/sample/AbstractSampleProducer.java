package com.fillumina.performance.infrastructure.sample;

import com.fillumina.performance.infrastructure.AbstractPerformanceProducer;
import com.fillumina.performance.infrastructure.stats.Stats;
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
    extends AbstractPerformanceProducer<I, S, Runnable, Map<Class<?>,S>>
    implements SampleProducer<I,S> {

}
