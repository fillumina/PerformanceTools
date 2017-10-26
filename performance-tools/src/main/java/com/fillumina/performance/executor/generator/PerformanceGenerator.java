package com.fillumina.performance.executor.generator;

import com.fillumina.performance.executor.MixedAssertableHolder;
import com.fillumina.performance.executor.param.ParameterizedTestProducer;
import com.fillumina.performance.executor.param.SequencedTestProducer;
import com.fillumina.performance.executor.stats.producer.ConfigurableStatsProducer;
import com.fillumina.performance.executor.stats.producer.ConsecutiveExecutorStatsProducer;
import com.fillumina.performance.executor.stats.producer.FixedSamplesAndIterationsStrategy;
import com.fillumina.performance.executor.stats.producer.RequiredMarginStrategy;
import com.fillumina.performance.executor.sample.AbstractSample;
import com.fillumina.performance.executor.sample.SampleProducer;
import com.fillumina.performance.executor.stats.Stats;
import java.util.List;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class PerformanceGenerator<S extends Stats<?>,
                                  A extends AbstractSample<A,?,S>> {

    public static final PerformanceGenerator<?,?> INSTANCE =
            new PerformanceGenerator<>();

    public static interface Configuration {
        public TestConfiguration<?> getTestConfig();
        public List<ProducerConfiguration> getProducers();
    }

    public MixedAssertableHolder executeMixedTests(Configuration conf) {
        return executeMixedTests(conf.getTestConfig(), conf.getProducers());
    }

    public MixedAssertableHolder executeMixedTests(
            TestConfiguration<?> testConfig,
            List<ProducerConfiguration> producers) {

        MixedAssertableHolder.Builder builder =
                MixedAssertableHolder.builder();

        // consolidate into a single MixedAssertableHolder
        producers.forEach(conf -> {
            MixedAssertableHolder mixedHolder =
                    executeSingleTest(testConfig, conf);
            if (mixedHolder != null) {
                mixedHolder.getStatsMap().forEach((type, holder) ->
                        builder.addAssertable(type, testConfig.getName(),
                                holder.getAssertable()));
            }
        });

        return builder.build();
    }

    @SuppressWarnings("unchecked")
    public MixedAssertableHolder executeSingleTest(
            TestConfiguration<?> testConfig,
            ProducerConfiguration prodConfig) {

        if (!prodConfig.isActive()) {
            return null;
        }

        ConfigurableStatsProducer.Strategy strategy = selectStrategy(prodConfig);

        ConfigurableStatsProducer<S, A> statsProducer =
                ((SampleProducer<?, A>) prodConfig.getSampleProducer())
                .instrumentedBy(new ConfigurableStatsProducer<>(
                                prodConfig, strategy));

        statsProducer
                .addSampleProgressionListener(prodConfig.getSampleListener())
                .addStatsProgressionListener(prodConfig.getStatsListener());

        SequencedTestProducer res = statsProducer
                .instrumentedBy(new ConsecutiveExecutorStatsProducer(prodConfig))
                .instrumentedBy(new ParameterizedTestProducer(testConfig))
                .instrumentedBy(new SequencedTestProducer(testConfig));

        return res
                .setName(testConfig.getName())
                .addTests(testConfig.getTests())

                .execute();
    }

    private ConfigurableStatsProducer.Strategy selectStrategy(
            ProducerConfiguration producerConfig) {
        final ConfigurableStatsProducer.Strategy strategy;
        int[] iterations = producerConfig.getIterations();
        if (iterations != null) {
            strategy = new FixedSamplesAndIterationsStrategy(producerConfig);
        } else {
            strategy = new RequiredMarginStrategy(producerConfig);
        }
        return strategy;
    }
}
