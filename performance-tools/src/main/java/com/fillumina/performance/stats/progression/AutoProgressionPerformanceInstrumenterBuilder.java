package com.fillumina.performance.stats.progression;

import com.fillumina.performance.stats.assertion.PerformanceAssertion;

public class AutoProgressionPerformanceInstrumenterBuilder
        extends AbstractIstrumenterBuilder<
            AutoProgressionPerformanceInstrumenterBuilder,
            AutoProgressionPerformanceInstrumenter>{

    private int iterations = 1_000;
    private int samples = 100;
    private double minConfidence = 0.70;
    private boolean incrementIterations = true;
    private double maxPercentageMargin = 0.05;
    private boolean autodiscoverBaseIterations = true;
    private PerformanceAssertion forcedAssertion = null;
    private boolean getSamplesUntilTimeout;

    public AutoProgressionPerformanceInstrumenterBuilder setBaseIterations(
            int iterations) {
        setAutodiscoverBaseIterations(false);
        this.iterations = iterations;
        return this;
    }

    public AutoProgressionPerformanceInstrumenterBuilder setForcedAssertion(
            PerformanceAssertion forcedAssertion) {
        this.forcedAssertion = forcedAssertion;
        return this;
    }

    public AutoProgressionPerformanceInstrumenterBuilder setSamples(
            int samples) {
        this.samples = samples;
        return this;
    }

    /** Insert the minimum confidence level acceptable (fraction 0.90). */
    public AutoProgressionPerformanceInstrumenterBuilder
                setMinConfidence(double minConfidence) {
        if (minConfidence > 0.9999 || minConfidence < 0.0001) {
            throw new IllegalArgumentException("minConfidence must be between" +
                    " 0 and 1 excluded, minConfidence " + minConfidence);
        }
        this.minConfidence = minConfidence;
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
                setConfidence(double confidence) {
        this.confidence = confidence;
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
                setGetSamplesUntilTimeout(boolean getSamplesUntilTimeout) {
        this.getSamplesUntilTimeout = getSamplesUntilTimeout;
        return this;
    }

    @Override
    public AutoProgressionPerformanceInstrumenter build() {
        return new AutoProgressionPerformanceInstrumenter(
                name,
                timeoutNs,
                garbageCollectorMillis,
                confidence,
                eliminateOutliers,
                iterations,
                samples,
                incrementIterations,
                minConfidence,
                maxPercentageMargin,
                autodiscoverBaseIterations,
                forcedAssertion,
                getSamplesUntilTimeout,
                performanceStatsConsumers);
    }

}
