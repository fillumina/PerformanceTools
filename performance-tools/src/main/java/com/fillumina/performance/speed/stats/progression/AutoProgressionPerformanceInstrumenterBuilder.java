package com.fillumina.performance.speed.stats.progression;

import com.fillumina.performance.assertion.StatsAssertion;
import com.fillumina.performance.speed.stats.SpeedStats;
import com.fillumina.performance.util.ComposedName;

public class AutoProgressionPerformanceInstrumenterBuilder
        extends AbstractIstrumenterBuilder<
            AutoProgressionPerformanceInstrumenterBuilder,
            AutoProgressionPerformanceInstrumenter>{

    public final static double MAX_PERCENTAGE_MARGIN = 5.0;
    public final static int SAMPLES = 100;

    private int iterations = 1_000;
    private int samples = -1;
    private boolean incrementIterations = true;
    private double maxPercentageMargin = 5;
    private boolean autodiscoverBaseIterations = true;
    private StatsAssertion<SpeedStats> forcedAssertion = null;
    private boolean getSamplesUntilTimeout;
    private int approximateSampleMillis = 75;

    public AutoProgressionPerformanceInstrumenterBuilder setBaseIterations(
            int iterations) {
        setAutodiscoverBaseIterations(false);
        this.iterations = iterations;
        return this;
    }

    public AutoProgressionPerformanceInstrumenterBuilder setForcedAssertion(
            StatsAssertion<SpeedStats> forcedAssertion) {
        this.forcedAssertion = forcedAssertion;
        return this;
    }

    /**
     * Setting samples to -1 uses an automatic value so that there are
     * at least 2_000 iterations completed (1_000 iterations are needed
     * on default JVM settings to start optimizing the code).
     *
     * @param samples
     * @return
     */
    public AutoProgressionPerformanceInstrumenterBuilder setSamples(
            int samples) {
        this.samples = samples;
        return this;
    }

    public AutoProgressionPerformanceInstrumenterBuilder
                setIncrementIterations(boolean incrementIteration) {
        this.incrementIterations = incrementIteration;
        return this;
    }

    public AutoProgressionPerformanceInstrumenterBuilder
                incrementIterations() {
        this.incrementIterations = true;
        return this;
    }

    public AutoProgressionPerformanceInstrumenterBuilder
                incrementSamples() {
        this.incrementIterations = false;
        return this;
    }

    public AutoProgressionPerformanceInstrumenterBuilder
                setMaxPercentageMargin(double maxPercentageMargin) {
        this.maxPercentageMargin = maxPercentageMargin;
        return this;
    }

    public AutoProgressionPerformanceInstrumenterBuilder
                setAutodiscoverBaseIterations(boolean autodiscoverBaseIterations) {
        this.autodiscoverBaseIterations = autodiscoverBaseIterations;
        return this;
    }

    public AutoProgressionPerformanceInstrumenterBuilder
                setApproximateSampleMillis(int approximateSampleMillis) {
        this.approximateSampleMillis = approximateSampleMillis;
        return this;
    }

    public AutoProgressionPerformanceInstrumenterBuilder
                setGetSamplesUntilTimeout(boolean getSamplesUntilTimeout) {
        this.getSamplesUntilTimeout = getSamplesUntilTimeout;
        return this;
    }

    public AutoProgressionPerformanceInstrumenterBuilder
                setAutoDiscoverSamples(boolean autodiscoverSamples) {
        if (autodiscoverSamples) {
            this.samples = -1;
        } else {
            this.samples = 33;
        }
        return this;
    }

    @Override
    public AutoProgressionPerformanceInstrumenter build() {
        return new AutoProgressionPerformanceInstrumenter(
                ComposedName.create(name),
                timeoutNs,
                garbageCollectorMillis,
                confidence,
                eliminateOutliers,
                iterations,
                samples,
                incrementIterations,
                maxPercentageMargin,
                autodiscoverBaseIterations,
                forcedAssertion,
                getSamplesUntilTimeout,
                approximateSampleMillis,
                performanceStatsConsumers);
    }

}
