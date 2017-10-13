package com.fillumina.performance.executor.progression;

import com.fillumina.performance.executor.sample.AbstractSample;
import com.fillumina.performance.executor.stats.Stats;
import java.util.Collection;

/**
 * Calculates the performance of tests executed a fixed number of times.
 * <p>
 * The progression defines a sequence of {@code iterations} values each
 * of them will be executed a number of times defined by the {@code sample} value.
 * The performances reported are the average performances of
 * the last iteration step executed.
 *
 * @author Francesco Illuminati
 */
public class FixedSamplesAndIterationsStrategy
        implements ConfigurableStatsProducer.Strategy {

    public interface Configuration {
        int[] getIterations();
        int getWarmupSamples();
        int getSamples();
    }

    public static class Builder {
        private int[] iterations;
        private int warmupSamples;
        private int samples;

        public Builder iterations(final int[] value) {
            this.iterations = value;
            return this;
        }

        public Builder warmupSamples(final int value) {
            this.warmupSamples = value;
            return this;
        }

        public Builder samples(final int value) {
            this.samples = value;
            return this;
        }

        private Configuration createConfiguration() {
            return new Configuration() {
                @Override public int[] getIterations() { return iterations; }
                @Override public int getWarmupSamples() { return warmupSamples; }
                @Override public int getSamples() { return samples; }
            };
        }

        public FixedSamplesAndIterationsStrategy build() {
            return new FixedSamplesAndIterationsStrategy(createConfiguration());
        }

        public <S extends Stats<?>, A extends AbstractSample<A,?,S>>
                ConfigurableStatsProducer<S,A> buildStatsProducer() {
            return new ConfigurableStatsProducer<>(build());
        };

    }

    private final int[] iterations;
    private final int warmupSamples;
    private final int samples;

    private boolean warmup = true;

    public static Builder builder() {
        return new Builder();
    }

    public FixedSamplesAndIterationsStrategy(Configuration config) {
        this.iterations = config.getIterations();
        this.warmupSamples = config.getWarmupSamples();
        this.samples = config.getSamples();
    }

    @Override
    public int getSamples() {
        return warmup ? warmupSamples : samples;
    }

    @Override
    public int[] getIterations() {
        return iterations;
    }

    @Override
    public boolean repeatExecution(final Collection<? extends Stats<?>> stats) {
        if (warmup) {
            warmup = false;
            return true;
        }
        return false;
    }

    @Override
    public String getErrorMessage() {
        return "error";
    }

    @Override
    public boolean continueTakingSamples(SampleProgressionStatus status) {
        return status.getExecutedSamples() < samples;
    }

    @Override
    public void onReset() {
    }
}
