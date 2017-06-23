package com.fillumina.performance.time.stats.progression;

import com.fillumina.performance.time.stats.TimeStats;
import com.fillumina.performance.util.Builder;
import com.fillumina.performance.util.stats.Ratio;
import java.io.Serializable;

public class RepeatingStatsProducerBuilder<C, T extends TimeStats>
        extends AbstractConfigurableStatsProducerBuilder
                    <RepeatingStatsProducerBuilder<C,T>, C, T>
        implements
            RepeatingStrategy.Configuration,
            Builder<ConfigurableStatsProducer<T>>,
            Serializable {

    private static final long serialVersionUID = 1L;

    private int iterations = 1_000;
    private int samples = 33;
    private boolean incrementIterations = true;
    private Ratio maxPercentageMargin = Ratio.percentage(5);
    private boolean autodiscoverBaseIterations = true;
    private int approximateSampleMillis = 250;
    private int warmupIterations;

    public static <T extends TimeStats>
            RepeatingStatsProducerBuilder<ConfigurableStatsProducer<T>, T>
                instance() {
        return new RepeatingStatsProducerBuilder<>();
    }

    public RepeatingStatsProducerBuilder() {
        super();
    }

    public RepeatingStatsProducerBuilder(C caller) {
        super(caller);
    }

    public RepeatingStatsProducerBuilder(
            Setter<C, ConfigurableStatsProducer<T>> setter) {
        super(setter);
    }

    public RepeatingStatsProducerBuilder<C,T> setBaseIterations(
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
    public RepeatingStatsProducerBuilder<C,T> setSamples(
            int samples) {
        this.samples = samples;
        return this;
    }

    public RepeatingStatsProducerBuilder<C,T>
                setIncrementIterations(boolean incrementIteration) {
        this.incrementIterations = incrementIteration;
        return this;
    }

    public RepeatingStatsProducerBuilder<C,T>
                incrementIterations() {
        this.incrementIterations = true;
        return this;
    }

    public RepeatingStatsProducerBuilder<C,T>
                incrementSamples() {
        this.incrementIterations = false;
        return this;
    }

    public RepeatingStatsProducerBuilder<C,T>
                setMaxPercentageMargin(Ratio maxPercentageMargin) {
        this.maxPercentageMargin = maxPercentageMargin;
        return this;
    }

    public RepeatingStatsProducerBuilder<C,T>
                setAutodiscoverBaseIterations(boolean autodiscoverBaseIterations) {
        this.autodiscoverBaseIterations = autodiscoverBaseIterations;
        return this;
    }

    public RepeatingStatsProducerBuilder<C,T>
                setApproximateSampleMillis(int approximateSampleMillis) {
        this.approximateSampleMillis = approximateSampleMillis;
        return this;
    }

    public RepeatingStatsProducerBuilder<C,T>
                setAutoDiscoverSamples(boolean autodiscoverSamples) {
        if (autodiscoverSamples) {
            this.samples = -1;
        } else {
            this.samples = 40;
        }
        return this;
    }

    public RepeatingStatsProducerBuilder<C,T> warmupIterations(final int value) {
        this.warmupIterations = value;
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
    public Ratio getMaxPercentageMargin() {
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
    public ConfigurableStatsProducer<T> build() {
        RepeatingStrategy<T> strategy = new RepeatingStrategy<>(this);
        return buildConfigurableStatsProducerWithStrategy(strategy);
    }
}
