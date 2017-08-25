package com.fillumina.performance.infrastructure.sample;

import com.fillumina.performance.infrastructure.AbstractPerformanceProducer;
import java.util.Map;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public abstract class AbstractSampleProducer
                        <I extends SampleProducer<I,S>,
                         S extends Sample<S, ? extends TestSample>>
    extends AbstractPerformanceProducer
                    <SampleProducer<I,S>, S, Runnable, Map<Class<?>,S>>
    implements SampleProducer<I,S> {

}
