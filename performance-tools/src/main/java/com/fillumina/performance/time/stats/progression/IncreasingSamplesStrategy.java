package com.fillumina.performance.time.stats.progression;

import com.fillumina.performance.time.sample.PerformanceTimer;
import com.fillumina.performance.time.stats.TimeSampleCollector;
import com.fillumina.performance.time.stats.TimeStats;
import com.fillumina.performance.util.stats.Ratio;

/**
 *
 * @author Francesco Illuminati
 */
public class IncreasingSamplesStrategy<T extends TimeStats>
        implements ConfigurableStatsProducer.Strategy<T> {
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

        TimeSampleCollector collector = status.getSpeedSampleCollector();
        TimeStats stats = collector.createPerformanceStatsAndFilterIf(true);
        final Ratio margin = stats.getMaximumPercentageMargin(Ratio.P_95);
        if (margin.isGreaterThan(maxPercentageMargin)) {
            message = "percentage ratio " +
                    margin.toString() +
                    " too high, required less than " +
                    maxPercentageMargin.toString();
            return true;
        }

        return false;
    }

    @Override
    public boolean repeatExecution(final T stats) {
        return false;
    }

    @Override
    public String getRejectionMessage() {
        return message;
    }
}
