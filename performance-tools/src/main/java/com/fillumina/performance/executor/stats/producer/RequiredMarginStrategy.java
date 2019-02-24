package com.fillumina.performance.executor.stats.producer;

import com.fillumina.performance.executor.stats.MixedStatsHolder;
import com.fillumina.performance.executor.stats.Stats;
import com.fillumina.performance.executor.stats.StatsHolder;
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
    private final boolean fixedSamples;

    private String message = "required";

    public interface Configuration {
        int getSamples();
        boolean isSamplesFixed();
        Ratio getMaxAllowedMargin();
        Ratio getConfidence();
    }

    public static class Builder
            extends ConfigurableStatsProducer.Builder<Builder> {
        private Ratio maxMargin = DEFAULT_MARGIN;
        private int samples = DEFAULT_SAMPLES;
        private boolean fixedSamples = false;
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
            this.confidence = value;
            return this;
        }

        public Builder fixedSampels(final boolean value) {
            this.fixedSamples = value;
            return this;
        }

        private Configuration createConfiguration() {
            return new Configuration() {
                @Override public int getSamples() { return samples; }
                @Override public boolean isSamplesFixed() { return fixedSamples; }
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

    public RequiredMarginStrategy() {
        this(DEFAULT_MARGIN);
    }

    public RequiredMarginStrategy(Configuration config) {
        this(config.getMaxAllowedMargin(),
                config.getSamples(),
                config.isSamplesFixed(),
                config.getConfidence());
    }

    public RequiredMarginStrategy(Ratio maxAllowedMargin) {
        this(maxAllowedMargin, DEFAULT_SAMPLES, false, DEFAULT_CONFIDENCE);
    }

    public RequiredMarginStrategy(
            Ratio maxAllowedMargin,
            int minSamples,
            boolean fixedSamples,
            Ratio confidence) {
        this.maxRequiredPercentageMargin = maxAllowedMargin;
        this.minSamples = calculateSamples(minSamples, DEFAULT_SAMPLES);
        this.confidence = confidence;
        this.fixedSamples = fixedSamples;
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
    public double errorToStopTakingSamplesCondition(SampleProgressionStatus status) {
        message = "required";

        // take at least a minimum number of samples
        if (status.getExecutedSamples() < minSamples) {
            return 1.0 - status.getExecutedSamples() * 1.0 / minSamples;
        }

        Ratio maxMargin =
                getMaxPercentageMargin(status.getLastStats(), confidence);

        if (!fixedSamples &&
                maxMargin.isGreaterThan(maxRequiredPercentageMargin)) {
            double error = maxMargin.getDecimal() -
                    maxRequiredPercentageMargin.getDecimal();

            message = "max margin= " + maxMargin.toString();
                    //"  error=" + error;

            return error;
        }

        return 0.0; // stop taking samples
    }

    protected static Ratio getMaxPercentageMargin(
            MixedStatsHolder mixedHolder, Ratio confidence) {
        Collection<StatsHolder> holders = mixedHolder.getStatsMap().values();

        Ratio max = Ratio.ZERO;
        for (StatsHolder h : holders) {
            Stats stats = h.getStats();
            final Ratio margin = stats.getMaximumPercentageMargin(confidence);
            if (margin.isGreaterThan(max)) {
                max = margin;
            }
        }
        return max;
    }

    @Override
    public boolean repeatExecution(final Collection<Stats> stats) {
        return false;
    }

    @Override
    public String getStatusMessage() {
        return message;
    }
}
