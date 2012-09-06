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
    private int[] iterationsProgression;

    public ProgressionPerformanceInstrumenterBuilder() {
        super();
        // init with default values
        setIterationProgression(1000, 10_000, 100_000, 1_000_000);
        setIterationsPerMagnitude(10);
        setTimeout(5, TimeUnit.SECONDS);
    }

    /**
     * Alternative to
     * {@link #setBaseAndMagnitude(int, int)  }.
     */
    @SuppressWarnings(value = "unchecked")
    public ProgressionPerformanceInstrumenterBuilder setIterationProgression(
            final int... iterationsProgression) {
        this.iterationsProgression = iterationsProgression;
        return this;
    }

    /**
     * Alternative to
     * {@link #setIterationProgression(int[])  }.
     */
    @SuppressWarnings(value = "unchecked")
    public ProgressionPerformanceInstrumenterBuilder setBaseAndMagnitude(
            final int baseTimes,
            final int maximumMagnitude) {
        iterationsProgression = new int[maximumMagnitude];
        for (int magnitude = 0; magnitude < maximumMagnitude;
                magnitude++) {
            iterationsProgression[magnitude] = calculateLoops(baseTimes,
                    magnitude);
        }
        return this;
    }

    private static int calculateLoops(final int baseTimes, final int magnitude) {
        return (int) Math.round(baseTimes * Math.pow(10, magnitude));
    }

    public int[] getIterationsProgression() {
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
