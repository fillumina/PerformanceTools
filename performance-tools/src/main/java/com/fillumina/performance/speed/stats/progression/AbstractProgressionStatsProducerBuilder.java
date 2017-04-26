package com.fillumina.performance.speed.stats.progression;

import com.fillumina.performance.infrastructure.PerformanceConsumer;
import com.fillumina.performance.speed.stats.SpeedStats;
import com.fillumina.performance.util.Builder;
import com.fillumina.performance.util.TimeLimited;
import com.fillumina.performance.util.stats.Ratio;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author Francesco Illuminati
 */
public abstract class AbstractProgressionStatsProducerBuilder
        <B extends AbstractProgressionStatsProducerBuilder<B,E>, E>
        implements  TimeLimited, Builder<E> {
    public final static double MAX_PERCENTAGE_MARGIN = 5.0;
    public final static int SAMPLES = 100;

    protected long timeoutNs = -1L; // no timeouts
    protected String name = null;
    protected int garbageCollectorMillis = 250;
    protected PerformanceConsumer<SpeedStats>[] performanceStatsConsumers;
    protected boolean filterSamples = true;
    protected boolean coolDownCpu = true;
    protected Ratio confidence = Ratio.P_95;

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
            PerformanceConsumer<SpeedStats>... performanceStatsConsumers) {
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
            PerformanceConsumer<SpeedStats>... performanceStatsConsumers) {
        this.performanceStatsConsumers = performanceStatsConsumers;
        return (B) this;
    }

    /**
     * Some collected samples might be affected by transients which could make
     * them irrelevant to the statistics. Those sample should be removed. This
     * switch activates the outliers removal algorithms.
     *
     * @param filterSamples if true activates the outliers removal.
     */
    @SuppressWarnings("unchecked")
    public B setEliminateOutliers(boolean filterSamples) {
        this.filterSamples = filterSamples;
        return (B) this;
    }

    /**
     * If true tries sleeps for some seconds if a hot CPU is detected.
     * When the CPU
     * gets hot it might changes its internal workings and decrease the
     * operative frequency. This can have a huge impact on the performance
     * tests.
     */
    @SuppressWarnings("unchecked")
    public B setCoolDownCpu(boolean coolDownCpu) {
        this.coolDownCpu = coolDownCpu;
        return (B) this;
    }

    /** Sets the confidence level (from 0 to 1, usually 0.95 or 0.99). */
    @SuppressWarnings("unchecked")
    public B setConfidence(Ratio confidence) {
        this.confidence = confidence;
        return (B) this;
    }
}
