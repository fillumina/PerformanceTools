package com.fillumina.performance.executor.progression;

import com.fillumina.performance.executor.AssertableHolder;
import com.fillumina.performance.executor.MixedAssertableHolder;
import com.fillumina.performance.executor.stats.Stats;
import com.fillumina.performance.util.stats.Ratio;
import java.util.Collection;

/**
 *
 * @author Francesco Illuminati
 */
public class MatchRequiredMarginStrategy
        implements ConfigurableStatsProducer.Strategy {
    private static final int DEFAULT_SAMPLES = 40;

    private final Ratio maxPercentageMargin;
    private final int millsPerSample;

    private int samples = 33;
    private String message = null;

    public interface Configuration {
        int getSamples();
        Ratio getMaxPercentageMargin();
        int getMillisecondsPerSample();
    }

    public MatchRequiredMarginStrategy(Configuration config) {
        this.samples = calculateSamples(config.getSamples(), DEFAULT_SAMPLES);
        this.millsPerSample = config.getMillisecondsPerSample();
        this.maxPercentageMargin = config.getMaxPercentageMargin();
    }

    private int calculateSamples(int givenSamples, int defaultSamples) {
        if (givenSamples <= 0) {
            return defaultSamples;
        }
        return givenSamples;
    }

    @Override
    public void onReset() {
        message = null;
    }

    private static final int[] EMPTY_INT_ARRAY = new int[0];
    @Override
    public int[] getIterations() {
        return EMPTY_INT_ARRAY;
    }

    @Override
    public int getSamples() {
        return samples;
    }

    @Override
    public boolean continueTakingSamples(SampleProgressionStatus status) {
        message = null;

        // take at least a minimum amount of samples
        if (status.getExecutedSamples() < samples) {
            return true;
        }

        MixedAssertableHolder mixedHolder = status.getLastStats();
        Collection<AssertableHolder<?>> holders =
                mixedHolder.getStatsMap().values();

        for (AssertableHolder<?> h : holders) {
            Stats<?> stats = (Stats<?>) h.getAssertable();
            final Ratio margin = stats.getMaximumPercentageMargin(Ratio.P_95);
            if (margin.isGreaterThan(maxPercentageMargin)) {
                message = "percentage ratio " +
                        margin.toString() +
                        " too high, required less than " +
                        maxPercentageMargin.toString();
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean repeatExecution(final Collection<? extends Stats<?>> stats) {
        return false;
    }

    @Override
    public String getErrorMessage() {
        return message;
    }
}
