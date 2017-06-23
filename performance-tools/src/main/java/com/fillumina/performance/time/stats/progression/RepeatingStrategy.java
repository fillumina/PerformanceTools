package com.fillumina.performance.time.stats.progression;

import com.fillumina.performance.time.sample.PerformanceTimer;
import com.fillumina.performance.time.stats.TimeStats;
import com.fillumina.performance.util.stats.Ratio;
import java.util.Arrays;

/**
 * Automatically finds the optimal parameters to perform a performance
 * estimation of the code under test. It increases the number of iterations
 * or samples (according to configuration) to meet the accuracy requirements.
 * <p>
 * It produces statistics based on the average results of the last round of
 * iterations.
 * <p>
 * It is useful to study how the measure changes with different parameters,
 * but it is less useful strategy as a whole. {@link IncreasingSampleStrategy}
 * should be preferred.
 *
 * @author Francesco Illuminati
 */
public class RepeatingStrategy<T extends TimeStats>
        implements ConfigurableStatsProducer.Strategy<T> {
    private static final int DEFAULT_SAMPLES = 40;

    private static final int USE_DEFAULT_SAMPLES = -1;
    private static final int USE_GIVEN_SAMPLES = -2;

    private final boolean incrementIteration;
    private final Ratio maxPercentageMargin;
    private final int startingIterations;
    private final int startingSamples;
    private final boolean startingAutodiscoverBaseIteration;
    private final int approximateSampleMillis;

    private int[] iterations;
    private int samples = -1;
    private boolean autodiscoverBaseIterations = true;
    private String message = null;

    public interface Configuration {
        int getIterations();
        int getSamples();
        boolean getIncrementIteration();
        Ratio getMaxPercentageMargin();
        boolean getAutodiscoverBaseIterations();
        int getApproximateSampleMillis();
    }

    public RepeatingStrategy(Configuration config) {
        this.startingIterations = config.getIterations();
        this.startingSamples = config.getSamples();

        this.incrementIteration = config.getIncrementIteration();
        this.startingAutodiscoverBaseIteration =
                config.getAutodiscoverBaseIterations();

        this.maxPercentageMargin = config.getMaxPercentageMargin();

        this.approximateSampleMillis = config.getApproximateSampleMillis();

        onReset();
    }

    @Override
    public void onReset() {
        this.iterations = null;
        this.samples = USE_GIVEN_SAMPLES;
        this.autodiscoverBaseIterations = startingAutodiscoverBaseIteration;
    }

    @Override
    public int[] getIterations(PerformanceTimer pt) {
        if (autodiscoverBaseIterations) {
            autodiscoverBaseIterations = false;
            iterations = pt.estimateIterations(approximateSampleMillis);
            return copy(iterations);
        }
        if (iterations == null) {
            iterations = new int[]{startingIterations};
            return copy(iterations);
        }
        if (incrementIteration) {
            for (int i=0; i<iterations.length; i++) {
                iterations[i] *= 2;
            }
        }
        return copy(iterations);
    }

    private static int[] copy(int[] array) {
        return Arrays.copyOf(array, array.length);
    }

    @Override
    public int getSamples() {
        if (samples == USE_DEFAULT_SAMPLES) {
            samples = DEFAULT_SAMPLES;
            return samples;
        }
        if (samples == USE_GIVEN_SAMPLES) {
            samples = startingSamples;
            return samples;
        }
        if (!incrementIteration) {
            samples *= 10;
        }
        return samples;
    }

    @Override
    public boolean repeatExecution(final T stats) {
        message = null;

        // checks ratio percentage margin of error for maximum error allowed
        final Ratio margin = stats.getMaximumPercentageMargin(Ratio.P_95);
        if (margin.isGreaterThan(maxPercentageMargin)) {
            message = "percentage ratio " + margin.toString() +
                    " too high, required less than " +
                    maxPercentageMargin.toString();
            return true;
        }

        return false;
    }

    @Override
    public boolean continueTakingSamples(SampleProgressionStatus status) {
        return status.getSample() < samples;
    }

    @Override
    public String getRejectionMessage() {
        return message;
    }
}
