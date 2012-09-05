package com.fillumina.performance.producer.progression;

import com.fillumina.performance.producer.PerformanceExecutor;
import com.fillumina.performance.producer.PerformanceExecutorInstrumenter;
import com.fillumina.performance.producer.timer.PerformanceTimer;
import java.io.Serializable;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author fra
 */
public abstract class AbstractIstrumenterBuilder
            <T extends PerformanceExecutorInstrumenter, V extends PerformanceExecutor<?>>
        implements PerformanceExecutorInstrumenter, Serializable {
    private static final long serialVersionUID = 1L;

    private PerformanceExecutor<?> performanceTimer;
    private int samples;
    private long timeout;

    public abstract V build();

    protected void check() {
        if (getSamples() <= 0) {
            throw new IllegalArgumentException(
                    "cannot manage negative or 0 samples: " + getSamples());
        }
        if (getTimeout() <= 0) {
            throw new IllegalArgumentException(
                    "cannot manage negative or 0 timeout: " + getTimeout());
        }
    }

    /**
     * Mandatory.
     * Most of the time it is not called directly by the user code.
     */
    @Override
    @SuppressWarnings("unchecked")
    public T instrument(final PerformanceExecutor<?> performanceExecutor) {
        this.performanceTimer = performanceExecutor;
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

    /** Specify the nanoseconds for the timeout. */
    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }

    public PerformanceExecutor<?> getPerformanceTimer() {
        return performanceTimer;
    }

    public int getSamples() {
        return samples;
    }

    public long getTimeout() {
        return timeout;
    }
}
