package com.fillumina.performance.speed.stats.progression;

import com.fillumina.performance.util.CallBackBuilder;
import java.util.concurrent.TimeUnit;

/**
 *
 * @param I self used for fluid interface
 * @param C call back object returned by {@link #end()}
 *
 * @author Francesco Illuminati
 */
public abstract class AbstractConfigurableStatsProducerBuilder<I, C>
        extends CallBackBuilder<C, ConfigurableStatsProducer>
        implements ConfigurableStatsProducer.Configuration {

    private long timeoutNs = -1L; // no timeouts
    private int garbageCollectorMillis = 250;
    private boolean filterSamples = true;
    private boolean coolDownCpu = true;

    public AbstractConfigurableStatsProducerBuilder() {
        super();
    }

    public AbstractConfigurableStatsProducerBuilder(C caller) {
        super(caller);
    }

    public AbstractConfigurableStatsProducerBuilder(
            Setter<C, ConfigurableStatsProducer> setter) {
        super(setter);
    }

    /**
     * Timeout after which the test is stopped with an exception,
     * default is 10 seconds.
     */
    @SuppressWarnings("unchecked")
    public I setTimeout(
            final long timeout,
            final TimeUnit unit) {
        this.timeoutNs = TimeUnit.NANOSECONDS.convert(timeout, unit);
        return (I) this;
    }

    /** Removes the timeout. */
    @SuppressWarnings("unchecked")
    public I setUnlimitedTimeout() {
        timeoutNs = -1;
        return (I) this;
    }

    /** Specifies the nanoseconds for the timeout. */
    @SuppressWarnings("unchecked")
    public I setTimeoutNanoseconds(
            final long timeout) {
        this.timeoutNs = timeout;
        return (I) this;
    }

    /** Specifies the seconds for the timeout. */
    public I setTimeoutSeconds(
            final int seconds) {
        return setTimeoutNanoseconds(seconds * 1_000_000_000L);
    }

    /** Specifies the minutes for the timeout. */
    public I setTimeoutMinutes(
            final int minutes) {
        return setTimeoutSeconds(minutes * 60);
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
    public I setGarbageCollectorMillis(
            int garbageCollectorMillis) {
        this.garbageCollectorMillis = garbageCollectorMillis;
        return (I) this;
    }

    /**
     * Some collected samples might be affected by transients which could make
     * them irrelevant to the statistics. Those sample should be removed. This
     * switch activates the outliers removal algorithms.
     *
     * @param filterSamples if true activates the outliers removal.
     */
    @SuppressWarnings("unchecked")
    public I setEliminateOutliers(
            boolean filterSamples) {
        this.filterSamples = filterSamples;
        return (I) this;
    }

    /**
     * If true tries sleeps for some seconds if a hot CPU is detected.
     * When the CPU
     * gets hot it might changes its internal workings and decrease the
     * operative frequency. This can have a huge impact on the performance
     * tests.
     */
    @SuppressWarnings("unchecked")
    public I setCoolDownCpu(
            boolean coolDownCpu) {
        this.coolDownCpu = coolDownCpu;
        return (I) this;
    }

    public long getTimeoutNs() {
        return timeoutNs;
    }

    @Override
    public int getGarbageCollectorMillis() {
        return garbageCollectorMillis;
    }

    @Override
    public boolean getFilterSamples() {
        return filterSamples;
    }

    @Override
    public boolean getCoolDownCpu() {
        return coolDownCpu;
    }

    @Override
    public long getTimeoutNanoseconds() {
        return timeoutNs;
    }

    protected ConfigurableStatsProducer
        buildConfigurableStatsProducerWithStrategy(
            ConfigurableStatsProducer.Strategy strategy) {
        return new ConfigurableStatsProducer(this, strategy);
    }
}
