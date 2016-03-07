package com.fillumina.performance.producer.progression;


public class AutoProgressionPerformanceInstrumenterBuilder
        extends AbstractIstrumenterBuilder<
            AutoProgressionPerformanceInstrumenterBuilder,
            AutoProgressionPerformanceInstrumenter>{

    private int iterations = 1_000;
    private int samples = 10;
    private double maxStandardDeviation = 5;
    private boolean incrementIterations = true;
    private boolean checkStdDeviation = true;

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

    public AutoProgressionPerformanceInstrumenterBuilder
                setMaxStandardDeviation(double maxStandardDeviation) {
        this.maxStandardDeviation = maxStandardDeviation;
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
                setCheckStdDeviation(boolean checkStdDeviation) {
        this.checkStdDeviation = checkStdDeviation;
        return this;
    }

    @Override
    public AutoProgressionPerformanceInstrumenter build() {
        return new AutoProgressionPerformanceInstrumenter(message, iterations,
                samples, maxStandardDeviation, timeoutNs,
                incrementIterations, checkStdDeviation);
    }

}
