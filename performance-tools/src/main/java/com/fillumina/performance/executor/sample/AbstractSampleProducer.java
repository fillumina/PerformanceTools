package com.fillumina.performance.executor.sample;

import com.fillumina.performance.executor.AbstractTestExecutor;
import com.fillumina.performance.executor.stats.StatsType;
import com.fillumina.performance.util.instrument.Instrumenter;
import java.util.Map;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public abstract class AbstractSampleProducer<I extends SampleProducer<I>>
    extends AbstractTestExecutor<I, Sample, Runnable, Map<StatsType, Sample>>
    implements SampleProducer<I> {

    @Override
    @SuppressWarnings("unchecked")
    public <T extends Instrumenter<SampleProducer<?>>> T instrumentedBy(
            T instrumenter) {
        instrumenter.instrument((I)this);
        return instrumenter;
    }
}
