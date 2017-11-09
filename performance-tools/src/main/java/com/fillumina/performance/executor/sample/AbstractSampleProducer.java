package com.fillumina.performance.executor.sample;

import com.fillumina.performance.executor.AbstractTestExecutor;
import com.fillumina.performance.executor.stats.Stats;
import com.fillumina.performance.util.instrument.Instrumenter;
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
    extends AbstractTestExecutor<I, S, Runnable, Map<Class<?>,S>>
    implements SampleProducer<I,S> {

    @Override
    public <T extends Instrumenter<SampleProducer<?, S>>> T instrumentedBy(
            T instrumenter) {
        instrumenter.instrument(this);
        return instrumenter;
    }
}
