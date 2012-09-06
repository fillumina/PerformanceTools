package com.fillumina.performance.producer.progression;

import com.fillumina.performance.producer.DefaultPerformanceExecutorInstrumenter;
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
        extends DefaultPerformanceExecutorInstrumenter<T>
        implements  Serializable {
    private static final long serialVersionUID = 1L;
    private int samplesPerMagnitude;
    private long timeout;

    public abstract V build();

    protected void check() {
        if (getSamplesPerMagnitude() <= 0) {
            throw new IllegalArgumentException(
                    "cannot manage negative or 0 samples: " + getSamplesPerMagnitude());
        }
        if (getTimeoutInNanoseconds() <= 0) {
            throw new IllegalArgumentException(
                    "cannot manage negative or 0 timeout: " + getTimeoutInNanoseconds());
        }
    }

    /**
     * Optional, default to 10 samples per iteration.
     * <b>IMPORTANT:</b> this parameter (either it is specified or left with
     * the default value) will override whatever has been specified
     * in the {@link PerformanceTimer#setIterations(int) }.
     */
    @SuppressWarnings("unchecked")
    public T setIterationsPerMagnitude(final int samples) {
        this.samplesPerMagnitude = samples;
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
    public void setTimeoutInNanoseconds(long timeout) {
        this.timeout = timeout;
    }

    protected int getSamplesPerMagnitude() {
        return samplesPerMagnitude;
    }

    protected long getTimeoutInNanoseconds() {
        return timeout;
    }
}
