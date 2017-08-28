package com.fillumina.performance.mem.sample;

import com.fillumina.performance.infrastructure.sample.AbstractSample;
import com.fillumina.performance.infrastructure.sample.SampleProducer;
import com.fillumina.performance.infrastructure.sample.SampleValue;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public interface MemConsumptionExecutor
                        <I extends MemConsumptionExecutor<I,S>,
                         S extends AbstractSample<S, SampleValue>>
    extends SampleProducer<I, S> {

    long execute(Runnable test);
}
