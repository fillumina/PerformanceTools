package com.fillumina.performance.producer.progression;

import java.io.Serializable;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author fra
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
        setIterationProgression(1000, 10_000L, 100_000L, 1_000_000L);
        setSamplePerIterations(10);
        setTimeout(5, TimeUnit.SECONDS);
    }

    /**
     * Alternative to
     * {@link #setBaseAndMagnitude(long[]) }.
     */
    @SuppressWarnings(value = "unchecked")
    public ProgressionPerformanceInstrumenterBuilder setIterationProgression(
            final long... iterationsProgression) {
        this.iterationsProgression = iterationsProgression;
        return this;
    }

    /**
     * Alternative to
     * {@link #setIterationsProgression(long[]) }.
     */
    @SuppressWarnings(value = "unchecked")
    public ProgressionPerformanceInstrumenterBuilder setBaseAndMagnitude(
            final int baseTimes,
            final int maximumMagnitude) {
        iterationsProgression = new long[maximumMagnitude];
        for (int magnitude = 0; magnitude < maximumMagnitude;
                magnitude++) {
            iterationsProgression[magnitude] = calculateLoops(baseTimes,
                    magnitude);
        }
        return this;
    }

    private static long calculateLoops(final int baseTimes, final int magnitude) {
        return Math.round(baseTimes * Math.pow(10, magnitude));
    }

    public long[] getIterationsProgression() {
        return iterationsProgression;
    }

    @Override
    protected void check() {
        super.check();
        if (iterationsProgression == null || iterationsProgression.length == 0) {
            throw new IllegalArgumentException(
                    "no iteration progression specified: " +
                    Arrays.toString(iterationsProgression));
        }
    }

    @Override
    public ProgressionPerformanceInstrumenter build() {
        check();
        return new ProgressionPerformanceInstrumenter(this);
    }

}
