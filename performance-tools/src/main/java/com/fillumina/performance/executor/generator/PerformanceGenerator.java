package com.fillumina.performance.executor.generator;

import com.fillumina.performance.executor.param.ParameterizedTestProducer;
import com.fillumina.performance.executor.param.SequencedTestProducer;
import com.fillumina.performance.executor.sample.SampleProducer;
import com.fillumina.performance.executor.stats.MixedStatsHolder;
import com.fillumina.performance.executor.stats.StatsHolder;
import com.fillumina.performance.executor.stats.StatsType;
import com.fillumina.performance.executor.stats.producer.AutoconfiguredStatsProducer;
import com.fillumina.performance.executor.stats.producer.ConfigurableStatsProducer;
import com.fillumina.performance.executor.stats.producer.ConsecutiveExecutorStatsProducer;
import com.fillumina.performance.executor.stats.producer.ExpressionStatsProducer;
import java.util.List;

/**
 * Executes tests based on configurations and returns statistics.
 * It is important to notice that this class represents just one way to use
 * this API and it provides a complete workflow able to provide statistics out
 * of user specified executor and tests. The API is designed in a way so that
 * it can be composed to create whichever workflow is needed.
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

    /** Executes a list of tests that return various different statistics. */
    public MixedStatsHolder executeMixedTests(Configuration conf) {
        return executeMixedTests(conf.getTestConfig(), conf.getProducers());
    }

    /** Executes a list of tests that return various different statistics. */
    public MixedStatsHolder executeMixedTests(
            TestConfiguration<?> testConfig,
            List<ProducerConfiguration> producers) {

        MixedStatsHolder.Builder builder = MixedStatsHolder.builder();

        producers.forEach(conf -> {
            MixedStatsHolder mixedHolder = executeSingleTest(testConfig, conf);

            // consolidate the statistics into a single MixedStatsHolder
            mixedHolder.getStatsMap().forEach(
                    (StatsType type, StatsHolder holder) ->
                        builder.addStats(holder));
        });

        return builder.build();
    }

    /** Executes a single test that returns various different statistics. */
    @SuppressWarnings("unchecked")
    public MixedStatsHolder executeSingleTest(
            TestConfiguration<?> testConfig,
            ProducerConfiguration prodConfig) {

        // if the configuration is not active returns empty statistics
        if (!prodConfig.isActive()) {
            return MixedStatsHolder.EMPTY;
        }

        // adds stats generator
        ConfigurableStatsProducer statsProducer =
                ((SampleProducer<?>) prodConfig.getSampleProducer())
                .instrumentedBy(new AutoconfiguredStatsProducer(prodConfig));

        // adds listeners
        statsProducer
                .addSampleProgressionListener(prodConfig.getSampleListener())
                .addStatsProgressionListener(prodConfig.getStatsListener());

        // adds consecutive executor, parameters, sequences and expressions
        ExpressionStatsProducer producer = statsProducer
                .instrumentedBy(new ConsecutiveExecutorStatsProducer(prodConfig))
                .instrumentedBy(new ParameterizedTestProducer(testConfig))
                .instrumentedBy(new SequencedTestProducer(testConfig))
                .instrumentedBy(new ExpressionStatsProducer(testConfig));

        // sets the name and finally adds and executes tests
        return producer
                .setPathName(testConfig.getPathName())
                .addTests(testConfig.getTests())
                .execute();
    }
}
