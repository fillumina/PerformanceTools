package com.fillumina.performance.producer.progression;

import java.io.Serializable;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class ProgressionPerformanceInstrumenterBuilder
        extends AbstractIstrumenterBuilder<
            ProgressionPerformanceInstrumenterBuilder,
            ProgressionPerformanceInstrumenter>
        implements Serializable {
    private static final long serialVersionUID = 1L;
    private long[] iterationsProgression;

    public ProgressionPerformanceInstrumenterBuilder() {
        super();
        // init with default values
        setIterationProgression(1000, 10_000, 100_000, 1_000_000);
        setSamplesPerMagnitude(10);
        setTimeout(5, TimeUnit.SECONDS);
    }

    /**
     * Alternative to
     * {@link #setBaseAndMagnitude(int, int)  }.
     */
    @SuppressWarnings(value = "unchecked")
    public ProgressionPerformanceInstrumenterBuilder setIterationProgression(
            final long... iterationsProgression) {
        this.iterationsProgression = iterationsProgression;
        return this;
    }

    /**
     * Alternative to
     * {@link #setIterationProgression(int[])  }.
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
