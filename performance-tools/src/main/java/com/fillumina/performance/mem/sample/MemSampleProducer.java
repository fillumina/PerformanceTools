package com.fillumina.performance.mem.sample;

import com.fillumina.performance.executor.sample.SampleProducer;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public interface MemSampleProducer<I extends MemSampleProducer<I>>
    extends SampleProducer<I> {

    long execute(Runnable test);
}
