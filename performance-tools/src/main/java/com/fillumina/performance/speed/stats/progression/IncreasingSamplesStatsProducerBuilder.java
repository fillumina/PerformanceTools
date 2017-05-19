package com.fillumina.performance.speed.stats.progression;

import com.fillumina.performance.util.Builder;
import java.io.Serializable;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class IncreasingSamplesStatsProducerBuilder<C>
        extends AbstractConfigurableStatsProducerBuilder
                    <IncreasingSamplesStatsProducerBuilder<C>, C>
        implements
            IncreasingSamplesStrategy.Configuration,
            Builder<ConfigurableStatsProducer>,
            Serializable {

    private static final long serialVersionUID = 1L;

    private int samples = 40;
    private double maxPercentageMargin = 5.0;
    private int approximateSampleMillis = 250;

    public static IncreasingSamplesStatsProducerBuilder
            <ConfigurableStatsProducer> instance() {
        return new IncreasingSamplesStatsProducerBuilder<>();
    }

    /** Creates a builder with a default progression. */
    public IncreasingSamplesStatsProducerBuilder() {
        super();
    }

    public IncreasingSamplesStatsProducerBuilder(C caller) {
        super(caller);
    }

    public IncreasingSamplesStatsProducerBuilder(
            Setter<C, ConfigurableStatsProducer> setter) {
        super(setter);
    }

    /** Sets the samples to be collected for each test. */
    public IncreasingSamplesStatsProducerBuilder<C> setSamples(int samples) {
        this.samples = samples;
        return this;
    }

    public IncreasingSamplesStatsProducerBuilder<C> samples(final int value) {
        this.samples = value;
        return this;
    }

    public IncreasingSamplesStatsProducerBuilder<C> maxPercentageMargin(
            final double value) {
        this.maxPercentageMargin = value;
        return this;
    }

    public IncreasingSamplesStatsProducerBuilder<C> approximateSampleMillis(
            final int value) {
        this.approximateSampleMillis = value;
        return this;
    }

    @Override
    public int getSamples() {
        return samples;
    }

    @Override
    public double getMaxPercentageMargin() {
        return maxPercentageMargin;
    }

    @Override
    public int getMillisecondsPerSample() {
        return approximateSampleMillis;
    }

    @Override
    public ConfigurableStatsProducer build() {
        IncreasingSamplesStrategy strategy = new IncreasingSamplesStrategy(this);
        return buildConfigurableStatsProducerWithStrategy(strategy);
    }
}
