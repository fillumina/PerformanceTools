package com.fillumina.performance.executor.generator;

import com.fillumina.performance.executor.MixedAssertableHolder;
import com.fillumina.performance.executor.param.ParameterizedTestProducer;
import com.fillumina.performance.executor.param.SequencedTestProducer;
import com.fillumina.performance.executor.progression.ConfigurableStatsProducer;
import com.fillumina.performance.executor.progression.ConsecutiveExecutorStatsProducer;
import com.fillumina.performance.executor.progression.FixedSamplesAndIterationsStrategy;
import com.fillumina.performance.executor.progression.MatchRequiredMarginStrategy;
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

        MixedAssertableHolder.Joiner joiner =
                MixedAssertableHolder.joiner(testConfig.getName());

        producers.forEach(conf -> {
            MixedAssertableHolder mixedHolder =
                    executeSingleTest(testConfig, conf);
            joiner.addSubExperiment(mixedHolder);
        });

        return joiner.join();
    }

    @SuppressWarnings("unchecked")
    public MixedAssertableHolder executeSingleTest(
            TestConfiguration<?> testConfig,
            ProducerConfiguration producer) {

        if (!producer.isActive()) {
            return null;
        }

        ConfigurableStatsProducer.Strategy strategy = selectStrategy(producer);

        return ((SampleProducer<?, A>) producer.getSampleProducer())
                .instrumentedBy(new ConfigurableStatsProducer<>(
                                    producer, strategy))

                .addSampleProgressionListener(producer.getSampleListener())
                .addStatsProgressionListener(producer.getStatsListener())

                .instrumentedBy(new ConsecutiveExecutorStatsProducer(producer))
                .instrumentedBy(new ParameterizedTestProducer(testConfig))
                .instrumentedBy(new SequencedTestProducer(testConfig))

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
            strategy = new MatchRequiredMarginStrategy(producerConfig);
        }
        return strategy;
    }
}
