package com.fillumina.performance.executor.stats.producer;

import com.fillumina.performance.executor.AssertableHolder;
import com.fillumina.performance.executor.MixedAssertableHolder;
import com.fillumina.performance.executor.stats.Stats;
import com.fillumina.performance.util.collection.UnmodifiableIntList;
import com.fillumina.performance.util.stats.Ratio;
import java.util.Collection;

/**
 *
 * @author Francesco Illuminati
 */
public class RequiredMarginStrategy
        implements ConfigurableStatsProducer.Strategy {
    private static final Ratio DEFAULT_MARGIN = Ratio.percentage(5);
    private static final int DEFAULT_SAMPLES = 33;
    private static final Ratio DEFAULT_CONFIDENCE = Ratio.P_999;

    private final Ratio maxRequiredPercentageMargin;
    private final Ratio confidence;
    private final int minSamples;

    private String message = null;

    public interface Configuration {
        int getSamples();
        Ratio getMaxAllowedMargin();
        Ratio getConfidence();
    }

    public static class Builder
            extends ConfigurableStatsProducer.Builder<Builder> {
        private Ratio maxMargin = DEFAULT_MARGIN;
        private int samples = DEFAULT_SAMPLES;
        private Ratio confidence = DEFAULT_CONFIDENCE;

        /** Minimum number of samples to be taken. */
        public Builder samples(final int value) {
            this.samples = value;
            return this;
        }

        public Builder maxAllowedMargin(final Ratio value) {
            this.maxMargin = value;
            return this;
        }

        public Builder confidence(final Ratio value) {
            this.confidence = confidence;
            return this;
        }

        private Configuration createConfiguration() {
            return new Configuration() {
                @Override public int getSamples() { return samples; }
                @Override public Ratio getMaxAllowedMargin() { return maxMargin; }
                @Override public Ratio getConfidence() { return confidence; }
            };
        }

        public RequiredMarginStrategy build() {
            return new RequiredMarginStrategy(createConfiguration());
        }

        public ConfigurableStatsProducer buildStatsProducer() {
            return new ConfigurableStatsProducer(
                    buildConfiguration(), build());
        };
    }

    public static Builder builder() {
        return new Builder();
    }

    public static ConfigurableStatsProducer createStatsProducer(
            Ratio maxAllowedMargin) {
        return new ConfigurableStatsProducer(
            new RequiredMarginStrategy(maxAllowedMargin));
    }

    public RequiredMarginStrategy(Configuration config) {
        this(config.getMaxAllowedMargin(),
                calculateSamples(config.getSamples(), DEFAULT_SAMPLES),
                config.getConfidence());
    }

    public RequiredMarginStrategy() {
        this(DEFAULT_MARGIN);
    }

    public RequiredMarginStrategy(Ratio maxAllowedMargin) {
        this(maxAllowedMargin, DEFAULT_SAMPLES, DEFAULT_CONFIDENCE);
    }

    public RequiredMarginStrategy(Ratio maxAllowedMargin, int minSamples,
            Ratio confidence) {
        this.maxRequiredPercentageMargin = maxAllowedMargin;
        this.minSamples = minSamples;
        this.confidence = confidence;
    }

    private static int calculateSamples(int givenSamples, int defaultSamples) {
        if (givenSamples <= 0) {
            return defaultSamples;
        }
        return givenSamples;
    }

    @Override
    public UnmodifiableIntList getIterations() {
        return UnmodifiableIntList.EMPTY;
    }

    @Override
    public int getExpectedNumberOfSamples() {
        return minSamples;
    }

    @Override
    public boolean continueTakingSamples(SampleProgressionStatus status) {
        message = null;

        // take at least a minimum amount of samples
        if (status.getExecutedSamples() < minSamples) {
            return true;
        }

        Ratio maxMargin =
                getMaxPercentageMargin(status.getLastStats(), confidence);
        if (maxMargin.isGreaterThan(maxRequiredPercentageMargin)) {
            message = "percentage ratio " +
                    maxMargin.toString() +
                    " too high, required less than " +
                    maxRequiredPercentageMargin.toString();
            return true;
        }

        return false;
    }

    protected static Ratio getMaxPercentageMargin(
            MixedAssertableHolder mixedHolder, Ratio confidence) {
        Collection<AssertableHolder<?>> holders =
                mixedHolder.getStatsMap().values();

        Ratio max = Ratio.ZERO;
        for (AssertableHolder<?> h : holders) {
            Stats stats = (Stats) h.getAssertable();
            final Ratio margin = stats.getMaximumPercentageMargin(confidence);
            if (margin.isGreaterThan(max)) {
                max = margin;
            }
        }
        return max;
    }

    @Override
    public boolean repeatExecution(final Collection<? extends Stats> stats) {
        return false;
    }

    @Override
    public String getStatusMessage() {
        return message;
    }
}
