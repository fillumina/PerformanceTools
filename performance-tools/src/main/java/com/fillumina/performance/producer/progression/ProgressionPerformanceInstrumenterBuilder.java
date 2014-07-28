package com.fillumina.performance.producer.progression;

import java.io.Serializable;
import java.util.Arrays;
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
    private long[] iterationsProgression;

    /**
     * Creates a builder with a default progression (from 1_000 to
     * 1_000_000 iterations) with 10 samples per step and a timeout of
     * 5 seconds.
     */
    public ProgressionPerformanceInstrumenterBuilder() {
        super();
        // init with default values
        setIterationProgression(1_000, 10_000, 100_000, 1_000_000);
        setSamplesPerStep(10);
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
            final long... iterationsProgression) {
        this.iterationsProgression = iterationsProgression;
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
        iterationsProgression = new long[maximumMagnitude];
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

    public long[] getIterationsProgression() {
        return iterationsProgression;
    }

    @Override
    protected void validate() {
        super.validate();
        if (iterationsProgression == null || iterationsProgression.length == 0) {
            throw new IllegalArgumentException(
                    "no iteration progression specified: " +
                    Arrays.toString(iterationsProgression));
        }
    }

    @Override
    public ProgressionPerformanceInstrumenter build() {
        validate();
        return new ProgressionPerformanceInstrumenter(this);
    }
}
