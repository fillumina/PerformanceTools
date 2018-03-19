package com.fillumina.performance.executor.stats.producer;

import com.fillumina.performance.executor.stats.Stats;
import com.fillumina.performance.util.collection.UnmodifiableIntList;
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
    public static final String TESTING_STATUS = "Testing";
    public static final String WARMUP_STATUS = "Warmup";

    public interface Configuration {
        int[] getIterations();
        int getWarmupSamples();
        int getSamples();
    }

    public static class Builder
            extends ConfigurableStatsProducer.Builder<Builder> {
        private int[] iterations;
        private int warmupSamples;
        private int samples;

        public Builder iterations(int... value) {
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

        public ConfigurableStatsProducer buildStatsProducer() {
            return new ConfigurableStatsProducer(buildConfiguration(), build());
        };
    }

    private final int[] iterations;
    private final int warmupSamples;
    private final int samples;

    private String message;
    private boolean warmup = true;

    public static Builder builder() {
        return new Builder();
    }

    public FixedSamplesAndIterationsStrategy(Configuration config) {
        this.iterations = config.getIterations();
        this.warmupSamples = config.getWarmupSamples();
        this.samples = config.getSamples();
        if (warmupSamples < 0) {
            throw new RuntimeException(
                    "warmup samples must be positive or zero, were " +
                            warmupSamples);
        }
        if (samples <= 0) {
            throw new RuntimeException(
                    "samples must be positive, were " + samples);
        }
        this.warmup = warmupSamples > 0;
    }

    @Override
    public int getExpectedNumberOfSamples() {
        if (warmup && warmupSamples > 0) {
            message = WARMUP_STATUS;
            return warmupSamples;
        } else {
            message = TESTING_STATUS;
            return samples;
        }
    }

    @Override
    public UnmodifiableIntList getIterations() {
        return new UnmodifiableIntList(iterations);
    }

    @Override
    public boolean repeatExecution(final Collection<Stats> stats) {
        if (warmup) {
            warmup = false;
            return true;
        } else {
            warmup = true;
            return false;
        }
    }

    @Override
    public String getStatusMessage() {
        return message;
    }

//    @Override
//    public boolean continueTakingSamples(SampleProgressionStatus status) {
//        return status.getExecutedSamples() <  (warmup ? warmupSamples : samples);
//    }

    @Override
    public double errorToStopTakingSamplesCondition(SampleProgressionStatus status) {
        double totalSamples = (warmup ? warmupSamples : samples);
        return totalSamples - status.getExecutedSamples();
    }

}
