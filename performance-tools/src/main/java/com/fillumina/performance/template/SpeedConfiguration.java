package com.fillumina.performance.template;

import com.fillumina.performance.infrastructure.PerformanceConsumer;
import com.fillumina.performance.infrastructure.PerformanceConsumerAggregator;
import com.fillumina.performance.speed.sample.SpeedSample;
import com.fillumina.performance.speed.sample.iterator.SelectorMultiThreadPerformanceExecutor;
import com.fillumina.performance.speed.stats.SpeedStats;
import com.fillumina.performance.speed.stats.progression.ConfigurableStatsProducer;
import com.fillumina.performance.speed.stats.progression.ConsecutiveExecutorStatsProducer;
import com.fillumina.performance.speed.stats.progression.IncreasingSamplesStrategy;
import com.fillumina.performance.util.Activable;
import com.fillumina.performance.util.CallBackBuilder;
import com.fillumina.performance.util.TName;
import java.util.concurrent.TimeUnit;

/**
 * Configures the tests using a <i>fluent interface</i>.
 *
 * @author Francesco Illuminati
 */
public class SpeedConfiguration<C>
        extends CallBackBuilder<C, SpeedConfiguration<C>>
        implements
            Activable,
            SelectorMultiThreadPerformanceExecutor.Configuration,
            ConsecutiveExecutorStatsProducer.Configuration,
            ConfigurableStatsProducer.Configuration,
            IncreasingSamplesStrategy.Configuration {

    private boolean active = false;

    private final  PerformanceConsumerAggregator<SpeedSample> sampleConsumer =
            new PerformanceConsumerAggregator<>();

    private final PerformanceConsumerAggregator<SpeedStats> statsConsumer =
            new PerformanceConsumerAggregator<>();

    public SpeedConfiguration() {
        super();
    }

    public SpeedConfiguration(C caller) {
        super(caller);
    }

    public SpeedConfiguration(Setter<C, SpeedConfiguration<C>> setter) {
        super(setter);
    }

    /** Sets speed test. */
    public SpeedConfiguration<C> setActive(boolean active) {
        this.active = active;
        return this;
    }

    @Override
    public boolean isActive() {
        return active;
    }

    protected SpeedConfiguration<C> setPerformanceSampleConsumer(
            PerformanceConsumer<SpeedSample> sampleConsumer) {
        this.sampleConsumer.add(sampleConsumer);
        return this;
    }

    /** Sets a statistics consumer. */
    public SpeedConfiguration<C> setPerformanceStatsConsumer(
            PerformanceConsumer<SpeedStats> statsPerformanceConsumer) {
        this.statsConsumer.add(statsPerformanceConsumer);
        return this;
    }

    private int concurrencyLevel;
    private int workerNumber;
    private long timeoutValue;
    private TimeUnit timeoutUnit;
    private boolean consecutiveExecution;
    private TName name;
    private int garbageCollectorMillis;
    private boolean filterSamples;
    private boolean coolDownCpu;
    private int samples;
    private double maxPercentageMargin;
    private int millisecondsPerSample;

    public SpeedConfiguration<C> setConcurrencyLevel(final int value) {
        this.concurrencyLevel = value;
        return this;
    }

    public SpeedConfiguration<C> setWorkerNumber(final int value) {
        this.workerNumber = value;
        return this;
    }

    public SpeedConfiguration<C> setTimeout(long time, TimeUnit unit) {
        setTimeoutValue(time);
        setTimeoutUnit(unit);
        return this;
    }

    public SpeedConfiguration<C> setTimeoutValue(final long value) {
        this.timeoutValue = value;
        return this;
    }

    public SpeedConfiguration<C> setTimeoutUnit(final TimeUnit value) {
        this.timeoutUnit = value;
        return this;
    }

    public SpeedConfiguration<C> setConsecutiveExecution(final boolean value) {
        this.consecutiveExecution = value;
        return this;
    }

    public SpeedConfiguration<C> setName(final TName value) {
        this.name = value;
        return this;
    }

    public SpeedConfiguration<C> setGarbageCollectorMillis(final int value) {
        this.garbageCollectorMillis = value;
        return this;
    }

    public SpeedConfiguration<C> setFilterSamples(final boolean value) {
        this.filterSamples = value;
        return this;
    }

    public SpeedConfiguration<C> setCoolDownCpu(final boolean value) {
        this.coolDownCpu = value;
        return this;
    }

    public SpeedConfiguration<C> setSamples(final int value) {
        this.samples = value;
        return this;
    }

    public SpeedConfiguration<C> setMaxPercentageMargin(final double value) {
        this.maxPercentageMargin = value;
        return this;
    }

    public SpeedConfiguration<C> setMillisecondsPerSample(final int value) {
        this.millisecondsPerSample = value;
        return this;
    }

    @Override
    public int getConcurrencyLevel() {
        return concurrencyLevel;
    }

    @Override
    public int getWorkerNumber() {
        return workerNumber;
    }

    @Override
    public long getTimeoutValue() {
        return timeoutValue;
    }

    @Override
    public TimeUnit getTimeoutUnit() {
        return timeoutUnit;
    }

    @Override
    public boolean isConsecutiveExecution() {
        return consecutiveExecution;
    }

    @Override
    public TName getName() {
        return name;
    }

    @Override
    public long getTimeoutNanoseconds() {
        return TimeUnit.NANOSECONDS.convert(timeoutValue, timeoutUnit);
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
    public PerformanceConsumer<SpeedStats> getStatsConsumers() {
        return statsConsumer;
    }

    @Override
    public int getSamples() {
        return samples;
    }

    @Override
    public double getMaxPercentageMargin() {
        return maxPercentageMargin;
    }

    @Override
    public int getMillisecondsPerSample() {
        return millisecondsPerSample;
    }

    @Override
    public SpeedConfiguration<C> build() {
        return this;
    }
}
