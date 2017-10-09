package com.fillumina.performance.executor.progression;

import com.fillumina.performance.util.Builder;
import com.fillumina.performance.util.stats.Ratio;
import java.io.Serializable;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class MatchRequiredMarginStrategyBuilder<C>
        extends AbstractConfigurableStatsProducerBuilder
                    <MatchRequiredMarginStrategyBuilder<C>, C>
        implements
            MatchRequiredMarginStrategy.Configuration,
            Builder<ConfigurableStatsProducer>,
            Serializable {

    private static final long serialVersionUID = 1L;

    private int samples = 40;
    private Ratio maxPercentageMargin = Ratio.percentage(5.0);
    private int approximateSampleMillis = 250;

    public static MatchRequiredMarginStrategyBuilder
                <ConfigurableStatsProducer> instance() {
        return new MatchRequiredMarginStrategyBuilder<>();
    }

    /** Creates a builder with a default progression. */
    public MatchRequiredMarginStrategyBuilder() {
        super();
    }

    public MatchRequiredMarginStrategyBuilder(C caller) {
        super(caller);
    }

    public MatchRequiredMarginStrategyBuilder(
            Setter<C, ConfigurableStatsProducer> setter) {
        super(setter);
    }

    /** Sets the samples to be collected for each test. */
    public MatchRequiredMarginStrategyBuilder<C> setSamples(int samples) {
        this.samples = samples;
        return this;
    }

    public MatchRequiredMarginStrategyBuilder<C> samples(final int value) {
        this.samples = value;
        return this;
    }

    public MatchRequiredMarginStrategyBuilder<C> maxPercentageMargin(
            final Ratio value) {
        this.maxPercentageMargin = value;
        return this;
    }

    public MatchRequiredMarginStrategyBuilder<C> approximateSampleMillis(
            final int value) {
        this.approximateSampleMillis = value;
        return this;
    }

    @Override
    public int getSamples() {
        return samples;
    }

    @Override
    public Ratio getMaxPercentageMargin() {
        return maxPercentageMargin;
    }

    @Override
    public int getMillisecondsPerSample() {
        return approximateSampleMillis;
    }

    @Override
    public ConfigurableStatsProducer build() {
        MatchRequiredMarginStrategy strategy =
                new MatchRequiredMarginStrategy(this);
        return buildConfigurableStatsProducerWithStrategy(strategy);
    }
}
