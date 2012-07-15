package com.fillumina.performance.producer.instrumenter;

import com.fillumina.performance.producer.timer.PerformanceTimer;
import com.fillumina.performance.producer.timer.RequiringPerformanceTimer;
import java.io.Serializable;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author fra
 */
public abstract class AbstractSequenceBuilder
            <T extends RequiringPerformanceTimer, V extends PerformanceInstrumenter<?>>
        implements RequiringPerformanceTimer, Serializable {
    private static final long serialVersionUID = 1L;

    private PerformanceTimer performanceTimer;
    private long[] iterationsProgression;
    private int samples;
    private long timeout;

    public abstract V build();

    /** Mandatory. */
    @Override
    @SuppressWarnings("unchecked")
    public T setPerformanceTimer(
            final PerformanceTimer performanceTimer) {
        this.performanceTimer = performanceTimer;
        return (T) this;
    }

    /**
     * Alternative to
     * {@link #setBaseAndMagnitude(long[]) }.
     */
    @SuppressWarnings("unchecked")
    public T setIterationProgression(
            final long... iterationsProgression) {
        this.iterationsProgression = iterationsProgression;
        return (T) this;
    }

    /**
     * Alternative to
     * {@link #setIterationsProgression(long[]) }.
     */
    @SuppressWarnings("unchecked")
    public T setBaseAndMagnitude(final int baseTimes,
            final int maximumMagnitude) {
        iterationsProgression = new long[maximumMagnitude];
        for (int magnitude = 0; magnitude < maximumMagnitude;
                magnitude++) {
            iterationsProgression[magnitude] = calculateLoops(baseTimes,
                    magnitude);
        }
        return (T) this;
    }

    /** Optional, default to 10 samples per iteration. */
    @SuppressWarnings("unchecked")
    public T setSamplePerIterations(final int samples) {
        this.samples = samples;
        return (T) this;
    }

    /** Optional, default to 10 seconds. */
    @SuppressWarnings("unchecked")
    public T setTimeout(final long timeout,
            final TimeUnit unit) {
        this.timeout = TimeUnit.NANOSECONDS.convert(timeout, unit);
        return (T) this;
    }

    private static long calculateLoops(final int baseTimes, final int magnitude) {
        return Math.round(baseTimes * Math.pow(10, magnitude));
    }

    public long[] getIterationsProgression() {
        return iterationsProgression;
    }

    public PerformanceTimer getPerformanceTimer() {
        return performanceTimer;
    }

    public int getSamples() {
        return samples;
    }

    public long getTimeout() {
        return timeout;
    }
}
