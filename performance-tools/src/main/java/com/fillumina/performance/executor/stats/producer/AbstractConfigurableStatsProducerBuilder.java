package com.fillumina.performance.executor.stats.producer;

import com.fillumina.performance.util.CallBackBuilder;
import com.fillumina.performance.util.filter.ListFilter;
import com.fillumina.performance.util.unit.IntervalUnit;
import com.fillumina.performance.util.unit.Quantity;

/**
 *
 * @param I self used for fluid interface
 * @param C call back object returned by {@link #end()}
 *
 * @author Francesco Illuminati
 */
public abstract class AbstractConfigurableStatsProducerBuilder<I, C>
        extends CallBackBuilder<C, ConfigurableStatsProducer<?,?>>
        implements ConfigurableStatsProducer.Configuration {
    private static final Quantity<IntervalUnit> UNLIMITED =
            IntervalUnit.NANOSECONDS.quantity(-1);

    private Quantity<IntervalUnit> timeoutNs = UNLIMITED;
    private int garbageCollectorMillis = 250;
    private ListFilter<Double> sampleFilter = null;
    private boolean coolDownCpu = true;

    public AbstractConfigurableStatsProducerBuilder() {
        super();
    }

    public AbstractConfigurableStatsProducerBuilder(C caller) {
        super(caller);
    }

    public AbstractConfigurableStatsProducerBuilder(
            Setter<C, ConfigurableStatsProducer<?,?>> setter) {
        super(setter);
    }

    /**
     * Timeout after which the test is stopped with an exception,
     * default is 10 seconds.
     */
    @SuppressWarnings("unchecked")
    public I setTimeout(Quantity<IntervalUnit> timeout) {
        this.timeoutNs = timeout;
        return (I) this;
    }

    /** Removes the timeout. */
    @SuppressWarnings("unchecked")
    public I setUnlimitedTimeout() {
        timeoutNs = UNLIMITED;
        return (I) this;
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
     * @param sampleFilter if true activates the outliers removal.
     */
    @SuppressWarnings("unchecked")
    public I setSampleFilter(
            ListFilter<Double> sampleFilter) {
        this.sampleFilter = sampleFilter;
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

    public Quantity<IntervalUnit> getTimeoutNs() {
        return timeoutNs;
    }

    @Override
    public int getGarbageCollectorMillis() {
        return garbageCollectorMillis;
    }

    @Override
    public ListFilter<Double> getSampleFilter() {
        return sampleFilter;
    }

    @Override
    public boolean isCoolDownCpuActive() {
        return coolDownCpu;
    }

    @Override
    public Quantity<IntervalUnit> getStatsTimeout() {
        return timeoutNs;
    }

    protected ConfigurableStatsProducer<?,?>
        buildConfigurableStatsProducerWithStrategy(
                ConfigurableStatsProducer.Strategy strategy) {
        return new ConfigurableStatsProducer<>(this, strategy);
    }
}
