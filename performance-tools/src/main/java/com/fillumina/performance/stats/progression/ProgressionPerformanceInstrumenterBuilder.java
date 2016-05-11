package com.fillumina.performance.stats.progression;

import java.io.Serializable;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author Francesco Illuminati
 */
public class ProgressionPerformanceInstrumenterBuilder
        extends AbstractIstrumenterBuilder<
            ProgressionPerformanceInstrumenterBuilder,
            ProgressionPerformanceInstrumenter>
        implements Serializable {
    private static final long serialVersionUID = 1L;
    private int[] iterationsProgression;
    private int samplesPerStep;

    /**
     * Creates a builder with a default progression (from 1_000 to
     * 1_000_000 iterations) with 10 samples per step and a timeout of
     * 5 seconds.
     */
    public ProgressionPerformanceInstrumenterBuilder() {
        super();
        // init with default values
        setIterationProgression(1_000, 10_000, 100_000, 1_000_000);
        setSamplesPerStep(30);
        setTimeout(5, TimeUnit.SECONDS);
    }

    /**
     * Allows to define a progression by directly insert the number
     * of iterations for each step.
     * <br>
     * Alternative to {@link #setBaseAndMagnitude(long, int) }.
     */
    @SuppressWarnings(value = "unchecked")
    public ProgressionPerformanceInstrumenterBuilder setIterationProgression(
            final int... iterationsProgression) {
        this.iterationsProgression = iterationsProgression;
        return this;
    }

    /**
     * How many times a test is repeated (with all its iterations) to
     * create the samples from which the average statistics will be
     * extracted (i.e. standard deviation).
     * Optional, default to 10 samples per magnitude.
     *
     */
    public ProgressionPerformanceInstrumenterBuilder setSamplesPerStep(
            final int samplesPerStep) {
        this.samplesPerStep = samplesPerStep;
        return this;
    }

    public ProgressionPerformanceInstrumenterBuilder setConfidence(
            final double confidence) {
        this.confidence = confidence;
        return this;
    }

    /**
     * Allows to define a progression by inserting a starting number and
     * than the number of times this number should be increased of magnitude
     * (multiplied by 10).
     * <br>
     * i.e.:
     * <pre>
     * base=100, magnitude=3 : 100, 1_000, 10_000
     * base=20,  magnitude=2 : 20, 200
     * </pre>
     * <br>
     * Alternative to
     * {@link #setIterationProgression(long...) }.
     */
    @SuppressWarnings(value = "unchecked")
    public ProgressionPerformanceInstrumenterBuilder setBaseAndMagnitude(
            final long baseIterations,
            final int maximumMagnitude) {
        iterationsProgression = new int[maximumMagnitude];
        for (int magnitude = 0; magnitude < maximumMagnitude; magnitude++) {
            iterationsProgression[magnitude] =
                    calculateIterationsProgression(baseIterations, magnitude);
        }
        return this;
    }

    private static int calculateIterationsProgression(
            final long baseIterations,
            final int magnitude) {
        return (int) Math.round(baseIterations * Math.pow(10, magnitude));
    }

    @Override
    public ProgressionPerformanceInstrumenter build() {
        return new ProgressionPerformanceInstrumenter(name,
                timeoutNs,
                garbageCollectorMillis,
                confidence,
                eliminateOutliers,
                addBaselineTest,
                iterationsProgression,
                samplesPerStep,
                performanceStatsConsumers);
    }
}
