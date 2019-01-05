package com.fillumina.performance.executor.stats.producer;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class AutoconfiguredStatsProducer
        extends ConfigurableStatsProducer {

    public interface Configuration
            extends
                FixedSamplesAndIterationsStrategy.Configuration,
                RequiredMarginStrategy.Configuration,
                ConfigurableStatsProducer.Configuration {
    }

    public AutoconfiguredStatsProducer(Configuration config) {
        super(config, selectStrategy(config));
    }

    private static ConfigurableStatsProducer.Strategy selectStrategy(
            Configuration config) {
        final ConfigurableStatsProducer.Strategy strategy;
        int[] iterations = config.getIterations();
        if (iterations != null) {
            strategy = new FixedSamplesAndIterationsStrategy(config);
        } else {
            strategy = new RequiredMarginStrategy(config);
        }
        return strategy;
    }

}
