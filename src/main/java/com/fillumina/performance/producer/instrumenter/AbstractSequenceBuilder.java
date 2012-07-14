package com.fillumina.performance.producer.instrumenter;

import com.fillumina.performance.producer.timer.PerformanceTimer;
import com.fillumina.performance.producer.timer.UsePerformanceTimer;
import java.io.Serializable;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author fra
 */
public abstract class AbstractSequenceBuilder<T>
        implements UsePerformanceTimer, Serializable {
    private static final long serialVersionUID = 1L;

    private PerformanceTimer performanceTimer;
    private long[] iterationsProgression;
    private int samples = 10;
    private long timeout = TimeUnit.NANOSECONDS.convert(5, TimeUnit.SECONDS);

    public abstract T build();

    /** Mandatory. */
    @Override
    public AbstractSequenceBuilder<T> setPerformanceTimer(
            final PerformanceTimer performanceTimer) {
        this.performanceTimer = performanceTimer;
        return this;
    }

    /**
     * Alternative to
     * {@link #setBaseAndMagnitude(long[]) }.
     */
    public AbstractSequenceBuilder<T> setIterationProgression(
            final long... iterationsProgression) {
        this.iterationsProgression = iterationsProgression;
        return this;
    }

    /**
     * Alternative to
     * {@link #setIterationsProgression(long[]) }.
     */
    public AbstractSequenceBuilder<T> setBaseAndMagnitude(final int baseTimes,
            final int maximumMagnitude) {
        iterationsProgression = new long[maximumMagnitude];
        for (int magnitude = 0; magnitude < maximumMagnitude;
                magnitude++) {
            iterationsProgression[magnitude] = calculateLoops(baseTimes,
                    magnitude);
        }
        return this;
    }

    /** Optional, default to 10 samples per iteration. */
    public AbstractSequenceBuilder<T> setSamplePerIterations(final int samples) {
        this.samples = samples;
        return this;
    }

    /** Optional, default to 10 seconds. */
    public AbstractSequenceBuilder<T> setTimeout(final long timeout,
            final TimeUnit unit) {
        this.timeout = TimeUnit.NANOSECONDS.convert(timeout, unit);
        return this;
    }

    private static long calculateLoops(final int baseTimes, final int magnitude) {
        return Math.round(baseTimes * Math.pow(10, magnitude));
    }

    protected long[] getIterationsProgression() {
        return iterationsProgression;
    }

    protected PerformanceTimer getPerformanceTimer() {
        return performanceTimer;
    }

    protected int getSamples() {
        return samples;
    }

    protected long getTimeout() {
        return timeout;
    }
}
