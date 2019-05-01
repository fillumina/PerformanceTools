package com.fillumina.performance.executor.sample;

import com.fillumina.performance.executor.AbstractTestExecutor;
import com.fillumina.performance.executor.stats.StatsType;
import java.util.Map;

/**
 * Defines the sample producer that will be used by the API to produce
 * statistics.
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public abstract class AbstractSampleProducer<I extends AbstractSampleProducer<I>>
    extends AbstractTestExecutor<I, Sample, Runnable, Map<StatsType, Sample>>
    implements SampleProducer<I> {

}
