package com.fillumina.performance.sample;

import com.fillumina.performance.stats.Stats;
import java.util.Map;
import com.fillumina.performance.executor.NamedTestExecutor;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public interface SampleProducer
                        <I extends SampleProducer<I,S>,
                         S extends AbstractSample<S,
                                                  ? extends SampleValue,
                                                  ? extends Stats<?>>>
    extends NamedTestExecutor
                    <I,
                     S,
                     Runnable,
                     Map<Class<?>,S>> {

}
