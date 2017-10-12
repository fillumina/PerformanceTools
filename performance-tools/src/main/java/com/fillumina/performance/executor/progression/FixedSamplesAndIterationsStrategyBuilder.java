package com.fillumina.performance.executor.progression;

import com.fillumina.performance.util.Builder;
import java.io.Serializable;

/**
 *
 * @author Francesco Illuminati
 */
public class FixedSamplesAndIterationsStrategyBuilder<C>
        extends AbstractConfigurableStatsProducerBuilder
                <FixedSamplesAndIterationsStrategyBuilder<C>, C>
        implements
            FixedSamplesAndIterationsStrategy.Configuration,
            Builder<ConfigurableStatsProducer<?,?>>,
            Serializable {

    private static final long serialVersionUID = 1L;

    private int[] iterations;
    private int warmupSamples = 10;
    private int samples = 30;

    public static FixedSamplesAndIterationsStrategyBuilder
                <ConfigurableStatsProducer<?,?>> instance() {
        return new FixedSamplesAndIterationsStrategyBuilder<>();
    }

    /** Creates a builder with a default progression. */
    public FixedSamplesAndIterationsStrategyBuilder() {
        super();
    }

    public FixedSamplesAndIterationsStrategyBuilder(C caller) {
        super(caller);
    }

    public FixedSamplesAndIterationsStrategyBuilder(
            Setter<C, ConfigurableStatsProducer<?,?>> setter) {
        super(setter);
    }

    /** Sets the iterations to be performed at each step. */
    public FixedSamplesAndIterationsStrategyBuilder<C> setIterations(
            final int... iterations) {
        this.iterations = iterations;
        return this;
    }

    /** Sets the samples to be collected for each test. */
    public FixedSamplesAndIterationsStrategyBuilder<C> setWarmupSamples(
            final int warmupSamples) {
        this.warmupSamples = warmupSamples;
        return this;
    }

    /** Sets the samples to be collected for each test. */
    public FixedSamplesAndIterationsStrategyBuilder<C> setSamples(
            final int samplesPerStep) {
        this.samples = samplesPerStep;
        return this;
    }

    @Override
    public int[] getIterations() {
        return iterations;
    }

    @Override
    public int getWarmupSamples() {
        return warmupSamples;
    }

    @Override
    public int getSamples() {
        return samples;
    }

    @Override
    public ConfigurableStatsProducer<?,?> build() {
        FixedSamplesAndIterationsStrategy strategy =
                new FixedSamplesAndIterationsStrategy(this);
        return buildConfigurableStatsProducerWithStrategy(strategy);
    }
}
