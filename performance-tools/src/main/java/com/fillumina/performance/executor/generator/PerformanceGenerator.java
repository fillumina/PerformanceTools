package com.fillumina.performance.executor.generator;

import com.fillumina.performance.executor.param.ParameterizedTestProducer;
import com.fillumina.performance.executor.param.SequencedTestProducer;
import com.fillumina.performance.executor.sample.SampleProducer;
import com.fillumina.performance.executor.stats.MixedStatsHolder;
import com.fillumina.performance.executor.stats.StatsHolder;
import com.fillumina.performance.executor.stats.StatsType;
import com.fillumina.performance.executor.stats.producer.ConfigurableStatsProducer;
import com.fillumina.performance.executor.stats.producer.ConsecutiveExecutorStatsProducer;
import com.fillumina.performance.executor.stats.producer.ExpressionStatsProducer;
import com.fillumina.performance.executor.stats.producer.FixedSamplesAndIterationsStrategy;
import com.fillumina.performance.executor.stats.producer.RequiredMarginStrategy;
import java.util.List;

/**
 * Executes tests based on configurations and returns statisitcs.
 * It is the main mechanism behind test execution.
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class PerformanceGenerator {

    public static final PerformanceGenerator INSTANCE =
            new PerformanceGenerator();

    public static interface Configuration {
        public TestConfiguration<?> getTestConfig();
        public List<ProducerConfiguration> getProducers();
    }

    public MixedStatsHolder executeMixedTests(Configuration conf) {
        return executeMixedTests(conf.getTestConfig(), conf.getProducers());
    }

    /** Executes a list of tests that return various different statistics. */
    public MixedStatsHolder executeMixedTests(
            TestConfiguration<?> testConfig,
            List<ProducerConfiguration> producers) {

        MixedStatsHolder.Builder builder = MixedStatsHolder.builder();

        // consolidate into a single MixedAssertableHolder
        producers.forEach(conf -> {
            MixedStatsHolder mixedHolder = executeSingleTest(testConfig, conf);
            mixedHolder.getStatsMap().forEach((StatsType type, StatsHolder holder) ->
                        builder.addAssertable(type, holder));
        });

        return builder.build();
    }

    /** Executes a single test that returns various different statistics. */
    @SuppressWarnings("unchecked")
    public MixedStatsHolder executeSingleTest(
            TestConfiguration<?> testConfig,
            ProducerConfiguration prodConfig) {

        if (!prodConfig.isActive()) {
            return MixedStatsHolder.EMPTY;
        }

        ConfigurableStatsProducer.Strategy strategy = selectStrategy(prodConfig);

        ConfigurableStatsProducer statsProducer =
                ((SampleProducer<?>) prodConfig.getSampleProducer())
                .instrumentedBy(
                        new ConfigurableStatsProducer(prodConfig, strategy));

        statsProducer
                .addSampleProgressionListener(prodConfig.getSampleListener())
                .addStatsProgressionListener(prodConfig.getStatsListener());

        ExpressionStatsProducer producer = statsProducer
                .instrumentedBy(new ConsecutiveExecutorStatsProducer(prodConfig))
                .instrumentedBy(new ParameterizedTestProducer(testConfig))
                .instrumentedBy(new SequencedTestProducer(testConfig))
                .instrumentedBy(new ExpressionStatsProducer(testConfig));

        return producer
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
