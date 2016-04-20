package com.fillumina.performance.progression;

import com.fillumina.performance.stats.TimeLimited;
import com.fillumina.performance.util.Builder;
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
        <B extends AbstractIstrumenterBuilder<B,E>, E>
        implements  TimeLimited, Builder<E> {
    protected long timeoutNs = 10_000_000_000L; // 10 sec
    protected String message = "test";

    /** Optional, default to 10 seconds. */
    @SuppressWarnings("unchecked")
    @Override
    public B setTimeout(final long timeout,
            final TimeUnit unit) {
        this.timeoutNs = TimeUnit.NANOSECONDS.convert(timeout, unit);
        return (B) this;
    }

    @SuppressWarnings("unchecked")
    public B setUnlimitedTimeout() {
        timeoutNs = -1;
        return (B) this;
    }

    /** Specify the nanoseconds for the timeout. */
    @SuppressWarnings("unchecked")
    public B setTimeoutNanoseconds(final long timeout) {
        this.timeoutNs = timeout;
        return (B) this;
    }

    /** Specify the seconds for the timeout. */
    public B setTimeoutSeconds(final int seconds) {
        return setTimeoutNanoseconds(seconds * 1_000_000_000L);
    }

    /** Specify the minutes for the timeout. */
    public B setTimeoutMinutes(final int minutes) {
        return setTimeoutSeconds(minutes * 60);
    }

    @SuppressWarnings("unchecked")
    public B setMessage(final String message) {
        this.message = message;
        return (B) this;
    }
}
