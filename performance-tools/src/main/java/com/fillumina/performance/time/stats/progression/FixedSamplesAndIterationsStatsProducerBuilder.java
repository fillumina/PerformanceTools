package com.fillumina.performance.time.stats.progression;

import com.fillumina.performance.util.Builder;
import java.io.Serializable;

/**
 *
 * @author Francesco Illuminati
 */
public class FixedSamplesAndIterationsStatsProducerBuilder<C>
        extends AbstractConfigurableStatsProducerBuilder
                    <FixedSamplesAndIterationsStatsProducerBuilder<C>, C>
        implements
            FixedSamplesAndIterationsStrategy.Configuration,
            Builder<ConfigurableStatsProducer>,
            Serializable {

    private static final long serialVersionUID = 1L;

    private int[] iterationsProgression = new int[]{1_000, 10_000, 100_000};
    private int samples = 30;
    private int warmupIterations;

    public static FixedSamplesAndIterationsStatsProducerBuilder
            <ConfigurableStatsProducer> instance() {
        return new FixedSamplesAndIterationsStatsProducerBuilder<>();
    }

    /** Creates a builder with a default progression. */
    public FixedSamplesAndIterationsStatsProducerBuilder() {
        super();
    }

    public FixedSamplesAndIterationsStatsProducerBuilder(C caller) {
        super(caller);
    }

    public FixedSamplesAndIterationsStatsProducerBuilder(
            Setter<C, ConfigurableStatsProducer> setter) {
        super(setter);
    }

    /** Sets the iterations to be performed at each step. */
    public FixedSamplesAndIterationsStatsProducerBuilder<C> setIterations(
            final int... iterationsProgression) {
        this.iterationsProgression = iterationsProgression;
        return this;
    }

    public FixedSamplesAndIterationsStatsProducerBuilder<C> warmupIterations(
            final int value) {
        this.warmupIterations = value;
        return this;
    }


    /** Sets the samples to be collected for each test. */
    public FixedSamplesAndIterationsStatsProducerBuilder<C> setSamples(
            final int samplesPerStep) {
        this.samples = samplesPerStep;
        return this;
    }

    @Override
    public int[] getIterations() {
        return iterationsProgression;
    }

    @Override
    public int getSamples() {
        return samples;
    }

    @Override
    public ConfigurableStatsProducer build() {
        FixedSamplesAndIterationsStrategy strategy =
                new FixedSamplesAndIterationsStrategy(this);
        return buildConfigurableStatsProducerWithStrategy(strategy);
    }
}
