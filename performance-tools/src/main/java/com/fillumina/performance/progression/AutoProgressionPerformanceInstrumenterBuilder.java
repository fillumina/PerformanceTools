package com.fillumina.performance.progression;


public class AutoProgressionPerformanceInstrumenterBuilder
        extends AbstractIstrumenterBuilder<
            AutoProgressionPerformanceInstrumenterBuilder,
            AutoProgressionPerformanceInstrumenter>{

    private int iterations = 1_000;
    private int samples = 30;
    private double minConfidence = 0.70;
    private boolean incrementIterations = true;
    private boolean checkConfidence = true;
    private double confidence = 0.95;
    private double maxPercentageMargin = 0.05;

    public AutoProgressionPerformanceInstrumenterBuilder setBaseIterations(
            int iterations) {
        this.iterations = iterations;
        return this;
    }

    public AutoProgressionPerformanceInstrumenterBuilder setBaseSamples(
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
                setCheckConfidence(boolean checkConfidence) {
        this.checkConfidence = checkConfidence;
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

    @Override
    public AutoProgressionPerformanceInstrumenter build() {
        return new AutoProgressionPerformanceInstrumenter(message, iterations,
                samples, minConfidence, timeoutNs,
                incrementIterations, confidence, maxPercentageMargin,
                garbageCollectorMillis, performanceStatsConsumer,
                eliminateOutliers);
    }

}
