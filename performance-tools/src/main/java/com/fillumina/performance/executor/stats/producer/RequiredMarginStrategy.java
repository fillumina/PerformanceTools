package com.fillumina.performance.executor.stats.producer;

import com.fillumina.performance.executor.AssertableHolder;
import com.fillumina.performance.executor.MixedAssertableHolder;
import com.fillumina.performance.executor.sample.AbstractSample;
import com.fillumina.performance.executor.stats.Stats;
import com.fillumina.performance.util.collection.ROIntList;
import com.fillumina.performance.util.stats.Ratio;
import java.util.Collection;

/**
 *
 * @author Francesco Illuminati
 */
public class RequiredMarginStrategy
        implements ConfigurableStatsProducer.Strategy {
    private static final int DEFAULT_SAMPLES = 40;

    private final Ratio maxRequiredPercentageMargin;
    private final int samples;

    private String message = null;

    public interface Configuration {
        int getSamples();
        Ratio getMaxAllowedMargin();
    }

    public static class Builder
            extends ConfigurableStatsProducer.Builder<Builder> {
        private int samples = 33;
        private Ratio maxMargin = Ratio.percentage(10);

        /** Minimum number of samples to be taken. */
        public Builder samples(final int value) {
            this.samples = value;
            return this;
        }

        public Builder maxAllowedMargin(final Ratio value) {
            this.maxMargin = value;
            return this;
        }

        private Configuration createConfiguration() {
            return new Configuration() {
                @Override public int getSamples() { return samples; }
                @Override public Ratio getMaxAllowedMargin() { return maxMargin; }
            };
        }

        public RequiredMarginStrategy build() {
            return new RequiredMarginStrategy(createConfiguration());
        }

        public <S extends Stats<?>, A extends AbstractSample<A,?,S>>
                ConfigurableStatsProducer<S,A> buildStatsProducer() {
            return new ConfigurableStatsProducer<>(
                    buildConfiguration(), build());
        };
    }

    public static Builder builder() {
        return new Builder();
    }

    public static <S extends Stats<?>, A extends AbstractSample<A,?,S>>
            ConfigurableStatsProducer<S,A>
            createStatsProducer(Ratio maxAllowedMargin) {
        return new ConfigurableStatsProducer<>(
            new RequiredMarginStrategy(maxAllowedMargin));
    }

    public RequiredMarginStrategy(Configuration config) {
        this.samples = calculateSamples(config.getSamples(), DEFAULT_SAMPLES);
        this.maxRequiredPercentageMargin = config.getMaxAllowedMargin();
    }

    public RequiredMarginStrategy(Ratio maxAllowedMargin) {
        this.samples = DEFAULT_SAMPLES;
        this.maxRequiredPercentageMargin = maxAllowedMargin;
    }

    private int calculateSamples(int givenSamples, int defaultSamples) {
        if (givenSamples <= 0) {
            return defaultSamples;
        }
        return givenSamples;
    }

    @Override
    public ROIntList getIterations() {
        return ROIntList.EMPTY;
    }

    @Override
    public int getExpectedNumberOfSamples() {
        return samples;
    }

    @Override
    public boolean continueTakingSamples(SampleProgressionStatus status) {
        message = null;

        // take at least a minimum amount of samples
        if (status.getExecutedSamples() < samples) {
            return true;
        }

        Ratio maxMargin = getMaxPercentageMargin(status.getLastStats());
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
            MixedAssertableHolder mixedHolder) {
        Collection<AssertableHolder<?>> holders =
                mixedHolder.getStatsMap().values();

        Ratio max = Ratio.ZERO;
        for (AssertableHolder<?> h : holders) {
            Stats<?> stats = (Stats<?>) h.getAssertable();
            final Ratio margin = stats.getMaximumPercentageMargin(Ratio.P_95);
            if (margin.isGreaterThan(max)) {
                max = margin;
            }
        }
        return max;
    }

    @Override
    public boolean repeatExecution(final Collection<? extends Stats<?>> stats) {
        return false;
    }

    @Override
    public String getStatusMessage() {
        return message;
    }
}
