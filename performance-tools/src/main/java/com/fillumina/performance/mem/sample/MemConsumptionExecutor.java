package com.fillumina.performance.mem.sample;

import com.fillumina.performance.infrastructure.AssertableProducer;
import com.fillumina.performance.infrastructure.TestContainer;
import com.fillumina.performance.infrastructure.sample.Sample;
import com.fillumina.performance.infrastructure.sample.TestSample;
import com.fillumina.performance.util.instrument.Instrumentable;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public interface MemConsumptionExecutor<S extends Sample<S, TestSample>>
        extends
            AssertableProducer<Runnable>,
            TestContainer<Runnable>,
            Instrumentable<MemConsumptionExecutor<S>> {

    long execute(Runnable runnable);
}
