package com.fillumina.performance.infrastructure.sample;

import com.fillumina.performance.infrastructure.PerformanceProducer;
import java.util.Map;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public interface SampleProducer
                        <I extends SampleProducer<I,S>,
                         S extends AbstractSample<S, ? extends SampleValue>>
    extends PerformanceProducer
                    <SampleProducer<I,S>,
                     S,
                     Runnable,
                     Map<Class<?>,S>> {

}
