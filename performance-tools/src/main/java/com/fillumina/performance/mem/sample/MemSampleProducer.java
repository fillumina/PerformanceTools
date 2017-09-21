package com.fillumina.performance.mem.sample;

import com.fillumina.performance.executor.sample.AbstractSample;
import com.fillumina.performance.executor.sample.SampleProducer;
import com.fillumina.performance.executor.sample.SampleValue;
import com.fillumina.performance.mem.stats.MemStats;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public interface MemSampleProducer
                        <I extends MemSampleProducer<I,A>,
                         A extends AbstractSample<A,
                                                  SampleValue,
                                                  ? extends MemStats>>
    extends SampleProducer<I, A> {

    long execute(Runnable test);
}
