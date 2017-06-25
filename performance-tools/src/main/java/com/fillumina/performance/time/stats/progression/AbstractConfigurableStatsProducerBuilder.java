package com.fillumina.performance.time.stats.progression;

import com.fillumina.performance.time.stats.TimeSampleCollector;
import com.fillumina.performance.time.stats.TimeStats;
import com.fillumina.performance.time.stats.TimeStatsType;
import com.fillumina.performance.util.CallBackBuilder;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

/**
 *
 * @param I self used for fluid interface
 * @param C call back object returned by {@link #end()}
 *
 * @author Francesco Illuminati
 */
public abstract class AbstractConfigurableStatsProducerBuilder
            <I, C, T extends TimeStats>
        extends CallBackBuilder<C, ConfigurableStatsProducer<T>>
        implements ConfigurableStatsProducer.Configuration {

    private long timeoutNs = -1L; // no timeouts
    private int garbageCollectorMillis = 250;
    private boolean filterSamples = true;
    private boolean coolDownCpu = true;
    private TimeStatsType type = TimeStatsType.AverageTime;

    public AbstractConfigurableStatsProducerBuilder() {
        super();
    }

    public AbstractConfigurableStatsProducerBuilder(C caller) {
        super(caller);
    }

    public AbstractConfigurableStatsProducerBuilder(
            Setter<C, ConfigurableStatsProducer<T>> setter) {
        super(setter);
    }

    @SuppressWarnings("unchecked")
    public I setStatsType(TimeStatsType type) {
        this.type = type;
        return (I) this;
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

    @Override
    @SuppressWarnings("unchecked")
    public Supplier<TimeSampleCollector<? extends TimeStats>> getCollector() {
        switch(type) {
            case AverageTime:
                return TimeSampleCollector::createSpeedCollector;
            case Throughput:
                return TimeSampleCollector::createFrequencyCollector;
        }
        throw new AssertionError("case not found: " + type);
    }

    protected ConfigurableStatsProducer<T>
        buildConfigurableStatsProducerWithStrategy(
                ConfigurableStatsProducer.Strategy<T> strategy) {
        return new ConfigurableStatsProducer<>(this, strategy);
    }
}
