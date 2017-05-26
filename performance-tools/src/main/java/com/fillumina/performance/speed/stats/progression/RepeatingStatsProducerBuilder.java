package com.fillumina.performance.speed.stats.progression;

import com.fillumina.performance.util.Builder;
import java.io.Serializable;

public class RepeatingStatsProducerBuilder<C>
        extends AbstractConfigurableStatsProducerBuilder
                    <RepeatingStatsProducerBuilder<C>, C>
        implements
            RepeatingStrategy.Configuration,
            Builder<ConfigurableStatsProducer>,
            Serializable {

    private static final long serialVersionUID = 1L;

    private int iterations = 1_000;
    private int samples = 33;
    private boolean incrementIterations = true;
    private double maxPercentageMargin = 5;
    private boolean autodiscoverBaseIterations = true;
    private int approximateSampleMillis = 250;

    public static RepeatingStatsProducerBuilder
            <ConfigurableStatsProducer> instance() {
        return new RepeatingStatsProducerBuilder<>();
    }

    public RepeatingStatsProducerBuilder() {
        super();
    }

    public RepeatingStatsProducerBuilder(C caller) {
        super(caller);
    }

    public RepeatingStatsProducerBuilder(
            Setter<C, ConfigurableStatsProducer> setter) {
        super(setter);
    }

    public RepeatingStatsProducerBuilder<C> setBaseIterations(
            int iterations) {
        setAutodiscoverBaseIterations(false);
        this.iterations = iterations;
        return this;
    }

    /**
     * Setting samples to -1 uses an automatic value so that there are
     * at least 2_000 iterations completed (1_000 iterations are needed
     * on default JVM settings to start optimizing the code).
     *
     * @param samples
     * @return
     */
    public RepeatingStatsProducerBuilder<C> setSamples(
            int samples) {
        this.samples = samples;
        return this;
    }

    public RepeatingStatsProducerBuilder<C>
                setIncrementIterations(boolean incrementIteration) {
        this.incrementIterations = incrementIteration;
        return this;
    }

    public RepeatingStatsProducerBuilder<C>
                incrementIterations() {
        this.incrementIterations = true;
        return this;
    }

    public RepeatingStatsProducerBuilder<C>
                incrementSamples() {
        this.incrementIterations = false;
        return this;
    }

    public RepeatingStatsProducerBuilder<C>
                setMaxPercentageMargin(double maxPercentageMargin) {
        this.maxPercentageMargin = maxPercentageMargin;
        return this;
    }

    public RepeatingStatsProducerBuilder<C>
                setAutodiscoverBaseIterations(boolean autodiscoverBaseIterations) {
        this.autodiscoverBaseIterations = autodiscoverBaseIterations;
        return this;
    }

    public RepeatingStatsProducerBuilder<C>
                setApproximateSampleMillis(int approximateSampleMillis) {
        this.approximateSampleMillis = approximateSampleMillis;
        return this;
    }

    public RepeatingStatsProducerBuilder<C>
                setAutoDiscoverSamples(boolean autodiscoverSamples) {
        if (autodiscoverSamples) {
            this.samples = -1;
        } else {
            this.samples = 40;
        }
        return this;
    }

    @Override
    public int getIterations() {
        return iterations;
    }

    @Override
    public int getSamples() {
        return samples;
    }

    @Override
    public boolean getIncrementIteration() {
        return incrementIterations;
    }

    @Override
    public double getMaxPercentageMargin() {
        return maxPercentageMargin;
    }

    @Override
    public boolean getAutodiscoverBaseIterations() {
        return autodiscoverBaseIterations;
    }

    @Override
    public int getApproximateSampleMillis() {
        return approximateSampleMillis;
    }

    @Override
    public ConfigurableStatsProducer build() {
        RepeatingStrategy strategy = new RepeatingStrategy(this);
        return buildConfigurableStatsProducerWithStrategy(strategy);
    }
}
