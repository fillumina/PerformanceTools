package com.fillumina.performance.executor.generator;

import com.fillumina.performance.executor.progression.ConfigurableStatsProducer;
import com.fillumina.performance.executor.progression.ConsecutiveExecutorStatsProducer;
import com.fillumina.performance.executor.progression.FixedSamplesAndIterationsStrategy;
import com.fillumina.performance.executor.progression.RequiredMarginStrategy;
import com.fillumina.performance.executor.progression.SampleProgressionStatusListener;
import com.fillumina.performance.executor.progression.StatsProgressionStatusListener;
import com.fillumina.performance.executor.sample.SampleProducer;
import com.fillumina.performance.time.sample.iterator.SelectorMultiThreadPerformanceExecutor;
import com.fillumina.performance.util.Activable;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public interface ProducerConfiguration
        extends
            Activable,
            ConfigurableStatsProducer.Configuration,
            ConsecutiveExecutorStatsProducer.Configuration,
            FixedSamplesAndIterationsStrategy.Configuration,
            RequiredMarginStrategy.Configuration,
            SelectorMultiThreadPerformanceExecutor.Configuration {

    SampleProducer<?,?> getSampleProducer();

    SampleProgressionStatusListener getSampleListener();

    StatsProgressionStatusListener getStatsListener();
}
