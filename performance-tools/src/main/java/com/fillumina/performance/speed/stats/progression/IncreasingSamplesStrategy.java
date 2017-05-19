package com.fillumina.performance.speed.stats.progression;

import com.fillumina.performance.speed.sample.PerformanceTimer;
import com.fillumina.performance.speed.stats.SpeedSampleCollector;
import com.fillumina.performance.speed.stats.SpeedStats;
import com.fillumina.performance.util.stats.Ratio;
import java.util.Locale;

/**
 *
 * @author Francesco Illuminati
 */
public class IncreasingSamplesStrategy
        implements ConfigurableStatsProducer.Strategy {
    private static final int DEFAULT_SAMPLES = 40;

    private final double maxPercentageMargin;
    private final int millsPerSample;

    private int samples = 33;
    private String message = null;

    public interface Configuration {
        int getSamples();
        double getMaxPercentageMargin();
        int getMillisecondsPerSample();
    }

    public IncreasingSamplesStrategy(Configuration config) {
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

    @Override
    public int[] getIterations(PerformanceTimer pt) {
        return pt.estimateIterations(millsPerSample);
    }

    @Override
    public int getSamples() {
        return samples;
    }

    @Override
    public boolean continueTakingSamples(SampleProgressionStatus status) {
        message = null;

        // take at least a minimum amount of samples
        if (status.getSample() < samples) {
            return true;
        }

        SpeedSampleCollector collector = status.getSpeedSampleCollector();
        SpeedStats stats = collector.createPerformanceStatsAndFilterIf(true);
        final double margin = stats.getMaximumPercentageMargin(Ratio.P_95)
                .getPercentage();
        if (margin > maxPercentageMargin) {
            message = String.format(Locale.US,
                    "percentage ratio %.2f %% too high, " +
                    "required less than %.2f %%", margin, maxPercentageMargin);
            return true;
        }

        return false;
    }

    @Override
    public boolean repeatExecution(final SpeedStats stats) {
        return false;
    }

    @Override
    public String getRejectionMessage() {
        return message;
    }
}
