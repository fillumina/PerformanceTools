package com.fillumina.performance.executor.generator;

import com.fillumina.performance.executor.sample.SampleProducer;
import com.fillumina.performance.executor.stats.producer.AutoconfiguredStatsProducer;
import com.fillumina.performance.executor.stats.producer.ConsecutiveExecutorStatsProducer;
import com.fillumina.performance.executor.stats.producer.FixedSamplesAndIterationsStrategy;
import com.fillumina.performance.executor.stats.producer.RequiredMarginStrategy;
import com.fillumina.performance.executor.stats.producer.SampleProgressionStatusListener;
import com.fillumina.performance.executor.stats.producer.StatsProgressionStatusListener;
import com.fillumina.performance.time.sample.iterator.SelectorMultiThreadPerformanceExecutor;
import com.fillumina.performance.util.Activable;

/**
 * The configuration of a producer that executes a test and generates
 * the measurements about it (samples).
 * Each iteration of the experiment produces a
 * {@link com.fillumina.performance.sample.Sample} containing the measurements.
 * The iterations are repeated to generate statistics as
 * {@link com.fillumina.performance.executor.stats.Stats}.
 * A configuration can include a sample and a stats listener that will be
 * notified when a new sample or a new statistics is available. A listener
 * typically shows info about the experiment.
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public interface ProducerConfiguration
        extends
            Activable,
            AutoconfiguredStatsProducer.Configuration,
            ConsecutiveExecutorStatsProducer.Configuration,
            FixedSamplesAndIterationsStrategy.Configuration,
            RequiredMarginStrategy.Configuration,
            SelectorMultiThreadPerformanceExecutor.Configuration {

    SampleProducer<?> getSampleProducer();

    SampleProgressionStatusListener getSampleListener();

    StatsProgressionStatusListener getStatsListener();
}
