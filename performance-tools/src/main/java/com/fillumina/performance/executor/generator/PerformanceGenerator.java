package com.fillumina.performance.executor.generator;

import com.fillumina.performance.executor.MixedAssertableHolder;
import com.fillumina.performance.executor.param.ParameterizedTestProducer;
import com.fillumina.performance.executor.param.SequencedTestProducer;
import com.fillumina.performance.executor.progression.ConfigurableStatsProducer;
import com.fillumina.performance.executor.progression.ConsecutiveExecutorStatsProducer;
import com.fillumina.performance.executor.progression.FixedSamplesAndIterationsStrategy;
import com.fillumina.performance.executor.progression.MatchRequiredMarginStrategy;
import com.fillumina.performance.executor.progression.SampleProgressionStatusListener;
import com.fillumina.performance.executor.progression.StatsProgressionStatusListener;
import com.fillumina.performance.executor.sample.AbstractSample;
import com.fillumina.performance.executor.sample.SampleProducer;
import com.fillumina.performance.executor.stats.Stats;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class PerformanceGenerator<S extends Stats<?>,
                                  A extends AbstractSample<A,?,S>> {

    public static final PerformanceGenerator<?,?> INSTANCE =
            new PerformanceGenerator<>();

    public MixedAssertableHolder generate(
            SampleProducer<?,A> sampleProducer,
            ProducerConfiguration<?> producerConfig,
            TestConfiguration<?> testConfig,
            SampleProgressionStatusListener sampleListener,
            StatsProgressionStatusListener statsListener,
            Verbosity verbosity) {

        if (!producerConfig.isActive()) {
            return null;
        }

        ConfigurableStatsProducer.Strategy strategy =
                selectStrategy(producerConfig);

        return sampleProducer
                .instrumentedBy(new ConfigurableStatsProducer<>(
                                    producerConfig, strategy))

                .addSampleProgressionListener(sampleListener)
                .addStatsProgressionListener(statsListener)

                .instrumentedBy(new ConsecutiveExecutorStatsProducer(producerConfig))
                .instrumentedBy(new ParameterizedTestProducer(testConfig))
                .instrumentedBy(new SequencedTestProducer(testConfig))

                .setName(testConfig.getName())
                .addTests(testConfig.getTests())

                .execute();
    }

    private ConfigurableStatsProducer.Strategy selectStrategy(
            ProducerConfiguration<?> producerConfig) {
        final ConfigurableStatsProducer.Strategy strategy;
        int[] iterations = producerConfig.getIterations();
        if (iterations != null) {
            strategy = new FixedSamplesAndIterationsStrategy(producerConfig);
        } else {
            strategy = new MatchRequiredMarginStrategy(producerConfig);
        }
        return strategy;
    }
}
