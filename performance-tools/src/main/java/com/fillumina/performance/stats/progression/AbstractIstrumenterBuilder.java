package com.fillumina.performance.stats.progression;

import com.fillumina.performance.infrastructure.PerformanceConsumer;
import com.fillumina.performance.stats.PerformanceStats;
import com.fillumina.performance.util.Builder;
import com.fillumina.performance.util.TimeLimited;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author Francesco Illuminati
 */
public abstract class AbstractIstrumenterBuilder
        <B extends AbstractIstrumenterBuilder<B,E>, E>
        implements  TimeLimited, Builder<E> {
    protected long timeoutNs = 10_000_000_000L; // 10 sec
    protected String name = null;
    protected int garbageCollectorMillis = 250;
    protected PerformanceConsumer[] performanceStatsConsumers;
    protected boolean eliminateOutliers = true;
    protected double confidence = 0.95;

    /**
     * Timeout after which the test is stopped with an exception,
     * default is 10 seconds.
     */
    @SuppressWarnings("unchecked")
    @Override
    public B setTimeout(final long timeout,
            final TimeUnit unit) {
        this.timeoutNs = TimeUnit.NANOSECONDS.convert(timeout, unit);
        return (B) this;
    }

    /** Removes the timeout. */
    @SuppressWarnings("unchecked")
    public B setUnlimitedTimeout() {
        timeoutNs = -1;
        return (B) this;
    }

    /** Specifies the nanoseconds for the timeout. */
    @SuppressWarnings("unchecked")
    public B setTimeoutNanoseconds(final long timeout) {
        this.timeoutNs = timeout;
        return (B) this;
    }

    /** Specifies the seconds for the timeout. */
    public B setTimeoutSeconds(final int seconds) {
        return setTimeoutNanoseconds(seconds * 1_000_000_000L);
    }

    /** Specifies the minutes for the timeout. */
    public B setTimeoutMinutes(final int minutes) {
        return setTimeoutSeconds(minutes * 60);
    }

    /** Sets the test name. */
    @SuppressWarnings("unchecked")
    public B setName(final String name) {
        this.name = name;
        return (B) this;
    }

    /**
     * {@link System#gc() } should return after having performed garbage
     * collection but sometimes it just waits for a better time. Setting
     * the current thread on wait for some time might help the JVM
     * deciding to actually perform garbage collection.
     * @param garbageCollectorMillis -1 disable garbage collector (default)
     *                               otherwise how many milliseconds to wait.
     */
    @SuppressWarnings("unchecked")
    public B setGarbageCollectorMillis(int garbageCollectorMillis) {
        this.garbageCollectorMillis = garbageCollectorMillis;
        return (B) this;
    }

    /**
     * If the {@code condition} is true dispatches the collected statistics
     * to the given {@link PerformanceConsumer<PerformanceStats>}s even if
     * thy should be rejected.
     *
     * @param condition has to be true to enable the consumers
     * @param performanceStatsConsumers consumers that receive the statistics
     */
    @SuppressWarnings("unchecked")
    public B setPerformanceStatsConsumerIf(boolean condition,
            PerformanceConsumer<PerformanceStats>... performanceStatsConsumers) {
        if (condition) {
            this.performanceStatsConsumers = performanceStatsConsumers;
        }
        return (B) this;
    }

    /**
     * Dispatches the collected statistics
     * to the given {@link PerformanceConsumer<PerformanceStats>}s even if
     * thy should be rejected.
     *
     * @param performanceStatsConsumers consumers that receive the statistics
     */
    @SuppressWarnings("unchecked")
    public B setPerformanceStatsConsumer(
            PerformanceConsumer<PerformanceStats>... performanceStatsConsumers) {
        this.performanceStatsConsumers = performanceStatsConsumers;
        return (B) this;
    }

    /**
     * Some collected samples might be affected by transients which could make
     * them irrelevant to the statistics. Those sample should be removed. This
     * switch activates the outliers removal algorithms.
     *
     * @param eliminateOutliers if true activates the outliers removal.
     */
    @SuppressWarnings("unchecked")
    public B setEliminateOutliers(boolean eliminateOutliers) {
        this.eliminateOutliers = eliminateOutliers;
        return (B) this;
    }

    /** Sets the confidence level (from 0 to 1, usually 0.95 or 0.99). */
    @SuppressWarnings("unchecked")
    public B setConfidence(double confidence) {
        this.confidence = confidence;
        return (B) this;
    }
}
