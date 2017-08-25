package com.fillumina.performance.mem.sample;

import com.fillumina.performance.infrastructure.sample.Sample;
import com.fillumina.performance.infrastructure.sample.SampleProducer;
import com.fillumina.performance.infrastructure.sample.TestSample;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public interface MemConsumptionExecutor
                        <I extends MemConsumptionExecutor<I,S>,
                         S extends Sample<S, TestSample>>
    extends SampleProducer<I, S> {

    long execute(Runnable test);
}
