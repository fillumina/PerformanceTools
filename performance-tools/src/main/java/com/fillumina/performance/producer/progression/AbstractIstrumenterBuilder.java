package com.fillumina.performance.producer.progression;

import com.fillumina.performance.producer.InstrumentablePerformanceExecutor;
import com.fillumina.performance.producer.TimeLimited;
import com.fillumina.performance.util.Builder;
import java.io.Serializable;
import java.util.concurrent.TimeUnit;

/**
 * A skeleton class with common logic for builders.
 * <p>
 * This builder is also
 * an {@link InstrumentablePerformanceExecutor} to allow being created
 * out of a
 * {@link InstrumentablePerformanceExecutor#instrumentedBy(com.fillumina.performance.producer.PerformanceExecutorInstrumenter)}:
 * <pre>
    performanceTimer.instrumentedBy(
            <b>AutoProgressionPerformanceInstrumenter.builder()</b>)
      .setMaxStandardDeviation(1)
      .build()
      .execute();
 * </pre>
 * which is equivalent (and can be used interchangeably) to:
 * <pre>
    performanceTimer.instrumentedBy(
            <b>AutoProgressionPerformanceInstrumenter.builder()
                .setMaxStandardDeviation(1)
                .build()</b>)
      .execute();
 * </pre>
 *
 * @author Francesco Illuminati
 */
public abstract class AbstractIstrumenterBuilder
        <T extends AbstractIstrumenterBuilder<T,V>,
            V extends InstrumentablePerformanceExecutor<?>>
        implements  TimeLimited, Serializable, Builder<V> {
    private static final long serialVersionUID = 1L;
    private int samplesPerStep;
    private Long timeoutNs;
    private String message = "";
    private boolean checkStdDeviation = true;

    protected void validate() {
        if (getSamplesPerStep() <= 0) {
            throw new IllegalArgumentException(
                    "cannot manage negative or 0 samples: " +
                    getSamplesPerStep());
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
    @SuppressWarnings("unchecked")
    public T setCheckStdDeviation(final boolean checkStdDeviation) {
        this.checkStdDeviation = checkStdDeviation;
        return (T) this;
    }

    /**
     * How many times a test is repeated (with all its iterations) to
     * create the samples from which the average statistics will be
     * extracted (i.e. standard deviation).
     * Optional, default to 10 samples per magnitude.
     *
     */
    @SuppressWarnings("unchecked")
    public T setSamplesPerStep(final int samplesPerStep) {
        this.samplesPerStep = samplesPerStep;
        return (T) this;
    }

    /** Optional, default to 10 seconds. */
    @SuppressWarnings("unchecked")
    @Override
    public T setTimeout(final long timeout,
            final TimeUnit unit) {
        this.timeoutNs = TimeUnit.NANOSECONDS.convert(timeout, unit);
        return (T) this;
    }

    @SuppressWarnings("unchecked")
    public T setUnlimitedTimeout() {
        timeoutNs = null;
        return (T) this;
    }

    /** Specify the nanoseconds for the timeout. */
    @SuppressWarnings("unchecked")
    public T setTimeoutInNanoseconds(final long timeout) {
        this.timeoutNs = timeout;
        return (T) this;
    }

    @SuppressWarnings("unchecked")
    public T setMessage(final String message) {
        this.message = message;
        return (T) this;
    }

    protected int getSamplesPerStep() {
        return samplesPerStep;
    }

    protected long getTimeoutInNanoseconds() {
        return timeoutNs;
    }

    protected String getMessage() {
        return message;
    }

    public boolean isCheckStdDeviation() {
        return checkStdDeviation;
    }
}
