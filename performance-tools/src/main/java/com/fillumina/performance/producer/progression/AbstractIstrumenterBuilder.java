package com.fillumina.performance.producer.progression;

import com.fillumina.performance.producer.InstrumentablePerformanceExecutor;
import java.io.Serializable;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public abstract class AbstractIstrumenterBuilder
        <T extends AbstractIstrumenterBuilder,
            V extends InstrumentablePerformanceExecutor<?>>
        implements  Serializable {
    private static final long serialVersionUID = 1L;
    private int samplesPerMagnitude;
    private Long timeout;
    private String message = "";
    private boolean checkStdDeviation = true;

    public abstract V build();

    protected void validate() {
        if (getSamplesPerMagnitude() <= 0) {
            throw new IllegalArgumentException(
                    "cannot manage negative or 0 samples: " +
                    getSamplesPerMagnitude());
        }
        if (getTimeoutInNanoseconds() <= 0) {
            throw new IllegalArgumentException(
                    "cannot manage negative or 0 timeout: " +
                    getTimeoutInNanoseconds());
        }
    }

    /**
     * If during the execution of a progression the stddev increases
     * in respect to the previous than repeat the current progression.
     * This is active by default but may be switched off in case of some
     * tests that relays on the number of tests executed.
     */
    public T setCheckStdDeviation(final boolean checkStdDeviation) {
        this.checkStdDeviation = checkStdDeviation;
        return (T) this;
    }

    /**
     * How many times a test is repeated (with all its iterations) to
     * create the samples from which the average statistics will be
     * extracted.
     * Optional, default to 10 samples per magnitude.
     *
     */
    @SuppressWarnings("unchecked")
    public T setSamplesPerMagnitude(final int samplesPerMagnitude) {
        this.samplesPerMagnitude = samplesPerMagnitude;
        return (T) this;
    }

    /** Optional, default to 10 seconds. */
    @SuppressWarnings("unchecked")
    public T setTimeout(final long timeout,
            final TimeUnit unit) {
        this.timeout = TimeUnit.NANOSECONDS.convert(timeout, unit);
        return (T) this;
    }

    @SuppressWarnings("unchecked")
    public T setUnlimitedTimeout() {
        timeout = null;
        return (T) this;
    }

    /** Specify the nanoseconds for the timeout. */
    @SuppressWarnings("unchecked")
    public T setTimeoutInNanoseconds(final long timeout) {
        this.timeout = timeout;
        return (T) this;
    }

    @SuppressWarnings("unchecked")
    public T setMessage(final String message) {
        this.message = message;
        return (T) this;
    }

    protected int getSamplesPerMagnitude() {
        return samplesPerMagnitude;
    }

    protected long getTimeoutInNanoseconds() {
        return timeout;
    }

    public String getMessage() {
        return message;
    }

    public boolean isCheckStdDeviation() {
        return checkStdDeviation;
    }
}
